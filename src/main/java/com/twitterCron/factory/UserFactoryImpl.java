package com.twitterCron.factory;

import com.twitterCron.domain.UserEntity;
import org.springframework.stereotype.Component;
import twitter4j.User;

import java.util.List;

@Component
public class UserFactoryImpl implements UserFactory{
    
    @Override
    public UserEntity create(User u, List<String> retweeters){
        UserEntity user = create(u);
        user.setRetweeters(retweeters);
        return user;
    }

    @Override
    public UserEntity create(User u){
        UserEntity user = new UserEntity();
        user.setCreatedAt(u.getCreatedAt().getTime());
        user.setEmail(u.getEmail());
        user.setFavouritesCount(u.getFavouritesCount());
        user.setId(String.valueOf(u.getId()));
        user.setName(u.getName());
        user.setScreenName(u.getScreenName());
        user.setFollowersCount(u.getFollowersCount());
        user.setFriendsCount(u.getFriendsCount());
        user.setIsVerified(u.isVerified());
        user.setLang(u.getLang());
        user.setStatusesCount(u.getStatusesCount());
        return user;
    }
}
