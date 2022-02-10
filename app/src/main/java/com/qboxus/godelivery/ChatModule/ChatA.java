package com.qboxus.godelivery.ChatModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qboxus.godelivery.ChatModule.Audio.PlayAudioF;
import com.qboxus.godelivery.ChatModule.Audio.SendAudio;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static android.app.Activity.RESULT_OK;


public class ChatA extends RootFragment {

    DatabaseReference rootRef;
    public String token = "null";
    EditText message;
    public DatabaseReference mChatRefReteriving;
    private DatabaseReference sendTypingIndication;
    public DatabaseReference receiveTypingIndication;
    RecyclerView chatRecyclerView;
    TextView reciverName;
    private List<ChatModel> mChats = new ArrayList<>();
    ChatAdapter mAdapter;
    ProgressBar pBar;
    Query queryGetChat;
    ImageView profileImage;
    public String senderId, receiverId, receiverName, receiverPic, orderId;
    private String chatChild ="";
    public String senderName, riderPic;
    private Context context;
    private View view;
    private RelativeLayout writeLayout;
    private  RecordView recordView;
    private ImageView sendBtn;
    private RecordButton micBtn;
    private SendAudio sendAudio;
    File direct;
    Preferences preferences;

    public static String uploadingImageId = "none";
    public static String uploadingAudioId = "none";

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };


    FragmentClickCallback unreadCallback;
    boolean isUnReadCallback=false;

    public ChatA(FragmentClickCallback unreadCallback, boolean isUnReadCallback) {
        this.unreadCallback = unreadCallback;
        this.isUnReadCallback=isUnReadCallback;
    }

    public ChatA() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_chat, container, false);

        context = getContext();

        preferences=new Preferences(view.getContext());
        preferences.setKeyIsChatOpen(true);
        Bundle bundle = getArguments();
        direct = new File(Variables.folderGoDelivery);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(downloadReceiver, filter);

        // intialize the database refer

        rootRef = FirebaseDatabase.getInstance().getReference();
        message = (EditText) view.findViewById(R.id.msgedittext);
        reciverName = view.findViewById(R.id.reciver_name);
        profileImage = view.findViewById(R.id.profileimage);
        writeLayout =view.findViewById(R.id.write_layout);

        riderPic = preferences.getKeyUserImage();

        if (riderPic == null || (riderPic.equals("") || riderPic.equals("null"))) {
            riderPic = "";
        }

        senderName = preferences.getKeyUserName();

        if (bundle != null) {

            senderId = bundle.getString("senderid");
            receiverId = bundle.getString("Receiverid");
            receiverName = bundle.getString("Receiver_name");
            receiverPic = bundle.getString("Receiver_pic");
            orderId = bundle.getString("Order_id");
            if (orderId.equalsIgnoreCase("0"))
            {
                chatChild = receiverId + "-" + senderId;
            }
            else
            {
                chatChild = receiverId + "-" + senderId +"-"+ orderId;
            }
            Log.d(Variables.tag, "Receiver id : " + receiverId);
            reciverName.setText(""+ receiverName);

        }

        CallApiReadMessage();

        token = "null";
        rootRef.child("Users").child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    token = dataSnapshot.child("token").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        pBar = view.findViewById(R.id.progress_bar);

        chatRecyclerView = (RecyclerView) view.findViewById(R.id.chatlist);
        final LinearLayoutManager layout = new LinearLayoutManager(context);
        layout.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layout);
        chatRecyclerView.setHasFixedSize(false);
        ((SimpleItemAnimator) chatRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        OverScrollDecoratorHelper.setUpOverScroll(chatRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        mAdapter = new ChatAdapter(mChats, senderId, context, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, ChatModel item, View v) {

                if (item.type.equals("image"))
                    OpenfullsizeImage(item);

                if (v.getId() == R.id.audio_bubble) {
                    RelativeLayout mainlayout = (RelativeLayout) v.getParent();
                    File fullpath = new File(Variables.folderGoDelivery + item.chat_id + ".mp3");
                    if (fullpath.exists()) {

                        if(playingId.equals(item.chat_id)){
                            StopPlaying();
                        }
                        else {
                            if (check_ReadStoragepermission())
                            PlayAudio(postion,item);
                        }

                    }
                    else {
                        DownloadAudio((ProgressBar) mainlayout.findViewById(R.id.p_bar), item);
                    }

                }
            }

        }, new ChatAdapter.OnLongClickListener() {
            @Override
            public void onLongclick(ChatModel item, View view) {

                if (senderId.equals(item.sender_id))
                    delete_Message_dialog(item);

            }
        });

        chatRecyclerView.setAdapter(mAdapter);

        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = layout.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutitems == 0 && mChats.size() > 9)) {
                    userScrolled = false;

                    rootRef.child("chat").child(chatChild).orderByChild("chat_id")
                            .endAt(mChats.get(0).chat_id).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<ChatModel> arrayList = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        ChatModel item = snapshot.getValue(ChatModel.class);
                                        arrayList.add(item);
                                    }
                                    for (int i = arrayList.size() - 2; i >= 0; i--) {
                                        mChats.add(0, arrayList.get(i));
                                    }

                                    mAdapter.notifyDataSetChanged();

                                    if (arrayList.size() > 8) {
                                        chatRecyclerView.scrollToPosition(arrayList.size());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });
        sendBtn = view.findViewById(R.id.sendbtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(message.getText().toString())) {
                    SendMessage(message.getText().toString());
                    message.setText(null);
                }
            }
        });

        view.findViewById(R.id.uploadimagebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectfile();
            }
        });

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.HideSoftKeyboard(getActivity());
                getFragmentManager().popBackStack();
            }
        });

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    SendTypingIndicator(false);
                }
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    SendTypingIndicator(true);
                    sendBtn.setVisibility(View.VISIBLE);
                    micBtn.setVisibility(View.GONE);
                } else {
                    sendBtn.setVisibility(View.GONE);
                    micBtn.setVisibility(View.VISIBLE);
                    SendTypingIndicator(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sendAudio = new SendAudio(context, message, rootRef,
                senderId, receiverId, orderId, receiverName, receiverPic, token);

        micBtn = view.findViewById(R.id.mic_btn);
        recordView = (RecordView) view.findViewById(R.id.record_view);
        micBtn.setRecordView(recordView);
        recordView.setSoundEnabled(true);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                writeLayout.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);
                if (check_Recordpermission() && check_writeStoragepermission()) {
                    sendAudio.startRecording();
                }
            }

            @Override
            public void onCancel() {

                sendAudio.stop_timer();
                recordView.setVisibility(View.GONE);
                writeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(long recordTime) {
                sendAudio.stopRecording();
                recordView.setVisibility(View.GONE);
                writeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLessThanSecond() {
                sendAudio.stopTimerWithoutRecoder();
                recordView.setVisibility(View.GONE);
                writeLayout.setVisibility(View.VISIBLE);
            }
        });
        recordView.setSlideToCancelText(getString(R.string.slide_to_cancel));
        micBtn.setListenForRecord(true);
        recordView.setLessThanSecondAllowed(false);


        ReceivetypeIndication();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getFragmentManager().popBackStack();
                    return false;
                }
                return true;
            }
        });


        return view;
    }

    private void CallApiReadMessage() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("order_id", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.CallApi(getContext(), ApiUrl.readNotification, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (resp!=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            Log.d(Variables.tag,"Read Message  Sucessfully");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    ValueEventListener valueEventListener;
    ChildEventListener eventListener;

    @Override
    public void onStart() {
        super.onStart();

        Variables.openedChatId = receiverId;

        mChats.clear();
        mChatRefReteriving = FirebaseDatabase.getInstance().getReference();

        queryGetChat = mChatRefReteriving.child("chat").child(chatChild);

        // this will get all the messages between two users
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    ChatModel model = dataSnapshot.getValue(ChatModel.class);
                    mChats.add(model);
                    mAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(mChats.size() - 1);
                } catch (Exception ex) {
                    Log.e("", ex.getMessage());
                }
                ChangeStatus();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot != null && dataSnapshot.getValue() != null) {

                    try {
                        ChatModel model = dataSnapshot.getValue(ChatModel.class);

                        for (int i = mChats.size() - 1; i >= 0; i--) {
                            if (mChats.get(i).timestamp.equals(dataSnapshot.child("timestamp").getValue())) {
                                mChats.remove(i);
                                mChats.add(i, model);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e("", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", databaseError.getMessage());
            }
        };
        // this will check the two user are do chat before or not
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatChild)) {
                    pBar.setVisibility(View.GONE);
                    queryGetChat.removeEventListener(valueEventListener);
                } else {
                    pBar.setVisibility(View.GONE);
                    queryGetChat.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        queryGetChat.limitToLast(20).addChildEventListener(eventListener);
        mChatRefReteriving.child("chat").addValueEventListener(valueEventListener);
    }


    // this method will change the status to ensure that
    // user is seen all the message or not (in both chat node and Chatinbox node)
    public void ChangeStatus() {
        final Date c = Calendar.getInstance().getTime();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Query query1 = reference.child("chat").child(chatChild).orderByChild("status").equalTo("0");
        final Query query2 = reference.child("chat").child(chatChild).orderByChild("status").equalTo("0");


        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderId)) {
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", Variables.df2.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if (!nodeDataSnapshot.child("sender_id").getValue().equals(senderId)) {
                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time", Variables.df2.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    // this will add the new message in chat node and update the ChatInbox by new message by present date
    public void SendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        final String current_user_ref = "chat" + "/" + chatChild;
        final String chat_user_ref = "chat" + "/" + chatChild;

        DatabaseReference reference = rootRef.child("chat").child(chatChild).push();
        final String pushid = reference.getKey();
        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", receiverId);
        message_user_map.put("sender_id", senderId);
        message_user_map.put("chat_id", pushid);
        message_user_map.put("text", message);
        message_user_map.put("type", "text");
        message_user_map.put("pic_url", "");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", senderName);
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);
        user_map.put(chat_user_ref + "/" + pushid, message_user_map);

        rootRef.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                //if first message then set the visibility of whoops layout gone

                SendNotificationFication(message,"user_customer_chat");

            }
        });
    }

    private void SendNotificationFication(String message, String type) {
        JSONObject sendobj = new JSONObject();

        try {

            sendobj.put("sender_id", ""+ senderId);
            sendobj.put("receiver_id", ""+ receiverId);
            sendobj.put("title", ""+preferences.getKeyUserName());
            sendobj.put("type", type);
            sendobj.put("message", ""+ message);
            if (!(orderId.equals("0")))
                sendobj.put("order_id", ""+ orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ApiRequest.CallApi(view.getContext(), ApiUrl.sendMessageNotification,sendobj, new Callback() {
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

    // this method will upload the image in chhat
    public void UploadImage(ByteArrayOutputStream byteArrayOutputStream) {
        byte[] data = byteArrayOutputStream.toByteArray();

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Variables.df.format(c);

        final StorageReference reference = FirebaseStorage.getInstance().getReference();

        DatabaseReference dref = rootRef.child("chat").child(chatChild).push();
        final String key = dref.getKey();
        uploadingImageId = key;

        final String current_user_ref = "chat" + "/" + chatChild;
        final String chat_user_ref = "chat" + "/" + chatChild;

        HashMap my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", receiverId);
        my_dummi_pic_map.put("sender_id", senderId);
        my_dummi_pic_map.put("chat_id", key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type", "image");
        my_dummi_pic_map.put("pic_url", "none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", senderName);
        my_dummi_pic_map.put("timestamp", formattedDate);
        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootRef.updateChildren(dummy_push);

        reference.child("images").child(key + ".jpg").putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                reference.child("images").child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        uploadingImageId = "none";

                        HashMap message_user_map = new HashMap<>();
                        message_user_map.put("receiver_id", receiverId);
                        message_user_map.put("sender_id", senderId);
                        message_user_map.put("chat_id", key);
                        message_user_map.put("text", "");
                        message_user_map.put("type", "image");
                        message_user_map.put("pic_url", uri.toString());
                        message_user_map.put("status", "0");
                        message_user_map.put("time", "");
                        message_user_map.put("sender_name", senderName);
                        message_user_map.put("timestamp", formattedDate);

                        HashMap user_map = new HashMap<>();

                        user_map.put(current_user_ref + "/" + key, message_user_map);
                        user_map.put(chat_user_ref + "/" + key, message_user_map);

                        rootRef.updateChildren(user_map, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                SendNotificationFication("Image receive ...","user_customer_chat");
                            }
                        });

                    }
                });

            }
        });
    }


    // this is the delete message diloge which will show after long press in chat message
    private void delete_Message_dialog(final ChatModel chat_getSet) {
        final CharSequence[] options;
        if (chat_getSet.type.equals("text")) {
            options = new CharSequence[]{ChatA.this.getString(R.string.copy), ChatA.this.getString(R.string.delete_this_message), ChatA.this.getString(R.string.cancel) };
        } else {

            options = new CharSequence[]{ChatA.this.getString(R.string.delete_this_message), ChatA.this.getString(R.string.cancel)};
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogStyle);

        builder.setTitle(null);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(ChatA.this.getString(R.string.delete_this_message))) {
                    update_message(chat_getSet);
                } else if (options[item].equals(ChatA.this.getString(R.string.cancel))) {
                    dialog.dismiss();
                } else if (options[item].equals(ChatA.this.getString(R.string.copy))) {

                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", chat_getSet.text);
                    clipboard.setPrimaryClip(clip);
                }
            }
        });
        builder.show();
    }


    // we will update the privious message means we will tells the other user that we have seen your message
    public void update_message(ChatModel item) {
        final String current_user_ref = "chat" + "/" + chatChild;
        final String chat_user_ref = "chat" + "/" + chatChild;


        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", item.receiver_id);
        message_user_map.put("sender_id", item.sender_id);
        message_user_map.put("chat_id", item.chat_id);
        message_user_map.put("text", "Delete this message");
        message_user_map.put("type", "delete");
        message_user_map.put("pic_url", "");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", senderName);
        message_user_map.put("timestamp", item.timestamp);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + item.chat_id, message_user_map);
        user_map.put(chat_user_ref + "/" + item.chat_id, message_user_map);

        rootRef.updateChildren(user_map);

    }


    // this method will show the dialog of select the either take a picture form camera or pick the image from gallary
    private void selectfile() {

        final CharSequence[] options = {ChatA.this.getString(R.string.take_photo), ChatA.this.getString(R.string.choose_photo_from_gallery), ChatA.this.getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogStyle);

        builder.setTitle("Select");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(ChatA.this.getString(R.string.take_photo))) {
                    if (check_camrapermission())
                        openCameraIntent();
                } else if (options[item].equals(ChatA.this.getString(R.string.choose_photo_from_gallery))) {
                    if (check_ReadStoragepermission()) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                } else if (options[item].equals(ChatA.this.getString(R.string.cancel))) {

                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    // below tis the four types of permission
    //get the permission to record audio
    public boolean check_Recordpermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {

            sendAudio.stopTimerWithoutRecoder();
            recordView.setVisibility(View.GONE);
            writeLayout.setVisibility(View.VISIBLE);
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    Variables.permissionRecordingAudio);
        }
        return false;
    }

    private boolean check_camrapermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA}, Variables.permissionCameraCode);
        }
        return false;
    }

    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Variables.permissionReadData);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    private boolean check_writeStoragepermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Variables.permissionWriteData);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Variables.permissionCameraCode) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.tap_again));

            } else {
                Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.camera_denied));
            }
        }

        if (requestCode == Variables.permissionReadData) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.tap_again));
            }
        }

        if (requestCode == Variables.permissionWriteData) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.tap_again));
            }
        }


        if (requestCode == Variables.permissionRecordingAudio) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.tap_again));
            }
        }
    }

    // below three method is related with taking the picture from camera
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, 1);
            }
        }
    }

    String imageFilePath;

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = ChatA.this.getString(R.string.not_found);
        }
        return result;
    }

    //on image select activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                Matrix matrix = new Matrix();
                try {
                    ExifInterface exif = new ExifInterface(imageFilePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

                InputStream imageStream = null;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

                String path = getPath(selectedImage);
                Matrix matrix = new Matrix();
                ExifInterface exif = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    try {
                        exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                UploadImage(baos);
            }
        }
    }

    // send the type indicator if the user is typing message
    public void SendTypingIndicator(boolean indicate) {
        // if the type incator is present then we remove it if not then we create the typing indicator
        if (indicate) {
            final HashMap message_user_map = new HashMap<>();
            message_user_map.put("receiver_id", receiverId);
            message_user_map.put("sender_id", senderId);

            sendTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
            sendTypingIndication.child(chatChild).setValue(message_user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendTypingIndication.child(chatChild).setValue(message_user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }
            });
        }

        else {
            sendTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");

            sendTypingIndication.child(chatChild).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    sendTypingIndication.child(chatChild).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });

                }
            });
        }
    }

    // receive the type indication to show that your friend is typing or not
    LinearLayout mainlayout;

    public void ReceivetypeIndication() {
        mainlayout = view.findViewById(R.id.typeindicator);

        receiveTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
        receiveTypingIndication.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(chatChild).exists()) {
                    String receiver = String.valueOf(dataSnapshot.child(chatChild).child("sender_id").getValue());
                    if (receiver.equals(receiverId)) {
                        mainlayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    mainlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // on destory delete the typing indicator
    @Override
    public void onDestroy() {
        preferences.setKeyIsChatOpen(false);
        uploadingImageId = "none";
        Variables.openedChatId = "null";
        SendTypingIndicator(false);
        queryGetChat.removeEventListener(eventListener);
        if (isUnReadCallback)
        {
            Bundle unreadcallback=new Bundle();
            unreadcallback.putBoolean("isRead",true);
            unreadCallback.OnItemClick(0,unreadcallback);
        }
        super.onDestroy();
    }

    // on stop delete the typing indicator and remove the value event listener
    @Override
    public void onStop() {
        super.onStop();
        SendTypingIndicator(false);
        queryGetChat.removeEventListener(eventListener);
    }


    //this method will get the big size of image in private chat
    public void OpenfullsizeImage(ChatModel item) {
        SeeFullImageF see_image_f = new SeeFullImageF();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle args = new Bundle();
        args.putSerializable("image_url", item.pic_url);
        args.putSerializable("chat_id", item.pic_url);
        see_image_f.setArguments(args);
        transaction.addToBackStack(null);

        transaction.replace(R.id.Chat_F, see_image_f).commit();
    }


    //this method will get the big size of image in private chat
    public void OpenAudio(String path) {

        PlayAudioF play_audio_f = new PlayAudioF();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("path", path);
        play_audio_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Chat_F, play_audio_f).commit();

    }


    public static MediaPlayer mediaPlayer;
    public static String playingId ="none";
    public static int mediaPlayerProgress =0;
    public int audioPostion;
    public void PlayAudio(int postion, ChatModel item){

        audioPostion =postion;
        mediaPlayerProgress =0;

        StopPlaying();

        File fullpath = new File(Variables.folderGoDelivery + item.chat_id + ".mp3");
        if (fullpath.exists()) {
            Uri uri= Uri.parse(fullpath.getAbsolutePath());

            mediaPlayer = MediaPlayer.create(context, uri);

            if(mediaPlayer !=null){
                mediaPlayer.start();
                CountdownTimer(true);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        StopPlaying();
                    }
                });
                playingId = item.chat_id;
                mAdapter.notifyDataSetChanged();
            }

        }
    }

    public void StopPlaying(){
        playingId ="none";
        CountdownTimer(false);
        mAdapter.notifyDataSetChanged();
        if(mediaPlayer !=null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer =null;
        }
    }


    CountDownTimer countDownTimer;
    public void CountdownTimer(boolean starttimer){

        if(countDownTimer!=null)
            countDownTimer.cancel();


            if (starttimer) {
                countDownTimer = new CountDownTimer(mediaPlayer.getDuration(), 300) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        mediaPlayerProgress = ((mediaPlayer.getCurrentPosition() *100) / mediaPlayer.getDuration());
                        if (mediaPlayerProgress > 95) {
                            CountdownTimer(false);
                            mediaPlayerProgress =0;
                        }
                        mAdapter.notifyItemChanged(audioPostion);
                    }

                    @Override
                    public void onFinish() {
                        mediaPlayerProgress =0;
                        CountdownTimer(false);
                        mAdapter.notifyItemChanged(audioPostion);
                    }
                };
                countDownTimer.start();


        }

    }

    public void DownloadAudio(final ProgressBar pBar, ChatModel item) {
        pBar.setVisibility(View.VISIBLE);
        PRDownloader.download(item.pic_url, direct.getPath(), item.chat_id + ".mp3")
                .build()
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        pBar.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Error error) {

                    }

                });
    }

}
