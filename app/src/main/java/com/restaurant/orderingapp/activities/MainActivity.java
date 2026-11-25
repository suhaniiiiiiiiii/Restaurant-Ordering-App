package com.restaurant.orderingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.adapters.MenuAdapter;
import com.restaurant.orderingapp.models.FoodItem;
import com.restaurant.orderingapp.utils.CartManager;
import com.restaurant.orderingapp.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MenuAdapter.OnAddToCartClickListener {

    private RecyclerView rvMenu;
    private MenuAdapter menuAdapter;
    private final List<FoodItem> fullMenuList = new ArrayList<>();
    private final List<FoodItem> filteredMenuList = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView tvEmptyMenu;
    private EditText etSearch;
    private ChipGroup chipGroupCategories;
    private ExtendedFloatingActionButton fabCart;

    private String selectedCategory = "All";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        initViews();
        setupRecyclerView();
        setupFilters();
        loadMenuItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartFab();
    }

    private void initViews() {
        rvMenu = findViewById(R.id.rvMenu);
        progressBar = findViewById(R.id.menuProgressBar);
        tvEmptyMenu = findViewById(R.id.tvEmptyMenu);
        etSearch = findViewById(R.id.etSearch);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        fabCart = findViewById(R.id.fabCart);

        fabCart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
    }

    private void setupRecyclerView() {
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new MenuAdapter(this, filteredMenuList, this);
        rvMenu.setAdapter(menuAdapter);
    }

    private void setupFilters() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim().toLowerCase(Locale.getDefault());
                filterMenu();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedCategory = "All";
            } else {
                Chip chip = findViewById(checkedIds.get(0));
                if (chip != null) {
                    selectedCategory = chip.getText().toString();
                } else {
                    selectedCategory = "All";
                }
            }
            filterMenu();
        });
    }

    private void loadMenuItems() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseHelper.getMenuRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                fullMenuList.clear();

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    // Seed sample data if database is fresh
                    FirebaseHelper.seedSampleMenu(null);
                    return;
                }

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    FoodItem item = itemSnap.getValue(FoodItem.class);
                    if (item != null) {
                        fullMenuList.add(item);
                    }
                }
                filterMenu();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Failed to load menu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterMenu() {
        filteredMenuList.clear();
        for (FoodItem item : fullMenuList) {
            boolean matchesCategory = selectedCategory.equalsIgnoreCase("All") ||
                    (item.getCategory() != null && item.getCategory().equalsIgnoreCase(selectedCategory));

            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    (item.getName() != null && item.getName().toLowerCase(Locale.getDefault()).contains(currentSearchQuery)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase(Locale.getDefault()).contains(currentSearchQuery));

            if (matchesCategory && matchesSearch) {
                filteredMenuList.add(item);
            }
        }

        menuAdapter.updateList(filteredMenuList);
        tvEmptyMenu.setVisibility(filteredMenuList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onAddToCart(FoodItem item) {
        CartManager.getInstance().addItem(item);
        Toast.makeText(this, item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        updateCartFab();
    }

    private void updateCartFab() {
        int count = CartManager.getInstance().getItemCount();
        if (count > 0) {
            fabCart.setText(String.format(Locale.getDefault(), "View Cart (%d) • ₹%.2f",
                    count, CartManager.getInstance().getGrandTotal()));
            fabCart.show();
        } else {
            fabCart.hide();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            return true;
        } else if (id == R.id.action_orders) {
            startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseHelper.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
