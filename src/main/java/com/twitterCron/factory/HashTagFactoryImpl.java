package com.twitterCron.factory;

import com.twitterCron.domain.HashTagEntity;
import com.twitterCron.domain.UserEntity;
import org.springframework.stereotype.Component;
import twitter4j.HashtagEntity;
import twitter4j.User;

@Component
public class HashTagFactoryImpl implements HashTagFactory{
    
    @Override
    public HashTagEntity create(HashtagEntity h) {
        HashTagEntity hashTag = new HashTagEntity();
        hashTag.setValue(h.getText());
        return hashTag;
    }

}
