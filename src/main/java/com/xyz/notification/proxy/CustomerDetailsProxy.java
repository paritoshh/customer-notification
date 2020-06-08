package com.xyz.notification.proxy;

import com.xyz.notification.model.CustomerDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer", url = "localhost:8081")
public interface CustomerDetailsProxy {

    @GetMapping("/customer/customerId/{customerId}")
    ResponseEntity<CustomerDetails> retrieveCustomer(@PathVariable String customerId);
}
