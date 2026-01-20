package com.mateandgit.candestore.domain.cartitem.repository;

import com.mateandgit.candestore.domain.cartitem.entity.CartItem;
import com.mateandgit.candestore.domain.product.entity.Product;
import com.mateandgit.candestore.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Page<CartItem> findAllByUser(UserEntity userEntity, Pageable pageable);

    @Query("SELECT SUM(ci.product.price * ci.amount) FROM CartItem ci WHERE ci.user = :user")
    BigDecimal getTotalCartPrice(@Param("user") UserEntity user);

    Optional<CartItem> findByUserAndProduct(UserEntity userEntity, Product product);

    Optional<CartItem> deleteByUser(UserEntity userEntity);
}
