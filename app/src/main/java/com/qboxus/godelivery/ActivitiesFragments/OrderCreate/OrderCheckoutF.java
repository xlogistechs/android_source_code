package com.qboxus.godelivery.ActivitiesFragments.OrderCreate;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qboxus.godelivery.HelpingClasses.Functions;
import com.qboxus.godelivery.HelpingClasses.RootFragment;
import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.Interfaces.FragmentClickCallback;
import com.qboxus.godelivery.R;


public class OrderCheckoutF extends RootFragment {

    View view;
    Context context;
    String closeLoader ="";
    public static WebView webView;
    String url="www.google.com";


    FragmentClickCallback fragmentCallback;
    String id;
    Bundle callback_bundle;

    public OrderCheckoutF(String id, FragmentClickCallback fragmentCallback) {
        this.id = id;
        this.fragmentCallback = fragmentCallback;
    }

    public OrderCheckoutF() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_order_checkout, container, false);
        context=getContext();
        callback_bundle=new Bundle();
        Bundle bundle=getArguments();
        if(bundle!=null){
            url = bundle.getString("url");
        }


        view.findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(max_handler!=null && max_runable!=null){
                    callback_bundle.putBoolean("IsSuccess",false);

                   if(fragmentCallback !=null)
                       fragmentCallback.OnItemClick(1,callback_bundle);

                }

                if (webView!=null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    getFragmentManager().popBackStackImmediate();
                }

            }
        });




        webView=view.findViewById(R.id.webview);
        Functions.ShowProgressLoader(view.getContext(),false,false);
        webView.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view, int progress) {
                if(progress>=80){
                    if (!(closeLoader.equals("payment/index.php?payment=cod")))
                   Functions.CancelProgressLoader();
                }
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                if(url.contains("payment/index.php?payment=cod")){
                    closeLoader ="payment/index.php?payment=cod";
                    Functions.ShowProgressLoader(view.getContext(),false,false);
                }
                Log.d(Variables.tag,"url "+url);
                if(url.contains("paymentSuccess")){
                    callback_bundle.putBoolean("IsSuccess",true);
                    startTimer();
                }
                if(url.contains("paymentFaild")){
                    callback_bundle.putBoolean("IsSuccess",true);
                    startTimer();
                }
                return false;
            }
        });


        return view;
    }



    Handler max_handler;
    Runnable max_runable;
    public void startTimer(){
        max_handler=new Handler();
        max_runable=new Runnable() {
            @Override
            public void run() {
                if(fragmentCallback !=null)
                    fragmentCallback.OnItemClick(1,callback_bundle);

                getFragmentManager().popBackStackImmediate();

            }
        };

        max_handler.postDelayed(max_runable,3500);
    }



    @Override
    public void onDetach() {
        super.onDetach();
        if(max_handler!=null && max_runable!=null){
            max_handler.removeCallbacks(max_runable);
        }
        Functions.CancelProgressLoader();
    }


}
