package com.qboxus.godelivery.HelpingClasses;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.qboxus.godelivery.ChatModule.ChatModel;
import com.qboxus.godelivery.Interfaces.InternetCheckCallback;
import com.qboxus.godelivery.Interfaces.Callback;
import com.qboxus.godelivery.R;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.DOWNLOAD_SERVICE;

public class Functions {


    private static Dialog dialog;
    private static double conversionUnit = 0.6214;
//    check email valid formate
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


//    get top active fragment according to fragment manager
    public static String getActiveFragment(FragmentManager fm) {
        if (fm.getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
        return tag;
    }


    // first letter captial
    public static String SetFirstLetterCapital(String word) {
        if (word.length()>1)
            word=word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        return word;
    }


    // Show Dynamic Toast
    public static void ShowToast(Context context,String msg) {
       if (Variables.isToastShow)
       {
           Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
       }
    }




//    open fragment and clear all previously active fragment use in side drawer navigation
    public static void OpenNavFragment(Fragment f, FragmentManager fm, String FragmentName, View view){

        if (FragmentName.equals(Functions.getActiveFragment(fm))) {
            //do nothing
        }
        else
        {
            clearFragmentByTag(fm);
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            ft.replace(view.getId(), f,FragmentName).addToBackStack(FragmentName).commit();
        }

    }

//    clear previously active fragment
    public static void clearFragmentByTag(FragmentManager fm) {
        try {
            for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
                Fragment fragment = fm.findFragmentByTag( fm.getBackStackEntryAt(i).getName());
                if(fragment != null)
                {
                    fm.beginTransaction().remove(fragment).commit();
                    fm.popBackStack();
                }

            }

        } catch (Exception e) {
            System.out.print("!====Popbackstack error : " + e);
            e.printStackTrace();
        }
    }

//  claer previous all support level fragments
    public static void clearFragment(FragmentManager fm) {
        try {

            for (int i = fm.getBackStackEntryCount() - 1; i >= 0; i--) {
                fm.popBackStack();
            }


        } catch (Exception e) {
            System.out.print("!====Popbackstack error : " + e);
            e.printStackTrace();
        }
    }


    //This method will show loader when getting data from server
    public static void ShowProgressLoader(Context context, boolean outsideTouch, boolean cancleable) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loader_dialog);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_corner_white_bkg));

        CamomileSpinner loader = dialog.findViewById(R.id.loader);
        loader.start();

        if(!outsideTouch)
            dialog.setCanceledOnTouchOutside(false);

        if(!cancleable)
            dialog.setCancelable(false);

        dialog.show();

    }



    //This method will cancel the running loader
    public static void CancelProgressLoader(){
        if(dialog!=null){
            dialog.cancel();
            dialog.dismiss();
        }
    }

    //This method hide opened softkeyboard
    public static void HideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    //This method is called to show softkeyboard on screen
    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    //This method will change the date format
    public static String ChangeDateFormat(String fromFormat, String toFormat, String date){

        SimpleDateFormat dateFormat = new SimpleDateFormat(fromFormat,Locale.getDefault());
        Date sourceDate = null;

        try {
            sourceDate = dateFormat.parse(date);

            SimpleDateFormat targetFormat = new SimpleDateFormat(toFormat,Locale.getDefault());

            return  targetFormat.format(sourceDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    //    calculate distence between two pins
    public static double getDistanceFromLatLonInKm(LatLng pickupLatlng, LatLng dropoffLatlng) {
        long R = 6371; // Radius of the earth in km

        double lat1 = pickupLatlng.latitude;
        double lon1 = pickupLatlng.longitude;
        double lat2 = dropoffLatlng.latitude;
        double lon2 = dropoffLatlng.longitude;

        double dLat = deg2rad(lat2-lat1);  // deg2rad below

        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dInKm = R * c; // Distance in km
        double dInMiles = (dInKm* conversionUnit); // Distance in miles
        return dInMiles;
    }

    public static double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }


//    get marker pin with rotation
    public static double getBearingBetweenTwoPoints1(LatLng latLng1, LatLng latLng2) {

        double lat1 = degreesToRadians(latLng1.latitude);
        double long1 = degreesToRadians(latLng1.longitude);
        double lat2 = degreesToRadians(latLng2.latitude);
        double long2 = degreesToRadians(latLng2.longitude);


        double dLon = (long2 - long1);


        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double radiansBearing = Math.atan2(y, x);


        return radiansToDegrees(radiansBearing);
    }


    //Converts device pixels to regular pixels to draw
    public static int convertDpToPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi/160f);
        return (int) px;
    }


    public static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }


    public static double radiansToDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }



    public static long DownloadData_forChat(Context context, ChatModel item) {

        long downloadReference;
        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(item.pic_url));

        //Setting title of request
        request.setTitle(item.sender_name);


        if (item.type.equals("audio")) {
            request.setDescription("Downloading Audio");
            request.setDestinationUri(Uri.fromFile(new File(Variables.folderGoDelivery + item.chat_id + ".mp3")));
        }


        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }


    public static String CheckImageStatus(Context context, long imageDownloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();

        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(imageDownloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if (cursor.moveToFirst()) {
            return DownloadStatus(cursor, imageDownloadId);
        }
        return "";
    }


    private static String DownloadStatus(Cursor cursor, long DownloadId) {

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                return "STATUS_FAILED";

            case DownloadManager.STATUS_PAUSED:
                return "";

            case DownloadManager.STATUS_PENDING:
                return "";

            case DownloadManager.STATUS_RUNNING:
                return "";

            case DownloadManager.STATUS_SUCCESSFUL:
                return "STATUS_SUCCESSFUL";
            default:
                return "none";
        }

    }


    public static void HideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setWidthAndHeight(Context context, View view, int width, int height) {
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
    }


    public static void PrintHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d(Variables.tag, "KeyHash : " + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }

    static BroadcastReceiver broadcastReceiver;
    static IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

    public static void unRegisterConnectivity(Context mContext) {
        try {
            if (broadcastReceiver != null)
                mContext.unregisterReceiver(broadcastReceiver);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    public static void RegisterConnectivity(Context context, final InternetCheckCallback callback) {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isConnectedToInternet(context)) {
                    callback.GetResponse("alert", "connected");
                } else {
                    callback.GetResponse("alert", "disconnected");
                }
            }
        };

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public static Boolean isConnectedToInternet(Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("NetworkChange", e.getMessage());
            return false;
        }
    }

    public static void Show_Alert(Context context,String title,String Message){
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.show_defult_alert_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tvYes,tvNo,tvTitle,tvMessage;
        tvTitle=dialog.findViewById(R.id.defult_alert_txt_title);
        tvMessage=dialog.findViewById(R.id.defult_alert_txt_message);
        tvNo=dialog.findViewById(R.id.defult_alert_btn_cancel_no);
        tvYes=dialog.findViewById(R.id.defult_alert_btn_cancel_yes);

        tvTitle.setText(""+title);
        tvMessage.setText(""+Message);
        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        dialog.show();
    }

    public static void Show_Alert(Activity activity, String title, String message, final Callback callback) {

        new android.app.AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.Responce("no");
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        callback.Responce("yes");
                    }
                }).show();

    }
}
