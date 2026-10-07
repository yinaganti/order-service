package com.order.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "audit", schema = "practice")
public class AuditEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "audit_seq"
    )
    @SequenceGenerator(
            name = "audit_seq",
            sequenceName = "audit_id_seq",
            initialValue = 1001,
            allocationSize = 1
    )
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "shipment_id")
    private String shipmentId;

    @Column(name = "exception_reason")
    private String exceptionReason;
}
