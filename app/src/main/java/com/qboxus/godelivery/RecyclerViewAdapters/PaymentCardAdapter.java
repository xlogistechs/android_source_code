package com.qboxus.godelivery.RecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.ModelClasses.CardModel;
import com.qboxus.godelivery.R;

import java.util.List;

public class PaymentCardAdapter extends RecyclerView.Adapter<PaymentCardAdapter.ViewHolder> {

    private List<CardModel> list;
    private AdapterClickListenerCallback clickListener;

    public PaymentCardAdapter(Context context, List<CardModel> list, AdapterClickListenerCallback clickListener) {
        this.list = list;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_card_item_view,null);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(v) ;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardModel model = list.get(position);

        holder.cardNumber.setText("**** **** **** "+model.getCardNumber());
        holder.cardBrand.setText(""+model.getBrand());
        holder.iv.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),getCardDrawable(model.getBrand())));
        holder.bind(position,model,clickListener);
    }

    private int getCardDrawable(String brand) {
        if (brand.equalsIgnoreCase("American Express"))
        {
            return R.drawable.ic_american_express;
        }
        if (brand.equalsIgnoreCase("MasterCard"))
        {
            return R.drawable.ic_master_card;
        }
        if (brand.equalsIgnoreCase("Visa"))
        {
            return R.drawable.ic_visa_card;
        }
        return R.drawable.ic_other_card;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout llMain;
        ImageView iv;
        TextView cardNumber,cardBrand;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llMain = itemView.findViewById(R.id.ll_main);
            iv = itemView.findViewById(R.id.iv_card);
            cardNumber = itemView.findViewById(R.id.tv_card_number);
            cardBrand = itemView.findViewById(R.id.tv_card_type);

        }



        void bind(final int item, final CardModel model, final AdapterClickListenerCallback listener) {

            llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(item,model,view);
                }
            });

            llMain.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.OnItemLongClick(item,model,v);
                    return true;
                }
            });

        }
    }
}
