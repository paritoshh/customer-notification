package com.xyz.notification.sms;

import com.xyz.notification.model.CustomerDetails;
import com.xyz.notification.model.NotificationRequest;

public interface NotificationSmsService {

    void sendSMSNotification(NotificationRequest notificationRequest, CustomerDetails customerDetails);
}
