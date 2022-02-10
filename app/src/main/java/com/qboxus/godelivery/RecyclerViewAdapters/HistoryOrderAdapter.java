package com.qboxus.godelivery.RecyclerViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.LoadingViewHolder;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.ModelClasses.OrderModel;
import com.qboxus.godelivery.R;

import java.util.List;

public class HistoryOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<OrderModel> list;
    private AdapterClickListenerCallback click;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public HistoryOrderAdapter(List<OrderModel> list, AdapterClickListenerCallback click) {
        this.list = list;
        this.click=click;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_history_order_item_view, null);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {

            populateItemRows((ViewHolder) holder, position);

        }
        else
        if (holder instanceof LoadingViewHolder) {
            // do nothing
        }


    }

    private void populateItemRows(ViewHolder holder, int position) {
        OrderModel model =  list.get(position);


        if (model.getDropoffTime().equalsIgnoreCase("0000-00-00 00:00:00"))
        {
            holder.tvDropoffTime.setVisibility(View.GONE);
        }
        else
        {
            holder.tvDropoffTime.setVisibility(View.VISIBLE);
        }

        if (model.getOrderStatus().equals("0"))
        {
            holder.tvEstimatedTime.setText(R.string.pending);
            holder.tabStatusType.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ractengle_round_solid_primary));
        }
        else
        if (model.getOrderStatus().equals("1"))
        {
            holder.tvEstimatedTime.setText(R.string.active);
            holder.tabStatusType.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ractengle_round_solid_primary));
        }
        else
        if (model.getOrderStatus().equals("2"))
        {
            holder.tvEstimatedTime.setText(R.string.completed);
            holder.tabStatusType.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ractengle_round_solid_green));
        }
        else
        {
            holder.tvEstimatedTime.setText(R.string.rejected);
            holder.tabStatusType.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.ractengle_round_solid_red));
        }

        holder.tvPickupTime.setText(Functions.ChangeDateFormat("yyyy-MM-dd hh:mm:ss","dd MMM hh:mm a",model.getCreatedTime()));
        holder.tvDropoffTime.setText(Functions.ChangeDateFormat("yyyy-MM-dd hh:mm:ss","dd MMM hh:mm a",model.getDropoffTime()));

        holder.tvPickupLoc.setText(model.getPickupAddress());
        holder.tvDropoffLoc.setText(model.getDropoffAddress());

        Glide.with(holder.itemView.getContext())
                .load(ApiUrl.baseUrl +""+model.getMapImg())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(holder.imgMyJob);



        holder.bind(position,model,click);
    }



    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        Preferences preferences;
        TextView tvPickupTime, tvPickupLoc, tvDropoffTime, tvDropoffLoc, tvEstimatedTime;
        PorterShapeImageView imgMyJob;
        LinearLayout tabStatusType;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            preferences=new Preferences(itemView.getContext());
            tvEstimatedTime =itemView.findViewById(R.id.tv_estimated_time);
            tvPickupTime =itemView.findViewById(R.id.tv_pickup_time);
            tvPickupLoc =itemView.findViewById(R.id.tv_pickup_loc);
            tvDropoffTime =itemView.findViewById(R.id.tv_dropoff_time);
            tvDropoffLoc =itemView.findViewById(R.id.tv_dropoff_loc);
            imgMyJob =itemView.findViewById(R.id.img_my_job);
            tabStatusType =itemView.findViewById(R.id.tab_status_type);
        }

        public void bind(final int item, final OrderModel model, final AdapterClickListenerCallback listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnItemClick(item,model,view);
                }
            });

        }
    }

}
