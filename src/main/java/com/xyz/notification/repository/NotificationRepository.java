package com.xyz.notification.repository;

import com.xyz.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByOrderIdAndStatusIdOrderByCreatedDateTimeDesc(String orderId, String orderStatusId);
}
