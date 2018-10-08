package com.twitterCron.domain;

import java.util.List;

public interface UserRepositoryCustom {

    Integer updateRetweeters(String userId, List<String> retweeters);

    List<UserEntity> findByList(List<String> userIdList);

}
