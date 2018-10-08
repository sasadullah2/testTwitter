package com.twitterCron.domain;

import java.util.List;

public interface NodeRepositoryCustom {


    List<Node> findByUserId(String userId);
}
