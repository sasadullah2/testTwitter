package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "followerNode")
public class FollowerNodeEntity {

    @Id
    private String userId;
    private Long cursor;
    private Integer followers;
    private Integer friends;
}