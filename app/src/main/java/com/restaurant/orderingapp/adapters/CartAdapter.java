package com.restaurant.orderingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.models.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartItemInteractionListener {
        void onQuantityIncrement(CartItem item);
        void onQuantityDecrement(CartItem item);
        void onItemRemoved(CartItem item);
    }

    private final Context context;
    private List<CartItem> cartItems;
    private final OnCartItemInteractionListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemInteractionListener listener) {
        this.context = context;
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<CartItem> newList) {
        this.cartItems = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        if (cartItem.getFoodItem() == null) return;

        holder.tvItemName.setText(cartItem.getFoodItem().getName());
        holder.tvUnitPrice.setText(String.format(Locale.getDefault(), "₹%.2f each", cartItem.getFoodItem().getPrice()));
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.tvSubtotal.setText(String.format(Locale.getDefault(), "₹%.2f", cartItem.getSubtotal()));

        holder.btnIncrement.setOnClickListener(v -> {
            if (listener != null) listener.onQuantityIncrement(cartItem);
        });

        holder.btnDecrement.setOnClickListener(v -> {
            if (listener != null) listener.onQuantityDecrement(cartItem);
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onItemRemoved(cartItem);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvUnitPrice, tvQuantity, tvSubtotal;
        ImageButton btnIncrement, btnDecrement, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvCartItemName);
            tvUnitPrice = itemView.findViewById(R.id.tvCartUnitPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
            btnIncrement = itemView.findViewById(R.id.btnCartIncrement);
            btnDecrement = itemView.findViewById(R.id.btnCartDecrement);
            btnRemove = itemView.findViewById(R.id.btnCartRemove);
        }
    }
}
