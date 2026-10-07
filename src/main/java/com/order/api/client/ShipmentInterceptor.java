package com.order.api.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ShipmentInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("Accept", "application/json");
        if(!template.method().equalsIgnoreCase("GET"))
            template.header("Content-Type", "application/json");
    }
}
