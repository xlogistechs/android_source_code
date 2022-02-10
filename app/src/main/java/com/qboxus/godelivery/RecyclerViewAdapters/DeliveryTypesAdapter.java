package com.qboxus.godelivery.RecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.qboxus.godelivery.ModelClasses.DeliveryTypesModel;
import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.R;

import java.util.List;

public class DeliveryTypesAdapter extends RecyclerView.Adapter<DeliveryTypesAdapter.ViewHolder> {

    private Context context;
    private List<DeliveryTypesModel> model;

    private AdapterClickListenerCallback itemClick;

    public DeliveryTypesAdapter(Context context, List<DeliveryTypesModel> model, AdapterClickListenerCallback click) {
        this.context = context;
        this.model = model;
        this.itemClick = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.delivery_type_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryTypesModel model = this.model.get(position);

        holder.tvVehicleName.setText(model.getVehicleName());
        holder.tvVehicalInfo.setText(model.getVehicleDesc());

        Glide.with(holder.itemView.getContext())
                .load(model.getVehicleImage())
                .placeholder(R.drawable.ic_delivery_car)
                .error(R.drawable.ic_delivery_car)
                .into(holder.imgVehivle);

        holder.bind(position, model, itemClick);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvVehicleName, tvVehicalInfo, tvTimeOrEstimatedFare;
        View v;
        ImageView imgVehivle;
        RelativeLayout rl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVehicleName = (TextView) itemView.findViewById(R.id.tv_vehicle_name);
            tvVehicalInfo = (TextView) itemView.findViewById(R.id.tv_vehical_info);
            imgVehivle = (ImageView) itemView.findViewById(R.id.iv_vehicle);
            rl = (RelativeLayout) itemView.findViewById(R.id.main_rl_id);


            v = (View) itemView.findViewById(R.id.view_id);

        }

        void bind(final int pos, final DeliveryTypesModel item, final AdapterClickListenerCallback onClickListner){
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListner.OnItemClick(pos,item,view);
                }
            });
        }
    }
}
