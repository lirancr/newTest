package com.news.app.news;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

        if(getIntent().getParcelableExtra("article") != null) {
            onItemClick((Article) getIntent().getParcelableExtra("article"));
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.one_min) {
            startAlarm(1);
        } else if (item.getItemId() == R.id.five_min) {
            startAlarm(5);
        } else if (item.getItemId() == R.id.ten_min) {
            startAlarm(10);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startAlarm(int minutes) {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        long when = System.currentTimeMillis() + minutes * 60000;         // notification time
        Intent intent = new Intent(this, ReminderService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, when, minutes * 60000, pendingIntent);
    }


}

