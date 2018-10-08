package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
public class UserNode {

    @Id
    private Long id;
    private String name;
    List<UserNode> retweeters;
    Integer score;

}