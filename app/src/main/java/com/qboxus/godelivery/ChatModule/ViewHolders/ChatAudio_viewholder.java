package com.qboxus.godelivery.ChatModule.ViewHolders;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.godelivery.ChatModule.ChatAdapter;
import com.qboxus.godelivery.ChatModule.ChatModel;
import com.qboxus.godelivery.R;


public  class ChatAudio_viewholder extends RecyclerView.ViewHolder{

    public TextView dateTxt, messageSeen, totalTime, msgDate;

    public ProgressBar pBar;
    public SeekBar seekBar;
    private LinearLayout audioBubble;
    public ImageView playBtn;
    private View view;

    public ChatAudio_viewholder(View itemView) {
        super(itemView);
        view = itemView;

        audioBubble =view.findViewById(R.id.audio_bubble);
        dateTxt =view.findViewById(R.id.datetxt);
        messageSeen =view.findViewById(R.id.message_seen);
        pBar =view.findViewById(R.id.p_bar);
        msgDate =view.findViewById(R.id.msg_date);
        playBtn = view.findViewById(R.id.play_btn);
        this.seekBar=(SeekBar) view.findViewById(R.id.seek_bar);
        this.totalTime =(TextView)view.findViewById(R.id.total_time);

    }





    @SuppressLint("ClickableViewAccessibility")
    public void bind(final int pos, final ChatModel item,
                     final ChatAdapter.OnItemClickListener listener,
                     final ChatAdapter.OnLongClickListener longListener) {

        audioBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(pos,item,v);
            }
        });

        audioBubble.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longListener.onLongclick(item,v);
                return false;
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }




}

