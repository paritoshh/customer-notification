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

import static com.xyz.notification.util.NotificationConstants.PREFERENCE_MAIL;
import static com.xyz.notification.util.NotificationConstants.PREFERENCE_SMS;

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


    /**
     * Notification service is playing a role of Consumer for Kafka.
     * It will keep listening the kakfa port and send the notifications.
     *
     * @param message : kafka message it's a JSON.
     *                eg. {  "orderId": "171-8762533-3016356",  "message": "We thought you'd like to know that we've dispatched your item(s).",  "userId": "003", "orderStatusId":"ORDERED"}
     */
    @KafkaListener(topics = "notifications_topic", groupId = "kafka-sandbox")
    public void listen(String message) {
        log.info("Received Message in group kafka-sandbox: " + message);
        //Mapping the kafka message into model class.
        NotificationRequest notificationRequest = notificationRequestMapper.mapNotificationStreamToRequest(message);
        CustomerDetails customerDetails = customerDetailsService.getCustomerDetails(notificationRequest.getUserId());
        if (notificationSpamCheck.isSpamNotificationRequest(notificationRequest)) {
            log.info("Notification spamming request found.: " + notificationRequest);
            return;
        }
        switch (customerDetails.getNotificationPreference()) {
            case PREFERENCE_SMS:
                log.info("Request for sending SMS notification.");
                notificationSmsService.sendSMSNotification(notificationRequest, customerDetails);
                break;
            case PREFERENCE_MAIL:
                log.info("Request for sending Email notification.");
                notificationEmailService.sendEmailWithTemplate(notificationRequest, customerDetails);
        }
    }
}