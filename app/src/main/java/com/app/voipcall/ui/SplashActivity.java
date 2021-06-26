package com.app.voipcall.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.app.voipcall.utils.CometChatConfig;
import com.app.voipcall.utils.MessagingService;
import com.app.voipcall.R;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.UUID;

public class SplashActivity extends AppCompatActivity {
    String TAG="voip_app";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String UID = null; // Replace with the UID of the user to login
        try {
            UID = createTransactionID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String authKey = CometChatConfig.authKey; // Replace with your App Auth Key

        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(UID, authKey, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.d(TAG, "Login Successful : " + user.toString());
                    String token = new MessagingService().getToken();

                    if (new MessagingService().getToken() == null) {
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }

                                        // Get new FCM registration token
                                        String token = task.getResult();
                                        registerPushNotification(token);
                                        // Log and toast
//                                        String msg = getString(R.string.msg_token_fmt, token);
                                        Log.d(TAG, token);
                                        Toast.makeText(SplashActivity.this, token, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        registerPushNotification(token);
                    }
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));


                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "Login failed with exception: " + e.getMessage());
                }
            });
        } else {
            // User already logged in
        }
    }
    public String createTransactionID() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
    private void registerPushNotification(String token) {
        CometChat.registerTokenForPushNotification(token, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(SplashActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        finish();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }
}