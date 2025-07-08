package com.example.domain;

import java.util.UUID;

import com.example.common.EntityBase;

public class Customer extends EntityBase {

    protected Customer(UUID id) {
        super(id);        
    }
    public static Customer create(UUID id){
        //customer.create
        return new Customer(id);
    }
    
}
