package com.example.furkan.sonproje;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.MODE_PRIVATE;

public class Alarm extends BroadcastReceiver {
    SQLiteDatabase db;
    @Override
    public void onReceive(Context context, Intent intent) {
        int notID = MainActivity.alertID;
        db = context.openOrCreateDatabase("notDB", MODE_PRIVATE, null);
        try{
            Cursor cursor = db.rawQuery("Select * from kuyruk order by kalan limit 1", null);
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                    builder.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.alarm)
                            .setContentTitle(cursor.getString(1))
                            .setContentText(cursor.getString(2))
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1,builder.build());
                    String sql = String.format("Delete from kuyruk where id = " + Integer.parseInt(cursor.getString(0)), null);
                    db.execSQL(sql);
                } while (cursor.moveToNext());
            }
        }catch(Exception ex){

        }

    }
}