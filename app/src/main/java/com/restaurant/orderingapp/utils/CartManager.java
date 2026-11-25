package com.restaurant.orderingapp.utils;

import com.restaurant.orderingapp.models.CartItem;
import com.restaurant.orderingapp.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton managing user's active shopping cart session.
 */
public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems;
    public static final double DEFAULT_TAX_RATE = 0.05; // 5%

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public synchronized List<CartItem> getItems() {
        return new ArrayList<>(cartItems);
    }

    public synchronized void addItem(FoodItem foodItem) {
        if (foodItem == null || !foodItem.isAvailable()) return;

        for (CartItem item : cartItems) {
            if (item.getFoodItem().getId().equals(foodItem.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(foodItem, 1));
    }

    public synchronized void updateQuantity(String foodId, int quantity) {
        if (foodId == null) return;
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            if (item.getFoodItem().getId().equals(foodId)) {
                if (quantity <= 0) {
                    cartItems.remove(i);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public synchronized void removeItem(String foodId) {
        if (foodId == null) return;
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getFoodItem().getId().equals(foodId)) {
                cartItems.remove(i);
                return;
            }
        }
    }

    public synchronized void clearCart() {
        cartItems.clear();
    }

    public synchronized int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public synchronized double getSubtotal() {
        return ValidationUtils.calculateSubtotal(cartItems);
    }

    public synchronized double getTax() {
        return ValidationUtils.calculateTax(getSubtotal(), DEFAULT_TAX_RATE);
    }

    public synchronized double getGrandTotal() {
        return ValidationUtils.calculateGrandTotal(getSubtotal(), getTax());
    }

    public synchronized boolean isEmpty() {
        return cartItems.isEmpty();
    }
}
