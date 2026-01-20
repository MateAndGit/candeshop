package com.mateandgit.candestore.domain.order.service;

import com.mateandgit.candestore.domain.cartitem.entity.CartItem;
import com.mateandgit.candestore.domain.cartitem.repository.CartItemRepository;
import com.mateandgit.candestore.domain.order.dto.OrderRequest;
import com.mateandgit.candestore.domain.order.entity.Order;
import com.mateandgit.candestore.domain.order.entity.OrderStatus;
import com.mateandgit.candestore.domain.order.repository.OrderRepository;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private com.mateandgit.candestore.util.EmailService emailService;

    private UserEntity testUser;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
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

        testProduct1 = Product.builder()
                .title("Product 1")
                .description("Description 1")
                .price(new BigDecimal("10000"))
                .build();
        testProduct1 = productRepository.save(testProduct1);

        testProduct2 = Product.builder()
                .title("Product 2")
                .description("Description 2")
                .price(new BigDecimal("20000"))
                .build();
        testProduct2 = productRepository.save(testProduct2);
    }

    @Test
    @DisplayName("주문 처리 성공")
    void processOrder_success() {
        // given
        CartItem cartItem1 = CartItem.builder()
                .user(testUser)
                .product(testProduct1)
                .amount(1L)
                .build();
        cartItemRepository.save(cartItem1);

        CartItem cartItem2 = CartItem.builder()
                .user(testUser)
                .product(testProduct2)
                .amount(2L)
                .build();
        cartItemRepository.save(cartItem2);

        OrderRequest request = new OrderRequest(
                List.of(
                        new OrderRequest.OrderItemDto(
                                testProduct1.getId(),
                                testProduct1.getPrice(),
                                1L
                        ),
                        new OrderRequest.OrderItemDto(
                                testProduct2.getId(),
                                testProduct2.getPrice(),
                                2L
                        )
                ),
                new BigDecimal("50000"), // 10000 + 20000 * 2
                3L
        );

        // when
        orderService.processOrder(testUser.getEmail(), request);

        // then
        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);

        Order order = orders.get(0);
        assertThat(order.getUser().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(order.getTotalPrice()).isEqualTo(new BigDecimal("50000"));
        assertThat(order.getTotalCount()).isEqualTo(3L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDERED);
        assertThat(order.getOrderItems()).hasSize(2);

        // 장바구니가 비워졌는지 확인
        assertThat(cartItemRepository.findAllByUser(testUser, org.springframework.data.domain.PageRequest.of(0, 10)).getContent()).isEmpty();

        // 이메일 발송 확인
        verify(emailService, times(1)).sendOrderConfirmation(any(), any());
        verify(emailService, times(1)).sendAdminNotification(any());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 주문 시 예외 발생")
    void processOrder_userNotFound_throwsException() {
        // given
        OrderRequest request = new OrderRequest(
                List.of(),
                new BigDecimal("0"),
                0L
        );

        // when & then
        assertThatThrownBy(() -> orderService.processOrder("notfound@example.com", request))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 주문 시 예외 발생")
    void processOrder_productNotFound_throwsException() {
        // given
        OrderRequest request = new OrderRequest(
                List.of(
                        new OrderRequest.OrderItemDto(
                                999L, // 존재하지 않는 상품 ID
                                new BigDecimal("10000"),
                                1L
                        )
                ),
                new BigDecimal("10000"),
                1L
        );

        // when & then
        assertThatThrownBy(() -> orderService.processOrder(testUser.getEmail(), request))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }
}
