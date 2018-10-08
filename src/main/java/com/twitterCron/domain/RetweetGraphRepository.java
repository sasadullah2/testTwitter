package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RetweetGraphRepository extends MongoRepository<RetweetGraphEntity, String>, RetweetGraphRepositoryCustom {
}
