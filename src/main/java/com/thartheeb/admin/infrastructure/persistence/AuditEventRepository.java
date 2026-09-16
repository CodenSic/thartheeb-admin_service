package com.thartheeb.admin.infrastructure.persistence;

import com.thartheeb.admin.domain.AuditEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    boolean existsBySourceAndCorrelationId(String source, String correlationId);
}
