package com.twitterCron.domain;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class UserRepositoryImpl implements UserRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Integer updateRetweeters(String userId, List<String> retweeters) {

        Query query = new Query(Criteria.where("id").is(userId));

        UserEntity user = mongoTemplate.findOne(query, UserEntity.class);
        if (user != null) {
            List<String> existingRetweeters = user.getRetweeters();
            if (existingRetweeters != null) {
                retweeters.addAll(existingRetweeters);
            }
            Update update = new Update();
            update.set("retweeters", retweeters);

            WriteResult result =
                    mongoTemplate.updateFirst(query, update, UserEntity.class);

            if(result != null)
                return result.getN();
        }
        return 0;
    }

    @Override
    public List<UserEntity> findByList(List<String> userIdList) {

        Query query = new Query(Criteria.where("id").in(userIdList));
        return mongoTemplate.find(query, UserEntity.class);
    }
}
