package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@Document(collection = "userScore")
public class UserScore {

    @Id
    private  String userId;

    private Double botScore;

    private Map<String, Double> spreadScoreMap;

    private Double spreadScore;

    private Map<String, Double> truthinessScoreMap;

    private Double propagationScore;

    private Double influenceScore;
}