package com.restaurant.orderingapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.adapters.OrderAdapter;
import com.restaurant.orderingapp.models.Order;
import com.restaurant.orderingapp.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private final List<Order> orderList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView tvEmptyOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_order_history);
        }

        initViews();
        setupRecyclerView();
        fetchOrders();
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.ordersProgressBar);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orderList);
        rvOrders.setAdapter(orderAdapter);
    }

    private void fetchOrders() {
        String uid = FirebaseHelper.getCurrentUserId();
        if (uid == null) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            tvEmptyOrders.setText("Please sign in to view orders.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Query orders matching current user's userId
        Query query = FirebaseHelper.getOrdersRef().orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                orderList.clear();

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    Order order = itemSnap.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }

                // Show latest orders first
                Collections.reverse(orderList);

                orderAdapter.updateList(orderList);
                tvEmptyOrders.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderHistoryActivity.this, "Error fetching orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
