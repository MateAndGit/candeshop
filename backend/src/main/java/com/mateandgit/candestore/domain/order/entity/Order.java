package com.mateandgit.candestore.domain.order.entity;

import com.mateandgit.candestore.domain.order.dto.OrderRequest;
import com.mateandgit.candestore.domain.orderitem.entity.OrderItem;
import com.mateandgit.candestore.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mateandgit.candestore.domain.order.entity.OrderStatus.ORDERED;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @CreatedDate
    private LocalDateTime orderDate;

    private BigDecimal totalPrice;

    private Long totalCount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order createOrder(UserEntity user, OrderRequest request) {
        Order order = Order.builder()
                .user(user)
                .totalPrice(request.totalPrice())
                .totalCount(request.totalCount())
                .status(ORDERED)
                .orderItems(new ArrayList<>())
                .build();

        return order;
    }

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        if (item.getOrder() != this) {
            item.setOrder(this);
        }
    }
}