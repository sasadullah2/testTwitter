package com.twitterCron.gateway;

import com.twitterCron.domain.BotometerRequest;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

public interface DoesFollowGateway {
    @GET("/{follower}/{followee}") //HQPLurUG9WmshUXrKmxMfcCsdKpsp1xQ1ljjsnVnamh1mNF1eS
    String doesFollow(@Path("follower") String follower, @Path("followee") String followee);
}
