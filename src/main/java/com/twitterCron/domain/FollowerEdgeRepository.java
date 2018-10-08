package com.twitterCron.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FollowerEdgeRepository extends MongoRepository<FollowerEdgeEntity, String> {
    @Query("{ 'follower' : '?0', 'followee' : '?1' } }")
    List<FollowerEdgeEntity> findByFollowerFollowee(String follower, String followee);
}
