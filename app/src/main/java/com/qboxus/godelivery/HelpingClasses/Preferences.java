package com.qboxus.godelivery.HelpingClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Use this for create local variables
 */

public class Preferences {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    private String KEY_IS_LOGIN = "is_login";
    private String KEY_LOCALE = "user_locale";
    private String KEY_NIGHT_MODE = "user_night_mode";
    private String KEY_USER_ID = "user_id";
    private String KEY_USER_FIRST_NAME = "user_first_name";
    private String KEY_USER_LAST_NAME = "user_last_name";
    private String KEY_USER_EMAIL = "user_email";
    private String KEY_USER_NAME = "user_name";
    private String KEY_USER_DATE_OF_BIRTH = "user_date_of_birth";
    private String KEY_USER_COUNTRY = "user_country";
    private String KEY_USER_COUNTRY_ID = "user_country_id";
    private String KEY_USER_COUNTRY_IOS = "user_country_ios";
    private String KEY_USER_COUNTRY_CODE = "user_country_code";
    private String KEY_USER_PHONE = "user_phone";
    private String KEY_USER_IMAGE = "user_image";
    private String KEY_SOCIAL_TYPE = "social_type";
    private String KEY_USER_LAT = "user_lat";
    private String KEY_USER_LNG = "user_lng";
    private String KEY_PICKUP_LAT = "pickup_lat";
    private String KEY_PICKUP_LNG = "pickup_lng";
    private String KEY_DROPOFF_LAT = "dropoff_lat";
    private String KEY_DROPOFF_LNG = "dropoff_lng";
    private String KEY_PICKUP_ADDRESS = "pickup_address";
    private String KEY_DROPOFF_ADDRESS = "dropoff_address";
    private String KEY_SENDER_PHONE = "sender_phone";
    private String KEY_RECEIVER_PHONE = "receiver_phone";
    private String KEY_ADD_PICKUP_DETAIL = "pickup_detail";
    private String KEY_ADD_DROPUP_DETAIL = "dropoff_detail";

    private String KEY_USER_DEVICE_TOKEN = "user_device_token";
    private String KEY_USER_AUTH_TOKEN = "user_auth_token";
    private String KEY_USER_ROLE = "user_role";


    private String KEY_CURRENCY_SYMBOL = "currency_symbol";
    private String KEY_CURRENCY_NAME = "currency_Name";
    private String KEY_USER_RIDER_COMMISSION = "user_rider_commission";
    private String KEY_USER_ACTIVE = "user_active";
    private String KEY_CREATED = "user_created";
    private String KEY_DOWNLOADING_ID = "downloading_id";
    private String KEY_IS_CHAT_OPEN = "isChatOpen";


