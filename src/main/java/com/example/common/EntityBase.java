package com.example.common;

import java.util.Objects;
import java.util.UUID;

public abstract class EntityBase {
    private final UUID id;

    public UUID getId() {
        return id;
    }

    public EntityBase(UUID id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
       return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        //JDK 8
        /*if(obj instanceof EntityBase){
            EntityBase e = (EntityBase)obj;
            return e.id.equals(id);
        }*/
        //JDK 16
        if(obj instanceof EntityBase e){
            return e.id.equals(id);
        }
        return false;
        
    }
}
