package com.thartheeb.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_policy_versions")
public class DocumentPolicyVersion {
    @Id private UUID id;
    @Column(nullable = false, length = 80) private String documentType;
    @Column(nullable = false) private boolean mandatory;
    @Column(nullable = false, length = 300) private String allowedMimeTypes;
    @Column(nullable = false) private long maxFileSizeBytes;
    @Column(nullable = false) private boolean expiryRequired;
    @Column(nullable = false) private boolean active;
    @Column(nullable = false) private int policyVersion;
    @Column(nullable = false) private Instant effectiveFrom;
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false, length = 120) private String createdBy;
    @Version private long rowVersion;

    protected DocumentPolicyVersion() {}

    public DocumentPolicyVersion(String documentType, boolean mandatory, String allowedMimeTypes,
                                 long maxFileSizeBytes, boolean expiryRequired, int policyVersion,
                                 String createdBy, Instant now) {
        this.id = UUID.randomUUID();
        this.documentType = documentType;
        this.mandatory = mandatory;
        this.allowedMimeTypes = allowedMimeTypes;
        this.maxFileSizeBytes = maxFileSizeBytes;
        this.expiryRequired = expiryRequired;
        this.active = true;
        this.policyVersion = policyVersion;
        this.effectiveFrom = now;
        this.createdAt = now;
        this.createdBy = createdBy;
    }

    public void retire() { this.active = false; }
    public UUID getId() { return id; }
    public String getDocumentType() { return documentType; }
    public boolean isMandatory() { return mandatory; }
    public String getAllowedMimeTypes() { return allowedMimeTypes; }
    public long getMaxFileSizeBytes() { return maxFileSizeBytes; }
    public boolean isExpiryRequired() { return expiryRequired; }
    public boolean isActive() { return active; }
    public int getPolicyVersion() { return policyVersion; }
    public Instant getEffectiveFrom() { return effectiveFrom; }
}
