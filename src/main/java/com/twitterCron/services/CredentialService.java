package com.twitterCron.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;

import java.util.List;

public class CredentialService {

    private TwitterFactory twitterFactoryList;
    private Integer resetTime;

}
