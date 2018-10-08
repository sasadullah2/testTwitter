package com.twitterCron.domain;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class RetweeterRepositoryImpl implements RetweeterRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Integer updateRetweeters(String statusId, List<String> retweeters) {

        Query query = new Query(Criteria.where("_id").is(statusId));

        RetweeterEntity retweeterEntity = mongoTemplate.findOne(query, RetweeterEntity.class);
        if (retweeterEntity != null) {
            List<String> existingRetweeters = retweeterEntity.getRetweeters();
            if (existingRetweeters != null) {
                retweeters.addAll(existingRetweeters);
            }
            Update update = new Update();
            update.set("retweeters", retweeters);

            WriteResult result =
                    mongoTemplate.updateFirst(query, update, RetweeterEntity.class);

            if(result != null)
                return result.getN();
        }
        return 0;
    }
}
