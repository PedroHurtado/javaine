package com.example.common.repository;

import com.example.common.EntityBase;

public interface Remove<T extends EntityBase, ID> extends Get<T,ID> {
    default void remove(T e){
        getData().remove(e);
    }
}
