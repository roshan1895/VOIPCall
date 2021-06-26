package com.app.voipcall.ui;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.app.voipcall.R;
import com.app.voipcall.utils.CometChatConfig;
import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;

public class MainApplication extends Application {
    String TAG="voip_call";
    @Override
    public void onCreate() {
        super.onCreate();

        AppSettings appSettings=new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(CometChatConfig.region).build();

        CometChat.init(this, CometChatConfig.appID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
//                UIKitSettings.setAuthKey(CometChatConfig.authKey);
//                CometChat.setSource("ui-kit","android","java");
                Log.d(TAG, "Initialization completed successfully");
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });
    }
    public void createNotificationChannel()
    {
        String name=getString(R.string.app_name);
        String description="Notification_channel";
        int importance= NotificationManager.IMPORTANCE_HIGH;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            NotificationChannel channel=new NotificationChannel("2",name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
