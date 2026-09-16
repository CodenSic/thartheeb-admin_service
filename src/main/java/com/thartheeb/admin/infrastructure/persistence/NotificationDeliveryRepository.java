package com.thartheeb.admin.infrastructure.persistence;

import com.thartheeb.admin.domain.NotificationDelivery;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, UUID> {
}
