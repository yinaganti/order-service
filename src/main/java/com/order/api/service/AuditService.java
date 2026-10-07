package com.order.api.service;

import com.order.api.entity.AuditEntity;
import com.order.api.repository.AuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AuditEntity auditEntity) {
        auditRepository.save(auditEntity);
    }
}
