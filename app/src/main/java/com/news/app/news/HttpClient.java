package com.news.app.news;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.news.app.news.entities.GetNewsResponse;

import org.json.JSONObject;

public class HttpClient {

    private static HttpClient instance;
    private RequestQueue client;

    private String NEWS_API_KEY;

    public static HttpClient getInstance(Context context) {
        if(instance == null) {
            instance = new HttpClient(context);
        }
        return instance;
    }

    private HttpClient(Context context) {
        NEWS_API_KEY = context.getString(R.string.news_api_key);
        client = Volley.newRequestQueue(context);
    }

    //using https://newsapi.org/docs/get-started
    public void fetchNews(Response.Listener<GetNewsResponse> listener, Response.ErrorListener errorListener) {
        client.add(new GsonRequest<>("https://newsapi.org/v2/top-headlines?country=us&apiKey="+NEWS_API_KEY,
                GetNewsResponse.class, null,
                listener,
                errorListener));
    }

}
