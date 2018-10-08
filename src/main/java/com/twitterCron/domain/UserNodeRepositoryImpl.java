package com.twitterCron.domain;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class UserNodeRepositoryImpl implements UserNodeRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Integer updateRetweetersAndScore(String userId, List<String> retweeters, Integer score) {

        Query query = new Query(Criteria.where("id").is(userId));

        UserNodeEntity user = mongoTemplate.findOne(query, UserNodeEntity.class);
        if (user != null) {
            List<String> existingRetweeters = user.getRetweeters();
            Integer existingScore = user.getScore();
            score = existingScore + score;
            if (existingRetweeters != null) {
                retweeters.addAll(existingRetweeters);
            }
            Update update = new Update();
            update.set("retweeters", retweeters);
            update.set("score", score);

            WriteResult result =
                    mongoTemplate.updateFirst(query, update, UserEntity.class);

            if(result != null)
                return result.getN();
        }
        return 0;
    }
}
