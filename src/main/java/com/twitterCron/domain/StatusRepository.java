package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface StatusRepository extends MongoRepository<StatusEntity, String>,
        StatusRepositoryCustom {
}
