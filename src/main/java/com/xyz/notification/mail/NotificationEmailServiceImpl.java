package com.xyz.notification.mail;

import com.xyz.notification.mapper.NotificationRequestMapper;
import com.xyz.notification.model.CustomerDetails;
import com.xyz.notification.model.NotificationRequest;
import com.xyz.notification.repository.NotificationRepository;
import com.xyz.notification.sms.NotificationSmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.xyz.notification.util.NotificationConstants.PREFERENCE_MAIL;

@Slf4j
@Service("EmailService")
public class NotificationEmailServiceImpl implements NotificationEmailService {

    @Value("${notification.email.subject}")
    private String emailSubject;

    @Value("${notification.email.content}")
    private String emailContent;

    @Qualifier("getJavaMailSender")
    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    NotificationRequestMapper notificationRequestMapper;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    NotificationSmsService notificationSmsService;

    /**
     * Sending Email notification to the customer.
     * @param notificationRequest notification details.
     * @param customerDetails customer details.
     */
    @Override
    public void sendEmailWithTemplate(NotificationRequest notificationRequest, CustomerDetails customerDetails) {

        String emailBodyText = getMailText(customerDetails.getCustomerName(), notificationRequest.getMessage(), notificationRequest.getOrderId());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(customerDetails.getCustomerEmail());
        message.setSubject(emailSubject + notificationRequest.getOrderId());
        message.setText(emailBodyText);
        try {
            emailSender.send(message);
            notificationRepository.save(notificationRequestMapper.getNotificationEntity(notificationRequest));
        } catch (MailException exception) {
            //Fallback mechanism : sending sms in case of exception in sending email notification, with a deadlock check.
            if (customerDetails.getNotificationPreference().equals(PREFERENCE_MAIL)) {
                log.info("Failed in sending mail notification to customer, now trying sending SMS." + customerDetails.getCustomerName() + exception.getMessage());
                notificationSmsService.sendSMSNotification(notificationRequest, customerDetails);
            } else {
                log.info("Failed in sending mail and sms notification to customer." + customerDetails.getCustomerName() + exception.getMessage());
            }
        }
    }

    /**
     * Formatting the mail text body.
     * @param customerName customer name from CMD service.
     * @param notificationMessage notification message from Kafka
     * @param orderId Order id from Kafka
     * @return mail text body.
     */
    private String getMailText(String customerName, String notificationMessage, String orderId) {
        return String.format(emailContent, customerName, notificationMessage, orderId);
    }
}