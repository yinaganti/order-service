package com.order.api.client;

import com.order.api.model.ShipmentReq;
import com.order.api.model.ShipmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.order.api.common.AppConstants.SHIPMENT_CREATE;

@FeignClient(name =  "ShipmentService", url = "${shipment.host.url}", configuration = ShipmentInterceptor.class)
public interface ShipmentFeignClient {

    @PostMapping(SHIPMENT_CREATE)
    ShipmentResponse create(@RequestBody ShipmentReq req);
}
