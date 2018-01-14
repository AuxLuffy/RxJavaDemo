package com.example.sunzh.rxjavademo.rest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by sunzh on 2017/4/13.
 */

public class RestGenerator {
    static String BASEURL = "https://api.github.com/users/basil2style";//baseurl总是以 "/" 结尾，如果是,则取最后一个 “/” 之前的地址作为baseurl

    /**
     * 注意：2.0不再提供默认的converter了。如果不显性的声明一个可用的converter的话，retrofit是会报错的：
     * 提醒你没有可用的converter.因为核心代码已经不再依赖序列化相关的第三方库了，我们依然提供对
     * converter的支持，不过需要自己引入这些依赖，同时显性的声明retrofit需要用的converter的依赖
     *
     * @return
     */
    public static GitHubService generatService() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new MyInterceptor());
        Retrofit retrofit = new Retrofit.Builder()
                //增加返回为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回为Observable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //因为json并没有什么继承上的约束，所以我们无法通过什么确切的条件来判断一个对象是否是json对象，
                // 以至于json的converters会对任何数据都回复说：我可以处理！
                // 这个一定要记住，json converter 一定要放在最后，不然会和你的预期不符
                //增加返回值为Gson的支持（以实体类返回）
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.github.com/")
                .client(builder.build())
                .build();
        return retrofit.create(GitHubService.class);
    }

}
