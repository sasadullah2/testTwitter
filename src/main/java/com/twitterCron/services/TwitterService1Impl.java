package com.twitterCron.services;

import com.twitterCron.domain.CredentialsDTO;
import com.twitterCron.domain.FollowerEdgeEntity;
import com.twitterCron.domain.FollowerEdgeRepository;
import com.twitterCron.domain.FollowerGraphEntity;
import com.twitterCron.domain.FollowerGraphRepository;
import com.twitterCron.domain.FollowerNodeEntity;
import com.twitterCron.domain.FollowerNodeRepository;
import com.twitterCron.domain.HashTagEntity;
import com.twitterCron.domain.HashTagRepository;
import com.twitterCron.domain.Node;
import com.twitterCron.domain.NodeIdEntity;
import com.twitterCron.domain.NodeIdRepository;
import com.twitterCron.domain.NodeRepository;
import com.twitterCron.domain.PlaceEntity;
import com.twitterCron.domain.PlaceRepository;
import com.twitterCron.domain.RetweetGraphEntity;
import com.twitterCron.domain.RetweetGraphRepository;
import com.twitterCron.domain.RetweeterEntity;
import com.twitterCron.domain.RetweeterRepository;
import com.twitterCron.domain.StatusEntity;
import com.twitterCron.domain.StatusRepository;
import com.twitterCron.domain.TweetCredResponseDto;
import com.twitterCron.domain.UserEntity;
import com.twitterCron.domain.UserNode;
import com.twitterCron.domain.UserNodeEntity;
import com.twitterCron.domain.UserNodeRepository;
import com.twitterCron.domain.UserRepository;
import com.twitterCron.domain.UserScore;
import com.twitterCron.domain.UserScoreRepository;
import com.twitterCron.factory.HashTagFactory;
import com.twitterCron.factory.PlaceFactory;
import com.twitterCron.factory.RetweeterEntityFactory;
import com.twitterCron.factory.StatusFactory;
import com.twitterCron.factory.UserFactory;
import com.twitterCron.factory.UserNodeFactory;
import com.twitterCron.gateway.BotometerGateway;
import com.twitterCron.gateway.DoesFollowGateway;
import com.twitterCron.gateway.TweetCredGateway;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import twitter4j.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TwitterService1Impl implements TwitterService1 {

    @Autowired
    HashTagFactory hashTagFactory;

    @Autowired
    UserFactory userFactory;

    @Autowired
    StatusFactory statusFactory;

    @Autowired
    PlaceFactory placeFactory;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    HashTagRepository hashTagRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RetweetGraphRepository retweetGraphRepository;

    @Autowired
    FollowerEdgeRepository followerEdgeRepository;

    @Autowired
    UserNodeRepository userNodeRepository;

    @Autowired
    UserNodeFactory userNodeFactory;

    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    Twitter twitter;

    @Autowired
    TweetCredGateway tweetCredGateway;

    @Autowired
    DoesFollowGateway doesFollowGateway;

    @Autowired
    RetweeterEntityFactory retweeterEntityFactory;

    @Autowired
    RetweeterRepository retweeterRepository;

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    FollowerNodeRepository followerNodeRepository;

    @Autowired
    FollowerGraphRepository followerGraphRepository;

    @Autowired
    NodeIdRepository nodeIdRepository;

    @Autowired
    UserScoreRepository userScoreRepository;

    @Autowired
    BotometerGateway botometerGateway;



    @Override
    public List<Status> search(String term) {
        List<Status> statusList = new ArrayList<>();
        Query query = new Query(term);
        query.setCount(100);
        query.setResultType(Query.MIXED);
//        GeoLocation geoLocation = new GeoLocation(
//                30.3753, 69.3451);
//        query.geoCode(geoLocation, 2000, "mi");
        QueryResult result = null;
        try {
            result = twitter.search(query);
            for(Status status: result.getTweets()) {
                if (!status.isRetweeted() && status.getLang().equalsIgnoreCase("en")) {
                    saveTweet(status, Boolean.TRUE);
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        if (result != null) {
            statusList =  result.getTweets();
        }
        return statusList;
    }

    @Override
    public void getUserStatus(String userId) {
        List<Status> statusList = new ArrayList<>();

        try {
            Paging paging = new Paging(1, 200);
            statusList = twitter.getUserTimeline(Long.parseLong(userId), paging);
            for(Status status: statusList) {
                if (!status.isRetweeted() && status.getLang().equalsIgnoreCase("en")) {
                    saveTweet(status, Boolean.TRUE);
                    getRetweeters(status.getId());
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void process(String screenName) {
/*        Query query = new Query("from"+screenName);
        query.setCount(1);
        query.setResultType(Query.RECENT);
        QueryResult result = null;
        try {
            result = twitter.search(query);

        } catch (TwitterException e) {
            e.printStackTrace();
        }*/
        try {
  //          Long tweetId = 1000931015358021632l;
//            Long tweetId = 1000931015358021632l;
            Long tweetId = 1002228032566853632l;

            Status status  = twitter.showStatus(tweetId);
            //checkBot(status.getUser)
            //checkTruthness()
            UserNode root = getRetweetGraph(status);
            printGraph(root);
            TweetCredResponseDto tweetCredResponseDto = getScore(tweetId); //score 1 to 7...  1 being lowest
            Integer multiplyFactor = 5;
            Integer credibility = (tweetCredResponseDto.getScore() - 4) * multiplyFactor;
            // user score will be normalized from -15 to 15

            assignScore(root, credibility);
            printGraph(root);
            saveNodes(root);

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void saveNodes(UserNode userNode) {

        if (userNode != null) {
            saveUserNode(userNode);
            for (UserNode retweeter: userNode.getRetweeters()) {
                saveNodes(retweeter);
            }
        }
    }


    public void assignScore(UserNode userNode, Integer score) {
        if (userNode != null) {
            userNode.setScore(score);
            for (UserNode retweeter: userNode.getRetweeters()) {
                assignScore(retweeter, score / 2);
            }
        }
    }


    @Override
    public TweetCredResponseDto getScore(Long tweetId) {
        return tweetCredGateway.getScore(tweetId, "9fa8d0c2d880ffb498f83506748f7682");
    }

    public void printGraph(UserNode root) {
        List<UserNode> nodeList = new ArrayList<>();
        nodeList.add(root);
        for (int i = 0; i < nodeList.size() ; i++) {
            System.out.println("node" + nodeList.get(i).getId()+ " , score = "+ nodeList.get(i).getScore());
            for (UserNode node : nodeList.get(i).getRetweeters()) {
                System.out.println("retweeters" + node.getId());
                nodeList.add(node);
            }
            System.out.println("    ");
            System.out.println("=====================");
            System.out.println("          ");
        }
    }

    public UserNode getRetweetGraph(Status status) {
        try {
            IDs retweeters = twitter.getRetweeterIds(status.getId(), -1l);
            Set<Long> retweeterSet = new HashSet<>();

            for (int i = 0 ; i < retweeters.getIDs().length; i++) {

                retweeterSet.add(retweeters.getIDs()[i]);

            }
            return generateGraph(retweeterSet, status.getUser().getId());

        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Node> getRetweetGraphNode(String userId) {
        List<StatusEntity> statusEntityList = statusRepository.findByUserId(userId, 2000);
        List<Node> nodeList = new ArrayList<>();
        for (StatusEntity status : statusEntityList) {
            List<Node> nodes = nodeRepository.findByUserStatus(userId, status.getId());
            if (CollectionUtils.isNotEmpty(nodes)) {
                nodeList.addAll(nodes);
                continue;
            }
            RetweeterEntity retweeterEntity = retweeterRepository.findOne(status.getId());
            if (retweeterEntity == null || CollectionUtils.isEmpty(retweeterEntity.getRetweeters())) {
                continue;
            }
            nodeList.add(getFollowerGraphList(status.getUserId(), retweeterEntity.getRetweeters(), status.getId()));
        }

        return nodeList;
    }

    @Override
    public List<Node> getRetweetGraphNode(String statusId, String screenName) {
        List<Node> nodeList = new ArrayList<>();
        List<FollowerEdgeEntity> followerEdgeEntityList = new ArrayList<>();

        User user = null;
        while (true) { // infinite loop so that we can avoid rate limit and resume
            try {
                user = twitter.showUser(screenName); // get user
                if (user != null) {
                    break;
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                if (e.getRateLimitStatus() == null) {
                    break;
                }
                try { // thread sleep till the end time of rate limit
                    Thread.sleep(e.getRateLimitStatus().getSecondsUntilReset() * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        List<User> retweeters = getRetweeterUsers(Long.parseLong(statusId)); // list of retweeters of that tweet
        Node root = Node.builder().name(screenName).id(String.valueOf(user.getId())).statusId(statusId).build(); // graph node
        List<Node> nodes = new ArrayList<>();
        nodes.add(root);
        List<User> retweeterList2 = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            List<Node> retweetNode = new ArrayList<>();
            for (User r : retweeters) {
                while(true) {
                    try{
                        Relationship relationship = twitter.showFriendship(nodes.get(i).getName(), r.getScreenName()); // check follow relationship
                        if (relationship != null) {
                            if (relationship.isSourceFollowedByTarget()) {
                                Node node = Node.builder().name(r.getScreenName()).id(String.valueOf(r.getId())).statusId(statusId).build();
                                nodes.add(node);
                                retweetNode.add(node);
                                followerEdgeEntityList.add(FollowerEdgeEntity.builder().follower(r.getScreenName())
                                        .followee(nodes.get(i).getName()).build());
                            } else {
                                retweeterList2.add(r);
                            }
                            break;
                        }

                    } catch (TwitterException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(e.getRateLimitStatus().getSecondsUntilReset() * 1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

            }
            retweeters = retweeterList2;
            retweeterList2 = new ArrayList<>();
            nodes.get(i).setChildren(retweetNode);

        }
        nodeList.add(root);
        nodeRepository.save(root);
        saveFollowersToFile(followerEdgeEntityList, screenName + statusId);
        return nodeList;
    }

    public UserNode generateGraph(Set<Long> retweetSet, Long userId) {

        List<Long> retweeterList = new ArrayList<>(retweetSet);
        List<Long> retweeterList2 = new ArrayList<>();


        UserNode userNode = UserNode.builder().id(userId).retweeters(new ArrayList<>()).build();
        UserNode rootNode = userNode;
        List<UserNode> userNodeList = new ArrayList<>();
        userNodeList.add(userNode);
        for (int i = 0 ; i < userNodeList.size(); i++) {
            if (retweeterList.size() > 0) {
                try {
                    IDs followersIDs = twitter.getFollowersIDs(userNodeList.get(i).getId(), -1l);

                    if (followersIDs.getIDs().length > 0) {
                        for (int j = 0; j < retweeterList.size(); j++) {
                            if (checkIfFollower(followersIDs, retweeterList.get(j))) {
                                UserNode retweetNode = UserNode.builder().build();
                                retweetNode.setId(retweeterList.get(j));
                                retweetNode.setRetweeters(new ArrayList<>());
                                userNodeList.get(i).getRetweeters().add(retweetNode);
                                userNodeList.add(retweetNode);
                            } else {
                                retweeterList2.add(retweeterList.get(j));
                            }
                        }
                        retweeterList = retweeterList2;
                        retweeterList2 = new ArrayList<>();
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }

        }

        return rootNode;
/*
    @Override
    public void generateFollowersGraph() {

        Long userId = 965627171573239809l;
        Map<Long, FollowerNode> followerNodeMap = new HashMap<>();
        followerNodeMap.put(userId,
                FollowerNode.builder().id(userId)
                .followers(new ArrayList<>()).currentScore(0f).previousScore(0f).friends(new ArrayList<>())
                        .build());
        List<Long> keys = new ArrayList<>();
        keys.add(userId);
        try {
            for (int i = 0; i < keys.size(); i++) {
                FollowerNode node = followerNodeMap.get(keys.get(i));
                IDs followersIDs = twitter.getFollowersIDs(node.getId(), -1l, 3);
                for (int j = 0 ; j < followersIDs.getIDs().length; j++) {

                    Long followerId = followersIDs.getIDs()[j];
                    FollowerNode followerNode = null;
                    if (followerNodeMap.containsKey(followerId)) {
                        followerNode = followerNodeMap.get(followerId);
                        followerNode.getFriends().add(node.getId());
                        node.getFollowers().add(followerNode.getId());
                    } else {
                        followerNode =
                                FollowerNode.builder()
                                        .id(followerId).followers(new ArrayList<>()).friends(new ArrayList<>())
                                        .currentScore(0f).previousScore(0f)
                                        .build();
                        followerNode.getFriends().add(node.getId());
                        node.getFollowers().add(followerNode.getId());
                        followerNodeMap.put(followerNode.getId(), followerNode);
                        if (i  <= 12 ) {
                            keys.add(followerNode.getId());
                        }
                    }
                }
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
*/

    }



    @Override
    public void spreadScore(List<String> userIds) {
        List<String> l = new ArrayList<>();
        l.add("0.04646545976791543")   ;
        l.add("0.008611272203783946")  ;
        l.add("0.019056768161219304")  ;
        l.add("0.021533552348481987")  ;
        l.add("0.04273969320041381" )  ;
        l.add("0.02494607736952959"  );
        l.add("0.013242339942407685"  );
        l.add("0.010875661546842434"  );
        l.add("0.05158083630464807"  );
        l.add("0.09329663189628534"  );

        int i = 0;
        for (String id : userIds) {
            UserScore userScore = null;//userScoreRepository.findOne(Integer.parseInt(id));
            if (userScore == null) {
                userScore = UserScore.builder().build();
            }
            UserEntity userEntity = userRepository.findOne(id);
            userScore.setSpreadScore((double)userEntity.getFollowersCount() / 36000000);
            userScore.setInfluenceScore(Double.parseDouble(l.get(i)));
            List<StatusEntity> s  = statusRepository.findByUserId(id, 2000);
            Integer score = 0;
            for (StatusEntity s1 : s) {
                score+=s1.getTweetCredScore()-4;
            }
            //userScore.setTruthinessScore((double)score / s.size());
            userScoreRepository.save(userScore);
            i++;

        }
    }

    @Override
    public List<Node> getR(String userId) {
        List<Node> nodeList = nodeRepository.findByUserId(userId);
        List<Node> children = new ArrayList<>();
        for (Node n : nodeList) {
            children.addAll(n.getChildren());
        }

        for(int i = 0; i < children.size()/2; i++ ){
           // children.get(i).getChildren() = new ArrayList<>();

        }
        for (Node n : children) {
            if (n.getChildren() == null) {
                n.setChildren(new ArrayList<>());
            }
            n.getChildren().add(children.get((children.size()/2)));
            n.getChildren().add(children.get((children.size()/3)));
            n.getChildren().add(children.get((children.size()/4)));

        }
        return nodeList;
    }

    @Override
    public void saveFollowers() {
        List<StatusEntity> statusEntityList = statusRepository.findAll();
        FileWriter fw = null;
        BufferedWriter writer = null;

        try {
            fw = new FileWriter("status3.csv");
            writer = new BufferedWriter(fw);
            writer.write("id,text,date,userId,screenName,lang,source,userVerified,isRetweeted,isFavourited,retweetCount,favouriteCount,tweetCredScore");
            for (StatusEntity statusEntity: statusEntityList) {
                UserEntity userEntity = userRepository.findOne(statusEntity.getUserId());
                String screenName = "";
                if (userEntity != null) {
                    screenName = userEntity.getScreenName();
                }
                writer.write(statusEntity.getId()+","+statusEntity.getText().replace(",","").replace("\n","")+","+
                        String.valueOf(LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(statusEntity.getCreatedAt()), ZoneId.systemDefault()))+ ","+
                        statusEntity.getUserId()+","+screenName+","+statusEntity.getLang()+","+statusEntity.getSource()+
                        ","+statusEntity.getVerified()+","+statusEntity.getIsRetweeted()+","+statusEntity.getIsFavorited()
                        +","+statusEntity.getRetweetCount()+","+statusEntity.getFavoriteCount()+","+statusEntity.getTweetCredScore()+"\n");
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
        calculatePropagationAndSpreadScore();
    }

    private void calculatePropagationAndSpreadScore() {
        List<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            List<StatusEntity> statusList = statusRepository.findByUserId(user.getId(), 2000);
            UserScore rootScore = userScoreRepository.findOne(user.getId());
            Double spreadScore = 0d;
            for (StatusEntity status : statusList) {
                RetweetGraphEntity retweetGraph = retweetGraphRepository.findOne(status.getId());
                Node root = retweetGraph.getRootNode();
                List<Node> nodeList = new ArrayList<>();
                nodeList.add(root);
                for (int i = 0; i < nodeList.size(); i++) {
                    Node node = nodeList.get(i);
                    Double propagationScore = 0d;
                    UserEntity nodeUser = null;
                    UserScore userScore = null;
                    if (node.getId() == root.getId()) { // is root Node i.e originator
                        propagationScore = 1d;
                        nodeUser = user;
                        userScore = rootScore;
                    } else {
                        propagationScore = 0.5d;
                        nodeUser = userRepository.findOne(node.getId());
                        userScore = userScoreRepository.findOne(nodeUser.getId());
                    }
                    if (status.getTweetCredScore() <= 4) { // false tweet
                        propagationScore *= -1;
                    }
                    userScore.setPropagationScore(userScore.getPropagationScore() + propagationScore);
                    spreadScore += node.getChildren().size() * node.getLevel();
                    userScoreRepository.save(userScore);
                }
            }
            spreadScore /= statusList.size(); // sum of all spread score  divided by number of tweets
            rootScore.setSpreadScore((rootScore.getSpreadScore() + spreadScore) / 2);
            userScoreRepository.save(rootScore);
        }
    }

/*

    public void pageRank(Map<Long, FollowerNode> followerNodeMap) {
        Float initialScore = 1/(float)followerNodeMap.size();
        for (Long id : followerNodeMap.keySet()) {
            followerNodeMap.get(id).setPreviousScore(initialScore);
        }
        for (int i = 0 ; i < 3; i++) {
            for (Long id : followerNodeMap.keySet()) {
                FollowerNode node = followerNodeMap.get(id);
                Float score = 0f;
                for(Long follower : node.getFollowers()) {
                    FollowerNode followerNode = followerNodeMap.get(follower);
                    score += (1 / (float) followerNode.getFriends().size()) * followerNode.getPreviousScore();
                }
                node.setCurrentScore(score);
            }
            for (Long id : followerNodeMap.keySet()) {
                FollowerNode node = followerNodeMap.get(id);
                followerNodeMap.get(id).setPreviousScore(node.getCurrentScore());
            }
        }
    }

*/
    public Boolean checkIfFollower(IDs followersIDs, Long retweeter) {
        for (int i = 0 ; i < followersIDs.getIDs().length; i++) {

            Long follower = followersIDs.getIDs()[i];
            if (retweeter.equals(follower)) {
                return Boolean.TRUE;
            }

        }

        return Boolean.FALSE;
    }

    public void saveTweet(Status status, Boolean retweets) {
        List<String> hashTags = new ArrayList<>();
        String userId;
        String placeId;
        List<String> retweeters = new ArrayList<>();

        if (status.getRetweetCount() > 0) {
            //retweeters = getRetweeters(status.getId());
        }
        if (status.getHashtagEntities() != null &&
                status.getHashtagEntities().length > 0) {
            for (int i = 0; i < status.getHashtagEntities().length; i++) {
                hashTags.add(saveHashTag(status.getHashtagEntities()[i]));
            }
        }
        userId = String.valueOf(status.getUser().getId());
        saveUser(status.getUser(), retweeters);
//        placeId = savePlace(status.getPlace());
        if (!statusRepository.exists(String.valueOf(status.getId()))) {
            Integer score = 1;
            try {
                score = getScore(status.getId()).getScore();
            } catch (Exception e) {
                Random rand = new Random();
                score = rand.nextInt(6) + 1;
            }
            StatusEntity statusEntity = statusFactory.create(
                    status, hashTags, "", userId, retweeters,
                    status.getUser().isVerified(), score);
            statusRepository.save(statusEntity);
            RetweeterEntity retweeterEntity = retweeterEntityFactory.create(String.valueOf(status.getId()));
            retweeterRepository.save(retweeterEntity);
        }


    }

    public void getRetweets(Long statusId) {
        IDs ids = null;
        List<Status> statusList = null;
        try {
            statusList = twitter.getRetweets(statusId);
            for (Status status: statusList) {
                saveTweet(status, Boolean.FALSE);
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    public List<String> getRetweeters(Long statusId) {
        IDs ids = null;
        Long cursor = -1l;
        List<String> retweeters = new ArrayList<>();
        Integer secondsUntilReset = 8 * 60;

        Integer count = 30;
        do {
            try {
                ids = twitter.getRetweeterIds(statusId, 100, cursor);
                cursor = ids.getNextCursor();
                if (ids.getIDs() != null) {
                    for (int i = 0; i < ids.getIDs().length; i++) {
                        retweeters.add(String.valueOf(ids.getIDs()[i]));
                    }
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(e.getRateLimitStatus().getSecondsUntilReset() * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            count --;
        } while (ids != null && ids.hasNext());// && count > 0);
        RetweeterEntity retweeterEntity = retweeterEntityFactory.create(String.valueOf(statusId));
        retweeterRepository.save(retweeterEntity);
        retweeterRepository.updateRetweeters(String.valueOf(statusId), retweeters);

            //getAndSave(retweeters);


        return retweeters;
    }

    public List<User> getRetweeterUsers(Long statusId) {
        IDs ids = null;
        Long cursor = -1l;
        List<String> retweeters = new ArrayList<>();
        List<User> retweeterUsers = new ArrayList<>();
        Integer secondsUntilReset = 8 * 60;

        Integer count = 30;
        do {
            try {
                ids = twitter.getRetweeterIds(statusId, 100, cursor);
                cursor = ids.getNextCursor();
                if (ids.getIDs() != null) {
                    for (int i = 0; i < ids.getIDs().length; i++) {
                        retweeters.add(String.valueOf(ids.getIDs()[i]));
                    }
                    List<User> users = twitter.lookupUsers(ids.getIDs());
                    retweeterUsers.addAll(users);
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(e.getRateLimitStatus().getSecondsUntilReset() * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            count --;
        } while (ids != null && ids.hasNext());// && count > 0);
        if (retweeterRepository.findOne(String.valueOf(statusId)) == null) {
            RetweeterEntity retweeterEntity = retweeterEntityFactory.create(String.valueOf(statusId));
            retweeterRepository.save(retweeterEntity);
        }
        retweeterRepository.updateRetweeters(String.valueOf(statusId), retweeters);

        //getAndSave(retweeters);


        return retweeterUsers;
    }

    @Override
    public void getRetweeters() {
        List<RetweeterEntity> retweeterEntities = retweeterRepository.findAll();
        for (RetweeterEntity retweeterEntity : retweeterEntities) {
            if (retweeterEntity.getRetweeters().size() == 0) {
                getRetweeters(Long.parseLong(retweeterEntity.getStatusId()));
            }
        }

    }

    @Override
    public void getFollowerGraph() {
        List<UserEntity> userEntities = userRepository.findAll();
        IDs ids = null;
        Long cursor = -1l;
        Integer secondsUntilReset = 8 * 60;

        for (UserEntity userEntity : userEntities) {
            List<String> userList = new ArrayList<>();
            Map<Integer, Integer> levelMap = new HashMap<>();
            userList.add(userEntity.getId());
            levelMap.put(0, 1);
            Integer level = 0;

            Integer degree = 3;
            Integer count = 0;
            Integer sameLevelNode = 0;
            Integer followersCount = 0;
            for (int i = 0; i < userList.size(); i++) {
                count++;
                followersCount = 0;
                if (level < degree) {
                    Integer limit = 5;
                    do {
                        if (limit == 0) {
                            break;
                        }
                        try {
                            ids = twitter.getFollowersIDs(Long.parseLong(userList.get(i)), cursor, 5000);

                            cursor = ids.getNextCursor();
                            secondsUntilReset = ids.getRateLimitStatus().getSecondsUntilReset();
                            if (ids.getIDs() != null) {
                                for (int j = 0; j < ids.getIDs().length; j++) {
                                    followersCount++;
                                    saveFollowerEdge(String.valueOf(ids.getIDs()[j]), userList.get(i));
                                    userList.add(String.valueOf(ids.getIDs()[j]));

                                }
                            }
                        } catch (TwitterException e) {
                            e.printStackTrace();
                            try {
                                Thread.sleep(secondsUntilReset * 1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        limit--;
                    } while (ids != null && ids.hasNext());
                    levelMap.put((level + 1), levelMap.getOrDefault((level + 1), 0) + followersCount);
                    if (levelMap.getOrDefault(level, 0) >= count) {
                        level++;
                        count = 0;
                    }
                }
                Integer limit = 5;
                do {
                    if (limit == 0) {
                        break;
                    }
                    try {
                        ids = twitter.getFriendsIDs(Long.parseLong(userList.get(i)), cursor, 5000);
                        cursor = ids.getNextCursor();
                        secondsUntilReset = ids.getRateLimitStatus().getSecondsUntilReset();
                        if (ids.getIDs() != null) {
                            for (int j = 0; j < ids.getIDs().length; j++) {
                                saveFollowerEdge(userList.get(i), String.valueOf(ids.getIDs()[j]));
                            }
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(secondsUntilReset * 1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    limit--;
                } while (ids != null && ids.hasNext());

            }
        }
    }
    public Node getFollowerGraphList(String userId, List<String> retweeters, String statusId) {
        //UserEntity userEntity = userRepository.findOne(userId);
        IDs ids = null;
        Long followerCursor = -1l;
        Long friendCursor = -1l;
        Integer noOfFollowers = 0;
        Integer noOfFriends = 0;
        Integer credIndex = 0;

        List<String> userList = new ArrayList<>();
        Map<Integer, Integer> levelMap = new HashMap<>();
        //userList.add(userEntity.getId());
        levelMap.put(0, 1);
        Integer level = 0;

        Integer degree = 2;
        Integer count = 0;
        Integer sameLevelNode = 0;
        Integer followersCount = 0;
        Integer friendsLimit = 1;
        Integer followersLimit = 1;
        List<FollowerEdgeEntity> followerEdgeEntityList = null;
        List<String> nodeIdList = null;
        followerEdgeEntityList = new ArrayList<>();
        Node root = Node.builder().id(userId).name(userId).statusId(statusId).build();
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(root);
        for (int i = 0; i < nodeList.size(); i++) {
            nodeIdList = new ArrayList<>();
            Long cursor = -1l;
//            if (nodeList.get(i).getId() == userEntity.getId()) {
//                noOfFollowers = userEntity.getFollowersCount();
//            }
            count++;
            followersCount = 0;
            List<String> retweeterList2 = new ArrayList<>();
            List<Node> retweeterNodeList = new ArrayList<>();

//            if (level < degree) {
                if (CollectionUtils.isEmpty(retweeters)) {
                    continue;
                }
//                for (String retweeter : retweeters) {
//                    if (CollectionUtils.isEmpty(followerEdgeRepository.findByFollowerFollowee(retweeter, userList.get(i)))) {
//                        retweeterList2.add(retweeter);
//                    } else {
//                        retweeterNodeList.add(Node.builder().id(retweeter).statusId(statusId).build());
//                        userList.add(retweeter); //foll
//                        followersCount++;
//                    }
//                }
                //retweeters = retweeterList2;
                retweeterList2 = retweeters;
                followersLimit = noOfFollowers != null && noOfFollowers > 0 ? ((noOfFollowers / 100) / 5000) + 1  : 5;
                followerCursor = cursor != null ? cursor : -1l;

                do {
                    try {
                        ids = twitter.getFollowersIDs(Long.parseLong(nodeList.get(i).getId()), followerCursor, 5000);
                        followerCursor = ids.getNextCursor();

                        if (ids.getIDs() != null) {
                            for (int j = 0; j < ids.getIDs().length; j++) {
                                String id = String.valueOf(ids.getIDs()[j]);
                                followerEdgeEntityList.add(saveFollowerEdge(id, nodeList.get(i).getId()));
                                retweeters = retweeterList2;
                                retweeterList2 = new ArrayList<>();
                                for (String rt : retweeters) {
                                    if (rt.equalsIgnoreCase(id)) {
                                        followersCount++;
                                        Node node = Node.builder().id(id).name(id).statusId(statusId).build();
                                        retweeterNodeList.add(node);
                                        nodeList.add(node);
                                    } else {
                                        retweeterList2.add(rt);
                                    }
                                }

                            }
                        }

                        followersLimit--;

                    } catch (TwitterException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(e.getRateLimitStatus().getSecondsUntilReset() * 1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                } while (ids != null && ids.hasNext());//&& followersLimit != 0);

                levelMap.put((level + 1), levelMap.getOrDefault((level + 1), 0) + followersCount);
                if (levelMap.getOrDefault(level, 0) >= count) {
                    level++;
                    count = 0;
                }
                nodeList.get(i).setChildren(retweeterNodeList);
                nodeList.addAll(retweeterNodeList);
                nodeRepository.save(nodeList.get(i));
//            }
        }
        return root;
    }

    private List<List<CredentialsDTO>> getCredentials() {
        List<List<CredentialsDTO>> credentialsDTOList = new ArrayList<>();
        BufferedReader br = null;
        FileReader fr = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource("cred2.txt");
            fr = new FileReader(classPathResource.getFile());
            br = new BufferedReader(fr);

            String sCurrentLine;

            Integer i = 0;
            List<CredentialsDTO> credentials = new ArrayList<>();
            while ((sCurrentLine = br.readLine()) != null) {
                String[] s = sCurrentLine.split("=");
                credentials.add(CredentialsDTO.builder().name(s[0]).value(s[1]).resetTime(0).build());
                i++;
                if (i == 4) {
                    credentialsDTOList.add(credentials);
                    credentials = new ArrayList<>();
                    i = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return credentialsDTOList;

    }

    private void setCredentials(List<CredentialsDTO> credentials) {
        credentials.stream().forEach(credentialsDTO -> {
            System.setProperty(credentialsDTO.getName(), credentialsDTO.getValue());
        });
    }

    @Override
    public void getFollowerGraphList(List<String> userIdList, List<String> retweeters) {
        //List<List<CredentialsDTO>> credentialsDTOList = getCredentials();
        List<UserEntity> userEntities = userRepository.findByList(userIdList);
        IDs ids = null;
        Long followerCursor = -1l;
        Long friendCursor = -1l;


        Integer noOfFollowers = 0;
        Integer noOfFriends = 0;
        Integer credIndex = 0;
        //setCredentials(credentialsDTOList.get(credIndex));

        for (UserEntity userEntity : userEntities) {
            List<String> userList = new ArrayList<>();
            Map<Integer, Integer> levelMap = new HashMap<>();
            userList.add(userEntity.getId());
            levelMap.put(0, 1);
            Integer level = 0;

            Integer degree = 2;
            Integer count = 0;
            Integer sameLevelNode = 0;
            Integer followersCount = 0;
            Integer friendsLimit = 1;
            Integer followersLimit = 1;
            List<FollowerEdgeEntity> followerEdgeEntityList = null;
            List<String> nodeIdList = null;
            followerEdgeEntityList = new ArrayList<>();
            for (int i = 0; i < userList.size(); i++) {
                nodeIdList = new ArrayList<>();
                FollowerNodeEntity followerNodeEntity = followerNodeRepository.findOne(userList.get(i));
                Long cursor = -1l;
                if (followerNodeEntity != null) {
                    noOfFollowers = followerNodeEntity.getFollowers();
                    cursor = followerNodeEntity.getCursor();
                } else if (userList.get(i) == userEntity.getId()) {
                    noOfFollowers = userEntity.getFollowersCount();
                }

                count++;
                followersCount = 0;
                if (level < degree) {
                    followersLimit = noOfFollowers != null && noOfFollowers > 0 ? ((noOfFollowers / 100) / 5000) + 1  : 5;
                    followerCursor = cursor != null ? cursor : -1l;
                    do {
                        try {
                            ids = twitter.getFollowersIDs(Long.parseLong(userList.get(i)), followerCursor, 5000);
                            followerCursor = ids.getNextCursor();

                            if (ids.getIDs() != null) {
                                for (int j = 0; j < ids.getIDs().length; j++) {
                                    followersCount++;
                                    String id = String.valueOf(ids.getIDs()[j]);
                                    followerEdgeEntityList.add(saveFollowerEdge(id, userList.get(i)));
                                    userList.add(id);
                                    if (!followerNodeRepository.exists(id)) {
                                        nodeIdList.add(id);
                                    }
                                }
                            }
                            followersLimit--;

                        } catch (TwitterException e) {
                            e.printStackTrace();
                            try {
                                Thread.sleep(e.getRateLimitStatus().getSecondsUntilReset() * 1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            /*credentialsDTOList.get(credIndex).get(0).setResetTime(e.getRateLimitStatus().getResetTimeInSeconds());
                            Integer min = 0;
                            for (Integer index = 0 ; index <  credentialsDTOList.size(); index++) {
                                if (credentialsDTOList.get(index).get(0).getResetTime() == 0) {
                                    min = index;
                                    break;
                                }
                                if (credentialsDTOList.get(index).get(0).getResetTime() < credentialsDTOList.get(min).get(0).getResetTime()) {
                                    min = index;
                                }
                            }
                            setCredentials(credentialsDTOList.get(min));
                            Long dif = 0l;
                            if (credentialsDTOList.get(min).get(0).getResetTime() > 0) {
                                dif = System.currentTimeMillis() - credentialsDTOList.get(min).get(0).getResetTime()*1000;
                            }
                            if (dif > 0){
                                try {
                                    Thread.sleep(dif);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }*/
                        }
                    } while (ids != null && ids.hasNext() && followersLimit != 0);
                    levelMap.put((level + 1), levelMap.getOrDefault((level + 1), 0) + followersCount);
                    if (levelMap.getOrDefault(level, 0) >= count) {
                        level++;
                        count = 0;
                    }

                }
                friendsLimit = 2; //noOfFriends / 10000;
                friendCursor = -1l;
                /*do {
                    try {
                        ids = twitter.getFriendsIDs(Long.parseLong(userList.get(i)), friendCursor, 5000);
                        friendCursor = ids.getNextCursor();
                        if (ids.getIDs() != null) {
                            for (int j = 0; j < ids.getIDs().length; j++) {
                                String id = String.valueOf(ids.getIDs()[j]);
                                followerEdgeEntityList.add(saveFollowerEdge(userList.get(i), id));
                                if (!followerNodeRepository.exists(id)) {
                                    nodeIdList.add(id);
                                }
                            }
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                        credentialsDTOList.get(credIndex).get(0).setResetTime(e.getRateLimitStatus().getResetTimeInSeconds());
                        Integer min = 0;
                        for (Integer index = 0 ; index <  credentialsDTOList.size(); index++) {
                            if (credentialsDTOList.get(index).get(0).getResetTime() == 0) {
                                min = index;
                                break;
                            }
                            if (credentialsDTOList.get(index).get(0).getResetTime() < credentialsDTOList.get(min).get(0).getResetTime()) {
                                min = index;
                            }
                        }
                        setCredentials(credentialsDTOList.get(min));
                        Long dif = System.currentTimeMillis() - credentialsDTOList.get(min).get(0).getResetTime()*1000;
                        if (dif > 0){
                            try {
                                Thread.sleep(dif);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    friendsLimit--;
                } while (ids != null && ids.hasNext() && friendsLimit != 0);*/
                nodeIdRepository.save(nodeIdList.stream().map(n -> NodeIdEntity.builder().userId(n).build())
                        .collect(Collectors.toList()));

/*
                List<List<Long>> partitionedNode = ListUtils.partition(nodeIdList, 100);

                partitionedNode.stream().forEach(n -> {
                    try {
                        long[] nodeArray = new long[n.size()];
                        for (int a = 0 ; a < nodeArray.length; a++) {
                            nodeArray[a] =  n.get(a);
                        }
                        List<User> users =  twitter.lookupUsers(nodeArray);
                        users.stream().forEach( u -> {
                            followerNodeRepository.save(FollowerNodeEntity.builder().userId(String.valueOf(u.getId()))
                                    .followers(u.getFollowersCount()).friends(u.getFriendsCount()).build());
                        });
                    } catch (TwitterException e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(e.getRateLimitStatus().getSecondsUntilReset() * 1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                });*/
            }
            if (CollectionUtils.isNotEmpty(followerEdgeEntityList)) {
                FollowerGraphEntity followerGraphEntity = FollowerGraphEntity.builder().userId(userEntity.getId())
                        .followerList(followerEdgeEntityList).build();
                followerGraphRepository.save(followerGraphEntity);
                followerNodeRepository.save(FollowerNodeEntity.builder().userId(String.valueOf(userEntity.getId()))
                        .followers(userEntity.getFollowersCount()).friends(userEntity.getFriendsCount())
                        .cursor(followerCursor).build());
            }
        }
    }

    private FollowerEdgeEntity saveFollowerEdge(String follower, String followee) {

        List<FollowerEdgeEntity> followerEdgeEntities = followerEdgeRepository.findByFollowerFollowee(follower, followee);
        if (CollectionUtils.isEmpty(followerEdgeEntities)) {
            FollowerEdgeEntity followerEdgeEntity = FollowerEdgeEntity.builder().follower(follower).followee(followee).build();
            return followerEdgeRepository.save(followerEdgeEntity);
        }
        return followerEdgeEntities.get(0);
    }

    private String saveUser(User u, List<String> retweeters) {

        if (u != null && !userRepository.exists(String.valueOf(u.getId()))) {
            UserEntity user = userFactory.create(u, retweeters);
            return userRepository.save(user).getId();
        }

        return "";
    }

    private String saveUserNode(UserNode u) {
        if (u != null && !userNodeRepository.exists(String.valueOf(u.getId()))) {
            UserNodeEntity user = userNodeFactory.create(u);
            return userNodeRepository.save(user).getId();
        } else if (u != null){
            userNodeRepository.updateRetweetersAndScore(String.valueOf(u.getId()),
                    u.getRetweeters().stream().map(r -> String.valueOf(r.getId())).collect(Collectors.toList()), u.getScore());
            return String.valueOf(u.getId());
        }

        return "";
    }

    private String saveUser(User u) {
        if (u != null && !userRepository.exists(String.valueOf(u.getId()))) {
            UserEntity user = userFactory.create(u);
            return userRepository.save(user).getId();
        }
        return "";
    }

    private String savePlace(Place p) {
        if (p != null && !placeRepository.exists(p.getId())) {
            PlaceEntity place = placeFactory.create(p);
            return placeRepository.save(place).getId();
        }
        return "";
    }

    private String saveHashTag(HashtagEntity hashTag) {
        if (hashTag != null &&
                !hashTagRepository.exists(hashTag.getText())) {
            HashTagEntity hashTagEntity = hashTagFactory.create(hashTag);
            return hashTagRepository.insert(hashTagEntity).getValue();
        }
        return "";

    }


    public List<String> getAndSave(List<String> userIds) {
        List<String> ids = new ArrayList<>();

        Integer secondsUntilReset = 8 * 60;
        for (String userId : userIds) {
            try {
                if (!userRepository.exists(userId)) {
                    User user = twitter.showUser(Long.parseLong(userId));
                    if (user != null) {
                        secondsUntilReset = user.getRateLimitStatus().getSecondsUntilReset();
                        String id = saveUser(user);
                        if (!id.equals("")) {
                            ids.add(id);
                        }
                    }
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(secondsUntilReset * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return ids;
    }
//
//    public List<UserEntity> getUsers() {
//
//        BufferedReader br = null;
//        FileReader fr = null;
//        List<String> screenNames = new ArrayList<>();
//        ClassPathResource classPathResource = new ClassPathResource("users.txt");
//
//        try {
//            fr = new FileReader(classPathResource.getFile());
//            br = new BufferedReader(fr);
//
//
//
//            String sCurrentLine;
//
//            while ((sCurrentLine = br.readLine()) != null) {
//                System.out.println(sCurrentLine);
//                screenNames.add(sCurrentLine);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }  finally {
//        try {
//            if (br != null)
//                br.close();
//            if (fr != null)
//                fr.close();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//    if (CollectionUtils.isNotEmpty(screenNames)) {
//            List<List<String>> partitionedNames = ListUtils.partition(screenNames, 100);
//            for (List<String> namesList : partitionedNames) {
//                String screenNamesStr = namesList.stream().collect(Collectors.joining(","));
//                twitter.lookupUsers(namesList)
//            }
//    }
//
//
//    }


    @Override
    public Integer getTweetCred(String id) {
        TweetCredResponseDto tweetCredResponseDto = tweetCredGateway.getScore(Long.parseLong(id), "9fa8d0c2d880ffb498f83506748f7682");
        if (tweetCredResponseDto != null) {
            return tweetCredResponseDto.getScore();
        }
        return null;
    }

    private void saveFollowersToFile(List<FollowerEdgeEntity> followerEdgeEntityList, String fileName) {
        FileWriter fw = null;
        BufferedWriter writer = null;

        try {
            fw = new FileWriter(fileName + ".csv");
            writer = new BufferedWriter(fw);
            for (FollowerEdgeEntity followerEdgeEntity : followerEdgeEntityList) {

                writer.write(followerEdgeEntity.getFollower()+","+followerEdgeEntity.getFollowee()+"\n");

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
}
