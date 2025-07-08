package com.example.common.repository;

import com.example.common.EntityBase;

public interface Repository<T extends EntityBase, ID>
    extends Update<T,ID>,Remove<T,ID>,Add<T>
{
    
}
