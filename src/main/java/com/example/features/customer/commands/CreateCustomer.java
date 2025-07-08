package com.example.features.customer.commands;

import java.util.UUID;

import com.example.common.repository.Add;
import com.example.domain.Customer;

//@Configuration
public class CreateCustomer {
    public interface Service {    
        void handler();
    }
    //@Service
    public class ServiceImpl implements Service {
        private final Add<Customer> repository;
        public ServiceImpl(Add<Customer> repository) {
            this.repository = repository;
        }
        @Override
        public void handler() {
            Customer customer = Customer.create(UUID.randomUUID());
            repository.add(customer);  
        }    
    }
}
