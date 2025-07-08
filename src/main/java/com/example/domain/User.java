package com.example.domain;

import java.util.UUID;

import com.example.common.EntityBase;

public class User extends EntityBase {
    
    protected User(UUID id) {
        super(id);       
    }
    public static  User create(UUID id){
        // event user.create
        return new User(id);
    }
    
}
