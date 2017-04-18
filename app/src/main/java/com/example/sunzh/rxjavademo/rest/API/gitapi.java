package com.example.sunzh.rxjavademo.rest.API;

import com.example.sunzh.rxjavademo.rest.model.gitmodel;

import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by sunzh on 2017/4/13.
 */

public interface gitapi {
    @GET("/users/{user}")
    public void getFeed(@Path("user") String user, Callback<gitmodel> response);
}
