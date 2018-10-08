package com.twitterCron.factory;

import com.twitterCron.domain.HashTagEntity;
import com.twitterCron.domain.UserEntity;
import twitter4j.HashtagEntity;
import twitter4j.User;

public interface HashTagFactory {
    HashTagEntity create(HashtagEntity h);
}
