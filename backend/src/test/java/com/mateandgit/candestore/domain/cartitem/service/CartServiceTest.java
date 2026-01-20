package com.mateandgit.candestore.domain.cartitem.service;

import com.mateandgit.candestore.domain.cartitem.dto.CartPageResponse;
import com.mateandgit.candestore.domain.cartitem.dto.CartRequest;
import com.mateandgit.candestore.domain.cartitem.dto.CartResponse;
import com.mateandgit.candestore.domain.cartitem.entity.CartItem;
import com.mateandgit.candestore.domain.cartitem.repository.CartItemRepository;
import com.mateandgit.candestore.domain.product.entity.Product;
import com.mateandgit.candestore.domain.product.repository.ProductRepository;
import com.mateandgit.candestore.domain.user.entity.UserEntity;
import com.mateandgit.candestore.domain.user.entity.UserRole;
import com.mateandgit.candestore.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        testUser = UserEntity.builder()
                .email("test@example.com")
                .username("testuser")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.USER)
                .build();
        userRepository.save(testUser);

        testProduct = Product.builder()
                .title("Test Product")
                .description("Test Description")
                .price(new BigDecimal("10000"))
                .build();
        productRepository.save(testProduct);
    }

    @Test
    @DisplayName("장바구니에 상품 추가 성공")
    void addCartItem_success() {
        // given
        CartRequest request = new CartRequest(testProduct.getId(), 1);

        // when
        cartService.addCartItem(testUser.getEmail(), request);

        // then
        CartItem savedItem = cartItemRepository.findByUserAndProduct(testUser, testProduct)
                .orElseThrow();
        assertThat(savedItem.getUser().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(savedItem.getProduct().getId()).isEqualTo(testProduct.getId());
        assertThat(savedItem.getAmount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("이미 장바구니에 있는 상품 추가 시 예외 발생")
    void addCartItem_duplicate_throwsException() {
        // given
        CartRequest request = new CartRequest(testProduct.getId(), 1);
        cartService.addCartItem(testUser.getEmail(), request);

        // when & then
        assertThatThrownBy(() -> cartService.addCartItem(testUser.getEmail(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 장바구니에 있습니다");
    }

    @Test
    @DisplayName("존재하지 않는 상품 추가 시 예외 발생")
    void addCartItem_productNotFound_throwsException() {
        // given
        CartRequest request = new CartRequest(999L, 1);

        // when & then
        assertThatThrownBy(() -> cartService.addCartItem(testUser.getEmail(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    @DisplayName("장바구니 목록 조회 성공")
    void getCartList_success() {
        // given
        CartItem cartItem = CartItem.builder()
                .user(testUser)
                .product(testProduct)
                .amount(1L)
                .build();
        cartItemRepository.save(cartItem);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        CartPageResponse result = cartService.getCartList(testUser.getEmail(), pageable);

        // then
        assertThat(result.cartItem().getContent()).hasSize(1);
        assertThat(result.totalCount()).isEqualTo(1);
        assertThat(result.totalCartPrice()).isEqualTo(new BigDecimal("10000"));
    }

    @Test
    @DisplayName("장바구니 아이템 삭제 성공")
    void deleteCartItem_success() {
        // given
        CartItem cartItem = CartItem.builder()
                .user(testUser)
                .product(testProduct)
                .amount(1L)
                .build();
        CartItem savedItem = cartItemRepository.save(cartItem);

        // when
        cartService.deleteCartItem(testUser.getEmail(), savedItem.getId());

        // then
        assertThat(cartItemRepository.findById(savedItem.getId())).isEmpty();
    }

    @Test
    @DisplayName("다른 사용자의 장바구니 아이템 삭제 시 예외 발생")
    void deleteCartItem_otherUser_throwsException() {
        // given
        UserEntity otherUser = UserEntity.builder()
                .email("other@example.com")
                .username("otheruser")
                .password(passwordEncoder.encode("password123"))
                .role(UserRole.USER)
                .build();
        userRepository.save(otherUser);

        CartItem cartItem = CartItem.builder()
                .user(testUser)
                .product(testProduct)
                .amount(1L)
                .build();
        CartItem savedItem = cartItemRepository.save(cartItem);

        // when & then
        assertThatThrownBy(() -> cartService.deleteCartItem(otherUser.getEmail(), savedItem.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인의 장바구니 아이템만 삭제할 수 있습니다");
    }

    @Test
    @DisplayName("존재하지 않는 장바구니 아이템 삭제 시 예외 발생")
    void deleteCartItem_notFound_throwsException() {
        // when & then
        assertThatThrownBy(() -> cartService.deleteCartItem(testUser.getEmail(), 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cart item not found");
    }
}
