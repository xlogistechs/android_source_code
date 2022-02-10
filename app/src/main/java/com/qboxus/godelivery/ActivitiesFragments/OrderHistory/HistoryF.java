package com.qboxus.godelivery.ActivitiesFragments.OrderHistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.ModelClasses.OrderModel;
import com.qboxus.godelivery.R;
import com.qboxus.godelivery.RecyclerViewAdapters.HistoryOrderAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class HistoryF extends RootFragment implements View.OnClickListener {

    private ImageView iv_back,img_option;
    private View view;
    PopupWindow popup_window;
    Preferences preferences;
    //tab in progress
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private HistoryOrderAdapter adapter;
    private List<OrderModel> order_data_list=new ArrayList<>();
    TextView tv_no_data,tv_title;
    private ProgressBar progressBar;
    View.OnClickListener nav_click_listener;
    String type="AllData";

    int current_page=0;
    boolean loading=true;
    boolean isFirstTime=true;
    boolean isPaginationStop=false;


    public HistoryF(View.OnClickListener nav_click_listener) {
        this.nav_click_listener=nav_click_listener;
    }

    public HistoryF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_history, container, false);
        METHOD_init_views();
        METHOD_init_Action();
        return view;
    }

    private void METHOD_init_Action() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                current_page=0;
                isFirstTime=true;
                CallApi_ShowUserOrders("Refresh");
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled( RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == order_data_list.size() - 1) {
                    //bottom of list!
                    if (loading)
                    {
                        if (isPaginationStop)
                        {
                            //do nothing
                        }
                        else
                        {
                            if (order_data_list.size()>6)
                            {
                                loading=false;
                                loadMore();
                            }
                        }

                    }
                    else
                    {
                        //wait process is continue
                    }
                }
            }
        });

    }

    private void loadMore() {
        order_data_list.add(null);
        adapter.notifyItemInserted(order_data_list.size()-1);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                order_data_list.remove(order_data_list.size() - 1);
                adapter.notifyDataSetChanged();
                ///////////
                CallApi_ShowUserOrders("none");

            }
        }, 1000);


    }


    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("type"))
                if (!(intent.getExtras().getString("type").equalsIgnoreCase("rider")))
                {
                    type="active";
                    current_page=0;
                    isFirstTime=true;
                    CallApi_ShowUserOrders("FirstTime");
            }

        }
    };


    private void METHOD_init_views() {
        iv_back = view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(nav_click_listener);
        img_option=view.findViewById(R.id.ic_option);
        img_option.setOnClickListener(this);
        preferences=new Preferences(view.getContext());
        tv_title=view.findViewById(R.id.tv_title);
        //tab_view_progress
        tv_no_data = view.findViewById(R.id.tv_no_data);
        progressBar = view.findViewById(R.id.progressbar);
        recyclerView=view.findViewById(R.id.recyclerview);
        refreshLayout=view.findViewById(R.id.swiperefresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new HistoryOrderAdapter(order_data_list, new AdapterClickListenerCallback() {
            @Override
            public void OnItemClick(int postion, Object model_obj, View view) {
                boolean isFromActive=true;
                if (((OrderModel) model_obj).getOrderStatus().equals("2"))
                {
                    isFromActive=false;
                }
                else
                {
                    isFromActive=true;
                }

                OrderDetailF f = new OrderDetailF(isFromActive, new FragmentClickCallback() {
                    @Override
                    public void OnItemClick(int postion, Bundle bundle) {
                        if (bundle!=null){
                            if (bundle.getString("status").equals("cancel")){
                                order_data_list.remove(bundle.getInt("item_pos"));
                                adapter.notifyItemRemoved(bundle.getInt("item_pos"));
                            }
                            if (bundle.getString("status").equals("refresh")){
                                current_page=0;
                                isFirstTime=true;
                                CallApi_ShowUserOrders("none");
                            }
                        }
                    }
                });
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();

                Bundle bundle = new Bundle();
                bundle.putSerializable("model", (OrderModel) model_obj);
                bundle.putInt("item_pos", postion);
                f.setArguments(bundle);
                ft.addToBackStack(null);
                ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                ft.replace(R.id.fl_id, f).commit();
            }

            @Override
            public void OnItemLongClick(int postion, Object model_obj, View view) {

            }
        });
        recyclerView.setAdapter(adapter);

        CallApi_ShowUserOrders("AllData");
    }

    private void CallApi_ShowUserOrders(String ProgressType) {
        if (type.equalsIgnoreCase("AllData"))
        {
            tv_title.setText(view.getContext().getString(R.string.order_history));
        }
        else
        if (type.equalsIgnoreCase("active"))
        {
            tv_title.setText(view.getContext().getString(R.string.active_order));
        }
        else
        {
            tv_title.setText(view.getContext().getString(R.string.past_order));
        }
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            if (type.equalsIgnoreCase("AllData"))
            {

            }
            else
            {
                sendobj.put("type", ""+type);
            }
            sendobj.put("starting_point", ""+(current_page));

        } catch (Exception e) {
            Log.d(Variables.tag,"Exception "+e);
        }
        if (ProgressType.equalsIgnoreCase("Refresh"))
        {
            refreshLayout.setRefreshing(true);
        }
        else
        if ((ProgressType.equalsIgnoreCase("FirstTime") || type.equalsIgnoreCase("AllData"))  && isFirstTime)
        {
            tv_no_data.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        ApiRequest.CallApi(view.getContext(), ApiUrl.showUserOrders, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                if (ProgressType.equalsIgnoreCase("Refresh"))
                {
                    refreshLayout.setRefreshing(false);
                }
                else
                if ((ProgressType.equalsIgnoreCase("FirstTime") || type.equalsIgnoreCase("AllData"))  && isFirstTime)
                {
                    progressBar.setVisibility(View.GONE);
                }

                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            isPaginationStop=false;
                            JSONArray msgarray = respobj.optJSONArray("msg");
                            METHOD_Orderslist(msgarray);

                        }
                        else
                        {
                            isPaginationStop=true;
                        }

                    } catch (Exception e) {
                        Log.d(Variables.tag,"Exception "+e);
                    }

                }
            }
        });
    }


    private void METHOD_Orderslist(JSONArray msgarray) {
        if (isFirstTime)
        {
            order_data_list.clear();
            isFirstTime=false;
        }

        for(int i = 0; i<msgarray.length(); i++){
            try {
                JSONObject Order_Obj = msgarray.getJSONObject(i).getJSONObject("Order");
                JSONObject Delivery_Type_Obj = msgarray.getJSONObject(i).getJSONObject("DeliveryType");
                JSONObject Rider_Order_Obj=Order_Obj.getJSONObject("RiderOrder");
                JSONObject Rider_Obj=Rider_Order_Obj.getJSONObject("Rider");
                JSONObject Package_Size_Obj=msgarray.getJSONObject(i).getJSONObject("PackageSize");
                JSONObject Package_Type_Obj=msgarray.getJSONObject(i).getJSONObject("GoodType");
                JSONObject Rating_Obj=msgarray.getJSONObject(i).getJSONObject("DriverRating");
                OrderModel model = new OrderModel();

                model.setDeliveryType(Delivery_Type_Obj.optString("title"));
                model.setMapImg(Order_Obj.optString("map"));
                model.setDropoffTime(Rider_Order_Obj.optString("delivered"));

                model.setSenderLat(Order_Obj.optDouble("sender_location_lat",0.0));
                model.setSenderLng(Order_Obj.optDouble("sender_location_long",0.0));
                model.setReceiverLat(Order_Obj.optDouble("receiver_location_lat",0.0));
                model.setReceiverLng(Order_Obj.optDouble("receiver_location_long",0.0));

                model.setOrderId(Integer.valueOf(Order_Obj.optString("id")));
                model.setOrderStatus(Order_Obj.optString("status"));
                model.setPickupAddress(Order_Obj.optString("sender_location_string"));
                model.setPickupExtraDetail(Order_Obj.optString("sender_address_detail"));
                model.setPickupTime(Order_Obj.optString("pickup_datetime"));
                model.setCreatedTime(Order_Obj.optString("created"));
                model.setDropoffAddress(Order_Obj.optString("receiver_location_string"));
                model.setDropoffExtraDetail(Order_Obj.optString("receiver_address_detail"));
                model.setFinalFare(Order_Obj.optString("total"));
                model.setSenderName(Order_Obj.optString("sender_name"));
                model.setSenderPhone(Order_Obj.optString("sender_phone"));
                model.setReceiverName(Order_Obj.optString("receiver_name"));
                model.setReceiverPhone(Order_Obj.optString("receiver_phone"));
                model.setPackageType(Package_Type_Obj.optString("name"));
                model.setPackageSize(Package_Size_Obj.optString("title"));

                model.setDriverId(Rider_Obj.optString("id"));
                model.setDriverName(Rider_Obj.optString("first_name")+" "+Rider_Obj.optString("last_name"));
                model.setDriverImg(Rider_Obj.optString("image"));
                model.setDriverRating(Rating_Obj.optString("star","0"));

                model.setNotificationCount(msgarray.getJSONObject(i).getJSONArray("OrderNotification").length());

                order_data_list.add(model);
                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                Log.d(Variables.tag,"Exception "+e);
            }
        }

        Collections.sort(order_data_list, new Comparator<OrderModel>() {
            @Override
            public int compare(OrderModel o1, OrderModel o2) {
                return o1.getOrderId() - o2.getOrderId();
            }
        });

        loading=true;
        current_page++;

        UpDateNoDataView();

    }

    private void UpDateNoDataView() {
        if (order_data_list.size()>0)
        {
            tv_no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else
        {
            tv_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ic_option:
                if (popup_window!=null)
                {
                    if (!(popup_window.isShowing()))
                    {
                        initiatePopupWindow();
                    }
                    else
                    {
                        popup_window.dismiss();
                    }
                }
                    else
                {
                    initiatePopupWindow();
                }
               break;
        }
    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        if (popup_window!=null)
        popup_window.dismiss();
    }

    private void initiatePopupWindow() {

        try {
            LayoutInflater mInflater= (LayoutInflater) view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = mInflater.inflate(R.layout.history_popup_window, null);
            popup_window = new PopupWindow(
                    layout,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            popup_window.setOutsideTouchable(true);
            if (preferences.getKeyLocale().equalsIgnoreCase("en"))
            {
                popup_window.showAtLocation(img_option,Gravity.TOP|Gravity.END,0,0);
            }
            else
            {
                popup_window.showAtLocation(img_option,Gravity.TOP|Gravity.START,0,0);
            }

            final TextView tv_current_order = (TextView) layout.findViewById(R.id.tv_current_order);
            final TextView tv_past_order = (TextView) layout.findViewById(R.id.tv_past_order);

            tv_current_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    type="active";
                    current_page=0;
                    isFirstTime=true;
                    order_data_list.clear();
                    CallApi_ShowUserOrders("FirstTime");
                    popup_window.dismiss();
                }
            });

            tv_past_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    type="completed";
                    current_page=0;
                    isFirstTime=true;
                    order_data_list.clear();
                    CallApi_ShowUserOrders("FirstTime");
                    popup_window.dismiss();
                }
            });

        } catch (Exception e) {
            Log.d(Variables.tag,"Error : "+e);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (broadcastReceiver!=null)
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("request_responce");
        getActivity().registerReceiver(broadcastReceiver,intentFilter);
    }
}
