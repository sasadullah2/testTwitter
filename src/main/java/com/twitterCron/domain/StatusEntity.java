package com.twitterCron.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import java.util.Date;
import java.util.List;

@Document(collection = "status")
@Data
public class StatusEntity {

    @Id
    private String id;

    @Indexed
    private long createdAt;
    @TextIndexed
    private String text;
    private String source;
    private Boolean isFavorited;
    private Boolean isRetweeted;
    private Integer favoriteCount;
    private String placeId ;
    private Integer retweetCount;
    private Boolean isPossiblySensitive;
    private String lang;
    private List<String> hashtags;
    @Indexed
    private String userId;
    private Boolean verified;

    private Integer tweetCredScore;
}