package com.mateandgit.candestore.domain.cartitem.service;

import com.mateandgit.candestore.domain.cartitem.dto.CartPageResponse;
import com.mateandgit.candestore.domain.cartitem.dto.CartRequest;
import com.mateandgit.candestore.domain.cartitem.dto.CartResponse;
import com.mateandgit.candestore.domain.cartitem.entity.CartItem;
import com.mateandgit.candestore.domain.cartitem.repository.CartItemRepository;
import com.mateandgit.candestore.domain.product.entity.Product;
import com.mateandgit.candestore.domain.product.repository.ProductRepository;
import com.mateandgit.candestore.domain.user.entity.UserEntity;
import com.mateandgit.candestore.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartRepository;

    @Transactional(readOnly = true)
    public CartPageResponse getCartList(String email, Pageable pageable) {

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Page<CartResponse> items = cartRepository.findAllByUser(userEntity, pageable)
                .map(CartResponse::new);

        BigDecimal totalCartPrice = cartRepository.getTotalCartPrice(userEntity);
        if (totalCartPrice == null) totalCartPrice = BigDecimal.ZERO;

        return new CartPageResponse(items, totalCartPrice, items.getTotalElements());
    }

    public void addCartItem(String email, CartRequest cartRequest) {

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Product product = productRepository.findById(cartRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cartRepository.findByUserAndProduct(userEntity, product)
                .ifPresent(item -> {
                    throw new IllegalArgumentException("해당 상품이 이미 장바구니에 있습니다!");
                });

        CartItem item = CartItem.builder()
                .user(userEntity)
                .product(product)
                .amount(1L)
                .build();

        cartRepository.save(item);
    }

    public void deleteCartItem(String email, Long itemId) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getUser().getId().equals(userEntity.getId())) {
            throw new IllegalArgumentException("본인의 장바구니 아이템만 삭제할 수 있습니다.");
        }

        cartRepository.delete(item);
    }
}
