package com.example.features.customer.commands;

import java.util.UUID;

import com.example.common.repository.Update;
import com.example.domain.Customer;

public class UpdateCustomer {
    public interface Service {
        void handler(UUID id);        
    }
    public class ServiceImpl implements Service{
        private final Update<Customer,UUID> repository;
        
        public ServiceImpl(Update<Customer, UUID> repository) {
            this.repository = repository;
        }

        @Override
        public void handler(UUID id) {
           Customer customer = repository.get(id);
           repository.update(customer);
        }

    }
}
