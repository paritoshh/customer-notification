package com.xyz.notification.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class NotificationRequest {

    private String orderId;
    private String message;
    private String userId;
    private String orderStatusId;
}
