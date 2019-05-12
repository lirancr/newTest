package com.news.app.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.news.app.news.entities.GetNewsResponse;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Response.Listener<GetNewsResponse>, Response.ErrorListener {

    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_list_frag, container, false);
        this.pullToRefresh = v.findViewById(R.id.pull_to_refresh);
        this.pullToRefresh.setOnRefreshListener(this);

        this.recyclerView = v.findViewById(R.id.recycler);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false));

        this.getNews();

        return v;
    }

    @Override
    public void onRefresh() {
        getNews();
    }

    private void getNews() {
        HttpClient.getInstance(getContext()).fetchNews(this, this);
    }

    @Override
    public void onResponse(GetNewsResponse response) {
        this.pullToRefresh.setRefreshing(false);
        this.recyclerView.setAdapter(new NewsRecyclerAdapter(response.articles));
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        this.pullToRefresh.setRefreshing(false);
        Log.e("News", error.networkResponse.toString());
    }
}
