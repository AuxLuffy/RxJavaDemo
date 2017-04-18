package com.example.sunzh.rxjavademo.rest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by sunzh on 2017/4/13.
 */

public interface GitHubService {
    @GET("users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);

    @GET("repos/{owner}/{repo}/contributors")
    List<Contributor> repoContributors(@Path("owner") String owner, @Path("repo") String repo);
}
