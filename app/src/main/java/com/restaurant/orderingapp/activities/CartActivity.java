package com.restaurant.orderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.adapters.CartAdapter;
import com.restaurant.orderingapp.models.CartItem;
import com.restaurant.orderingapp.utils.CartManager;

import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemInteractionListener {

    private RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private TextView tvSubtotal, tvTax, tvGrandTotal;
    private Button btnCheckout, btnBrowseMenu;
    private LinearLayout layoutCartSummary, layoutEmptyCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_cart);
        }

        initViews();
        setupRecyclerView();
        updateUI();
    }

    private void initViews() {
        rvCart = findViewById(R.id.rvCart);
        tvSubtotal = findViewById(R.id.tvCartSubtotal);
        tvTax = findViewById(R.id.tvCartTax);
        tvGrandTotal = findViewById(R.id.tvCartGrandTotal);
        btnCheckout = findViewById(R.id.btnCartCheckout);
        btnBrowseMenu = findViewById(R.id.btnBrowseMenu);
        layoutCartSummary = findViewById(R.id.layoutCartSummary);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().isEmpty()) {
                Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
        });

        btnBrowseMenu.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, CartManager.getInstance().getItems(), this);
        rvCart.setAdapter(cartAdapter);
    }

    private void updateUI() {
        CartManager cm = CartManager.getInstance();
        boolean isEmpty = cm.isEmpty();

        if (isEmpty) {
            rvCart.setVisibility(View.GONE);
            layoutCartSummary.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE);
        } else {
            rvCart.setVisibility(View.VISIBLE);
            layoutCartSummary.setVisibility(View.VISIBLE);
            layoutEmptyCart.setVisibility(View.GONE);

            cartAdapter.updateList(cm.getItems());
            tvSubtotal.setText(String.format(Locale.getDefault(), "₹%.2f", cm.getSubtotal()));
            tvTax.setText(String.format(Locale.getDefault(), "₹%.2f", cm.getTax()));
            tvGrandTotal.setText(String.format(Locale.getDefault(), "₹%.2f", cm.getGrandTotal()));
        }
    }

    @Override
    public void onQuantityIncrement(CartItem item) {
        if (item.getFoodItem() == null) return;
        CartManager.getInstance().updateQuantity(item.getFoodItem().getId(), item.getQuantity() + 1);
        updateUI();
    }

    @Override
    public void onQuantityDecrement(CartItem item) {
        if (item.getFoodItem() == null) return;
        CartManager.getInstance().updateQuantity(item.getFoodItem().getId(), item.getQuantity() - 1);
        updateUI();
    }

    @Override
    public void onItemRemoved(CartItem item) {
        if (item.getFoodItem() == null) return;
        CartManager.getInstance().removeItem(item.getFoodItem().getId());
        Toast.makeText(this, item.getFoodItem().getName() + " removed", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
