package com.qboxus.godelivery.HelpingClasses;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ApiRequest {
    public static RequestQueue requestQueue;

    public static void CallApi(final Context context, String url, JSONObject jsonObject,
                               final Callback callback){

        Log.d(Variables.tag,url);
        Log.d(Variables.tag, jsonObject.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.Responce(response.toString());
                        Log.d(Variables.tag, response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.Responce(error.toString());
            }

        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Api-Key",ApiUrl.ApiKey);
                headers.put("User-Id",""+new Preferences(context).getKeyUserId());
                headers.put("Auth-Token",""+ new Preferences(context).getKeyUserAuthToken());
                Log.d(Variables.tag,headers.toString());
                return headers;

            }
        };

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);

    }




    public static void CallApi(final Context context, String url, JSONObject jsonObject,
                               final Callback callback, int type){

        Log.d(Variables.tag,url);
        Log.d(Variables.tag, jsonObject.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(type,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.Responce(response.toString());
                        Log.d(Variables.tag, response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.Responce(error.toString());
            }



        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjReq);

    }
}
