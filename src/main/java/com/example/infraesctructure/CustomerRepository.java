package com.example.infraesctructure;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.example.common.repository.Repository;
import com.example.domain.Customer;

public class CustomerRepository implements Repository<Customer,UUID> {

    private static Set<Customer> data = new HashSet<>();
    @Override
    public Set<Customer> getData() {
        return data;
    }
    
}
