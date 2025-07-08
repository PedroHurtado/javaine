package com.example.common.repository;

import java.util.Set;

import com.example.common.EntityBase;

public interface Data<T extends EntityBase> {
    Set<T> getData(); //metodo abstracto
}
