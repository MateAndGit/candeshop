package com.mateandgit.candestore.domain.order.service;

import com.mateandgit.candestore.domain.cartitem.repository.CartItemRepository;
import com.mateandgit.candestore.domain.order.dto.OrderRequest;
import com.mateandgit.candestore.domain.order.entity.Order;
import com.mateandgit.candestore.domain.order.repository.OrderRepository;
import com.mateandgit.candestore.domain.orderitem.entity.OrderItem;
import com.mateandgit.candestore.domain.product.entity.Product;
import com.mateandgit.candestore.domain.product.repository.ProductRepository;
import com.mateandgit.candestore.domain.user.entity.UserEntity;
import com.mateandgit.candestore.domain.user.repository.UserRepository;
import com.mateandgit.candestore.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public void processOrder(String email, OrderRequest request) {
        // 1. 유저 확인
        UserEntity user = userRepository.findByEmail(email).orElseThrow();

        // 2. Order 엔티티 생성 (팩토리 메서드 활용)
        Order order = Order.createOrder(user, request);

        // 3. 각 상품 아이디로 실제 Product를 찾아 OrderItem 연결
        request.orderItems().forEach(dto -> {
            Product product = productRepository.findById(dto.productId()).orElseThrow();
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .orderPrice(dto.price())
                    .amount(dto.count())
                    .order(order)
                    .build();
            order.addOrderItem(item);
        });

        // 4. 저장 및 장바구니 비우기
        orderRepository.save(order);
        cartItemRepository.deleteByUser(user);

        // 5. 이메일 발송
        emailService.sendOrderConfirmation(email, order);
        emailService.sendAdminNotification(order);
    }
}
