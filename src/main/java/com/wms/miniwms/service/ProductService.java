package com.wms.miniwms.service;

import com.wms.miniwms.domain.Product;
import com.wms.miniwms.dto.ProductCreateRequest;
import com.wms.miniwms.dto.ProductResponse;
import com.wms.miniwms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 신규 상품 등록(입고)
    @Transactional // 데이터 변경이 발생하는 Method에서는 필수
    public Long registerProduct(ProductCreateRequest request) {
        // 1. DTO 데이터를 바탕으로 Builder 패턴을 써서 엔티티 객체 생성
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        // 2. DB에 저장
        Product saveProduct = productRepository.save(product);

        // 3. 저장된 상품의 고유 Id 반환
        return saveProduct.getId();
    }

    // 상품 단건 상세 조회
    @Transactional(readOnly = true) // 조회만 하는 메서드에는 성능 최적화를 위해 readOnly 추가
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + id));
        return new ProductResponse(product);
    }

    // 전체 상품 목록 조회
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new) // 찾아온 엔티티들을 DTO로 변환
                .toList();
    }
}
