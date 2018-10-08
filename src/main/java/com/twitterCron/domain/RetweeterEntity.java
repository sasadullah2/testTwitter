package com.twitterCron.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "retweeter")
@Data
public class RetweeterEntity {

    @Id
    private String statusId;

    List<String> retweeters;

}