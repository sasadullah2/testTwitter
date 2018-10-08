package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NodeIdRepository extends MongoRepository<NodeIdEntity, String> {
}
