package com.wms.miniwms.dto;

import com.wms.miniwms.domain.InboundOutboundType;
import com.wms.miniwms.domain.ProductHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductHistoryResponse {

    private Long id;
    private Long productId;
    private String productName;
    private InboundOutboundType type;
    private Integer amount;
    private LocalDateTime createdAt;

    public ProductHistoryResponse(ProductHistory history) {
        this.id = history.getId();
        this.productId = history.getProduct().getId();
        this.productName = history.getProduct().getName();
        this.type = history.getType();
        this.amount = history.getAmount();
        this.createdAt = history.getCreatedAt();
    }
}
