package app.dku.searchmabzip;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private final List<PlaceDocument> restaurantList;
    private final Context context;
    private final RatingRepository ratingRepository;
    private final Map<String, PlaceRating> ratingCache = new ConcurrentHashMap<>();

    public RestaurantAdapter(Context context, List<PlaceDocument> restaurantList, RatingRepository ratingRepository) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.ratingRepository = ratingRepository;
    }

    // --- ViewHolder 정의 ---
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName;
        public TextView tvCategories;
        public TextView tvRating;
        public ImageView restaurantPhoto;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            tvCategories = itemView.findViewById(R.id.tv_categories);
            tvRating = itemView.findViewById(R.id.tv_rating);
            restaurantPhoto = itemView.findViewById(R.id.iv_restaurant_photo);
        }
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        PlaceDocument place = restaurantList.get(position);

        holder.restaurantName.setText(place.getPlaceName());

        String formattedCategories = formatCategories(place.getCategoryName());
        holder.tvCategories.setText(formattedCategories);

        String ratingText = formatRatingText(place.getId());
        holder.tvRating.setText(ratingText);

        holder.itemView.setOnClickListener(v -> promptRatingDialog(place, holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void setRatingCache(Map<String, PlaceRating> ratings) {
        ratingCache.clear();
        if (ratings != null) {
            ratingCache.putAll(ratings);
        }
        notifyDataSetChanged();
    }

    private String formatRatingText(String placeId) {
        PlaceRating rating = ratingCache.get(placeId);
        if (rating == null) {
            return "";
        }
        return "별점: " + rating.getRating() + "/5";
    }

    private String formatCategories(String fullCategoryName) {
        if (fullCategoryName == null || fullCategoryName.isEmpty()) {
            return "";
        }
        String[] parts = fullCategoryName.split(">");
        StringBuilder tags = new StringBuilder();

        for (int i = 1; i < parts.length; i++) {
            String tag = parts[i].trim();
            if (!tag.isEmpty()) {
                tags.append("#").append(tag).append(" ");
            }
        }
        return tags.toString().trim();
    }

    private void promptRatingDialog(PlaceDocument place, int adapterPosition) {
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }
        String placeId = place.getId();
        if (TextUtils.isEmpty(placeId)) {
            Toast.makeText(context, "장소 ID가 없어 별점을 남길 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rating, null, false);
        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        int current = ratingCache.getOrDefault(placeId, new PlaceRating(placeId, 3, place.getCategoryName(), System.currentTimeMillis())).getRating();
        ratingBar.setRating(current);

        new AlertDialog.Builder(context)
                .setTitle(place.getPlaceName() + " 별점 선택")
                .setView(dialogView)
                .setPositiveButton("확인", (dialog, which) -> {
                    int rating = Math.round(ratingBar.getRating());
                    ratingRepository.saveRating(placeId, place.getCategoryName(), rating, () -> {
                        ratingCache.put(placeId, new PlaceRating(placeId, rating, place.getCategoryName(), System.currentTimeMillis()));
                        RatedPlaceStore.savePlaceMeta(context, placeId, place.getPlaceName(), place.getCategoryName());
                        notifyItemChanged(adapterPosition);
                        Toast.makeText(context, "별점 저장됨: " + rating, Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
