package com.qboxus.godelivery.HelpingClasses;

public class ApiUrl {

    public static String ApiKey="api-key";

    public static String baseUrl = "domain.com";

    private static String apiDomain = baseUrl +"api/";

    public static String paymentCheckoutUrl = baseUrl +"payment/index.php?id=";

    public static String registerUser = apiDomain + "registerUser";

    public static String showCountries = apiDomain + "showCountries";

    public static String giveRatingsToDriver = apiDomain + "giveRatingsToDriver";

    public static String login = apiDomain + "login";

    public static String forgotPassword = apiDomain + "forgotPassword";

    public static String verifyForgotPasswordcode = apiDomain + "verifyforgotPasswordCode";

    public static String verifyRegisterphoneAuthcode = apiDomain + "verifyPhoneNo";

    public static String changeForgotPassword = apiDomain + "changePasswordForgot";

    public static String changePassword = apiDomain + "changePassword";

    public static String editProfile = apiDomain + "editProfile";

    public static String showGoodTypes = apiDomain + "showGoodTypes";

    public static String showVehicleTypes = apiDomain + "showVehicleTypes";

    public static String showPackageSize = apiDomain + "showPackageSize";

    public static String readNotification = apiDomain + "readNotification";

    public static String showUserOrders = apiDomain + "showUserOrders";

    public static String verifyCoupon = apiDomain + "verifyCoupon";

    public static String addOrderSession = apiDomain + "addOrderSession";

    public static String addUserImage = apiDomain +"addUserImage";

    public static String showRiderOrderDetails = apiDomain +"showRiderOrderDetails";

    public static String updateOrderStatus = apiDomain +"updateOrderStatus";

    public static String addPaymentMethod = apiDomain +"addPaymentCard";

    public static String showPaymentDetails = apiDomain +"showPaymentDetails";

    public static String deletePaymentCard = apiDomain +"deletePaymentCard";

    public static String addDeviceData = apiDomain +"addDeviceData";

    public static String sendMessageNotification = apiDomain +"sendMessageNotification";

    public static String logout = apiDomain +"logout";

    public static String showDefaultCurrency = apiDomain +"showDefaultCurrency";

    public static String apiForIP = "https://api.ipify.org/?format=json";

    public static String changePhoneNo = apiDomain + "changePhoneNo";


}
