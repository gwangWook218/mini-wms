package com.wms.miniwms.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 상품명

    @Column(nullable = false)
    private Integer price; // 상품 가격

    @Column(nullable = false)
    private Integer quantity; // 현재 재고 수량

    // 상품을 등록할 때 사용할 생성자
    @Builder
    public Product(String name, Integer price, Integer quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // 비즈니스 메서드: 재고 수량 증가(입고)
    public void addQuantity(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("입고 수량은 0보다 커야 합니다.");
        }
        this.quantity += amount;
    }
}
