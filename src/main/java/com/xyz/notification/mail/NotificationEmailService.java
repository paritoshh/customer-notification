package com.xyz.notification.mail;

import com.xyz.notification.model.CustomerDetails;
import com.xyz.notification.model.NotificationRequest;

public interface NotificationEmailService {

    void sendEmailWithTemplate(NotificationRequest notificationRequest, CustomerDetails customerDetails);
}
