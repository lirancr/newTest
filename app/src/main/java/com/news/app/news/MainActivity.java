package com.news.app.news;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.news.app.news.entities.Article;

public class MainActivity extends AppCompatActivity implements NewsRecyclerAdapter.OnItemClickListener {

    final String LIST_FRAG_TAG = "list_frag";
    final String LOCATION_FRAG_TAG = "location_frag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.top_frag, new NewsFragment())
                .commit();
    }

    @Override
    public void onItemClick(Article item) {
        getSupportFragmentManager().beginTransaction().replace(R.id.top_frag, NewsItemFragment.newInstance(item))
                .addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}