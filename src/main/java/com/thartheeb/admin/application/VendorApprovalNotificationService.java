package com.thartheeb.admin.application;

import static com.thartheeb.admin.api.AdminContracts.*;

import com.thartheeb.admin.domain.NotificationDelivery;
import com.thartheeb.admin.infrastructure.persistence.NotificationDeliveryRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendorApprovalNotificationService {
    private final JavaMailSender mailSender;
    private final NotificationDeliveryRepository deliveries;
    private final String activationPageUrl;
    private final String from;

    public VendorApprovalNotificationService(
        JavaMailSender mailSender,
        NotificationDeliveryRepository deliveries,
        @Value("${thartheeb.notification.vendor-activation-page-url}") String activationPageUrl,
        @Value("${thartheeb.notification.from}") String from
    ) {
        this.mailSender = mailSender;
        this.deliveries = deliveries;
        this.activationPageUrl = activationPageUrl;
        this.from = from;
    }

    @Transactional(noRollbackFor = MailException.class)
    public AcceptedResponse send(VendorApprovalNotification request) {
        NotificationDelivery delivery = deliveries.save(
            new NotificationDelivery("VENDOR_APPROVED", request.identifier()));
        try {
            String link = activationPageUrl + "?token="
                + URLEncoder.encode(request.token(), StandardCharsets.UTF_8);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(request.identifier());
            message.setSubject("Your Thartheeb Vendor account is approved");
            message.setText("Hello " + request.companyName() + ",\n\n"
                + "Your Vendor account has been approved by Thartheeb Admin.\n"
                + "Set your password using this one-time link before " + request.expiresAt()
                + ":\n" + link + "\n\n"
                + "This link expires in 30 minutes. If you were not expecting this email, "
                + "contact Thartheeb Support.");
            mailSender.send(message);
            delivery.sent();
        } catch (MailException ex) {
            delivery.failed(ex.getMessage());
            throw ex;
        }
        return new AcceptedResponse(delivery.getId(), delivery.getStatus());
    }
}
