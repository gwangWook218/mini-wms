package com.wms.miniwms.controller;

import com.wms.miniwms.dto.ProductCreateRequest;
import com.wms.miniwms.dto.ProductInboundRequest;
import com.wms.miniwms.dto.ProductOutboundRequest;
import com.wms.miniwms.dto.ProductResponse;
import com.wms.miniwms.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 신규 상품 등록 API
    @PostMapping
    public ResponseEntity<Long> registerProduct(@Valid @RequestBody ProductCreateRequest request) {
        // @Valid: DTO에 설정한 @NotBlank, @Min 등의 검증을 실제로 수행합니다.
        // @RequestBody: JSON 형식으로 들어오는 요청 데이터를 자바 객체로 변환해 줍니다.
        Long productId = productService.registerProduct(request);

        // HTTP 상태 코드 200 OK와 함께 생성된 상품의 ID를 반환합니다.
        return ResponseEntity.ok(productId);
    }

    // 상품 단건 상세 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        ProductResponse response = productService.getProduct(id);
        return ResponseEntity.ok(response);
    }

    // 전체 상품 목록 조회 API
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responses = productService.getAllProducts();
        return ResponseEntity.ok(responses);
    }

    // 기존 상품 재고 입고(추가) API
    @PatchMapping("/{id}/inbound")
    public ResponseEntity<Void> inboundProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductInboundRequest request) {
        productService.inboundProduct(id, request);
        return ResponseEntity.ok().build(); // 특별히 반환할 데이터가 없으므로 200 OK 상태코드만 깔끔하게 반환
    }

    // 기존 상품 재고 출고(차감) API
    @PatchMapping("/{id}/outbound")
    public ResponseEntity<Void> outboundProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductOutboundRequest request
            ) {
        productService.outboundProduct(id, request);
        return ResponseEntity.ok().build();
    }
}
