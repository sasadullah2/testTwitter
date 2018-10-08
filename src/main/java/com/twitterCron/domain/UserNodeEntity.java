package com.twitterCron.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "userNode")
@Data
public class UserNodeEntity {

    @Id
    private String id;
    private String name;
    List<String> retweeters;
    Integer score;

}