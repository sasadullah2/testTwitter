package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "retweeterNode")
@CompoundIndex(name = "retweet_node_idx", def = "{'name' : 1, 'statusId' : 1}")
public class Node {

    @Indexed
    private String id;
    private String name;

    @Indexed
    private String statusId;

    private Attribute attributes;
    private List<Node> children;
    private Integer level;
}