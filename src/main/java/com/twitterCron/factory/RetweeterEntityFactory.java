package com.twitterCron.factory;

import com.twitterCron.domain.HashTagEntity;
import com.twitterCron.domain.RetweeterEntity;
import twitter4j.HashtagEntity;

public interface RetweeterEntityFactory {
    RetweeterEntity create(String statusId);
}
