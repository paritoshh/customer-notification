package com.xyz.notification.util;

import com.xyz.notification.entity.NotificationEntity;
import com.xyz.notification.model.NotificationRequest;
import com.xyz.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class NotificationSpamCheck {

    @Value("${notification.spamming.minutes.duration}")
    int spammingTimeDifferenceInMinute;

    @Autowired
    NotificationRepository notificationRepository;

    public boolean isSpamNotificationRequest(NotificationRequest notificationRequest) {

        List<NotificationEntity> notificationEntityForOrderIdAndStatusId = notificationRepository.findByOrderIdAndStatusIdOrderByCreatedDateTimeDesc(notificationRequest.getOrderId(), notificationRequest.getOrderStatusId());
        if(!notificationEntityForOrderIdAndStatusId.isEmpty()){
            LocalDateTime lastNotificationTime = notificationEntityForOrderIdAndStatusId.get(0).getCreatedDateTime();
            long sameNotificationRequestDuration = lastNotificationTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
            return sameNotificationRequestDuration<spammingTimeDifferenceInMinute;
        }
        return false;
    }
}
