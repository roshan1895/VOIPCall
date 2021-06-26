package com.app.voipcall.utils;

import android.os.Build;
import android.os.Bundle;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import com.cometchat.pro.core.Call;


public class CallConnectionService extends ConnectionService {
      CallConnection conn=null;

    @Override
    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Bundle bundle = request.getExtras();
        String sessionID = bundle.getString("SESSIONID");
        String name = bundle.getString("NAME");
        String receiverType = bundle.getString("RECEIVERTYPE");
        String callType = bundle.getString("CALLTYPE");
        String receiverID = bundle.getString("RECEIVERID");
        Call call = new Call(receiverID, receiverType, callType);
        call.setSessionId(sessionID);
        conn =new CallConnection(getApplicationContext(), call);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        }
        conn.setCallerDisplayName(name,TelecomManager.PRESENTATION_ALLOWED);
        conn.setAddress(request.getAddress(),TelecomManager.PRESENTATION_ALLOWED);
        conn.setInitializing();
        conn.setActive();
        return conn;
    }

    @Override
    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        Bundle bundle = request.getExtras();
        String sessionID = bundle.getString("SESSIONID");
        String name = bundle.getString("NAME");
        String receiverType = bundle.getString("RECEIVERTYPE");
        String callType = bundle.getString("CALLTYPE");
        String receiverID = bundle.getString("RECEIVERID");
        Log.e("onCreateOutgoingConn","${bundle.toString()} \n $sessionID $name $receiverID $receiverType $callType");
        Call call =new  Call(receiverID, receiverType, callType);
        call.setSessionId(sessionID);
        conn = new CallConnection(getApplicationContext(), call);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            conn.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED);
        }
        conn.setCallerDisplayName(name,TelecomManager.PRESENTATION_ALLOWED);
        conn.setAddress(request.getAddress(),TelecomManager.PRESENTATION_ALLOWED);
        conn.setInitializing();
        conn.setActive();
        return conn;
    }

    @Override
    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request);
        Log.e("onCreateIncomingFailed:",request.toString());
        Toast.makeText(getApplicationContext(),"onCreateIncomingConnectionFailed",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request);
        Log.e("onCreateOutgoingFailed:",request.toString());
        Toast.makeText(getApplicationContext(),"onCreateOutgoingConnectionFailed",Toast.LENGTH_LONG).show();

    }
}
