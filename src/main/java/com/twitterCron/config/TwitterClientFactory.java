package com.twitterCron.config;

import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class TwitterClientFactory {

    List<TwitterClientDTO> twitterList;

    public static final Integer CRED_COUNT = 2;

    private Environment env;

    @Autowired
    public TwitterClientFactory(Environment env) {
        if (env != null) {
            twitterList = new ArrayList<>();
            for (Integer i = 0; i < CRED_COUNT; i++) {
                String consumerKey = env.getProperty("cred" + i + ".consumerKey");
                String consumerSecret = env.getProperty("cred" + i + ".consumerSecret");
                String accessToken = env.getProperty("cred" + i + ".accessToken");
                String accessTokenSecret = env.getProperty("cred" + i + ".accessTokenSecret");
                Preconditions.checkNotNull(consumerKey);
                Preconditions.checkNotNull(consumerSecret);
                Preconditions.checkNotNull(accessToken);
                Preconditions.checkNotNull(accessTokenSecret);
                twitterList.add(TwitterClientDTO.builder().twitter(new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret)).build());
            }
        }
    }


    public TwitterClientDTO getTwitterClient() {

        TwitterClientDTO min = twitterList.get(0);
        for (Integer i = 1; i < CRED_COUNT; i++) {
            if (twitterList.get(i).getResetTime() < min.getResetTime()) {
                min = twitterList.get(i);
            }
        }
        if (min.getResetTime() != null && System.currentTimeMillis() < min.getResetTime()) {
            try {
                Thread.sleep(min.getResetTime() - System.currentTimeMillis() );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return min;
    }
}
