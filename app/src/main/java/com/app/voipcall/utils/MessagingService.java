package com.app.voipcall.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.voipcall.R;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.cometchat.pro.uikit.ui_resources.utils.Utils.getBitmapFromURL;

public class MessagingService extends FirebaseMessagingService {
    int count = 0;
    String token = null;
    int MESSAGE_REQUEST = 80;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
      Map<String,String> payload =  remoteMessage.getData();
        BaseMessage message = null;
        try {
            message = CometChatHelper.processMessage(new JSONObject(payload.get("message")));
            String title=payload.get("title");
            String alert=payload.get("alert");
            Log.e("onMessageReceived: ", payload + " \n " + message + " \n " + title + ' ' + alert);
            if (message instanceof Call) {
                initiateCallService((Call) message);
            } else {
                count++;
                showMessageNotification(message, title, alert);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    void initiateCallService(Call call)
    {
        try
        {

        }
        catch (Exception e)
        {

        }
    }

    void  showMessageNotification(BaseMessage baseMessage,String title,String alert)
    {
        Intent messageIntent = new Intent(getApplicationContext(), CometChatMessageListActivity.class);
        messageIntent.putExtra(UIKitConstants.IntentStrings.TYPE, baseMessage.getReceiverType());
        if (baseMessage.getReceiverType() == CometChatConstants.RECEIVER_TYPE_USER) {
            messageIntent.putExtra(UIKitConstants.IntentStrings.NAME, baseMessage.getSender().getName());
            messageIntent.putExtra(UIKitConstants.IntentStrings.UID, baseMessage.getSender().getUid());
            messageIntent.putExtra(UIKitConstants.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
            messageIntent.putExtra(UIKitConstants.IntentStrings.STATUS, baseMessage.getSender().getStatus());
        }
        else if (baseMessage.getReceiverType() == CometChatConstants.RECEIVER_TYPE_GROUP) {
            messageIntent.putExtra(UIKitConstants.IntentStrings.GUID, ((Group)baseMessage.getReceiver()).getGuid());
            messageIntent.putExtra(UIKitConstants.IntentStrings.NAME,((Group)baseMessage.getReceiver()).getName());
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_DESC,((Group)baseMessage.getReceiver()).getDescription());
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_TYPE, ((Group)baseMessage.getReceiver()).getGroupType());
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER, ((Group)baseMessage.getReceiver()).getOwner());
            messageIntent.putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT, ((Group)baseMessage.getReceiver()).getMembersCount());
        }
        PendingIntent messagePendingIntent = PendingIntent.getActivity(getApplicationContext(),
                MESSAGE_REQUEST, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Drawable logoDrawable = getApplicationContext().getResources().getDrawable(R.mipmap.ic_launcher,getResources().newTheme());
        Bitmap bmpLogo = Bitmap.createBitmap(logoDrawable.getIntrinsicWidth(),logoDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpLogo);
        logoDrawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        logoDrawable.draw(canvas);
        Bitmap largeIcon;
        if(baseMessage.getSender().getAvatar()!=null&&!baseMessage.getSender().getAvatar().equalsIgnoreCase("null"))
        {
            largeIcon=getBitmapFromURL(baseMessage.getSender().getAvatar());
        }
        else
        {
            largeIcon=bmpLogo;
        }

        NotificationCompat.Builder builder =new NotificationCompat.Builder(this, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setLargeIcon(largeIcon)
                .setGroup("GROUP_ID")
                .setContentIntent(messagePendingIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        if (baseMessage.getType() == CometChatConstants.MESSAGE_TYPE_IMAGE) {
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(getBitmapFromURL(((MediaMessage)baseMessage).getAttachment().getFileUrl())));
        }
        NotificationCompat.Builder summaryBuilder =new  NotificationCompat.Builder(this, "2")
                .setContentTitle(getString(R.string.app_name))
                .setContentText("$count messages")
                .setSmallIcon(R.drawable.cc)
                .setGroup("GROUP_ID")
                .setGroupSummary(true);
        NotificationManagerCompat notificationManager =  NotificationManagerCompat.from((Context)this);
        notificationManager.notify(baseMessage.getId(), builder.build());
        notificationManager.notify(0, summaryBuilder.build());
    }
    public final int getCount() {
        return this.count;
    }

    public final void setCount(int var1) {
        this.count = var1;
    }

    @Nullable
    public final String getToken() {
        return this.token;
    }

    public final void setToken(@Nullable String var1) {
        this.token = var1;
    }

    public final int getMESSAGE_REQUEST() {
        return this.MESSAGE_REQUEST;
    }

    public final void setMESSAGE_REQUEST(int var1) {
        this.MESSAGE_REQUEST = var1;
    }

}
