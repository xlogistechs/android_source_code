package com.qboxus.godelivery.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.godelivery.HelpingClasses.Variables;

import org.json.JSONObject;

public class FirebaseNotificationReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context=context;
        Bundle extras = intent.getExtras();
        try {

            if (extras!=null) {
                String type=extras.getString("type");

                if(type!=null){
                    Intent intent1=new Intent();
                    intent1.setAction("request_responce");
                    intent1.putExtras(intent.getExtras());
                    context.sendBroadcast(intent1);
                }
            }
        }
        catch (Exception e)
        {
            Log.d(Variables.tag,"broadcast notification "+e);
        }
    }

}





