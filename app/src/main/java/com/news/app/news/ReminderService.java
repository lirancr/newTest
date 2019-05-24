package com.news.app.news;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.news.app.news.entities.Article;
import com.news.app.news.entities.GetNewsResponse;

public class ReminderService extends IntentService implements Response.Listener<GetNewsResponse>, Response.ErrorListener {
    private static final int NOTIF_ID = 1;

    public ReminderService(){
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpClient.getInstance(this).fetchNews(this, this);
//            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            long when = System.currentTimeMillis();         // notification time
//            Notification notification = new Notification(R.drawable.news_icon, "reminder", when);
//            notification.defaults |= Notification.DEFAULT_SOUND;
//            notification.flags |= notification.FLAG_AUTO_CANCEL;
//            Intent notificationIntent = new Intent(this, MainActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT);
//            nm.notify(NOTIF_ID, notification);
    }

    @Override
    public void onResponse(GetNewsResponse response) {
        if(response.articles.length > 0) {
            Article article = response.articles[0];

            NotificationManagerCompat nm = NotificationManagerCompat.from(this);
            long when = System.currentTimeMillis();         // notification time
            //TODO create channel
            Notification.Builder builder;
            if(Build.VERSION.SDK_INT >= 26){
                builder = new Notification.Builder(this, "mychannel");
            } else {
                builder = new Notification.Builder(this);
            }
            builder.setContentTitle(article.title);
            builder.setContentText(article.description);
            builder.setSmallIcon(R.drawable.news_icon);
            builder.setAutoCancel(true);

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.putExtra("article", article);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            nm.notify(NOTIF_ID, builder.build());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("liran","oh no");
    }
}
