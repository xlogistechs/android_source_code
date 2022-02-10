package com.qboxus.godelivery.ActivitiesFragments.MainHome;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.qboxus.godelivery.HelpingClasses.ApiRequest;
import com.qboxus.godelivery.HelpingClasses.ApiUrl;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.ModelClasses.GoodsModel;
import com.qboxus.godelivery.R;
import com.qboxus.godelivery.RecyclerViewAdapters.CountryAndItemAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CountryAndGoodsF extends RootFragment implements View.OnClickListener {

    private TextView tvTbTitle, tvApply;
    private EditText etSearch;
    LinearLayout tabSearchCountry;
    private RecyclerView rv;
    private List<GoodsModel> list;

    private CountryAndItemAdapter adp;

    public static String selectedGoodsType = "", selectedGoodsTypeId,
            selectedCountry = "", selectedCountryId, selectedCountryCode = "", selectedCountryIos = "";

    Boolean is_from_register ;
    private FragmentClickCallback callBack;
    ProgressBar progressBar;
    private View view;


    public CountryAndGoodsF(Boolean is_from_register, FragmentClickCallback callBack) {
        this.is_from_register = is_from_register;
        this.callBack = callBack;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_country_and_goods, container, false);

        METHOD_init_views();

        return view;
    }



    private void METHOD_init_views() {
        view.findViewById(R.id.iv_back).setOnClickListener(this);

        tabSearchCountry =view.findViewById(R.id.tab_search_country);
        tvTbTitle = view.findViewById(R.id.tv_goods_title);
        progressBar=view.findViewById(R.id.good_type_progress);
        tvApply = view.findViewById(R.id.tv_apply);
        tvApply.setOnClickListener(this);


        //stop previously selected item
        selectedGoodsType = ""; selectedGoodsTypeId ="";
        selectedCountry = "";
        selectedCountryId ="";
        selectedCountryCode = "";
        selectedCountryIos = "";

        etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FilterList(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        rv = view.findViewById(R.id.rv);
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        if (is_from_register) {
            tabSearchCountry.setVisibility(View.VISIBLE);
            tvTbTitle.setText(R.string.select_country);
            CallApi_showCountries();
        }else {
            tabSearchCountry.setVisibility(View.GONE);
            tvTbTitle.setText(R.string.types_of_goods);
            CallApi_showGoodsTypes();
        }

    }

    private void FilterList(CharSequence s) {
        try {
            List<GoodsModel> filter_list=new ArrayList<>();
            for (GoodsModel model:list)
            {
                if (model.countryName.toLowerCase().contains(s.toString().toLowerCase()))
                {
                    filter_list.add(model);
                }
            }

            if (filter_list.size()>0)
            {
                adp.filter(filter_list);
            }

        }
        catch (Exception e)
        {
            Log.d(Variables.tag,"Error : "+e);
        }
    }


    private void CallApi_showGoodsTypes() {
        ApiRequest.CallApi(getContext(), ApiUrl.showGoodTypes, new JSONObject(), new Callback() {
            @Override
            public void Responce(String resp) {
                progressBar.setVisibility(View.VISIBLE);
                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            JSONArray msgarray = respobj.getJSONArray("msg");

                            METHOD_gettingGoodsList(msgarray);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }



    private void CallApi_showCountries() {
        ApiRequest.CallApi(getContext(), ApiUrl.showCountries, new JSONObject(), new Callback() {
            @Override
            public void Responce(String resp) {
                progressBar.setVisibility(View.VISIBLE);
                if (resp!=null){

                    try {
                        JSONObject respobj = new JSONObject(resp);

                        if (respobj.getString("code").equals("200")){
                            JSONArray msgarray = respobj.getJSONArray("msg");

                            METHOD_gettingCountriesList(msgarray);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }



    private void METHOD_gettingCountriesList(JSONArray msgarray) {
        try {
            list.clear();
            for (int i = 0; i<msgarray.length(); i++){
                JSONObject countriesobj = msgarray.getJSONObject(i).getJSONObject("Country");

                GoodsModel model = new GoodsModel();
                model.countryId = ""+countriesobj.optString("id");
                model.countryName = ""+countriesobj.optString("name");
                model.countryCode = ""+countriesobj.optString("country_code");
                model.countryIos = ""+countriesobj.optString("iso");

                list.add(model);
            }
            METHOD_setAdapter();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void METHOD_gettingGoodsList(JSONArray msgarray) {
        try {
            list.clear();
            for (int i = 0; i<msgarray.length(); i++){
                JSONObject goodsobj = msgarray.getJSONObject(i).getJSONObject("GoodType");

                GoodsModel model = new GoodsModel();
                model.id = ""+goodsobj.optString("id");
                model.tv = ""+goodsobj.optString("name");


                list.add(model);
            }
            METHOD_setAdapter();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void METHOD_setAdapter() {
        progressBar.setVisibility(View.GONE);
        adp = new CountryAndItemAdapter( is_from_register, list, new AdapterClickListenerCallback() {

            @Override
            public void OnItemClick(int postion, Object model_obj, View view) {
                GoodsModel model = (GoodsModel) model_obj;
                if (is_from_register){
                    selectedCountry = model.countryName;
                    selectedCountryId = model.countryId;
                    selectedCountryCode = model.countryCode;
                    selectedCountryIos = model.countryIos;
                }else {
                    selectedGoodsType = model.tv;
                    selectedGoodsTypeId = model.id;
                }


                int position=list.indexOf(model);
                list.set(position, model);
                adp.notifyDataSetChanged();

            }

            @Override
            public void OnItemLongClick(int postion, Object model_obj, View view) {

            }
        });

        rv.setAdapter(adp);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getFragmentManager().popBackStackImmediate();
                break;

            case R.id.tv_apply:
                String apply_text;
                if (is_from_register)
                    apply_text= selectedCountry;
                else
                    apply_text= selectedGoodsType;
                if (TextUtils.isEmpty(apply_text))
                {
                   Functions.Show_Alert(view.getContext(),view.getContext().getString(R.string.goods_status),view.getContext().getString(R.string.select_anyone_for_proceed));
                    return;
                }
                Bundle bundle = new Bundle();
                if (is_from_register){
                    bundle.putString("selected_country", selectedCountry);
                    bundle.putString("selected_country_id", selectedCountryId);
                    bundle.putString("selected_country_code", selectedCountryCode);
                    bundle.putString("selected_country_ios", selectedCountryIos);
                }else {
                    bundle.putString("selected_goods_type", selectedGoodsType);
                    bundle.putString("selected_goods_type_id", selectedGoodsTypeId);
                }

                callBack.OnItemClick(0, bundle);
                Functions.HideSoftKeyboard(getActivity());
                getFragmentManager().popBackStackImmediate();
                break;


        }
    }

}


