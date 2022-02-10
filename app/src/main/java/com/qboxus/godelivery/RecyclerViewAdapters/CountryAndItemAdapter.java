package com.qboxus.godelivery.RecyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.godelivery.ActivitiesFragments.MainHome.CountryAndGoodsF;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.ModelClasses.GoodsModel;
import com.qboxus.godelivery.R;

import java.util.List;


public class CountryAndItemAdapter extends RecyclerView.Adapter<CountryAndItemAdapter.ViewHolder> {

    private List<GoodsModel> modellist;

    Boolean isFromRegister = true;
    private AdapterClickListenerCallback click;

    public CountryAndItemAdapter(Boolean isFromRegister, List<GoodsModel> modellist,
                                 AdapterClickListenerCallback click) {
        this.isFromRegister = isFromRegister;
        this.modellist = modellist;
        this.click = click;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_country_and_goods_item_view, null);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GoodsModel model = modellist.get(position);

        if (isFromRegister){


            holder.tvListitem.setText(Functions.SetFirstLetterCapital(model.countryName));

            if (model.countryName.equals(CountryAndGoodsF.selectedCountry)){
                holder.ivTick.setVisibility(View.VISIBLE);
            }else {
                holder.ivTick.setVisibility(View.GONE);
            }

        }else {

            holder.tvListitem.setText(Functions.SetFirstLetterCapital(model.tv));
            if (model.tv.equals(CountryAndGoodsF.selectedGoodsType)){
                holder.ivTick.setVisibility(View.VISIBLE);
            }else {
                holder.ivTick.setVisibility(View.GONE);
            }
        }

        holder.bind(position, model, click);
    }



    @Override
    public int getItemCount() {
        return modellist.size();
    }




    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvListitem;
        ImageView ivTick;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivTick = itemView.findViewById(R.id.iv_tick);
            tvListitem = itemView.findViewById(R.id.tv_listitem);

        }


        public void bind(final int item, final GoodsModel model,
                         final AdapterClickListenerCallback listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(item,model,v);
                }

            });


        }

    }


    public void filter(List<GoodsModel> filter_list) {
        this.modellist=filter_list;
        notifyDataSetChanged();
    }
}