package com.xyz.notification.customer;

import com.xyz.notification.model.CustomerDetails;

public interface CustomerDetailsService {
    CustomerDetails getCustomerDetails(String customerId);
}
