package com.thartheeb.admin.api;

import static com.thartheeb.admin.api.AdminContracts.*;

import com.thartheeb.admin.application.AdminGovernanceService;
import com.thartheeb.admin.common.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@Tag(name = "Admin governance", description = "Vendor document policy and central audit query APIs")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
    @ApiResponse(responseCode = "400", description = "Request validation failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "401", description = "Bearer token missing or invalid", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
    @ApiResponse(responseCode = "403", description = "Admin or compliance reviewer role required", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
})
public class AdminController {
    private final AdminGovernanceService service;

    public AdminController(AdminGovernanceService service) { this.service = service; }

    @GetMapping("/vendor-document-policies")
    @Operation(summary = "List active Vendor document policies",
        description = "Returns active mandatory/optional rules, allowed MIME types, size, expiry requirement and version.")
    public List<PolicyResponse> policies() { return service.activePolicies(); }

    @PostMapping("/vendor-document-policies")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Replace a Vendor document policy",
        description = "Retires the current active policy for the type and creates the next auditable version.")
    public PolicyResponse replace(@Valid @RequestBody PolicyRequest request, Authentication auth) {
        return service.replacePolicy(request, auth.getName());
    }

    @GetMapping("/audit-events")
    @Operation(summary = "List central audit events",
        description = "Returns pageable approval, authentication and authorisation audit evidence.")
    public Page<AuditResponse> audit(Pageable pageable) { return service.audit(pageable); }
}
