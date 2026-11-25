package com.restaurant.orderingapp.utils;

import com.restaurant.orderingapp.models.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility methods for input validation and financial calculations.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        return !trimmed.isEmpty() && EMAIL_PATTERN.matcher(trimmed).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.trim().length() >= 6;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String clean = phone.replaceAll("[\\s-()]", "");
        return PHONE_PATTERN.matcher(clean).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null) return false;
        return name.trim().length() >= 2;
    }

    public static boolean isValidAddress(String address) {
        if (address == null) return false;
        return address.trim().length() >= 5;
    }

    public static double calculateSubtotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) return 0.0;
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item != null && item.getFoodItem() != null && item.getQuantity() > 0) {
                BigDecimal price = BigDecimal.valueOf(item.getFoodItem().getPrice());
                BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
                subtotal = subtotal.add(price.multiply(qty));
            }
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double calculateTax(double subtotal, double taxRate) {
        if (subtotal <= 0 || taxRate <= 0) return 0.0;
        BigDecimal sub = BigDecimal.valueOf(subtotal);
        BigDecimal rate = BigDecimal.valueOf(taxRate);
        return sub.multiply(rate).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double calculateGrandTotal(double subtotal, double tax) {
        BigDecimal sub = BigDecimal.valueOf(Math.max(0.0, subtotal));
        BigDecimal tx = BigDecimal.valueOf(Math.max(0.0, tax));
        return sub.add(tx).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