    public Preferences(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("RiderDelivery", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public Boolean getKeyIsChatOpen() {
        return prefs.getBoolean(KEY_IS_CHAT_OPEN, false);
    }

    public void setKeyIsChatOpen(Boolean isChatOpen) {
        editor.putBoolean(KEY_IS_CHAT_OPEN, isChatOpen);
        editor.commit();
    }


    public Boolean getKeyIsNightMode() {
        return prefs.getBoolean(KEY_NIGHT_MODE, false);
    }

    public void setKeyIsNightMode(Boolean keyIsNightMode) {
        editor.putBoolean(KEY_NIGHT_MODE, keyIsNightMode);
        editor.commit();
    }

    public String getKeySocialType() {
        return prefs.getString(KEY_SOCIAL_TYPE, "");
    }

    public void setKeySocialType(String KeySocialType) {
        editor.putString(KEY_SOCIAL_TYPE, KeySocialType);
        editor.commit();
    }

    public String getKeyPickupAdress() {
        return prefs.getString(KEY_PICKUP_ADDRESS, "");
    }

    public void setKeyPickupAdress(String KeyPickupAdress) {
        editor.putString(KEY_PICKUP_ADDRESS, KeyPickupAdress);
        editor.commit();
    }


    public String getKeyDropoffAdress() {
        return prefs.getString(KEY_DROPOFF_ADDRESS, "");
    }

    public void setKeyDropoffAdress(String KeyDropoffAdress) {
        editor.putString(KEY_DROPOFF_ADDRESS, KeyDropoffAdress);
        editor.commit();
    }



    public String getKeySenderPhone() {
        return prefs.getString(KEY_SENDER_PHONE, "");
    }

    public void setKeySenderPhone(String KeySenderPhone) {
        editor.putString(KEY_SENDER_PHONE, KeySenderPhone);
        editor.commit();
    }

    public String getKeyCountryCode() {
        return prefs.getString(KEY_USER_COUNTRY_CODE, "");
    }

    public void setKeyCountryCode(String KeyCountryCode) {
        editor.putString(KEY_USER_COUNTRY_CODE, KeyCountryCode);
        editor.commit();
    }

    public String getKeyReceiverPhone() {
        return prefs.getString(KEY_RECEIVER_PHONE, "");
    }

    public void setKeyReceiverPhone(String KeyReceiverPhone) {
        editor.putString(KEY_RECEIVER_PHONE, KeyReceiverPhone);
        editor.commit();
    }


    public String getKeyDropoffDetail() {
        return prefs.getString(KEY_ADD_DROPUP_DETAIL, "");
    }

    public void setKeyDropoffDetail(String KeyDropoffDetail) {
        editor.putString(KEY_ADD_DROPUP_DETAIL, KeyDropoffDetail);
        editor.commit();
    }


    public String getKeyPickupDetail() {
        return prefs.getString(KEY_ADD_PICKUP_DETAIL, "");
    }

    public void setKeyPickupDetail(String KeyPickupDetail) {
        editor.putString(KEY_ADD_PICKUP_DETAIL, KeyPickupDetail);
        editor.commit();
    }


    public String getKeyUserDateOfBirth() {
        return prefs.getString(KEY_USER_DATE_OF_BIRTH, "");
    }

    public void setKeyUserDateOfBirth(String KeyDateOfBirth) {
        editor.putString(KEY_USER_DATE_OF_BIRTH, KeyDateOfBirth);
        editor.commit();
    }

    public String getKeyUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    public void setKeyUserName(String KeyUser_Name) {
        editor.putString(KEY_USER_NAME, KeyUser_Name);
        editor.commit();
    }

    public String getKeyDownloading_id() {
        return prefs.getString(KEY_DOWNLOADING_ID, "0");
    }

    public void setKeyDownloading_id(String KeyDownloading_id) {
        editor.putString(KEY_DOWNLOADING_ID, KeyDownloading_id);
        editor.commit();
    }


    public String getKeyCurrencyName() {
        return prefs.getString(KEY_CURRENCY_NAME, "US Doller");
    }

    public void setKeyCurrencyName(String KeyCurrencyName) {
        editor.putString(KEY_CURRENCY_NAME, KeyCurrencyName);
        editor.commit();
    }



    public String getKeyCurrencySymbol() {
        return prefs.getString(KEY_CURRENCY_SYMBOL, "$");
    }

    public void setKeyCurrencySymbol(String KeyCurrency) {
        editor.putString(KEY_CURRENCY_SYMBOL, KeyCurrency);
        editor.commit();
    }


    public String getKeyLocale() {
        return prefs.getString(KEY_LOCALE, "en");
    }

    public void setKeyLocale(String keyLocale) {
        editor.putString(KEY_LOCALE, keyLocale);
        editor.commit();
    }

    public String getKeyUserFirstName() {
        return prefs.getString(KEY_USER_FIRST_NAME, "");
    }

    public void setKeyUserFirstName(String key_user_first_name) {
        editor.putString(KEY_USER_FIRST_NAME, key_user_first_name);
        editor.commit();
    }

    public String getKeyUserLastName() {
        return prefs.getString(KEY_USER_LAST_NAME, "");
    }

    public void setKeyUserLastName(String key_user_last_name) {
        editor.putString(KEY_USER_LAST_NAME, key_user_last_name);
        editor.commit();
    }


    public String getKeyUserCountryIOS() {
        return prefs.getString(KEY_USER_COUNTRY_IOS, "");
    }

    public void setKeyUserCountryIOS(String key_user_ios) {
        editor.putString(KEY_USER_COUNTRY_IOS, key_user_ios);
        editor.commit();
    }

    public String getKeyUserCountry() {
        return prefs.getString(KEY_USER_COUNTRY, "");
    }

    public void setKeyUserCountry(String key_user_country) {
        editor.putString(KEY_USER_COUNTRY, key_user_country);
        editor.commit();
    }

    public String getKeyUserCountryId() {
        return prefs.getString(KEY_USER_COUNTRY_ID, "0");
    }

    public void setKeyUserCountryId(String key_user_country_id) {
        editor.putString(KEY_USER_COUNTRY_ID, key_user_country_id);
        editor.commit();
    }


    public String getKeyUserImage() {
        return prefs.getString(KEY_USER_IMAGE, "app/godelivery");
    }

    public void setKeyUserImage(String key_user_image) {
        editor.putString(KEY_USER_IMAGE, key_user_image);
        editor.commit();
    }

    public String getKeyUserDeviceToken() {
        return prefs.getString(KEY_USER_DEVICE_TOKEN, "");
    }

    public void setKeyUserDeviceToken(String key_user_device_token) {
        editor.putString(KEY_USER_DEVICE_TOKEN, key_user_device_token);
        editor.commit();
    }

    public String getKeyUserAuthToken() {
        return prefs.getString(KEY_USER_AUTH_TOKEN, "");
    }

    public void setKeyUserAuthToken(String key_user_auth_token) {
        editor.putString(KEY_USER_AUTH_TOKEN, key_user_auth_token);
        editor.commit();
    }

    public String getKeyUserRole() {
        return prefs.getString(KEY_USER_ROLE, "");
    }

    public void setKeyUserRole(String key_user_role) {
        editor.putString(KEY_USER_ROLE, key_user_role);
        editor.commit();
    }

    public String getKeyDropoffLat() {
        return prefs.getString(KEY_DROPOFF_LAT, "");
    }

    public void setKeyDropoffLat(String key_Dropoff_lat) {
        editor.putString(KEY_DROPOFF_LAT, key_Dropoff_lat);
        editor.commit();
    }

    public String getKeyDropoffLng() {
        return prefs.getString(KEY_DROPOFF_LNG, "");
    }

    public void setKeyDropoffLng(String key_Dropoff_Lng) {
        editor.putString(KEY_DROPOFF_LNG, key_Dropoff_Lng);
        editor.commit();
    }

    public String getKeyPickupLat() {
        return prefs.getString(KEY_PICKUP_LAT, "");
    }

    public void setKeyPickupLat(String key_Pickup_lat) {
        editor.putString(KEY_PICKUP_LAT, key_Pickup_lat);
        editor.commit();
    }

    public String getKeyPickupLng() {
        return prefs.getString(KEY_PICKUP_LNG, "");
    }

    public void setKeyPickupLng(String key_Pickup_Lng) {
        editor.putString(KEY_PICKUP_LNG, key_Pickup_Lng);
        editor.commit();
    }

    public String getKeyUserLat() {
        return prefs.getString(KEY_USER_LAT, "0.0");
    }

    public void setKeyUserLat(String key_user_lat) {
        editor.putString(KEY_USER_LAT, key_user_lat);
        editor.commit();
    }

    public String getKeyUserLng() {
        return prefs.getString(KEY_USER_LNG, "0.0");
    }

    public void setKeyUserLng(String key_user_lng) {
        editor.putString(KEY_USER_LNG, key_user_lng);
        editor.commit();
    }

    public String getKeyUserRiderCommission() {
        return prefs.getString(KEY_USER_RIDER_COMMISSION, "");
    }

    public void setKeyUserRiderCommission(String key_user_rider_commission) {
        editor.putString(KEY_USER_RIDER_COMMISSION, key_user_rider_commission);
        editor.commit();
    }

    public String getKeyUserActive() {
        return prefs.getString(KEY_USER_ACTIVE, "");
    }

    public void setKeyUserActive(String key_user_active) {
        editor.putString(KEY_USER_ACTIVE, key_user_active);
        editor.commit();
    }

    public String getKeyUserCreated() {
        return prefs.getString(KEY_CREATED, "");
    }

    public void setKeyUserCreated(String key_created) {
        editor.putString(KEY_CREATED, key_created);
        editor.commit();
    }

    public Boolean getKeyIsLogin() {
        return prefs.getBoolean(KEY_IS_LOGIN, false);
    }

    public void setKeyIsLogin(Boolean keyIsLogin) {
        editor.putBoolean(KEY_IS_LOGIN, keyIsLogin);
        editor.commit();
    }

    public String getKeyUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public void setKeyUserEmail(String keyEmail) {
        editor.putString(KEY_USER_EMAIL, keyEmail);
        editor.commit();
    }

    public String getKeyUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    public void setKeyUserId(String keyUserId) {
        editor.putString(KEY_USER_ID, keyUserId);
        editor.commit();
    }

    public String getKeyUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    public void setKeyUserPhone(String keyUserPhone) {
        editor.putString(KEY_USER_PHONE, keyUserPhone);
        editor.commit();
    }

    public void clearSharedPreferences() {
        editor.clear();
        editor.commit();
    }




}
