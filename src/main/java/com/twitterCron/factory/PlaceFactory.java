package com.twitterCron.factory;

import com.twitterCron.domain.PlaceEntity;
import twitter4j.Place;

public interface PlaceFactory {
    PlaceEntity create(Place p);
}
