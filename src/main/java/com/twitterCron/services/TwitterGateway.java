package com.twitterCron.services;

import com.twitterCron.domain.Node;
import com.twitterCron.domain.TweetCredResponseDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.TwitterFactory;

import java.util.List;

@Component
public class TwitterGateway {

    private List<TwitterFactory> twitterFactoryList;

    TwitterGateway() {
        ClassPathResource classPathResource = new ClassPathResource("cred.txt");


    }
}
