package app.dku.searchmabzip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RatedRestaurantAdapter extends RecyclerView.Adapter<RatedRestaurantAdapter.RatedViewHolder> {

    public interface OnRatingEditedListener {
        void onRatingChanged(RatedPlaceItem item, int rating);
    }

    private final Context context;
    private final OnRatingEditedListener listener;
    private final List<RatedPlaceItem> items = new ArrayList<>();

    public RatedRestaurantAdapter(Context context, OnRatingEditedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RatedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rated_restaurant, parent, false);
        return new RatedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatedViewHolder holder, int position) {
        RatedPlaceItem item = items.get(position);
        holder.name.setText(item.getDisplayName());
        holder.category.setText(item.getCategoryName());

        holder.ratingBar.setOnRatingBarChangeListener(null);
        holder.ratingBar.setRating(item.getRating());
        holder.ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser && listener != null) {
                listener.onRatingChanged(item, Math.round(rating));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<RatedPlaceItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void updateRating(String placeId, int rating) {
        for (int i = 0; i < items.size(); i++) {
            RatedPlaceItem item = items.get(i);
            if (item.getPlaceId().equals(placeId)) {
                item.setRating(rating);
                notifyItemChanged(i);
                return;
            }
        }
    }

    static class RatedViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView category;
        RatingBar ratingBar;

        RatedViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.rated_place_name);
            category = itemView.findViewById(R.id.rated_place_category);
            ratingBar = itemView.findViewById(R.id.rated_place_rating);
        }
    }

    public static class RatedPlaceItem {
        private final String placeId;
        private final String displayName;
        private final String categoryName;
        private final long savedAt;
        private int rating;

        public RatedPlaceItem(String placeId, String displayName, String categoryName, int rating, long savedAt) {
            this.placeId = placeId;
            this.displayName = displayName;
            this.categoryName = categoryName;
            this.rating = rating;
            this.savedAt = savedAt;
        }

        public String getPlaceId() {
            return placeId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public long getSavedAt() {
            return savedAt;
        }
    }
}
