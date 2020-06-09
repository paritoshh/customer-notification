package com.xyz.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CustomerDetails {

    private String customerId;
    private String customerName;
    private String customerEmail;
    private String mobileNumber;
    private String notificationPreference;
}
