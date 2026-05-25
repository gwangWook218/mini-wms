package com.wms.miniwms.service;

import com.wms.miniwms.domain.InboundOutboundType;
import com.wms.miniwms.domain.Product;
import com.wms.miniwms.domain.ProductHistory;
import com.wms.miniwms.dto.*;
import com.wms.miniwms.repository.ProductHistoryRepository;
import com.wms.miniwms.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductHistoryRepository productHistoryRepository;

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

        // 2-[추가]. 신규 등록 시 입력한 수량도 '최초 입고 박스'로 이력에 등록
        if (request.getQuantity() > 0) {
            ProductHistory history = ProductHistory.builder()
                    .product(saveProduct)
                    .type(InboundOutboundType.INBOUND)
                    .amount(request.getQuantity())
                    .remainedQuantity(request.getQuantity()) // 최초 등록 수량이 곧 남은 수량
                    .build();
            productHistoryRepository.save(history);
        }

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

    // 상품 입고 (재고 추가 + 이력 기록)
    @Transactional // 데이터가 변경되므로 readOnly 없이 일반 @Transactional 필수!
    public void inboundProduct(Long id, ProductInboundRequest request) {
        // 1. 창고에서 해당 상품이 존재하는지 먼저 확인
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + id));

        // 2. 찾아온 상품 객체에 재고 증가 위임
        product.addQuantity(request.getAmount());

        // 3. 입고 이력 빌드 및 저장
        ProductHistory history = ProductHistory.builder()
                .product(product)
                .type(InboundOutboundType.INBOUND)
                .amount(request.getAmount())
                .remainedQuantity(request.getAmount())
                .build();
        productHistoryRepository.save(history);
    }

    // 상품 출고 (재고 차감 + 이력 기록)
    @Transactional
    public void outboundProduct(Long id, ProductOutboundRequest request) {
        // 1. 창고에서 해당 상품이 존재하는지 먼저 확인
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + id));

        // 2. 찾아온 상품 객체에 재고 차감 위임 (재고 부족 시 엔티티 내부에서 예외 처리)
        product.removeQuantity(request.getAmount());

        // 3. [FIFO 알고리즘 시작] 오래된 순서대로 잔여 재고가 남아있는 입고 박스들 소환
        List<ProductHistory> inboundReceipts = productHistoryRepository
                .findByProductAndTypeAndRemainedQuantityGreaterThanOrderByCreatedAtAsc(product, InboundOutboundType.INBOUND, 0);

        int totalToAmount = request.getAmount(); // 손님이 요청한 출고 총 수량

        for (ProductHistory receipt :inboundReceipts) {
            if (totalToAmount <= 0) {
                break; // 손님이 달라는 수량을 다 채웠으면 탈출!
            }

            int remained = receipt.getRemainedQuantity(); // 현재 박스에 남아있는 상품 수량

            if (remained <= totalToAmount) {
                // 케이스 A: 현재 박스의 상품이 필요한 양보다 적거나 딱 맞음 -> 모든 잔여량 차감
                totalToAmount -= remained; // 박스에 있던 수량만큼 요구량 차감
                receipt.consumeQuantity(remained); // 이 박스의 남은 수량은 0이 됨
            } else {
                // 케이스 B: 현재 박스의 상품이 넉넉함 -> 필요한 만큼 차감
                receipt.consumeQuantity(totalToAmount); // 필요한 만큼만 차감
                totalToAmount = 0; // 요구량 충족
            }
        }

        // 4. 출고 영수증 최종 발행
        ProductHistory outboundHistory = ProductHistory.builder()
                .product(product)
                .type(InboundOutboundType.OUTBOUND)
                .amount(request.getAmount())
                .remainedQuantity(0)
                .build();
        productHistoryRepository.save(outboundHistory);
    }

    // 전체 입출고 이력 조회
    @Transactional(readOnly = true) // 찾아온 엔티티들을 DTO로 변환
    public List<ProductHistoryResponse> getAllHistories() {
        return productHistoryRepository.findAll().stream()
                .map(ProductHistoryResponse::new) // 찾아온 엔티티들을 DTO로 변환
                .toList();
    }
}
