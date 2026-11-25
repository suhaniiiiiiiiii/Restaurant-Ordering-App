package com.restaurant.orderingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.models.FoodItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    public interface OnAddToCartClickListener {
        void onAddToCart(FoodItem item);
    }

    private final Context context;
    private List<FoodItem> foodList;
    private final OnAddToCartClickListener listener;

    public MenuAdapter(Context context, List<FoodItem> foodList, OnAddToCartClickListener listener) {
        this.context = context;
        this.foodList = foodList != null ? foodList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<FoodItem> newList) {
        this.foodList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        FoodItem item = foodList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDescription.setText(item.getDescription());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));
        holder.tvCategory.setText(item.getCategory());
        holder.tvRating.setText(String.format(Locale.getDefault(), "★ %.1f", item.getRating()));

        holder.btnAddToCart.setEnabled(item.isAvailable());
        if (item.isAvailable()) {
            holder.btnAddToCart.setText(R.string.add_to_cart);
            holder.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCart(item);
                }
            });
        } else {
            holder.btnAddToCart.setText("Out of Stock");
        }
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice, tvCategory, tvRating;
        Button btnAddToCart;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvDescription = itemView.findViewById(R.id.tvFoodDescription);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvCategory = itemView.findViewById(R.id.tvFoodCategory);
            tvRating = itemView.findViewById(R.id.tvFoodRating);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
