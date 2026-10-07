package com.order.api.service;

import com.order.api.client.KafkaProducer;
import com.order.api.client.ShipmentFeignClient;
import com.order.api.entity.AuditEntity;
import com.order.api.entity.OrderEntity;
import com.order.api.exception.ApplicationException;
import com.order.api.exception.ShipmentException;
import com.order.api.model.OrderRequest;
import com.order.api.model.OrderResponse;
import com.order.api.model.ShipmentReq;
import com.order.api.model.ShipmentResponse;
import com.order.api.repository.OrderRepository;
import com.order.api.util.AppUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.order.api.common.AppConstants.SHIPMENT_CREATE;

@Slf4j
@Service
public class OrderService {

   final ModelMapper modelMapper;
   final OrderRepository orderRepository;
   final AppUtil appUtil;
   final ShipmentFeignClient shipmentFeignClient;
   final AuditService auditService;
   final KafkaProducer kafkaProducer;
   final ObjectMapper objectMapper;

   @Value("${shipment.host.url}")
   String url;

   @Value("${spring.kafka.order.topic}")
   String orderTopic;

   ExecutorService platformThreadExecutor = Executors.newFixedThreadPool(100);
   ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

   OrderService(ModelMapper modelMapper, OrderRepository orderRepository, AppUtil appUtil, ShipmentFeignClient shipmentFeignClient, AuditService auditService, KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
       this.modelMapper = modelMapper;
       this.orderRepository = orderRepository;
       this.appUtil = appUtil;
       this.shipmentFeignClient = shipmentFeignClient;
       this.auditService = auditService;
       this.kafkaProducer = kafkaProducer;
       this.objectMapper = objectMapper;
   }

   @Transactional(rollbackFor = ShipmentException.class)
    public String createOrder(OrderRequest order) throws ApplicationException, ShipmentException {
        log.info("Order created, thread = {}", Thread.currentThread());
       String orderId = UUID.randomUUID().toString();
        OrderEntity orderEntity = modelMapper.map(order, OrderEntity.class);
        orderEntity.setOrderId(orderId);
        orderEntity.setOrderCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        orderRepository.save(orderEntity);
       AuditEntity auditEntity = AuditEntity.builder().orderId(orderId).build();
       ShipmentReq req = ShipmentReq.builder().orderId(orderId).build();
       // ShipmentResponse response = appUtil.postForObject(url + SHIPMENT_CREATE, req, ShipmentResponse.class);
       ShipmentResponse response = null;
       try {

           response = shipmentFeignClient.create(req);
           auditEntity.setShipmentId(response.getShipmentId());
          // kafkaProducer.send(orderTopic, orderId, objectMapper.writeValueAsString(req));
        } catch (Exception e) {
           auditEntity.setExceptionReason(e.getMessage());
           /*
           if(e instanceof RetryableException) {
               throw new ApplicationException(e.getMessage());
           } */
           auditEntity.setOrderId(null);
           throw new ShipmentException(e.getMessage());
        } finally {
           auditService.save(auditEntity);
       }

       // log.info("Shipment response: {}", response);
        return orderId;
    }

    public String createOrderByPlatform(OrderRequest order) throws ExecutionException, InterruptedException {
       Future<String> future = platformThreadExecutor.submit(() -> {
           log.info("Order created: platform thread = {}", Thread.currentThread());
           String orderId = UUID.randomUUID().toString();
           OrderEntity orderEntity = modelMapper.map(order, OrderEntity.class);
           orderEntity.setOrderId(orderId);
           orderEntity.setOrderCreateTime(Timestamp.valueOf(LocalDateTime.now()));
           orderRepository.save(orderEntity);
           return orderId;
       });
       return future.get();
    }

    public String createOrderByVirtual(OrderRequest order) throws ExecutionException, InterruptedException {
        Future<String> future = virtualThreadExecutor.submit(() -> {
           log.info("Order created: virtual thread = {}", Thread.currentThread());
           String orderId = UUID.randomUUID().toString();
           OrderEntity orderEntity = modelMapper.map(order, OrderEntity.class);
           orderEntity.setOrderId(orderId);
           orderEntity.setOrderCreateTime(Timestamp.valueOf(LocalDateTime.now()));
           orderRepository.save(orderEntity);
           return orderId;
       });
        return future.get();
    }

    public OrderResponse getOrder(String orderId) {
        log.info("OrderController:getOrder");
        Optional<OrderEntity> entityOptional = orderRepository.findByOrderId(orderId);
        if(entityOptional.isPresent()) {
            return entityOptional.map(entity -> OrderResponse.builder()
                    .orderId(entity.getOrderId())
                    .orderName(entity.getOrderName())
                    .orderStatus(entity.getOrderStatus())
                    .orderCreateTime(entity.getOrderCreateTime())
                    .build()).orElse(null);
        } else {
            return null;
        }
    }
}
