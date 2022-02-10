package com.qboxus.godelivery.ChatModule;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qboxus.godelivery.ChatModule.ViewHolders.Alert_viewholder;
import com.qboxus.godelivery.ChatModule.ViewHolders.ChatAudio_viewholder;
import com.qboxus.godelivery.ChatModule.ViewHolders.ChatImage_viewholder;
import com.qboxus.godelivery.ChatModule.ViewHolders.Chat_viewholder;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.R;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatModel> mDataSet;
    String myID="";
    private static final int myChat = 1;
    private static final int friendChat = 2;
    private static final int myChatImage = 3;
    private static final int otherChatImage = 4;
    private static final int alertMessage = 7;
    private static final int myAudioMessage = 8;
    private static final int otherAudioMessage = 9;

    Context context;
    Integer todayDay = 0;

    private OnItemClickListener listener;
    private ChatAdapter.OnLongClickListener longListener;

    public interface OnItemClickListener {
        void onItemClick(int postion, ChatModel item, View view);
    }

    public interface OnLongClickListener {
        void onLongclick(ChatModel item, View view);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param dataSet Message list
     *                Device id
     */

    ChatAdapter(List<ChatModel> dataSet, String id, Context context, ChatAdapter.OnItemClickListener listener, ChatAdapter.OnLongClickListener long_listener) {
        mDataSet = dataSet;
        this.myID = id;
        this.context = context;
        this.listener = listener;
        this.longListener = long_listener;
        Calendar cal = Calendar.getInstance();
        todayDay = cal.get(Calendar.DAY_OF_MONTH);
    }


    // this is the all types of view that is used in the chat
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View v = null;
        switch (viewtype) {
            // we have 4 type of layout in chat activity text chat of my and other and also
            // image layout of my and other
            case myChat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                Chat_viewholder mychatHolder = new Chat_viewholder(v);
                return mychatHolder;
            case friendChat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                Chat_viewholder friendchatHolder = new Chat_viewholder(v);
                return friendchatHolder;
            case myChatImage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_my, viewGroup, false);
                ChatImage_viewholder mychatimageHolder = new ChatImage_viewholder(v);
                return mychatimageHolder;
            case otherChatImage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_other, viewGroup, false);
                ChatImage_viewholder otherchatimageHolder = new ChatImage_viewholder(v);
                return otherchatimageHolder;

            case myAudioMessage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_my, viewGroup, false);
                ChatAudio_viewholder chatAudioviewholder = new ChatAudio_viewholder(v);
                return chatAudioviewholder;

            case otherAudioMessage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_other, viewGroup, false);
                ChatAudio_viewholder other = new ChatAudio_viewholder(v);
                return other;

            case alertMessage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                Alert_viewholder alertviewholder = new Alert_viewholder(v);
                return alertviewholder;

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatModel chat = mDataSet.get(position);

          if (chat.type.equals("text")) {
            Chat_viewholder chatviewholder = (Chat_viewholder) holder;
            // check if the message is from sender or receiver

            if (chat.sender_id.equals(myID)) {
                if (chat.status.equals("1"))
                    chatviewholder.messageSeen.setText(context.getString(R.string.seen_at)+" " + ChangeDate_to_time(chat.time));
                else
                    chatviewholder.messageSeen.setText(""+context.getString(R.string.sent));
            } else {

                chatviewholder.messageSeen.setText("");
            }

            chatviewholder.message.setText(chat.text);
            chatviewholder.msgDate.setText(Show_Message_Time(chat.timestamp));

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.timestamp.substring(0, 2).equals(chat.timestamp.substring(0, 2))) {
                    chatviewholder.dateTxt.setVisibility(View.GONE);
                } else {
                    chatviewholder.dateTxt.setVisibility(View.VISIBLE);
                    chatviewholder.dateTxt.setText(ChangeDate(chat.timestamp));
                }

            } else {
                chatviewholder.dateTxt.setVisibility(View.VISIBLE);
                chatviewholder.dateTxt.setText(ChangeDate(chat.timestamp));
            }

            chatviewholder.bind(chat, longListener);

        }

         else if (chat.type.equals("image")) {
            final ChatImage_viewholder chatimageholder = (ChatImage_viewholder) holder;
            // check if the message is from sender or receiver
            if (chat.sender_id.equals(myID)) {
                if (chat.status.equals("1"))
                    chatimageholder.messageSeen.setText(context.getString(R.string.seen_at)+" " + ChangeDate_to_time(chat.time));
                else
                    chatimageholder.messageSeen.setText(""+context.getString(R.string.sent));

            } else {
                chatimageholder.messageSeen.setText("");
            }
            chatimageholder.msgDate.setText(Show_Message_Time(chat.timestamp));

            if (chat.pic_url.equals("none")) {
                if (ChatA.uploadingImageId.equals(chat.chat_id)) {
                    chatimageholder.pBar.setVisibility(View.VISIBLE);
                    chatimageholder.messageSeen.setText("");
                } else {
                    chatimageholder.pBar.setVisibility(View.GONE);
                    chatimageholder.notSendMessageIcon.setVisibility(View.VISIBLE);
                    chatimageholder.messageSeen.setText(context.getString(R.string.not_delivered)+" ");
                }
            } else {
                chatimageholder.notSendMessageIcon.setVisibility(View.GONE);
                chatimageholder.pBar.setVisibility(View.GONE);
            }

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.timestamp.substring(0, 2).equals(chat.timestamp.substring(0, 2))) {
                    chatimageholder.dateTxt.setVisibility(View.GONE);
                } else {
                    chatimageholder.dateTxt.setVisibility(View.VISIBLE);
                    chatimageholder.dateTxt.setText(ChangeDate(chat.timestamp));
                }
                Glide.with(context)
                        .load(chat.pic_url)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .override(400, 400)
                        .centerCrop()
                        .into(chatimageholder.chatImage);
            } else {
                chatimageholder.dateTxt.setVisibility(View.VISIBLE);
                chatimageholder.dateTxt.setText(ChangeDate(chat.timestamp));
                Glide.with(context)
                        .load(chat.pic_url)
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .override(400, 400)
                        .centerCrop()
                        .into(chatimageholder.chatImage);
            }

            chatimageholder.bind(position,mDataSet.get(position), listener, longListener);
        }

          else if (chat.type.equals("audio")) {
            final ChatAudio_viewholder chatAudioviewholder = (ChatAudio_viewholder) holder;
            // check if the message is from sender or receiver
            if (chat.sender_id.equals(myID)) {
                if (chat.status.equals("1"))
                    chatAudioviewholder.messageSeen.setText(context.getString(R.string.seen_at)+" "+ChangeDate_to_time(chat.time));
                else
                    chatAudioviewholder.messageSeen.setText(""+context.getString(R.string.sent));

            } else {
                chatAudioviewholder.messageSeen.setText("");
            }


            chatAudioviewholder.msgDate.setText(Show_Message_Time(chat.timestamp));

            if (chat.sender_id.equals(myID) && chat.pic_url.equals("none")) {
                chatAudioviewholder.pBar.setVisibility(View.VISIBLE);
            } else {
                chatAudioviewholder.pBar.setVisibility(View.GONE);
            }

            String downloadid = Variables.downloadSharedPreferences.getString(chat.chat_id, "");
            if (!downloadid.equals("")) {
                String status = Functions.CheckImageStatus(context, Long.parseLong(downloadid));
                if (status.equals("STATUS_FAILED") || status.equals("STATUS_SUCCESSFUL")) {
                    chatAudioviewholder.pBar.setVisibility(View.GONE);
                    Variables.downloadSharedPreferences.edit().remove(chat.chat_id).commit();
                } else {
                    chatAudioviewholder.pBar.setVisibility(View.VISIBLE);
                }
            }

            // make the group of message by date set the gap of 1 min
            // means message send with in 1 min will show as a group
            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.timestamp.substring(0, 2).equals(chat.timestamp.substring(0, 2))) {
                    chatAudioviewholder.dateTxt.setVisibility(View.GONE);
                } else {
                    chatAudioviewholder.dateTxt.setVisibility(View.VISIBLE);
                    chatAudioviewholder.dateTxt.setText(ChangeDate(chat.timestamp));
                }
            }
            else {
                chatAudioviewholder.dateTxt.setVisibility(View.VISIBLE);
                chatAudioviewholder.dateTxt.setText(ChangeDate(chat.timestamp));

            }

            File fullpath = new File(Variables.folderGoDelivery + chat.chat_id + ".mp3");
            if (fullpath.exists()) {
                chatAudioviewholder.totalTime.setText(getfileduration(Uri.parse(fullpath.getAbsolutePath())));
            } else {
                chatAudioviewholder.totalTime.setText(null);
            }


              if (ChatA.playingId.equals(chat.chat_id) && ChatA.mediaPlayer != null) {
                  chatAudioviewholder.playBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause_icon));
                  chatAudioviewholder.seekBar.setProgress(ChatA.mediaPlayerProgress);
              } else {
                  chatAudioviewholder.playBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play_icon));
                  chatAudioviewholder.seekBar.setProgress(0);
              }


            chatAudioviewholder.bind(position,mDataSet.get(position), listener, longListener);

        }

          else if (chat.type.equals("delete")) {
            Alert_viewholder alertviewholder = (Alert_viewholder) holder;
            alertviewholder.message.setTextColor(context.getResources().getColor(R.color.newColorBlack));
            alertviewholder.message.setBackground(context.getResources().getDrawable(R.drawable.d_border_gray_line));

            alertviewholder.message.setText(""+context.getString(R.string.this_message_was_deleted));

            if (position != 0) {
                ChatModel chat2 = mDataSet.get(position - 1);
                if (chat2.timestamp.substring(11, 13).equals(chat.timestamp.substring(11, 13))) {
                    alertviewholder.dateTxt.setVisibility(View.GONE);
                } else {
                    alertviewholder.dateTxt.setVisibility(View.VISIBLE);
                    alertviewholder.dateTxt.setText(ChangeDate(chat.timestamp));
                }

            } else {
                alertviewholder.dateTxt.setVisibility(View.VISIBLE);
                alertviewholder.dateTxt.setText(ChangeDate(chat.timestamp));
            }

        }
    }

    @Override
    public int getItemViewType(int position) {

         if (mDataSet.get(position).type.equals("text")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return myChat;
            }
            return friendChat;
        } else if (mDataSet.get(position).type.equals("image")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return myChatImage;
            }

            return otherChatImage;
        } else if (mDataSet.get(position).type.equals("audio")) {
            if (mDataSet.get(position).sender_id.equals(myID)) {
                return myAudioMessage;
            }
            return otherAudioMessage;
        } else {
            return alertMessage;
        }
    }



    public String ChangeDate(String date) {
        long currenttime = System.currentTimeMillis();

        long databasedate = 0;
        Date d = null;
        try {
            d = Variables.df.parse(date);
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = currenttime - databasedate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if (todayDay == chatday)
                return "Today";
            else if ((todayDay - chatday) == 1)
                return "Yesterday";
        } else if (difference < 172800000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            if ((todayDay - chatday) == 1)
                return "Yesterday";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());

        if (d != null)
            return sdf.format(d);
        else
            return "";
    }


    public String Show_Message_Time(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.getDefault());

        Date d = null;
        try {
            d = Variables.df.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d != null)
            return sdf.format(d);
        else
            return "null";
    }

    public String ChangeDate_to_time(String date) {

        Date d = null;
        try {
            d = Variables.df2.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.getDefault());

        if (d != null)
            return sdf.format(d);
        else
            return "";
    }


      public String getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            long second = (file_duration / 1000) % 60;
            long minute = (file_duration / (1000 * 60)) % 60;

            return String.format("%02d:%02d", minute, second);
        } catch (Exception e) {
            return null;
        }

    }
}
