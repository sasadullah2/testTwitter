package com.twitterCron.domain;

import java.util.List;

public interface StatusRepositoryCustom {

    //Integer updateRetweeters(String statusId, List<String> retweeters);

    List<StatusEntity> findByUserId(String userId, Integer limit);
}
