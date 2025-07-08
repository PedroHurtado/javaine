package com.example.common.repository;

import com.example.common.EntityBase;

public interface Update<T extends EntityBase,ID> extends Get<T,ID> {
    default void update(T e){
        getData().remove(e);
        getData().add(e);
    }
} 
