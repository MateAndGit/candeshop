package com.mateandgit.candestore.api;

import com.mateandgit.candestore.domain.cartitem.dto.CartPageResponse;
import com.mateandgit.candestore.domain.cartitem.dto.CartRequest;
import com.mateandgit.candestore.domain.cartitem.service.CartService;
import com.mateandgit.candestore.domain.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartPageResponse> getCartList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(cartService.getCartList(customUserDetails.getUsername(), pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addCart(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody CartRequest cartRequest
    ) {
        cartService.addCartItem(customUserDetails.getUsername(), cartRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteCartItem(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long itemId
    ) {
        cartService.deleteCartItem(customUserDetails.getUsername(), itemId);
        return ResponseEntity.ok().build();
    }
}
