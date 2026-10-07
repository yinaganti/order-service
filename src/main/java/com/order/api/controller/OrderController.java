package com.order.api.controller;

import com.order.api.exception.ApplicationException;
import com.order.api.exception.ShipmentException;
import com.order.api.model.OrderRequest;
import com.order.api.model.OrderResponse;
import com.order.api.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /*
    ab -n 300 -c 100 -p create_order_request.json -T application/json http://localhost:8080/order/create
     */
    /*
       without thread, it was running from Apache thread pool
     */
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest order) throws ApplicationException, ShipmentException {
        log.info("Order created:");
        String orderId = orderService.createOrder(order);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    /*
       Platform thread
     */
    @PostMapping("/platform/create")
    public ResponseEntity<String> createOrderByPlatform(@RequestBody OrderRequest order) throws ExecutionException, InterruptedException {
        log.info("Order created by platform");
        String orderId = orderService.createOrderByPlatform(order);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    /*
        Virtual thread
     */
    @PostMapping("/virtual/create")
    public ResponseEntity<String> createOrderByVirtual(@RequestBody OrderRequest order) throws ExecutionException, InterruptedException {
        log.info("Order created by virtual");
        String orderId = orderService.createOrderByVirtual(order);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        log.info("OrderController:getOrderById");
        return new ResponseEntity<>(orderService.getOrder(orderId), HttpStatus.OK);
    }

}
