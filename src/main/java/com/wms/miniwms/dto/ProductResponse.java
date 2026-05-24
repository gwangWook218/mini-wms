package com.wms.miniwms.dto;

import com.wms.miniwms.domain.Product;
import lombok.Getter;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private Integer price;
    private Integer quantity;

    // 엔티티 객체를 받아서 DTO로 변환해주는 생성자
    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
    }
}
