package com.example.common.repository;

import com.example.common.EntityBase;

public interface Add<T extends EntityBase> extends Data<T> {
    default void add(T e){
        getData().add(e);
    }
}
