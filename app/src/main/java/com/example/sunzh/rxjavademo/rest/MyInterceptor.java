package com.example.sunzh.rxjavademo.rest;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by sunzh on 2017/4/18.
 */

public class MyInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        Log.i("send", "Sending request: " + request.url() + " on " + chain.connection() + request.headers());
        Log.i("request", "requestbody begin\n" + bodyToString(request) + "\nrequest body end");
        Response response = chain.proceed(request);
        ResponseBody body = response.body();
        String responseString = body.string();
        Response newResponse = response.newBuilder().body(ResponseBody.create(body.contentType(), responseString.getBytes())).build();
        long t2 = System.nanoTime();
        Log.i("receive", "receive response for " + response.request().url() + " in " + (t2 - t1) / 1e6d + "% n" + response.headers());
        Log.i("response", "response body begin\n" + responseString + "\nresponse body end");
        return newResponse;
    }

    private static String bodyToString(Request request) {
        Request copy = request.newBuilder().build();
        Buffer buffer = new Buffer();
        try {
            if (copy.body() != null) {
                copy.body().writeTo(buffer);
            }
            return buffer.readUtf8();
        } catch (IOException e) {
            e.printStackTrace();
            return "did not work";
        }
    }
}
