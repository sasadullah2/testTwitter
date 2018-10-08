package com.twitterCron.services;

import com.twitterCron.domain.Node;
import com.twitterCron.domain.TweetCredResponseDto;
import twitter4j.Status;
import twitter4j.User;

import java.util.List;

public interface TwitterService1 {
    List<Status> search(String term);
    void process(String screenName);
    List<Node> getR(String userId);
    TweetCredResponseDto getScore(Long tweetId);
    //void generateFollowersGraph();
    void getUserStatus(String screenName);
    void getRetweeters();
    void getFollowerGraph();
    void getFollowerGraphList(List<String> userIdList, List<String> retweeters);
    List<Node> getRetweetGraphNode(String userId);
    void spreadScore(List<String> userIds);
    Integer getTweetCred(String id);
    List<Node> getRetweetGraphNode(String statusId, String screenName);
    void saveFollowers();
}
