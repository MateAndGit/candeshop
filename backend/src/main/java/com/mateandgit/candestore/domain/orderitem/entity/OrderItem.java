package com.mateandgit.candestore.domain.orderitem.entity;

import com.mateandgit.candestore.domain.order.entity.Order;
import com.mateandgit.candestore.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private BigDecimal orderPrice;

    @Builder.Default // 빌더 사용 시 기본값 유지
    private Long amount = 1L;

    public void setOrder(Order order) {
        this.order = order;
    }
}