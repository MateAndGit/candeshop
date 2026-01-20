package com.mateandgit.candestore.domain.order.repository;


import com.mateandgit.candestore.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
