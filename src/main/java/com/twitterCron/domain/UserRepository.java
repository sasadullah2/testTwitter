package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String>, UserRepositoryCustom{
}
