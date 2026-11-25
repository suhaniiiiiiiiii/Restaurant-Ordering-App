package com.restaurant.orderingapp.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.restaurant.orderingapp.models.FoodItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralized helper for Firebase Auth and Realtime Database references.
 */
public class FirebaseHelper {
    public static final String REF_USERS = "users";
    public static final String REF_MENU = "menu_items";
    public static final String REF_ORDERS = "orders";

    private static FirebaseAuth auth;
    private static FirebaseDatabase database;

    public static FirebaseAuth getAuth() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        return database;
    }

    public static DatabaseReference getRootRef() {
        return getDatabase().getReference();
    }

    public static DatabaseReference getUsersRef() {
        return getRootRef().child(REF_USERS);
    }

    public static DatabaseReference getMenuRef() {
        return getRootRef().child(REF_MENU);
    }

    public static DatabaseReference getOrdersRef() {
        return getRootRef().child(REF_ORDERS);
    }

    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public static boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public static void signOut() {
        getAuth().signOut();
        CartManager.getInstance().clearCart();
    }

    /**
     * Helper to seed sample menu items if the database is freshly initialized.
     */
    public static void seedSampleMenu(OnCompleteListener<Void> onCompleteListener) {
        DatabaseReference menuRef = getMenuRef();
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem("item_1", "Margherita Pizza", "Classic cheese and fresh tomato sauce with basil", 299.00, "Pizza", "", true, 4.8));
        items.add(new FoodItem("item_2", "Crispy Chicken Burger", "Crispy fried chicken breast, spicy mayo, lettuce & pickle", 199.00, "Burgers", "", true, 4.6));
        items.add(new FoodItem("item_3", "Paneer Tikka Roll", "Charcoal-grilled spiced cottage cheese wrapped in roomali roti", 159.00, "Wraps", "", true, 4.7));
        items.add(new FoodItem("item_4", "Creamy Alfredo Pasta", "Fettuccine pasta in rich garlic parmesan cream sauce", 249.00, "Pasta", "", true, 4.5));
        items.add(new FoodItem("item_5", "Farmhouse Veggie Pizza", "Loaded with bell peppers, olives, mushrooms and sweet corn", 349.00, "Pizza", "", true, 4.7));
        items.add(new FoodItem("item_6", "Belgian Chocolate Shake", "Thick creamy shake made with authentic Belgian dark chocolate", 129.00, "Beverages", "", true, 4.9));

        for (FoodItem item : items) {
            menuRef.child(item.getId()).setValue(item);
        }
    }
}
