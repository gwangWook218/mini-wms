package com.wms.miniwms.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // 등록 시간을 자동으로 기록
@Table(name = "product_histories")
public class ProductHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 성능 최적화를 위해 실무 필수인 지연 로딩(LAZY) 적용
    @JoinColumn(name = "product_id" ,nullable = false)
    private Product product; // 연관된 상품

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InboundOutboundType type; // INBOUND 또는 OUTBOUND

    @Column(nullable = false)
    private Integer amount; // 입출고 수량

    @CreatedDate // 데이터가 축적되는 시점의 날짜/시간이 자동으로 저장됩니다.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer remainedQuantity; // 해당 입고 건의 '현재 남은 재고 수량' (출고 건일 경우 0으로 세팅)

    @Builder
    public ProductHistory(Product product, InboundOutboundType type, Integer amount, Integer remainedQuantity) {
        this.product = product;
        this.type = type;
        this.amount = amount;
        this.remainedQuantity = remainedQuantity;
    }

    // 비즈니스 메서드: 선입선출 차감 시 해당 입고 건의 남은 수량 감소시킴
    public void consumeQuantity(Integer count) {
        if (this.remainedQuantity < count) {
            throw new IllegalArgumentException("해당 입고 건의 잔여 수량이 부족합니다.");
        }
        this.remainedQuantity -= count;
    }
}
