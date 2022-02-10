package com.qboxus.godelivery.ActivitiesFragments.MainHome;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.qboxus.godelivery.ActivitiesFragments.OrderCreate.OrderCreateF;
import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.Preferences;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.AdapterClickListenerCallback;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.ModelClasses.NearbyLocationModel;
import com.qboxus.godelivery.R;
import com.qboxus.godelivery.RecyclerViewAdapters.NearbyLocationAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class SearchLocationF extends RootFragment implements View.OnClickListener {

    private View view;
    Bundle callBackBundle = new Bundle();
    private RecyclerView rcNearbyLocation;
    private NearbyLocationAdapter nearbyAdapter;
    private List<NearbyLocationModel> predictedLocationList = new ArrayList<>();
    private List<NearbyLocationModel> nearbyList = new ArrayList<>();
    ProgressBar nearByLocationProgress;
    private EditText etTextWatcher;
    ImageView ivBack;
    String title;
    TextView tvSearchTitle;
    final FragmentClickCallback fragmentCallBack;
    Preferences preferences;
    int tvCount =0;
    long delay=1000;
    Timer timer;
    String initQuery;

    public SearchLocationF(String query, String title, FragmentClickCallback fragmentCallBack) {
        this.initQuery=query;
        this.title = title;
        this.fragmentCallBack = fragmentCallBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_location_search, container, false);


        InitControl();
        ActionControl();


        etTextWatcher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCount =charSequence.toString().length();
                if (tvCount >0)
                {
                    if (timer!=null)
                    {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            predictedLocationList.clear();
                            authoCompleteSaerch(charSequence.toString());
                        }
                    },delay);
                }
                else
                {
                    if (timer!=null)
                    {
                        timer.cancel();
                    }
                    predictedLocationList.clear();
                    predictedLocationList.addAll(nearbyList);
                    nearbyAdapter.updateList(predictedLocationList);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void authoCompleteSaerch(String query) {
        predictedLocationList.clear();
        if (!Places.isInitialized()) {
            Places.initialize(view.getContext(), view.getContext().getString(R.string.places_api_key), Locale.US);
        }
        PlacesClient placesClient = Places.createClient(view.getContext());
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(Variables.tag, prediction.getPlaceId());

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);
                FetchPlaceRequest place_request = FetchPlaceRequest.builder(prediction.getPlaceId(), fields).build();
                placesClient.fetchPlace(place_request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place=fetchPlaceResponse.getPlace();
                        Log.d(Variables.tag,"Test : "+place);
                        Log.d(Variables.tag,"size : "+ predictedLocationList.size());

                        NearbyLocationModel model = new NearbyLocationModel();

                        model.setTitle(place.getName());
                        model.setAddress(place.getAddress());
                        model.setLat(place.getLatLng().latitude);
                        model.setLng(place.getLatLng().longitude);
                        model.setPlaceId(place.getId());
                        predictedLocationList.add(model);
                        nearbyAdapter.notifyDataSetChanged();


                    }
                });
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(Variables.tag, "Place not found: " + apiException.getStatusCode());
            }
        });


    }


    private void NearBySearch() {
        nearbyList.clear();
        predictedLocationList.clear();
        if (!Places.isInitialized()) {
            Places.initialize(view.getContext(), view.getContext().getString(R.string.places_api_key), Locale.US);
        }
        PlacesClient placesClient = Places.createClient(view.getContext());
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(initQuery)
                .build();
        nearByLocationProgress.setVisibility(View.VISIBLE);
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            nearByLocationProgress.setVisibility(View.GONE);
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(Variables.tag, prediction.getPlaceId());

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS);
                FetchPlaceRequest place_request = FetchPlaceRequest.builder(prediction.getPlaceId(), fields).build();
                placesClient.fetchPlace(place_request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place=fetchPlaceResponse.getPlace();
                        Log.d(Variables.tag,"Test : "+place);

                        NearbyLocationModel model = new NearbyLocationModel();

                        model.setTitle(place.getName());
                        model.setAddress(place.getAddress());
                        model.setLat(place.getLatLng().latitude);
                        model.setLng(place.getLatLng().longitude);
                        model.setPlaceId(place.getId());
                        predictedLocationList.add(model);
                        nearbyList.add(model);
                        nearbyAdapter.notifyDataSetChanged();

                    }
                });
            }
        }).addOnFailureListener((exception) -> {
            nearByLocationProgress.setVisibility(View.GONE);
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(Variables.tag, "Place not found: " + apiException.getStatusCode());
            }
        });


    }


    public void InitControl() {
        preferences=new Preferences(view.getContext());
        ivBack = view.findViewById(R.id.iv_back);
        rcNearbyLocation = view.findViewById(R.id.rc_Nearby_location);
        nearByLocationProgress =view.findViewById(R.id.near_by_location_progress);
        etTextWatcher = view.findViewById(R.id.et_text_watcher);
        tvSearchTitle = view.findViewById(R.id.tv_search_title);


        SetUpScreenData();
    }

    private void SetUpScreenData() {
        tvSearchTitle.setText(title);
        METHODSetAdapter();
        NearBySearch();
    }


    private void METHODSetAdapter() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rcNearbyLocation.setLayoutManager(layoutManager);
        nearbyAdapter = new NearbyLocationAdapter(predictedLocationList, new AdapterClickListenerCallback() {
            @Override
            public void OnItemClick(int postion, Object object, View view) {
                NearbyLocationModel model = (NearbyLocationModel) object;

                switch (view.getId()) {
                    default:
                    {
                        try {
                            OrderCreateF.pre_lat= ""+model.getLat();
                            OrderCreateF.pre_lng=""+model.getLng();
                            OrderCreateF.pre_address=""+model.getAddress();
                        }
                        catch (Exception e){}

                        callBackBundle.putString("title", model.getTitle());
                        callBackBundle.putString("address", model.getAddress());
                        callBackBundle.putDouble("lat", model.getLat());
                        callBackBundle.putDouble("lng", model.getLng());
                        fragmentCallBack.OnItemClick(0, callBackBundle);
                        getActivity().onBackPressed();
                    }
                }
            }

            @Override
            public void OnItemLongClick(int postion, Object Model, View view) {

            }
        });
        rcNearbyLocation.setAdapter(nearbyAdapter);
    }



    public void ActionControl() {
        ivBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                Functions.HideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
                break;
        }
    }

}
