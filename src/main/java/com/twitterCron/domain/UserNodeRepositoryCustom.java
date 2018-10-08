package com.twitterCron.domain;

import java.util.List;

public interface UserNodeRepositoryCustom {

    Integer updateRetweetersAndScore(String userId, List<String> retweeters, Integer score);

}
