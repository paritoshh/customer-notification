package com.xyz.notification.consumer;

import com.xyz.notification.customer.CustomerDetailsService;
import com.xyz.notification.mail.NotificationEmailService;
import com.xyz.notification.mapper.NotificationRequestMapper;
import com.xyz.notification.model.CustomerDetails;
import com.xyz.notification.model.NotificationRequest;
import com.xyz.notification.sms.NotificationSmsService;
import com.xyz.notification.util.NotificationSpamCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationConsumer {

    @Autowired
    NotificationEmailService notificationEmailService;

    @Autowired
    NotificationSmsService notificationSmsService;

    @Autowired
    NotificationRequestMapper notificationRequestMapper;

    @Autowired
    NotificationSpamCheck notificationSpamCheck;

    @Autowired
    CustomerDetailsService customerDetailsService;


    @KafkaListener(topics = "notifications_topic", groupId = "kafka-sandbox")
    public void listen(String message) {
        log.info("Received Message in group kafka-sandbox: " + message);
        NotificationRequest notificationRequest = notificationRequestMapper.mapNotificationStreamToRequest(message);
        CustomerDetails customerDetails = customerDetailsService.getCustomerDetails(notificationRequest.getUserId());
        if (notificationSpamCheck.isSpamNotificationRequest(notificationRequest)) {
            log.info("Notification spamming request found.: " + notificationRequest);
            return;
        }
        switch (customerDetails.getNotificationPreference()) {
            case "SMS":
                log.info("Request for sending SMS notification.");
                notificationSmsService.sendSMSNotification(notificationRequest, customerDetails);
                break;
            case "MAIL":
                log.info("Request for sending Email notification.");
                notificationEmailService.sendEmailWithTemplate(notificationRequest, customerDetails);
        }

    }
}