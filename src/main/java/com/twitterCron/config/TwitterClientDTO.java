package com.twitterCron.config;


import lombok.Builder;
import lombok.Data;
import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class TwitterClientDTO {

    private Twitter twitter;
    private Long resetTime  = 0l;
}
