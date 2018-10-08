package com.twitterCron.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "place")
@Data
public class PlaceEntity {

    @Id
    private String id;
    private String name;
    private String streetAddress;
    private String countryCode;
    private String country;
    private String placeType;
    private String fullName;

}