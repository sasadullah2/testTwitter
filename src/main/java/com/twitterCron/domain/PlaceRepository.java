package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlaceRepository extends MongoRepository<PlaceEntity, String> {
}
