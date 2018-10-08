package com.twitterCron.factory;

import com.twitterCron.domain.HashTagEntity;
import com.twitterCron.domain.UserNode;
import com.twitterCron.domain.UserNodeEntity;
import twitter4j.HashtagEntity;

public interface UserNodeFactory {
    UserNodeEntity create(UserNode u);
}
