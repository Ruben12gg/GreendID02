package com.example.greenid_esmad;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class GLOBALS extends Application {

    private String userIdGlobal;

    public String getUserIdGlobal() {
        return userIdGlobal;
    }

    public void setUserIdGlobal(String userIdGlobal) {
        this.userIdGlobal = userIdGlobal;
    }

    public static final String CHANNEL_1_ID = "generalNotifs";

    @Override
    public void onCreate() {
        super.onCreate();

        CreateNotifsChannel();

    }

    private void CreateNotifsChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "General Interactions",
                    NotificationManager.IMPORTANCE_HIGH


            );

            notificationChannel.setDescription("Used to show general notifications such as likes.");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }




}
