package com.wms.miniwms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductInboundRequest {

    @NotNull(message = "입고 수량은 필수 항목입니다.")
    @Min(value = 1, message = "입고 수량은 최소 1개 이상이어야 합니다.")
    private Integer amount; // 입고할 수량
}
