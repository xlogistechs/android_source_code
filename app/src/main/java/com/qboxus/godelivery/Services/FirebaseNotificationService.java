package com.qboxus.godelivery.Services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.qboxus.godelivery.ActivitiesFragments.MainHome.MainA;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public class FirebaseNotificationService extends FirebaseMessagingService {

    public String title;
    public String type;
    Preferences preferences;


    @SuppressLint("WrongThread")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        preferences=new Preferences(getApplicationContext());
        if (remoteMessage.getData().size() > 0) {

            try {

                JSONObject notiObj=new JSONObject(remoteMessage.getData());
                Log.d(Variables.tag,"notification "+notiObj);
                String orderId="";
                String riderId="";
                String type=notiObj.optString("type");

                Log.d(Variables.tag,"Log "+notiObj.optString("type"));
                if (type.equalsIgnoreCase("rider"))
                {
                    orderId=new JSONObject(""+notiObj.getString("order")).optString("id");

                    Log.d(Variables.tag,"Log "+orderId);



                    try {
                        riderId=new JSONObject(notiObj.getString("rider_order")).optString("id");
                        Log.d(Variables.tag,"Log rider order "+riderId);
                    }
                    catch (Exception e)
                    {
                        Log.d(Variables.tag,"Exception rider_order"+e);
                    }

                    try {
                        riderId=new JSONObject(notiObj.getString("sender")).optString("id");
                        Log.d(Variables.tag,"Log reciver "+riderId);
                    }
                    catch (Exception e)
                    {
                        Log.d(Variables.tag,"Exception receiver"+e);
                    }

                }
                else
                {
                    orderId=notiObj.optString("order_id");

                    Log.d(Variables.tag,"Log "+orderId);
                    Log.d(Variables.tag,"Log "+type);
                }

                title = remoteMessage.getData().get("title");

                Log.d(Variables.tag,"Test "+type);

                if (!(preferences.getKeyIsChatOpen()) || type.equalsIgnoreCase(""))
                showNotification(getApplicationContext(), title,orderId,riderId,notiObj.optString("type"));

            }
            catch (Exception e)
            {
                Log.d(Variables.tag,"error "+e);
            }


        }

    }


    public void showNotification(Context context, String title,String orderId,String riderId,String type){
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationId = 1;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            Intent resultIntent = new Intent(context, MainA.class);
            resultIntent.putExtra("order_id",""+orderId);
            resultIntent.putExtra("rider_id",""+riderId);
            resultIntent.putExtra("type",""+type);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.
                    getActivity(context, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);
            notificationManager.notify(notificationId, mBuilder.build());
        }
        catch (Exception e)
        {
            Log.d(Variables.tag,"Notification Error "+e);
        }
    }

}
