package com.twitterCron.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attribute {
    private String id;
    private String score;
}