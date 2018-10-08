package com.twitterCron.gateway;

import com.twitterCron.domain.TweetCredResponseDto;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

public interface TweetCredGateway {
    @GET("/index.php/credibility/{tweetId}")
    TweetCredResponseDto getScore(@Path("tweetId") Long tweetId,
                                     @Query("token") String token);
}
