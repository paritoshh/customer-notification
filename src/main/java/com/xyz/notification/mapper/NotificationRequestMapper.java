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

    /**
     * Mapping kafka notification request into model class.
     * @param notificationRequestStream kafka notification stream json.
     * @return mapped NotificationRequest
     */
    public NotificationRequest mapNotificationStreamToRequest(String notificationRequestStream) {
        return gson.fromJson(notificationRequestStream, NotificationRequest.class);
    }

    /**
     * Map notification model into database entity
     * @param notificationRequest model
     * @return NotificationEntity to be saved in DB
     */
    public NotificationEntity getNotificationEntity(NotificationRequest notificationRequest) {
        return NotificationEntity.builder()
                .orderId(notificationRequest.getOrderId())
                .statusId(notificationRequest.getOrderStatusId())
                .createdDateTime(LocalDateTime.now())
                .userId(notificationRequest.getUserId())
                .build();
    }

}
