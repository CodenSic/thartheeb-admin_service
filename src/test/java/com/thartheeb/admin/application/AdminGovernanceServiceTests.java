package com.thartheeb.admin.application;

import static com.thartheeb.admin.api.AdminContracts.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminGovernanceServiceTests {
    @Autowired AdminGovernanceService service;

    @Test
    void replacingAPolicyRetiresTheOldVersion() {
        String type = ("TEST_POLICY_" + UUID.randomUUID().toString().replace("-", ""))
            .toUpperCase();
        PolicyResponse first = service.replacePolicy(new PolicyRequest(type, true,
            List.of("application/pdf"), 1024, true), "admin-1");
        PolicyResponse second = service.replacePolicy(new PolicyRequest(type, false,
            List.of("image/png"), 2048, false), "admin-2");

        assertThat(first.version()).isEqualTo(1);
        assertThat(second.version()).isEqualTo(2);
        assertThat(service.activePolicies().stream()
            .filter(value -> value.documentType().equals(type)).toList())
            .singleElement()
            .satisfies(active -> {
                assertThat(active.id()).isEqualTo(second.id());
                assertThat(active.version()).isEqualTo(2);
                assertThat(active.mandatory()).isFalse();
                assertThat(active.allowedMimeTypes()).containsExactly("image/png");
                assertThat(active.maxFileSizeBytes()).isEqualTo(2048);
                assertThat(active.expiryRequired()).isFalse();
            });
    }
}
