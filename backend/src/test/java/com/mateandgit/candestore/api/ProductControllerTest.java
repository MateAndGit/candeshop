package com.mateandgit.candestore.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateandgit.candestore.domain.product.dto.ProductRequest;
import com.mateandgit.candestore.domain.product.entity.Product;
import com.mateandgit.candestore.domain.product.repository.ProductRepository;
import com.mateandgit.candestore.domain.user.entity.UserEntity;
import com.mateandgit.candestore.domain.user.entity.UserRole;
import com.mateandgit.candestore.domain.user.repository.UserRepository;
import com.mateandgit.candestore.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private UserEntity adminUser;
    private UserEntity normalUser;
    private String adminToken;
    private String normalToken;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        userRepository.deleteAll();

        // 관리자 유저 생성
        adminUser = UserEntity.builder()
                .email("admin@example.com")
                .username("admin")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(adminUser);
        adminToken = jwtUtil.createJwt("access", adminUser.getEmail(), adminUser.getRole().name(), 600000L);

        // 일반 유저 생성
        normalUser = UserEntity.builder()
                .email("user@example.com")
                .username("user")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.USER)
                .build();
        userRepository.save(normalUser);
        normalToken = jwtUtil.createJwt("access", normalUser.getEmail(), normalUser.getRole().name(), 600000L);
    }

    @Test
    @DisplayName("상품 목록 조회 성공 (인증된 사용자)")
    void getProductList_success() throws Exception {
        // given
        Product product = Product.builder()
                .title("Test Product")
                .description("Test Description")
                .price(new BigDecimal("10000"))
                .build();
        productRepository.save(product);

        // when & then
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + normalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Test Product"));
    }

    @Test
    @DisplayName("상품 목록 조회 실패 (인증되지 않은 사용자)")
    void getProductList_unauthorized() throws Exception {
        // when & then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void getProduct_success() throws Exception {
        // given
        Product product = Product.builder()
                .title("Test Product")
                .description("Test Description")
                .price(new BigDecimal("10000"))
                .build();
        Product savedProduct = productRepository.save(product);

        // when & then
        mockMvc.perform(get("/api/products/" + savedProduct.getId())
                        .header("Authorization", "Bearer " + normalToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Product"))
                .andExpect(jsonPath("$.price").value(10000));
    }

    @Test
    @DisplayName("관리자 상품 등록 성공")
    void postProduct_admin_success() throws Exception {
        // given
        ProductRequest request = new ProductRequest();
        request.setTitle("New Product");
        request.setDescription("New Description");
        request.setPrice(new BigDecimal("20000"));

        // when & then
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Product savedProduct = productRepository.findAll().get(0);
        assert savedProduct.getTitle().equals("New Product");
    }

    @Test
    @DisplayName("일반 사용자 상품 등록 실패 (권한 없음)")
    void postProduct_normalUser_forbidden() throws Exception {
        // given
        ProductRequest request = new ProductRequest();
        request.setTitle("New Product");
        request.setDescription("New Description");
        request.setPrice(new BigDecimal("20000"));

        // when & then
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + normalToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 상품 수정 성공")
    void editProduct_admin_success() throws Exception {
        // given
        Product product = Product.builder()
                .title("Original Title")
                .description("Original Description")
                .price(new BigDecimal("10000"))
                .build();
        Product savedProduct = productRepository.save(product);

        ProductRequest request = new ProductRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setPrice(new BigDecimal("20000"));

        // when & then
        mockMvc.perform(put("/api/products/edit/" + savedProduct.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assert updatedProduct.getTitle().equals("Updated Title");
    }

    @Test
    @DisplayName("관리자 상품 삭제 성공")
    void deleteProduct_admin_success() throws Exception {
        // given
        Product product = Product.builder()
                .title("Product to Delete")
                .description("Description")
                .price(new BigDecimal("10000"))
                .build();
        Product savedProduct = productRepository.save(product);

        // when & then
        mockMvc.perform(delete("/api/products/" + savedProduct.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        assert productRepository.findById(savedProduct.getId()).isEmpty();
    }
}
