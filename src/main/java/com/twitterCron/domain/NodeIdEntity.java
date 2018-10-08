package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "nodeId")
public class NodeIdEntity {

    @Id
    private String userId;
}