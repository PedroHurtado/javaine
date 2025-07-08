package com.example.infraesctructure;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.example.common.repository.Get;
import com.example.domain.User;

public class UserRepository implements Get<User,UUID> {
    private static Set<User> data = new HashSet<>();
    @Override
    public Set<User> getData() {
        return data;    
    }
    
}
