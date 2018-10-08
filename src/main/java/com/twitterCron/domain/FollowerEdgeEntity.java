package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "followerEdge")
@Data
@Builder
@CompoundIndex(name = "follower_graph_idx", def = "{'follower' : 1, 'followee' : 1}")
public class FollowerEdgeEntity {

    @Id
    private String id;

    @Indexed
    private String follower;

    private String followerName;

    @Indexed
    private String followee;

    private String followeeName;

}