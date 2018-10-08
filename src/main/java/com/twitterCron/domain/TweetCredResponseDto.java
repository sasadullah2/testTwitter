package com.twitterCron.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TweetCredResponseDto {
    @JsonProperty("status_id")
    private Long statusId;
    @JsonProperty("status_credibility")
    private Integer score;
}
