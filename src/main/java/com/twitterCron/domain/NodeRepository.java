package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NodeRepository extends MongoRepository<Node, String> , NodeRepositoryCustom{
    @Query("{ 'name' : '?0', 'statusId' : '?1' } }")
    List<Node> findByUserStatus(String userId, String statusId);

}
