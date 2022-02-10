package com.qboxus.godelivery.ModelClasses;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class OrderModel implements Serializable {

    private String deliveryType,orderStatus,pickupExtraDetail,pickupAddress,pickupTime,dropoffAddress,dropoffExtraDetail,dropoffTime
            ,senderName,senderPhone,mapImg,driverImg,driverName,driverId,receiverPhone,packageType,receiverName,packageSize,createdTime,
            finalFare ="0",driverRating ="0";
    private int orderId;
    private double senderLat,senderLng,receiverLat,receiverLng;
    private long notificationCount;

    public OrderModel() {
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getPickupExtraDetail() {
        return pickupExtraDetail;
    }

    public void setPickupExtraDetail(String pickupExtraDetail) {
        this.pickupExtraDetail = pickupExtraDetail;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getDropoffAddress() {
        return dropoffAddress;
    }

    public void setDropoffAddress(String dropoffAddress) {
        this.dropoffAddress = dropoffAddress;
    }

    public String getDropoffExtraDetail() {
        return dropoffExtraDetail;
    }

    public void setDropoffExtraDetail(String dropoffExtraDetail) {
        this.dropoffExtraDetail = dropoffExtraDetail;
    }

    public String getDropoffTime() {
        return dropoffTime;
    }

    public void setDropoffTime(String dropoffTime) {
        this.dropoffTime = dropoffTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public double getSenderLat() {
        return senderLat;
    }

    public void setSenderLat(double senderLat) {
        this.senderLat = senderLat;
    }

    public double getSenderLng() {
        return senderLng;
    }

    public void setSenderLng(double senderLng) {
        this.senderLng = senderLng;
    }

    public double getReceiverLat() {
        return receiverLat;
    }

    public void setReceiverLat(double receiverLat) {
        this.receiverLat = receiverLat;
    }

    public double getReceiverLng() {
        return receiverLng;
    }

    public void setReceiverLng(double receiverLng) {
        this.receiverLng = receiverLng;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(String packageSize) {
        this.packageSize = packageSize;
    }

    public String getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(String finalFare) {
        this.finalFare = finalFare;
    }

    public String getMapImg() {
        return mapImg;
    }

    public void setMapImg(String mapImg) {
        this.mapImg = mapImg;
    }

    public String getDriverImg() {
        return driverImg;
    }

    public void setDriverImg(String driverImg) {
        this.driverImg = driverImg;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(String driverRating) {
        this.driverRating = driverRating;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public long getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(long notificationCount) {
        this.notificationCount = notificationCount;
    }
}
