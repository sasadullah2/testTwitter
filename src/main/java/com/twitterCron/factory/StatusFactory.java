package com.twitterCron.factory;

import com.twitterCron.domain.StatusEntity;
import twitter4j.Status;

import java.util.List;

public interface StatusFactory {
    StatusEntity create(Status s, List<String> hashTags,
                        String placeId, String userId, List<String> retweeters
            , Boolean verified, Integer tweetCredScore);
}
