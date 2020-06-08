package com.xyz.notification.mapper;

import com.google.gson.Gson;
import com.xyz.notification.entity.NotificationEntity;
import com.xyz.notification.model.NotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationRequestMapper {

    @Autowired
    Gson gson;

    public NotificationRequest mapNotificationStreamToRequest(String notificationRequestStream) {
        return gson.fromJson(notificationRequestStream, NotificationRequest.class);
    }

    public NotificationEntity getNotificationEntity(NotificationRequest notificationRequest) {
        return NotificationEntity.builder()
                .orderId(notificationRequest.getOrderId())
                .statusId(notificationRequest.getOrderStatusId())
                .createdDateTime(LocalDateTime.now())
                .userId(notificationRequest.getUserId())
                .build();
    }

}
