package com.thartheeb.admin.application;

import static com.thartheeb.admin.api.AdminContracts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thartheeb.admin.infrastructure.persistence.NotificationDeliveryRepository;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class VendorApprovalNotificationServiceTests {
    @Test
    void sendsApprovalMessageWithFrontendPasswordSetupLink() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        NotificationDeliveryRepository deliveries = mock(NotificationDeliveryRepository.class);
        when(deliveries.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        VendorApprovalNotificationService service = new VendorApprovalNotificationService(
            mailSender, deliveries, "https://vendors.thartheeb.qa/set-password",
            "no-reply@thartheeb.qa");
        Instant expiresAt = Instant.now().plusSeconds(1800);

        AcceptedResponse response = service.send(new VendorApprovalNotification(
            "vendor@example.qa", "Desert Works W.L.L.", "header.payload.signature", expiresAt));

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(message.capture());
        assertThat(message.getValue().getTo()).containsExactly("vendor@example.qa");
        assertThat(message.getValue().getText())
            .contains("approved by Thartheeb Admin")
            .contains("https://vendors.thartheeb.qa/set-password?token=header.payload.signature")
            .contains("expires in 30 minutes");
        assertThat(response.status()).isEqualTo("SENT");
    }
}
