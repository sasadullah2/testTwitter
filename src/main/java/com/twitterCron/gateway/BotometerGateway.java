package com.twitterCron.gateway;

import com.twitterCron.domain.BotometerRequest;
import com.twitterCron.domain.TweetCredResponseDto;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface BotometerGateway {
    @POST("/2/check_account") //HQPLurUG9WmshUXrKmxMfcCsdKpsp1xQ1ljjsnVnamh1mNF1eS
    Object checkAccount(@Header("X-Mashape-Key") String token, @Body BotometerRequest botometerRequest);
}
