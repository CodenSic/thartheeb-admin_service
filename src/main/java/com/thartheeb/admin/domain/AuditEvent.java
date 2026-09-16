package com.thartheeb.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id private UUID id;
    @Column(nullable = false, length = 80) private String source;
    @Column(length = 80) private String eventType;
    @Column(length = 120) private String actorId;
    @Column(length = 80) private String actorType;
    @Column(length = 120) private String tenantId;
    @Column(nullable = false, length = 120) private String action;
    @Column(nullable = false, length = 30) private String outcome;
    @Column(length = 1000) private String reason;
    @Column(length = 120) private String correlationId;
    @Column(columnDefinition = "text") private String metadataJson;
    @Column(nullable = false) private Instant occurredAt;
    @Column(nullable = false) private Instant receivedAt;

    protected AuditEvent() {}

    public AuditEvent(String source, String eventType, String actorId, String actorType,
                      String tenantId, String action, String outcome, String reason,
                      String correlationId, String metadataJson, Instant occurredAt) {
        this.id = UUID.randomUUID();
        this.source = source;
        this.eventType = eventType;
        this.actorId = actorId;
        this.actorType = actorType;
        this.tenantId = tenantId;
        this.action = action;
        this.outcome = outcome;
        this.reason = reason;
        this.correlationId = correlationId;
        this.metadataJson = metadataJson;
        this.occurredAt = occurredAt;
        this.receivedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getSource() { return source; }
    public String getEventType() { return eventType; }
    public String getActorId() { return actorId; }
    public String getActorType() { return actorType; }
    public String getTenantId() { return tenantId; }
    public String getAction() { return action; }
    public String getOutcome() { return outcome; }
    public String getReason() { return reason; }
    public String getCorrelationId() { return correlationId; }
    public String getMetadataJson() { return metadataJson; }
    public Instant getOccurredAt() { return occurredAt; }
}
