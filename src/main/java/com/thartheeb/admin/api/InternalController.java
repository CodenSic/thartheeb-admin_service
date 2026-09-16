package com.thartheeb.admin.api;

import static com.thartheeb.admin.api.AdminContracts.*;

import com.thartheeb.admin.application.AdminGovernanceService;
import com.thartheeb.admin.application.PasswordResetNotificationService;
import com.thartheeb.admin.common.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1")
@Tag(name = "Internal Admin APIs", description = "Private audit ingestion, policy distribution and password-reset delivery")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
    @ApiResponse(responseCode = "400", description = "Request validation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "401", description = "Service token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "403", description = "Internal scope required", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "500", description = "SMTP delivery failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
})
public class InternalController {
    private final AdminGovernanceService governance;
    private final PasswordResetNotificationService notifications;

    public InternalController(AdminGovernanceService governance,
                              PasswordResetNotificationService notifications) {
        this.governance = governance;
        this.notifications = notifications;
    }

    @PostMapping("/audit-events")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Ingest an audit event",
        description = "Stores the event once; source plus correlationId makes outbox retries idempotent.")
    public void audit(@Valid @RequestBody AuditRequest request) { governance.record(request); }

    @GetMapping("/vendor-document-policies")
    @Operation(summary = "Get active Vendor document policies",
        description = "Private policy feed consumed by Vendor service for upload and approval validation.")
    public List<PolicyResponse> documentPolicies() { return governance.activePolicies(); }

    @PostMapping("/notifications/password-reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Deliver a Vendor password-reset email",
        description = "Builds and sends a one-time reset link without storing the raw token in the delivery record.")
    public AcceptedResponse passwordReset(@Valid @RequestBody PasswordResetNotification request) {
        return notifications.send(request);
    }
}
