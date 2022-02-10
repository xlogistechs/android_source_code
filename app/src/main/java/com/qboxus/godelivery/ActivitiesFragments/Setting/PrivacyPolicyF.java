package com.qboxus.godelivery.ActivitiesFragments.Setting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.qboxus.godelivery.HelpingClasses.Variables;
import com.qboxus.godelivery.R;


public class PrivacyPolicyF extends Fragment {


    View view;

    ProgressBar progressBar;
    WebView webView;
    String url="www.google.com";

    public PrivacyPolicyF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_privacy_policy, container, false);

       url= Variables.privacyPolicy;


        view.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        webView=view.findViewById(R.id.webview);
        progressBar =view.findViewById(R.id.progress_bar);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if(progress>=80){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(url);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });


        return view;
    }


}
