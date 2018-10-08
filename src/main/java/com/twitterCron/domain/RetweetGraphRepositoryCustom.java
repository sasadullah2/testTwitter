package com.twitterCron.domain;

import java.util.List;

public interface RetweetGraphRepositoryCustom {

    //Integer updateRetweeters(String statusId, List<String> retweeters);

    List<RetweetGraphEntity> findByUserId(String userId);


}
