package com.restaurant.orderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.models.Order;
import com.restaurant.orderingapp.models.User;
import com.restaurant.orderingapp.utils.CartManager;
import com.restaurant.orderingapp.utils.FirebaseHelper;
import com.restaurant.orderingapp.utils.ValidationUtils;

import java.util.Locale;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {

    private TextInputLayout tilAddress;
    private TextInputEditText etAddress;
    private RadioGroup rgPayment;
    private TextView tvCheckoutSubtotal, tvCheckoutTax, tvCheckoutTotal;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;

    private User currentUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_checkout);
        }

        initViews();
        loadUserProfile();
        displayOrderSummary();
        setupListeners();
    }

    private void initViews() {
        tilAddress = findViewById(R.id.tilCheckoutAddress);
        etAddress = findViewById(R.id.etCheckoutAddress);
        rgPayment = findViewById(R.id.rgPaymentMethod);
        tvCheckoutSubtotal = findViewById(R.id.tvCheckoutSubtotal);
        tvCheckoutTax = findViewById(R.id.tvCheckoutTax);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        progressBar = findViewById(R.id.checkoutProgressBar);
    }

    private void loadUserProfile() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid != null) {
            FirebaseHelper.getUsersRef().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentUserProfile = snapshot.getValue(User.class);
                        if (currentUserProfile != null && currentUserProfile.getDefaultAddress() != null && !currentUserProfile.getDefaultAddress().isEmpty()) {
                            etAddress.setText(currentUserProfile.getDefaultAddress());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void displayOrderSummary() {
        CartManager cm = CartManager.getInstance();
        tvCheckoutSubtotal.setText(String.format(Locale.getDefault(), "₹%.2f", cm.getSubtotal()));
        tvCheckoutTax.setText(String.format(Locale.getDefault(), "₹%.2f", cm.getTax()));
        tvCheckoutTotal.setText(String.format(Locale.getDefault(), "₹%.2f", cm.getGrandTotal()));
    }

    private void setupListeners() {
        btnPlaceOrder.setOnClickListener(v -> attemptPlaceOrder());
    }

    private void attemptPlaceOrder() {
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        tilAddress.setError(null);

        if (!ValidationUtils.isValidAddress(address)) {
            tilAddress.setError("Please enter a complete delivery address (min 5 characters)");
            return;
        }

        CartManager cm = CartManager.getInstance();
        if (cm.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int selectedPaymentId = rgPayment.getCheckedRadioButtonId();
        RadioButton rbSelected = findViewById(selectedPaymentId);
        String paymentMethod = (rbSelected != null) ? rbSelected.getText().toString() : "Cash on Delivery";

        setLoading(true);

        FirebaseUser fbUser = FirebaseHelper.getCurrentUser();
        String uid = (fbUser != null) ? fbUser.getUid() : "guest_user";
        String email = (fbUser != null && fbUser.getEmail() != null) ? fbUser.getEmail() : "customer@example.com";
        String name = (currentUserProfile != null && currentUserProfile.getName() != null) ? currentUserProfile.getName() : "Customer";

        String orderId = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Order order = new Order(
                orderId,
                uid,
                email,
                name,
                address,
                paymentMethod,
                cm.getItems(),
                cm.getSubtotal(),
                cm.getTax(),
                cm.getGrandTotal(),
                System.currentTimeMillis(),
                "PLACED"
        );

        DatabaseReference orderRef = FirebaseHelper.getOrdersRef().child(orderId);
        orderRef.setValue(order).addOnCompleteListener(task -> {
            setLoading(false);
            if (task.isSuccessful()) {
                // Link order under user profile
                if (fbUser != null) {
                    FirebaseHelper.getUsersRef().child(uid).child("orders").child(orderId).setValue(true);
                }

                // Clear cart session
                cm.clearCart();

                Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(CheckoutActivity.this, OrderHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "Failed to place order";
                Toast.makeText(CheckoutActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnPlaceOrder.setEnabled(!isLoading);
        etAddress.setEnabled(!isLoading);
        rgPayment.setEnabled(!isLoading);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
