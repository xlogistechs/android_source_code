package com.qboxus.godelivery.RecyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.ModelClasses.NearbyLocationModel;
import com.qboxus.godelivery.R;

import java.util.List;

public class NearbyLocationAdapter extends RecyclerView.Adapter<NearbyLocationAdapter.ViewHolder> {

    private List<NearbyLocationModel> list;
    private AdapterClickListenerCallback onitemclick;

    public NearbyLocationAdapter(List<NearbyLocationModel> list, AdapterClickListenerCallback onClickListner) {
        this.list = list;
        this.onitemclick = onClickListner;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.near_by_search_item_view, null);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        NearbyLocationModel model = list.get(i);

        viewHolder.tvLoc.setText(model.getTitle());
        viewHolder.tvLocationDesc.setText(model.getAddress());

        viewHolder.bind(i, model, onitemclick);

    }


    public void updateList(List<NearbyLocationModel> sorted_list) {
        list = sorted_list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLoc, tvLocationDesc;

        ViewHolder(View itemView) {
            super(itemView);

            tvLoc = itemView.findViewById(R.id.tv_loc);

            tvLocationDesc = itemView.findViewById(R.id.tv_location_desc);


        }


        void bind(final int pos, final NearbyLocationModel item, final AdapterClickListenerCallback listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(pos, item, view);
                }
            });

        }
    }
}
