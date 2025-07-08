package com.example.features.user.queries;

import java.util.UUID;

import com.example.common.repository.Get;
import com.example.domain.User;

public class GetUser {
    public interface Service{
        void handler(UUID id);
    }
    public class ServiceImpl implements Service{

        private final Get<User,UUID> repository;
        public ServiceImpl(Get<User, UUID> repository) {
            this.repository = repository;
        }
        @Override
        public void handler(UUID id) {
           repository.get(id);
        }
    }
}
