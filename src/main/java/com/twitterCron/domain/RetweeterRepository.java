package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RetweeterRepository extends MongoRepository<RetweeterEntity, String>,
        RetweeterRepositoryCustom {
}
