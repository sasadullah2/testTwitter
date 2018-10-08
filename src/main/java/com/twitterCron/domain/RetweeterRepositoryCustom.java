package com.twitterCron.domain;

import java.util.List;

public interface RetweeterRepositoryCustom {

    Integer updateRetweeters(String statusId, List<String> retweeters);

}
