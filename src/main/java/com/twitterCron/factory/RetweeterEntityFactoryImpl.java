package com.twitterCron.factory;

import com.twitterCron.domain.HashTagEntity;
import com.twitterCron.domain.RetweeterEntity;
import org.springframework.stereotype.Component;
import twitter4j.HashtagEntity;

import java.util.ArrayList;

@Component
public class RetweeterEntityFactoryImpl implements RetweeterEntityFactory{


    @Override
    public RetweeterEntity create(String statusId) {
        RetweeterEntity retweeterEntity = new RetweeterEntity();
        retweeterEntity.setStatusId(statusId);
        retweeterEntity.setRetweeters(new ArrayList<>());
        return retweeterEntity;
    }
}
