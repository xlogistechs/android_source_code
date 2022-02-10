package com.qboxus.godelivery.ChatModule.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.godelivery.ChatModule.ChatAdapter;
import com.qboxus.godelivery.ChatModule.ChatModel;
import com.qboxus.godelivery.R;


public class ChatImage_viewholder extends RecyclerView.ViewHolder {

    public ImageView chatImage;
    public TextView dateTxt, messageSeen, msgDate;
    public ProgressBar pBar;
    public ImageView notSendMessageIcon;
    public View view;


    public ChatImage_viewholder(View itemView) {
        super(itemView);
        view = itemView;

        this.chatImage = view.findViewById(R.id.chatimage);
        this.dateTxt = view.findViewById(R.id.datetxt);
        messageSeen = view.findViewById(R.id.message_seen);
        msgDate = view.findViewById(R.id.msg_date);
        notSendMessageIcon = view.findViewById(R.id.not_send_messsage);
        pBar = view.findViewById(R.id.p_bar);

    }

    public void bind(final int pos,final ChatModel item,
                     final ChatAdapter.OnItemClickListener listener,
                     final ChatAdapter.OnLongClickListener longListener) {

        chatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(pos,item,v);
            }
        });

        chatImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longListener.onLongclick(item,v);
                return false;
            }
        });
    }
}
