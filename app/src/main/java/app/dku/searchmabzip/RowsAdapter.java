package app.dku.searchmabzip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RowsAdapter extends RecyclerView.Adapter<RowsAdapter.HeaderViewHolder> {

    private final List<Row> items;
    private final Context context;

    public RowsAdapter(Context context, List<Row> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header, parent, false);
        return new HeaderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        Row row = items.get(position);
        Row.Header header = (Row.Header) row;

        holder.tvRestaurantName.setText(header.getRestaurantName());
        holder.tvCategories.setText(header.getCategories());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Header ViewHolder만 유지
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRestaurantPhoto;
        TextView tvRestaurantName;
        TextView tvCategories;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvRestaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            tvCategories = itemView.findViewById(R.id.tv_categories);
        }
    }
}
