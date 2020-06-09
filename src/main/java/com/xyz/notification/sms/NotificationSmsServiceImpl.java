package com.xyz.notification.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.xyz.notification.mail.NotificationEmailService;
import com.xyz.notification.mapper.NotificationRequestMapper;
import com.xyz.notification.model.CustomerDetails;
import com.xyz.notification.model.NotificationRequest;
import com.xyz.notification.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.xyz.notification.util.NotificationConstants.PREFERENCE_SMS;

@Slf4j
@Service
public class NotificationSmsServiceImpl implements NotificationSmsService {

    @Autowired
    NotificationRequestMapper notificationRequestMapper;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationEmailService notificationEmailService;


    @Value("${notification.sms.sid}")
    private String sid;

    @Value("${notification.sms.auth}")
    private String auth;

    @Value("${notification.sms.sender}")
    private String senderNumber;

    @Value("${notification.sms.content}")
    private String smsContent;


    /**
     *  Sending SMS notification to the customer.
     * @param notificationRequest notification details
     * @param customerDetails customer data
     */
    @Override
    public void sendSMSNotification(NotificationRequest notificationRequest, CustomerDetails customerDetails) {
        String smsText = getSMSText(customerDetails.getCustomerName(), notificationRequest.getMessage(), notificationRequest.getOrderId());
        try {
            Twilio.init(sid, auth);
            Message.creator(
                    new PhoneNumber(customerDetails.getMobileNumber()),
                    new PhoneNumber(senderNumber),
                    smsText)
                    .create();
            notificationRepository.save(notificationRequestMapper.getNotificationEntity(notificationRequest));
        } catch (Exception exception) {
            //Fallback mechanism : sending mail in case of exception in sending SMS notification, with a deadlock check.
            if (customerDetails.getNotificationPreference().equals(PREFERENCE_SMS)) {
                log.info("Failed in sending sms notification to customer, now trying sending Email." + customerDetails.getCustomerName() + exception.getMessage());
                notificationEmailService.sendEmailWithTemplate(notificationRequest, customerDetails);
            } else {
                log.info("Failed in sending mail and sms notification to customer." + customerDetails.getCustomerName() + exception.getMessage());
            }
        }
    }

    /**
     * Formatting the sms text body.
     * @param customerName customer name from CMD service.
     * @param notificationMessage notification message from Kafka
     * @param orderId Order id from Kafka
     * @return sms text body.
     */
    private String getSMSText(String customerName, String notificationMessage, String orderId) {

        return String.format(smsContent, customerName, notificationMessage, orderId);
    }
}