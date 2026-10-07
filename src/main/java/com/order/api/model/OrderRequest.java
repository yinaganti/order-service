package com.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderRequest {

    private String orderName;
    private String orderStatus;
}
