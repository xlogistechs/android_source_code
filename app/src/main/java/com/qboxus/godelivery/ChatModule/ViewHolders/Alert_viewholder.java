package com.qboxus.godelivery.ChatModule.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.godelivery.R;


public class Alert_viewholder extends RecyclerView.ViewHolder {


    public TextView message, dateTxt;
    public View view;

    public Alert_viewholder(View itemView) {
        super(itemView);
        view = itemView;
        this.message = view.findViewById(R.id.message);
        this.dateTxt = view.findViewById(R.id.datetxt);
    }

}