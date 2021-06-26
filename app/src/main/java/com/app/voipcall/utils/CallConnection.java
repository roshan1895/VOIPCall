package com.app.voipcall.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.util.Log;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_components.calls.call_manager.CometChatCallActivity;
import com.cometchat.pro.uikit.ui_components.calls.call_manager.CometChatStartCallActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;

public class CallConnection extends Connection {
    String TAG = "CallConnection";

    Call call;
    Context context;
    public  CallConnection(Context context, Call call)
    {
       this.context=context;
       this.call=call;
    }

    @Override
    public void onCallAudioStateChanged(CallAudioState state) {
        super.onCallAudioStateChanged(state);
        Log.e(TAG, "onCallAudioStateChange:" + state.toString());

    }

      @Override
      public void onDisconnect() {
        super.onDisconnect();
        destroyConnection();
        Log.e(TAG,"onDisconnect");
        setDisconnected(new DisconnectCause(DisconnectCause.LOCAL, "Missed"));
        if (CometChat.getActiveCall()!=null)
            onDisconnect(CometChat.getActiveCall());
    }

    private void onDisconnect(Call activeCall) {
        Log.e(TAG,"onDisconnect Call: $call");
        CometChat.rejectCall(call.getSessionId(), CometChatConstants.CALL_STATUS_CANCELLED, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                Log.e(TAG, "onSuccess: reject");

            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(context,"Unable to end call due to ${p0?.code}",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onHold() {
        super.onHold();
    }

    @Override
    public void onAnswer(int videoState) {
        super.onAnswer(videoState);
    }

    @Override
    public void onAnswer() {
        if (call.getSessionId() != null) {
            CometChat.acceptCall(call.getSessionId(), new CometChat.CallbackListener<Call>() {
                @Override
                public void onSuccess(Call call) {
                destroyConnection();
                    Intent acceptIntent=new Intent(context, CometChatStartCallActivity.class);
                    acceptIntent.putExtra(UIKitConstants.IntentStrings.SESSION_ID,call.getSessionId());
                    acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(acceptIntent);

                }

                @Override
                public void onError(CometChatException e) {
                    destroyConnection();
                    Toast.makeText(context, "Call cannot be answered due to " + e.getCode(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }
    void destroyConnection() {
        setDisconnected(new DisconnectCause(DisconnectCause.REMOTE, "Rejected"));
        Log.e(TAG, "destroyConnection" );
        super.destroy();
    }
    @Override
    public void onStopDtmfTone() {
        super.onStopDtmfTone();
        Log.e(TAG, "onStopDtmfTone: " );
    }

    @Override
    public void onCallEvent(String event, Bundle extras) {
        super.onCallEvent(event, extras);
    }

    @Override
    public void onShowIncomingCallUi() {
        super.onShowIncomingCallUi();
    }

    @Override
    public void onReject() {
        super.onReject();
    }

    @Override
    public void onAbort() {
        super.onAbort();
        Log.e(TAG,"OnAbort");

    }
    void onOutgoingReject() {
        Log.e(TAG,"onDisconnect");
        destroyConnection();
        setDisconnected(new DisconnectCause(DisconnectCause.REMOTE, "REJECTED"));
    }

    @Override
    public void onReject(int rejectReason) {
        Log.e(TAG, "onReject: $rejectReason");
        super.onReject(rejectReason);
    }

    @Override
    public void onReject(String replyMessage) {
        Log.e(TAG, "onReject: $replyMessage" );
        super.onReject(replyMessage);
    }


}
