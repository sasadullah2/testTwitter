package com.twitterCron.factory;

import com.twitterCron.domain.PlaceEntity;
import com.twitterCron.domain.StatusEntity;
import org.springframework.stereotype.Component;
import twitter4j.Place;
import twitter4j.Status;

import java.util.List;

@Component
public class StatusFactoryImpl implements StatusFactory {

    @Override
    public StatusEntity create(Status s, List<String> hashTags,
                               String placeId, String userId,
                               List<String> retweeters, Boolean verified, Integer tweetCredScore) {
        StatusEntity status =  new StatusEntity();
        status.setId(String.valueOf(s.getId()));
        status.setCreatedAt(s.getCreatedAt().getTime());
        status.setFavoriteCount(s.getFavoriteCount());
        status.setHashtags(hashTags);
        status.setIsFavorited(s.isFavorited());
        status.setIsPossiblySensitive(s.isPossiblySensitive());
        status.setIsRetweeted(s.isRetweeted());
        status.setLang(s.getLang());
        status.setPlaceId(placeId);
        status.setRetweetCount(s.getRetweetCount());
        status.setSource(s.getSource());
        status.setText(s.getText());
        status.setUserId(userId);
        status.setVerified(verified);
        status.setTweetCredScore(tweetCredScore);
        return status;
    }
}
