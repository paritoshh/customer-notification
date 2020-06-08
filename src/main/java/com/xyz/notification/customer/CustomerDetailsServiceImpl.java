package com.xyz.notification.customer;

import com.xyz.notification.model.CustomerDetails;
import com.xyz.notification.proxy.CustomerDetailsProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CustomerDetailsServiceImpl implements CustomerDetailsService {

    @Autowired
    CustomerDetailsProxy customerDetailsProxy;

    @Override
    public CustomerDetails getCustomerDetails(String customerId) {

        return customerDetailsProxy.retrieveCustomer(customerId).getBody();
    }
}
