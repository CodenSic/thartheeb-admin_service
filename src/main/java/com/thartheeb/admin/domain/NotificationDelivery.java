package com.thartheeb.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification_deliveries")
public class NotificationDelivery {
    @Id private UUID id;
    @Column(nullable = false, length = 50) private String template;
    @Column(nullable = false, length = 320) private String recipient;
    @Column(nullable = false, length = 30) private String status;
    @Column(length = 500) private String failureReason;
    @Column(nullable = false) private Instant createdAt;
    private Instant completedAt;

    protected NotificationDelivery() {}

    public NotificationDelivery(String template, String recipient) {
        this.id = UUID.randomUUID();
        this.template = template;
        this.recipient = recipient;
        this.status = "PENDING";
        this.createdAt = Instant.now();
    }

    public void sent() { status = "SENT"; completedAt = Instant.now(); }
    public void failed(String reason) {
        status = "FAILED";
        failureReason = reason == null ? "Delivery failed" : reason.substring(0, Math.min(reason.length(), 500));
        completedAt = Instant.now();
    }
    public UUID getId() { return id; }
    public String getStatus() { return status; }
}
