package com.twitterCron.factory;

import com.twitterCron.domain.HashTagEntity;
import com.twitterCron.domain.UserNode;
import com.twitterCron.domain.UserNodeEntity;
import org.springframework.stereotype.Component;
import twitter4j.HashtagEntity;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class UserNodeFactoryImpl implements UserNodeFactory{


    @Override
    public UserNodeEntity create(UserNode u) {
        UserNodeEntity userNodeEntity = new UserNodeEntity();
        userNodeEntity.setId(String.valueOf(u.getId()));
        userNodeEntity.setRetweeters(u.getRetweeters().stream().map(r -> String.valueOf(r.getId())).collect(Collectors.toList()));
        userNodeEntity.setScore(u.getScore());
        return userNodeEntity;
    }
}
