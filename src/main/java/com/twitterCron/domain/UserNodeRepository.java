package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserNodeRepository extends MongoRepository<UserNodeEntity, String>, UserNodeRepositoryCustom{
}
