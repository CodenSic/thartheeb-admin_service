package com.thartheeb.admin.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class AdminContracts {
    private AdminContracts() {}

    public record PolicyRequest(
        @NotBlank String documentType,
        boolean mandatory,
        @NotEmpty List<@NotBlank String> allowedMimeTypes,
        @Positive long maxFileSizeBytes,
        boolean expiryRequired) {}

    public record PolicyResponse(UUID id, String documentType, boolean mandatory,
                                 List<String> allowedMimeTypes, long maxFileSizeBytes,
                                 boolean expiryRequired, int version, Instant effectiveFrom) {}

    public record AuditRequest(@NotBlank String source, String eventType, String actorId,
                               String actorType, String tenantId, @NotBlank String action,
                               @NotBlank String outcome, String reason, String correlationId,
                               String metadataJson, Instant occurredAt) {}

    public record AuditResponse(UUID id, String source, String eventType, String actorId,
                                String actorType, String tenantId, String action, String outcome,
                                String reason, String correlationId, String metadataJson,
                                Instant occurredAt) {}

    public record PasswordResetNotification(@Email @NotBlank String identifier,
                                            @NotBlank String token,
                                            @Future Instant expiresAt) {}

    public record VendorApprovalNotification(
        @Email @NotBlank String identifier,
        @NotBlank String companyName,
        @NotBlank String token,
        @Future Instant expiresAt) {}

    public record AcceptedResponse(UUID deliveryId, String status) {}
}
