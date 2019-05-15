package com.news.app.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.news.app.news.entities.Article;
import com.news.app.news.entities.GetNewsResponse;
import com.squareup.picasso.Picasso;

public class NewsItemFragment extends Fragment {

    TextView title, author, description;
    ImageView imageView;

    public static NewsItemFragment newInstance(Article article) {

        Bundle args = new Bundle();
        args.putParcelable("article", article);
        NewsItemFragment fragment = new NewsItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NewsItemFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putCharSequence("article-url", url);
        NewsItemFragment fragment = new NewsItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rv_item, container, false);
        title = rootView.findViewById(R.id.title);
        imageView = rootView.findViewById(R.id.image);
        author = rootView.findViewById(R.id.author);
        description = rootView.findViewById(R.id.description);

        Article article = getArguments().getParcelable("article");
        if(article != null) {
            populate(article);
        } else {
           HttpClient.getInstance(container.getContext()).fetchNews(new Response.Listener<GetNewsResponse>() {
               @Override
               public void onResponse(GetNewsResponse response) {
                   for (Article article : response.articles) {
                       if (article.url.equals(getArguments().getString("article-url"))){
                           populate(article);
                           return;
                       }
                   }
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   // do nothing
               }
           });
        }
        return rootView;
    }

    private void populate(Article article) {
        title.setText(article.title);
        author.setText(article.author);
        description.setText(article.description);

        if(TextUtils.isEmpty(article.urlToImage)){
            imageView.setVisibility(View.INVISIBLE);
        } else {
            Picasso.get().load(article.urlToImage).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
