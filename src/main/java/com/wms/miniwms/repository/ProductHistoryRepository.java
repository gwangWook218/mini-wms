package com.wms.miniwms.repository;

import com.wms.miniwms.domain.InboundOutboundType;
import com.wms.miniwms.domain.Product;
import com.wms.miniwms.domain.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {

//    선입선출 핵심 쿼리:
//    특정 상품의 이력 중 [입고(INBOUND)] 타입이면서 [잔여 재고가 0보다 큰] 건들을 [날짜 오름차순(오래된 순)]으로 조회
    List<ProductHistory> findByProductAndTypeAndRemainedQuantityGreaterThanOrderByCreatedAtAsc(
            Product product,
            InboundOutboundType type,
            Integer remainedQuantity
    );
}
