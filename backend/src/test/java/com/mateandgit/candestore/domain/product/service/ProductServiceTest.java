package com.mateandgit.candestore.domain.product.service;

import com.mateandgit.candestore.domain.product.dto.ProductRequest;
import com.mateandgit.candestore.domain.product.dto.ProductResponse;
import com.mateandgit.candestore.domain.product.entity.Product;
import com.mateandgit.candestore.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    void getProductList_success() {
        // given
        Product product1 = Product.builder()
                .title("Product 1")
                .description("Description 1")
                .price(new BigDecimal("10000"))
                .build();
        Product product2 = Product.builder()
                .title("Product 2")
                .description("Description 2")
                .price(new BigDecimal("20000"))
                .build();
        productRepository.save(product1);
        productRepository.save(product2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ProductResponse> result = productService.getProductList(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void getProduct_success() {
        // given
        Product product = Product.builder()
                .title("Test Product")
                .description("Test Description")
                .price(new BigDecimal("15000"))
                .build();
        Product savedProduct = productRepository.save(product);

        // when
        ProductResponse result = productService.getProduct(savedProduct.getId());

        // then
        assertThat(result.title()).isEqualTo("Test Product");
        assertThat(result.description()).isEqualTo("Test Description");
        assertThat(result.price()).isEqualTo(new BigDecimal("15000"));
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외 발생")
    void getProduct_notFound_throwsException() {
        // when & then
        assertThatThrownBy(() -> productService.getProduct(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    @DisplayName("상품 등록 성공")
    void postProduct_success() {
        // given
        ProductRequest request = new ProductRequest();
        request.setTitle("New Product");
        request.setDescription("New Description");
        request.setPrice(new BigDecimal("25000"));

        // when
        productService.postProduct(request);

        // then
        Product savedProduct = productRepository.findAll().get(0);
        assertThat(savedProduct.getTitle()).isEqualTo("New Product");
        assertThat(savedProduct.getDescription()).isEqualTo("New Description");
        assertThat(savedProduct.getPrice()).isEqualTo(new BigDecimal("25000"));
    }

    @Test
    @DisplayName("상품 수정 성공")
    void editProduct_success() {
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

        // when
        productService.editProduct(request, savedProduct.getId());

        // then
        Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedProduct.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("20000"));
    }

    @Test
    @DisplayName("존재하지 않는 상품 수정 시 예외 발생")
    void editProduct_notFound_throwsException() {
        // given
        ProductRequest request = new ProductRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setPrice(new BigDecimal("20000"));

        // when & then
        assertThatThrownBy(() -> productService.editProduct(request, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProduct_success() {
        // given
        Product product = Product.builder()
                .title("Product to Delete")
                .description("Description")
                .price(new BigDecimal("10000"))
                .build();
        Product savedProduct = productRepository.save(product);

        // when
        productService.deleteProduct(savedProduct.getId());

        // then
        assertThat(productRepository.findById(savedProduct.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 상품 삭제 시 예외 발생")
    void deleteProduct_notFound_throwsException() {
        // when & then
        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }
}
