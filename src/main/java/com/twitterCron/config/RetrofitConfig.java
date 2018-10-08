package com.twitterCron.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.twitterCron.gateway.BotometerGateway;
import com.twitterCron.gateway.DoesFollowGateway;
import com.twitterCron.gateway.TweetCredGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;

/**
 * Created on 5/5/2016.
 */
@Configuration
public class RetrofitConfig {

    @Bean
    public TweetCredGateway tweetCredGateway() {
        RestAdapter build = restAdapterBuilder("http://precog.iiitd.edu.in/tools/spzf/twitdigestserver/twit-digest-plugin/api-v2");
        return build.create(TweetCredGateway.class);
    }

    @Bean
    public DoesFollowGateway doesFollowGateway() {
        RestAdapter build = restAdapterBuilder("https://doesfollow.com");
        return build.create(DoesFollowGateway.class);
    }

    @Bean
    public BotometerGateway botometerGateway() {
        RestAdapter build = restAdapterBuilder("https://osome-botometer.p.mashape.com");
        return build.create(BotometerGateway.class);
    }

    @Bean
    public JacksonConverter jsonConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        JacksonConverter converter = new JacksonConverter(mapper);
        return converter;
    }


    private RestAdapter restAdapterBuilder(String endpoint) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setClient(initHttpClient())
                .setConverter(jsonConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL);
        return builder.build();
    }

    private OkClient initHttpClient() {
        RetrofitClientBuilder retrofitClientBuilder = new RetrofitClientBuilder();

        return retrofitClientBuilder.build();
    }
}
