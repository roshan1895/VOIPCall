package com.app.voipcall.utils;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telecom.VideoProfile;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;

public class CallHandler {
    Context context;
    PhoneAccountHandle phoneAccountHandle;
    TelecomManager telecomManager;
    public  CallHandler(Context context)
    {
        this.context=context;
        telecomManager=(TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

    }
    void init()
    {
        ComponentName componentName = new ComponentName(context, CallConnectionService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phoneAccountHandle = new PhoneAccountHandle(componentName, "VoIP Calling");
            PhoneAccount phoneAccount = PhoneAccount.builder(phoneAccountHandle, "VoIP Calling")
                    .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build();
            telecomManager.registerPhoneAccount(phoneAccount);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    void startOutgoingCall(Call call) {
        Bundle extras = new Bundle();
        extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true);
        ComponentName componentName = new ComponentName(context, CallConnectionService.class);
        PhoneAccountHandle phoneAccountHandle =new  PhoneAccountHandle(componentName, "estosConnectionServiceId");
        Bundle test = new Bundle();
        User receiver =(User) call.getCallReceiver();
         String number = receiver.getStatusMessage();
         if(number==null||number.isEmpty())
             number = "09999999999";
        extras.putString("NAME",receiver.getName());
        extras.putString("SESSIONID", call.getSessionId());
        extras.putString("RECEIVERTYPE", call.getReceiverType());
        extras.putString("CALLTYPE", call.getType());
        extras.putString("RECEIVERID",receiver.getUid());
        if (call.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP))
            extras.putString(UIKitConstants.IntentStrings.NAME, ((Group)(call.getReceiver())).getName());
        else
        extras.putString(UIKitConstants.IntentStrings.NAME,((User)call.getCallInitiator()).getName());

        test.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
        test.putInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, VideoProfile.STATE_BIDIRECTIONAL);
        test.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, extras);
        try {
            if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS)
                    == PackageManager.PERMISSION_GRANTED) {
                telecomManager.placeCall(Uri.parse("tel:$number"), test);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void startIncomingCall(Call call) {
        if (context.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) ==
                PackageManager.PERMISSION_GRANTED) {
            Bundle extras = new Bundle();
            Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, call.getSessionId().substring(0, 11),
                    null);
            extras.putString("SESSIONID", call.getSessionId());
            extras.putString("RECEIVERTYPE", call.getReceiverType());
            extras.putString("CALLTYPE", call.getType());
            extras.putString("RECEIVERID", call.getReceiverUid());
            if (call.getReceiverType().equalsIgnoreCase(CometChatConstants.RECEIVER_TYPE_GROUP))

                extras.putString(UIKitConstants.IntentStrings.NAME, ((Group)(call.getReceiver())).getName());
            else
            extras.putString(UIKitConstants.IntentStrings.NAME,((User)call.getCallInitiator()).getName());

            if (call.getType().equalsIgnoreCase(CometChatConstants.CALL_TYPE_VIDEO))
                extras.putInt(TelecomManager.EXTRA_INCOMING_VIDEO_STATE, VideoProfile.STATE_BIDIRECTIONAL);
            else
                extras.putInt(TelecomManager.EXTRA_INCOMING_VIDEO_STATE, VideoProfile.STATE_AUDIO_ONLY);

            extras.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri);
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
            extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true);
            boolean isCallPermitted = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              isCallPermitted=  telecomManager.isIncomingCallPermitted(phoneAccountHandle);
            } else {
               isCallPermitted= true;
            }
            try {
                Log.e("startIncomingCall: ",extras.toString()+"\n"+isCallPermitted);
                telecomManager.addNewIncomingCall(phoneAccountHandle, extras);
            } catch (SecurityException e) {
                Intent intent = new Intent();
                intent.setAction(TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS);
//                val componentName = ComponentName("com.android.server.telecom", "com.android.server.telecom.settings.EnableAccountPreferenceActivity")
//                intent.setComponent(componentName)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context,"Error occured:"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e("startIncomingCall: ","Permission not granted");
        }
    }
}
