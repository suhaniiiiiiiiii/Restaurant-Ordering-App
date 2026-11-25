package com.restaurant.orderingapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model representing a customer food order.
 */
public class Order implements Serializable {
    private String orderId;
    private String userId;
    private String userEmail;
    private String userName;
    private String deliveryAddress;
    private String paymentMethod;
    private List<CartItem> items;
    private double subtotal;
    private double tax;
    private double grandTotal;
    private long timestamp;
    private String status; // PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(String orderId, String userId, String userEmail, String userName,
                 String deliveryAddress, String paymentMethod, List<CartItem> items,
                 double subtotal, double tax, double grandTotal, long timestamp, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.items = items != null ? items : new ArrayList<>();
        this.subtotal = subtotal;
        this.tax = tax;
        this.grandTotal = grandTotal;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
