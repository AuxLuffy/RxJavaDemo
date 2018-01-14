package com.example.sunzh.rxjavademo.rest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by sunzh on 2017/4/13.
 */

public interface GitHubService {
    @GET("users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);

    //此种写法待定，不一定能实现
    @GET("repos/{owner}/{repo}/contributors")
    List<Contributor> repoContributors(@Path("owner") String owner, @Path("repo") String repo);

    //若要重新定义接口地址可以使用@url，例如：
    @GET
    Call<List<Repo>> listRepos(@Url String url,
                               @Path("user") String user);
}
