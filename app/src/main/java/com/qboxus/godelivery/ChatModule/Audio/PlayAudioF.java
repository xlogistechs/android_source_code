package com.qboxus.godelivery.ChatModule.Audio;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.R;

import nl.changer.audiowife.AudioWife;


public class PlayAudioF extends RootFragment {

    View view;
    Context context;
    ImageButton playBtn, pauseBtn;
    SeekBar seekBar;
    TextView durationTime, totalTime;
    AudioWife audioWife;
    ImageView closeBtn;

    public PlayAudioF() {
        // Required empty public constructor
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.play_audio_f, container, false);

        context=getContext();
        closeBtn =view.findViewById(R.id.close_btn);
        playBtn =(ImageButton) view.findViewById(R.id.play_btn);
        pauseBtn =(ImageButton)  view.findViewById(R.id.pause_btn);
        seekBar=(SeekBar) view.findViewById(R.id.seek_bar);
        durationTime =(TextView)view.findViewById(R.id.duration_time);
        totalTime =(TextView)view.findViewById(R.id.total_time);
        String filepath=getArguments().getString("path");
        Uri uri= Uri.parse(filepath);

        // this is the third partty library that will show get the audio player view
        // and run the audio file and handle the player view itself

        audioWife= AudioWife.getInstance();
        audioWife.init(context, uri)
                .setPlayView(playBtn)
                .setPauseView(pauseBtn)
                .setSeekBar(seekBar)
                .setRuntimeView(durationTime)
                .setTotalTimeView(totalTime);

        audioWife.play();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        audioWife.pause();
        audioWife.release();
    }
}
