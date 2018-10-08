package com.twitterCron.domain;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class StatusRepositoryImpl implements StatusRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    /*@Override
    public Integer updateRetweeters(String statusId, List<String> retweeters) {

        Query query = new Query(Criteria.where("id").is(statusId));

        StatusEntity status = mongoTemplate.findOne(query, StatusEntity.class);
        if (status != null) {
            List<String> existingRetweeters = status.getRetweeters();
            if (existingRetweeters != null) {
                retweeters.addAll(existingRetweeters);
            }
            Update update = new Update();
            update.set("retweeters", retweeters);

            WriteResult result =
                    mongoTemplate.updateFirst(query, update, StatusEntity.class);

            if(result != null)
                return result.getN();
        }
        return 0;
    }*/

    @Override
    public List<StatusEntity> findByUserId(String userId, Integer limit) {
        Query query = new Query(Criteria.where("userId").is(userId));
        query.limit(limit);
        return mongoTemplate.find(query, StatusEntity.class);

    }
}
