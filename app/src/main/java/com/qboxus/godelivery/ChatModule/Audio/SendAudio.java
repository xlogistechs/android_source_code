package com.qboxus.godelivery.ChatModule.Audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qboxus.godelivery.ChatModule.ChatA;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendAudio {


    DatabaseReference rootRef;
    String senderId = "";
    String mReceiverId = "";
    String mOrderId = "";
    String mReceiverName = "";
    String mReceiverPic = "null";
    String token;
    Preferences preferences;
    Context context;

    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private String chatChild ="";
    EditText messageField;


    public SendAudio(Context context, EditText messageField,
                     DatabaseReference rootref, String senderid,
                     String receiverid,String OrderId, String mReceiverName, String mReceiverPic
            , String token) {

        this.context = context;
        preferences=new Preferences(context);
        this.messageField = messageField;
        this.rootRef = rootref;
        this.senderId = senderid;
        this.mReceiverId = receiverid;
        this.mOrderId =OrderId;
        this.mReceiverName = mReceiverName;
        this.mReceiverPic = mReceiverPic;
        this.token = token;
        mFileName = context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.mp3";

        if (OrderId.equalsIgnoreCase("0"))
        {
            chatChild = mReceiverId + "-" + senderid;
        }
        else
        {
            chatChild = mReceiverId + "-" + senderid+"-"+OrderId;
        }

    }


    public void startRecording() {

        try {


            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

            mRecorder = new MediaRecorder();

            if (mRecorder != null)
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            if (mRecorder != null)
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            if (mRecorder != null)
                mRecorder.setOutputFile(mFileName);

            if (mRecorder != null)
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                if (mRecorder != null)
                    mRecorder.prepare();
            } catch (IOException e) {
                Log.e("resp", "prepare() failed");
            }
            if (mRecorder != null)
                mRecorder.start();

        } catch (Exception e) {

        }
    }


    public void stopRecording() {
        try {


            stopTimerWithoutRecoder();
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
               // Runbeep("stop");
                UploadAudio();
            }

        } catch (Exception e) {

        }
    }


    public void UploadAudio() {

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        final StorageReference reference = FirebaseStorage.getInstance().getReference();
        DatabaseReference dref = rootRef.child("chat").child(chatChild).push();
        final String key = dref.getKey();
        ChatA.uploadingAudioId = key;
        final String current_user_ref = "chat" + "/" + chatChild;
        final String chat_user_ref = "chat" + "/" + chatChild;

        HashMap my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", mReceiverId);
        my_dummi_pic_map.put("sender_id", senderId);
        my_dummi_pic_map.put("chat_id", key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type", "audio");
        my_dummi_pic_map.put("pic_url", "none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", preferences.getKeyUserName());
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootRef.updateChildren(dummy_push);

        Uri uri = Uri.fromFile(new File(mFileName));

        reference.child("Audio").child(key + ".mp3").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                reference.child("Audio").child(key + ".mp3").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        ChatA.uploadingAudioId = "none";

                        HashMap message_user_map = new HashMap<>();
                        message_user_map.put("receiver_id", mReceiverId);
                        message_user_map.put("sender_id", senderId);
                        message_user_map.put("chat_id", key);
                        message_user_map.put("text", "");
                        message_user_map.put("type", "audio");
                        message_user_map.put("pic_url", uri.toString());
                        message_user_map.put("status", "0");
                        message_user_map.put("time", "");
                        message_user_map.put("sender_name",preferences.getKeyUserName());
                        message_user_map.put("timestamp", formattedDate);

                        HashMap user_map = new HashMap<>();

                        user_map.put(current_user_ref + "/" + key, message_user_map);
                        user_map.put(chat_user_ref + "/" + key, message_user_map);


                        rootRef.updateChildren(user_map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                SendNotificationFication("Voice message ...","user_customer_chat");
                                SendPushNotification("Send an audio message");
                            }
                        });

                    }
                });
            }
        });

    }

    private void SendNotificationFication(String message, String type) {
        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("sender_id", ""+ senderId);
            sendobj.put("receiver_id", ""+ mReceiverId);
            sendobj.put("title", ""+preferences.getKeyUserName());
            sendobj.put("type", type);
            sendobj.put("message", ""+ message);
            if (!(mOrderId.equals("0")))
            sendobj.put("order_id", ""+ mOrderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequest.CallApi(context, ApiUrl.sendMessageNotification,sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void stop_timer() {

        try {


            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

            messageField.setText(null);

        } catch (Exception e) {

        }
    }


    public void stopTimerWithoutRecoder() {

        try {

            messageField.setText(null);

        } catch (Exception e) {

        }
    }


    public void SendPushNotification(String message) {
        if (!token.equals("null")) {

            Map<String, String> notimap = new HashMap<>();
            notimap.put("name",preferences.getKeyUserName());
            notimap.put("msg", message);
            notimap.put("picture", preferences.getKeyUserImage());
            notimap.put("token", token);
            notimap.put("RidorGroupid", senderId);
            notimap.put("FromWhere", "store");
            rootRef.child("notifications_f").child(preferences.getKeyUserId()).push().setValue(notimap);

        }

    }
}
