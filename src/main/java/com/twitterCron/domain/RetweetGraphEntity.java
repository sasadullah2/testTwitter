package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "followerGraph")
@Data
@Builder
public class RetweetGraphEntity {

    @Id
    private String statusId;

    @Indexed
    private String userId;

    private Node rootNode;

}