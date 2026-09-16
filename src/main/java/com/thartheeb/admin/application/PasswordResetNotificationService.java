package com.thartheeb.admin.application;

import static com.thartheeb.admin.api.AdminContracts.*;

import com.thartheeb.admin.domain.NotificationDelivery;
import com.thartheeb.admin.infrastructure.persistence.NotificationDeliveryRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetNotificationService {
    private final JavaMailSender mailSender;
    private final NotificationDeliveryRepository deliveries;
    private final String resetPageUrl;
    private final String from;

    public PasswordResetNotificationService(JavaMailSender mailSender,
                                            NotificationDeliveryRepository deliveries,
                                            @Value("${thartheeb.notification.reset-page-url}") String resetPageUrl,
                                            @Value("${thartheeb.notification.from}") String from) {
        this.mailSender = mailSender;
        this.deliveries = deliveries;
        this.resetPageUrl = resetPageUrl;
        this.from = from;
    }

    @Transactional(noRollbackFor = MailException.class)
    public AcceptedResponse send(PasswordResetNotification request) {
        NotificationDelivery delivery = deliveries.save(
            new NotificationDelivery("VENDOR_PASSWORD_RESET", request.identifier()));
        try {
            String link = resetPageUrl + "?token=" +
                URLEncoder.encode(request.token(), StandardCharsets.UTF_8);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(request.identifier());
            message.setSubject("Reset your Thartheeb password");
            message.setText("Use this one-time link before " + request.expiresAt() + ":\n" + link +
                "\nIf you did not request this, you can ignore this message.");
            mailSender.send(message);
            delivery.sent();
        } catch (MailException ex) {
            delivery.failed(ex.getMessage());
            throw ex;
        }
        return new AcceptedResponse(delivery.getId(), delivery.getStatus());
    }
}
