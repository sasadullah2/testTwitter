package com.twitterCron.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import twitter4j.Status;
import twitter4j.URLEntity;

import java.util.Date;
import java.util.List;

@Document(collection = "user")
@Data
public class UserEntity {

    @Id
    private String id;
    @TextIndexed
    private String name;
    private String email;
    @Indexed
    private String screenName;
    private String location;
    private Integer followersCount;
    private Integer friendsCount;
    @Indexed
    private long createdAt;
    private Integer favouritesCount;
    private String lang;
    private Integer statusesCount;
    private Boolean isVerified;
    @Indexed
    private List<String> retweeters;
}