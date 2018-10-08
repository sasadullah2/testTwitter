package com.twitterCron.controllers;

import com.twitterCron.domain.Node;
import com.twitterCron.domain.TweetCredResponseDto;
import com.twitterCron.services.TwitterService1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import twitter4j.Status;

import java.util.List;

@RestController
@RequestMapping("/twitter/")
public class TwitterController {

    @Autowired
    TwitterService1 twitterService1;

    @RequestMapping(value = "search", method = { RequestMethod.GET })
    public @ResponseBody List<Status> search(@RequestParam String term) {
        return twitterService1.search(term);
    }

    @RequestMapping(value = "followers", method = { RequestMethod.GET })
    public String getFollowers() {
        twitterService1.process("");
        return "hello";
    }

    @RequestMapping(value = "score", method = { RequestMethod.GET })
    public TweetCredResponseDto getScore(@RequestParam Long tweetId) {
        return twitterService1.getScore(tweetId);
    }
    @RequestMapping(value = "fg", method = { RequestMethod.GET })
    public void followersGraph() {
        // twitterService1.generateFollowersGraph();
    }


    @CrossOrigin(origins = "*")
    @RequestMapping(value = "retweetGraph", method = { RequestMethod.GET })
    public List<Node> getRetweetGraph(@RequestParam String userId) {

        return twitterService1.getR(userId);
    }







}
