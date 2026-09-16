package com.thartheeb.admin.application;

import static com.thartheeb.admin.api.AdminContracts.*;

import com.thartheeb.admin.domain.AuditEvent;
import com.thartheeb.admin.domain.DocumentPolicyVersion;
import com.thartheeb.admin.infrastructure.persistence.AuditEventRepository;
import com.thartheeb.admin.infrastructure.persistence.DocumentPolicyRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminGovernanceService {
    private final DocumentPolicyRepository policies;
    private final AuditEventRepository auditEvents;

    public AdminGovernanceService(DocumentPolicyRepository policies, AuditEventRepository auditEvents) {
        this.policies = policies;
        this.auditEvents = auditEvents;
    }

    @Transactional(readOnly = true)
    public List<PolicyResponse> activePolicies() {
        return policies.findByActiveTrueOrderByDocumentTypeAsc().stream().map(this::policyResponse).toList();
    }

    @Transactional
    public PolicyResponse replacePolicy(PolicyRequest request, String actorId) {
        String type = request.documentType().trim().toUpperCase();
        var current = policies.findFirstByDocumentTypeAndActiveTrueOrderByPolicyVersionDesc(type);
        current.ifPresent(DocumentPolicyVersion::retire);
        int version = current.map(value -> value.getPolicyVersion() + 1).orElse(1);
        var policy = policies.save(new DocumentPolicyVersion(type, request.mandatory(),
            String.join(",", request.allowedMimeTypes()), request.maxFileSizeBytes(),
            request.expiryRequired(), version, actorId, Instant.now()));
        auditEvents.save(new AuditEvent("admin-service", "DOCUMENT_POLICY_CHANGED", actorId,
            "USER", null, "REPLACE_DOCUMENT_POLICY", "SUCCESS", null, null,
            "{\"documentType\":\"" + type + "\",\"version\":" + version + "}", Instant.now()));
        return policyResponse(policy);
    }

    @Transactional
    public void record(AuditRequest request) {
        if (request.correlationId() != null &&
            auditEvents.existsBySourceAndCorrelationId(request.source(), request.correlationId())) {
            return;
        }
        auditEvents.save(new AuditEvent(request.source(), request.eventType(), request.actorId(),
            request.actorType(), request.tenantId(), request.action(), request.outcome(),
            request.reason(), request.correlationId(), request.metadataJson(),
            request.occurredAt() == null ? Instant.now() : request.occurredAt()));
    }

    @Transactional(readOnly = true)
    public Page<AuditResponse> audit(Pageable pageable) {
        return auditEvents.findAll(pageable).map(this::auditResponse);
    }

    private PolicyResponse policyResponse(DocumentPolicyVersion value) {
        return new PolicyResponse(value.getId(), value.getDocumentType(), value.isMandatory(),
            Arrays.stream(value.getAllowedMimeTypes().split(",")).toList(),
            value.getMaxFileSizeBytes(), value.isExpiryRequired(), value.getPolicyVersion(),
            value.getEffectiveFrom());
    }

    private AuditResponse auditResponse(AuditEvent value) {
        return new AuditResponse(value.getId(), value.getSource(), value.getEventType(),
            value.getActorId(), value.getActorType(), value.getTenantId(), value.getAction(),
            value.getOutcome(), value.getReason(), value.getCorrelationId(),
            value.getMetadataJson(), value.getOccurredAt());
    }
}
