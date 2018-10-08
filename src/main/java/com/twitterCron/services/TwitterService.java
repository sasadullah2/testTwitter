package com.twitterCron.services;

import com.twitterCron.config.TwitterClientDTO;
import com.twitterCron.config.TwitterClientFactory;
import com.twitterCron.domain.FollowerEdgeEntity;
import com.twitterCron.domain.UserEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.RateLimitExceededException;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.RateLimitStatus;
import org.springframework.social.twitter.api.ResourceFamily;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TwitterService {

    @Autowired
    TwitterClientFactory twitterClientFactory;


    public void printFollowers(Long userId) {
        Integer p = 1;
        Long cursor = -1l;
        TwitterClientDTO twitterClient = getTwitterClient();
        do {
            if (p == 16) {
                Integer av = 0;
                System.out.println("q");
            }
            try {
                CursoredList<Long> followersList = twitterClient.getTwitter().friendOperations().getFollowerIdsInCursor(userId, cursor);

                cursor = followersList.getNextCursor();
            } catch (RateLimitExceededException e) {
                System.out.println(e.getMessage());
                twitterClient = getNewTwitterClient(twitterClient, ResourceFamily.FOLLOWERS);

            }
            p++;
        } while (cursor != 0l);

    }

    public void getFollowerGraph(List<String> userIds, String fileName) {
        CursoredList<Long> ids = null;
        Long cursor = -1l;
        TwitterClientDTO twitterClient = getTwitterClient();
        FileWriter fw = null;
        BufferedWriter writer = null;

        try {
            fw = new FileWriter(fileName + ".csv");
            writer = new BufferedWriter(fw);


        for (String userId : userIds) {
            List<String> userList = new ArrayList<>();
            Map<Integer, Integer> levelMap = new HashMap<>();
            userList.add(userId);
            levelMap.put(0, 1);
            Integer level = 0;

            Integer degree = 5;
            Integer count = 0;
            Integer sameLevelNode = 0;
            Integer followersCount = 0;
            for (int i = 0; i < userList.size(); i++) {
                List<String> followers = new ArrayList<>();
                count++;
                followersCount = 0;
                if (level < degree) {
                    Integer limit = 5;
                    do {
                        if (limit == 0) {
                            break;
                        }
                        try {
                            ids = twitterClient.getTwitter().friendOperations().getFollowerIdsInCursor(
                                    Long.parseLong(userList.get(i)), cursor);

                            cursor = ids.getNextCursor();
                            if (CollectionUtils.isNotEmpty(ids)) {
                                for (int j = 0; j < ids.size(); j++) {
                                    followers.add(String.valueOf(ids.get(j)));
                                    //saveFollowerEdge(String.valueOf(ids.getIDs()[j]), userList.get(i));
                                    userList.add(String.valueOf(ids.get(j)));

                                }
                            }
                        } catch (RateLimitExceededException e) {
                            System.out.println(e.getMessage());
                            twitterClient = getNewTwitterClient(twitterClient, ResourceFamily.FOLLOWERS);

                        }
                        limit--;
                    } while (ids != null && ids.hasNext());
                    levelMap.put((level + 1), levelMap.getOrDefault((level + 1), 0) + followersCount);
                    if (levelMap.getOrDefault(level, 0) >= count) {
                        level++;
                        count = 0;
                    }
                }



            }
        }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if ( writer!= null)
                    writer.close();

                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


/*

    private void saveFollowersToFile(List<String> followers, String userId, BufferedWriter writer) {

        try {
            List<List<String>> followersList = ListUtils.partition(followers, 100);
            for (List<String> flList : followersList) {
                getTwitterClient().getTwitter().userOperations().getScreenName();
                *//*writer.write(followerEdgeEntity.getFollower()+","+followerEdgeEntity.getFollowee()+"\n");*//*

            }
        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if ( writer!= null)
                    writer.close();
*//*
                if (fw != null)
                    fw.close();*//*
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }*/

    private TwitterClientDTO getNewTwitterClient(TwitterClientDTO twitterClient, ResourceFamily resourceFamily) {
        Map<ResourceFamily, List<RateLimitStatus>> rateLimitMap = twitterClient.getTwitter().userOperations().getRateLimitStatus(resourceFamily);
        Integer minResetIndex = 0;
        for (Integer j = 1; j < rateLimitMap.get(resourceFamily).size(); j++) {
            if (rateLimitMap.get(resourceFamily).get(j).getResetTimeInSeconds() < rateLimitMap.get(resourceFamily).get(minResetIndex).getResetTimeInSeconds()) {
                minResetIndex = j;
            }
        }
        twitterClient.setResetTime((rateLimitMap.get(resourceFamily).get(minResetIndex).getResetTimeInSeconds()) * 1000);
        return getTwitterClient();
    }

    private TwitterClientDTO getTwitterClient() {
        return twitterClientFactory.getTwitterClient();
    }
}
