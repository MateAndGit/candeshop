package com.mateandgit.candestore.api;

import com.mateandgit.candestore.domain.order.dto.OrderRequest;
import com.mateandgit.candestore.domain.order.service.OrderService;
import com.mateandgit.candestore.domain.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<Void> createOrder(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody OrderRequest request
    ) {
        orderService.processOrder(customUserDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }
}
