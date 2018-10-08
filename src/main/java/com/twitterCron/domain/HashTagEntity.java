package com.twitterCron.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hashTag")
@Data
public class HashTagEntity {

    @Id
    private String value;

}