package com.twitterCron.factory;

import com.twitterCron.domain.PlaceEntity;
import org.springframework.stereotype.Component;
import twitter4j.Place;

@Component
public class PlaceFactoryImpl implements PlaceFactory {

    @Override
    public PlaceEntity create(Place p) {
        PlaceEntity place = new PlaceEntity();
        place.setCountry(p.getCountry());
        place.setCountryCode(p.getCountryCode());
        place.setFullName(p.getFullName());
        place.setId(p.getId());
        place.setName(p.getName());
        place.setPlaceType(p.getPlaceType());
        place.setStreetAddress(p.getStreetAddress());
        return place;
    }
}
