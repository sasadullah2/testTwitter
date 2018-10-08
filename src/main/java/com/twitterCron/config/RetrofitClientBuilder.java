package com.twitterCron.config;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import retrofit.client.OkClient;

import java.util.concurrent.TimeUnit;

public class RetrofitClientBuilder {

    protected OkHttpClient okHttpClient = new OkHttpClient();

    public RetrofitClientBuilder setConnectionTimeout(int connectionTimeout) {
        okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    public OkClient build() {
        return new OkClient(okHttpClient);
    }


    public RetrofitClientBuilder setReadTimeout(int timeout, TimeUnit unit) {
        okHttpClient.setReadTimeout(timeout, unit);
        return this;
    }
}