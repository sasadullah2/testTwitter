package com.twitterCron.factory;

import com.twitterCron.domain.UserEntity;
import twitter4j.User;

import java.util.List;

public interface UserFactory {
    UserEntity create(User u, List<String> retweeters);
    UserEntity create(User u);
}
