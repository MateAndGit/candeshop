package com.mateandgit.candestore.api;

import com.mateandgit.candestore.domain.product.dto.ProductRequest;
import com.mateandgit.candestore.domain.product.dto.ProductResponse;
import com.mateandgit.candestore.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProductList(
            @PageableDefault(size = 6, sort = "createdDate", direction = Sort.Direction.DESC)Pageable pageable) {
        Page<ProductResponse> productList = productService.getProductList(pageable);
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping
    public ResponseEntity<String> postProduct(@Valid @RequestBody ProductRequest request ) {
        productService.postProduct(request);
        return ResponseEntity.ok("post success");
    }

    @PutMapping("edit/{id}")
    public ResponseEntity<String> editProduct(@Valid @RequestBody ProductRequest request, @PathVariable Long id) {
        productService.editProduct(request,id);
        return ResponseEntity.ok("edit success");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delectProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("delete success");
    }
}
