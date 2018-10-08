package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import twitter4j.Status;
import twitter4j.User;

import java.util.List;

@Data
@Builder
public class BotometerRequest {

    private User user;

    private List<Status> timeline;

    private List<Status> mentions;
}