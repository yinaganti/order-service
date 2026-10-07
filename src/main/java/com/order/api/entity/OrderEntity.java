package com.order.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "order", schema = "practice")
public class OrderEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_seq"
    )
    @SequenceGenerator(
            name = "order_seq",
            sequenceName = "orders_id_seq",
            initialValue = 1001,
            allocationSize = 1
    )
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_name")
    private String orderName;

    @Column(name = "order_create_time")
    private Timestamp orderCreateTime;
}
