package com.restaurant.orderingapp.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.restaurant.orderingapp.R;
import com.restaurant.orderingapp.models.CartItem;
import com.restaurant.orderingapp.models.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders != null ? orders : new ArrayList<>();
    }

    public void updateList(List<Order> newOrders) {
        this.orders = newOrders != null ? newOrders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Order #" + order.getOrderId());
        CharSequence dateStr = DateFormat.format("dd MMM yyyy, hh:mm a", new Date(order.getTimestamp()));
        holder.tvOrderDate.setText(dateStr);

        // Format summary of items
        StringBuilder summary = new StringBuilder();
        if (order.getItems() != null) {
            for (CartItem ci : order.getItems()) {
                if (ci.getFoodItem() != null) {
                    summary.append(ci.getQuantity())
                            .append("x ")
                            .append(ci.getFoodItem().getName())
                            .append(", ");
                }
            }
            if (summary.length() > 2) {
                summary.setLength(summary.length() - 2);
            }
        }
        holder.tvOrderSummary.setText(summary.toString());
        holder.tvOrderAddress.setText("Delivery: " + order.getDeliveryAddress());
        holder.tvOrderTotal.setText(String.format(Locale.getDefault(), "Total: ₹%.2f (%s)", order.getGrandTotal(), order.getPaymentMethod()));

        String status = order.getStatus() != null ? order.getStatus() : "PLACED";
        holder.tvOrderStatus.setText(status);

        switch (status) {
            case "DELIVERED":
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.status_delivered));
                break;
            case "OUT_FOR_DELIVERY":
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.status_success));
                break;
            case "CANCELLED":
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.error));
                break;
            default:
                holder.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderSummary, tvOrderAddress, tvOrderTotal, tvOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderSummary = itemView.findViewById(R.id.tvOrderSummary);
            tvOrderAddress = itemView.findViewById(R.id.tvOrderAddress);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}
