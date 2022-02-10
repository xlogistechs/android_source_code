package com.qboxus.godelivery.ActivitiesFragments.PaymentMethod;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.ModelClasses.CardModel;
import com.qboxus.godelivery.R;
import com.qboxus.godelivery.RecyclerViewAdapters.PaymentCardAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodF extends RootFragment implements View.OnClickListener {

    View view;
    ImageView btnAdd, btnBack;
    RecyclerView payCardsRv;
    TextView txtNoData;
    PaymentCardAdapter adapter;
    private List<CardModel> cardList;
    View.OnClickListener navClickListener;
    Preferences preferences;
    ProgressBar progressBar;

    public PaymentMethodF(View.OnClickListener navClickListener) {
        this.navClickListener = navClickListener;
    }

    public PaymentMethodF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payment_method, container, false);


        METHOD_initViews();

        return view;
    }


    private void METHOD_initViews() {
        txtNoData =view.findViewById(R.id.txt_no_data);
        btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(navClickListener);
        preferences=new Preferences(view.getContext());
        payCardsRv = view.findViewById(R.id.pay_card_rv);
        progressBar =view.findViewById(R.id.progressbar);
        cardList = new ArrayList<>();

        payCardsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        payCardsRv.setHasFixedSize(false);

        view.findViewById(R.id.payment_method_fl).setOnClickListener(this);
        CallApi_showPaymentDetails();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                METHOD_openAddCard_F();
                break;
            default:
                break;
        }
    }


    private void METHOD_openAddCard_F() {
        AddPaymentCardF f = new AddPaymentCardF(new FragmentClickCallback() {
            @Override
            public void OnItemClick(int postion, Bundle bundle) {
                if (bundle.getBoolean("IsUpdate",false))
                {
                    CallApi_showPaymentDetails();
                }
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.setCustomAnimations(R.anim.in_from_bottom,R.anim.out_to_top);
        ft.replace(R.id.payment_method_fl, f).commit();
    }


    private void CallApi_showPaymentDetails() {
        JSONObject sendobj = new JSONObject();
        try {
            sendobj.put("user_id",preferences.getKeyUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiRequest.CallApi(getContext(), ApiUrl.showPaymentDetails, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                progressBar.setVisibility(View.GONE);
                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.optString("code").equals("200")){

                            JSONArray msgarray = respobj.getJSONArray("msg");

                            METHOD_showPaymentMethods(msgarray);

                        }
                        else
                        {
                            if (cardList.size()>0)
                            {
                                txtNoData.setVisibility(View.GONE);
                                payCardsRv.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                txtNoData.setVisibility(View.VISIBLE);
                                payCardsRv.setVisibility(View.GONE);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void METHOD_showPaymentMethods(JSONArray msgarray) {
        cardList.clear();
        for (int i = 0; i<msgarray.length(); i++){
            try {
                JSONObject obj = msgarray.getJSONObject(i);
                CardModel model = new CardModel();

                model.setCardId(obj.getJSONObject("PaymentCard").optString("id"));
                model.setCardNumber(obj.optString("last4"));
                model.setBrand(obj.optString("brand"));

                cardList.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (cardList.size()>0)
        {
            txtNoData.setVisibility(View.GONE);
            payCardsRv.setVisibility(View.VISIBLE);
        }
        else
        {
            txtNoData.setVisibility(View.VISIBLE);
            payCardsRv.setVisibility(View.GONE);
        }

        setAdapter();
    }

    private void setAdapter() {
        adapter = new PaymentCardAdapter(getContext(), cardList, new AdapterClickListenerCallback() {

            @Override
            public void OnItemClick(int postion, Object Model, View view) {

            }

            @Override
            public void OnItemLongClick(int postion, Object Model, View view) {
                CardModel model = (CardModel) Model;

                METHOD_showDeleteCardDialog(model.getCardId(),postion,model);
            }

        });

        payCardsRv.setAdapter(adapter);
    }

    private void METHOD_showDeleteCardDialog(String id, int pos, CardModel model){
        final CharSequence[] options = { getString(R.string.delete_card), getString(R.string.cancel) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogStyle);
        builder.setTitle(null);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.delete_card))) {
                    METHOD_deletePaymentCard(id);
                    cardList.remove(model);
                    adapter.notifyItemRemoved(pos);
                } else if (options[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void METHOD_deletePaymentCard(String id){
        JSONObject sendobj = new JSONObject();

        try {
            sendobj.put("user_id", preferences.getKeyUserId());
            sendobj.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiRequest.CallApi(getContext(), ApiUrl.deletePaymentCard, sendobj, new Callback() {
            @Override
            public void Responce(String resp) {
                progressBar.setVisibility(View.GONE);
                if (resp !=null){
                    try {
                        JSONObject respobj = new JSONObject(resp);
                        if (respobj.getString("code").equals("200")){
                            Functions.ShowToast(view.getContext(), view.getContext().getString(R.string.deleted_successfully));
                        }
                        else
                        {
                            if (cardList.size()>0)
                            {
                                txtNoData.setVisibility(View.GONE);
                                payCardsRv.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                txtNoData.setVisibility(View.VISIBLE);
                                payCardsRv.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Check : payment ");
    }

}
