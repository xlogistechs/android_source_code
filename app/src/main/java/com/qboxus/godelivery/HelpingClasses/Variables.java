package com.qboxus.godelivery.HelpingClasses;

import android.content.SharedPreferences;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Variables {

    public static String tag = "parcel_log";
    public static float mapZoomLevel =17;
    public static final int permissionLocation =790;
    public static final int permissionCameraCode =786;
    public static final int permissionWriteData =788;
    public static final int permissionReadData =789;
    public static final int permissionGalleryCode =787;
    public static final int permissionRecordingAudio =790;
    public static final boolean isToastShow =true;

    public static final String privacyPolicy ="https://www.privacypolicygenerator.info/live.php?token=T9l87uR8faA81IpWmODKeitM18Q9VryI";
    public static SharedPreferences downloadSharedPreferences;
    public static String downloadPref = "download_pref";
    public static String openedChatId ="null";
    public static String folderGoDelivery = Environment.getExternalStorageDirectory() +"/GoDeliver/";
    public static String folderGoDeliveryDCIM = Environment.getExternalStorageDirectory() +"/DCIM/GoDeliver/";
    public static SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ",Locale.getDefault());
    public static SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mmZZ",Locale.getDefault());



}
