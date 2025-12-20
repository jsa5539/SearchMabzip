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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private final List<PlaceDocument> restaurantList;
    private final Context context;
    private final RatingRepository ratingRepository;
    private final Map<String, PlaceRating> ratingCache = new ConcurrentHashMap<>();
    private final Set<String> recommendedPlaceIds = ConcurrentHashMap.newKeySet();

    private static final int K_NEIGHBORS = 3;
    private static final double RECOMMEND_THRESHOLD = 3.5;
    private static final double MIN_SIMILARITY = 0.1;

    public RestaurantAdapter(Context context, List<PlaceDocument> restaurantList, RatingRepository ratingRepository) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.ratingRepository = ratingRepository;
    }

    // --- ViewHolder ---
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName;
        public TextView tvCategories;
        public TextView recommendBadge;
        public ImageView restaurantPhoto;
        public RatingBar ratingBarDisplay;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            tvCategories = itemView.findViewById(R.id.tv_categories);
            recommendBadge = itemView.findViewById(R.id.tv_recommend_badge);
            ratingBarDisplay = itemView.findViewById(R.id.rating_bar_display);
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

        PlaceRating rating = ratingCache.get(place.getId());
        if (rating != null) {
            holder.ratingBarDisplay.setVisibility(View.VISIBLE);
            holder.ratingBarDisplay.setRating(rating.getRating());
        } else {
            holder.ratingBarDisplay.setVisibility(View.GONE);
        }

        boolean recommended = isRecommended(place.getId());
        holder.recommendBadge.setVisibility(recommended ? View.VISIBLE : View.GONE);

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
        recalculateRecommendations();
        notifyDataSetChanged();
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

    private boolean isRecommended(String placeId) {
        return recommendedPlaceIds.contains(placeId);
    }

    private void recalculateRecommendations() {
        recommendedPlaceIds.clear();
        if (ratingCache.isEmpty()) {
            return;
        }
        for (PlaceDocument place : restaurantList) {
            String placeId = place.getId();
            if (TextUtils.isEmpty(placeId)) {
                continue;
            }
            double predicted = predictScoreForPlace(place);
            if (!Double.isNaN(predicted) && predicted >= RECOMMEND_THRESHOLD) {
                recommendedPlaceIds.add(placeId);
            }
        }
    }

    private double predictScoreForPlace(PlaceDocument place) {
        String candidateCategory = place.getCategoryName();
        if (TextUtils.isEmpty(candidateCategory)) {
            return Double.NaN;
        }

        List<Neighbor> neighbors = new ArrayList<>();
        for (PlaceRating rating : ratingCache.values()) {
            double similarity = computeCategorySimilarity(candidateCategory, rating.getCategoryName());
            if (similarity >= MIN_SIMILARITY) {
                neighbors.add(new Neighbor(similarity, rating.getRating()));
            }
        }
        if (neighbors.isEmpty()) {
            return Double.NaN;
        }

        neighbors.sort((a, b) -> Double.compare(b.similarity, a.similarity));
        int limit = Math.min(K_NEIGHBORS, neighbors.size());

        double weightedSum = 0.0;
        double weightSum = 0.0;
        for (int i = 0; i < limit; i++) {
            Neighbor neighbor = neighbors.get(i);
            weightedSum += neighbor.similarity * neighbor.rating;
            weightSum += neighbor.similarity;
        }
        if (weightSum == 0.0) {
            return Double.NaN;
        }
        return weightedSum / weightSum;
    }

    private double computeCategorySimilarity(String categoryA, String categoryB) {
        List<String> tokensA = normalizeCategory(categoryA);
        List<String> tokensB = normalizeCategory(categoryB);
        if (tokensA.isEmpty() || tokensB.isEmpty()) {
            return 0.0;
        }

        Set<String> setA = new HashSet<>(tokensA);
        Set<String> setB = new HashSet<>(tokensB);

        int intersection = 0;
        for (String token : setA) {
            if (setB.contains(token)) {
                intersection++;
            }
        }
        int union = setA.size() + setB.size() - intersection;
        double jaccard = union == 0 ? 0.0 : (double) intersection / union;

        // Boost if the main category (first token) matches to favor closer neighbors.
        if (!tokensA.isEmpty() && !tokensB.isEmpty() && tokensA.get(0).equals(tokensB.get(0))) {
            jaccard += 0.2;
        }
        return Math.min(jaccard, 1.0);
    }

    private List<String> normalizeCategory(String raw) {
        if (raw == null) {
            return Collections.emptyList();
        }
        String[] parts = raw.split(">");
        List<String> tokens = new ArrayList<>();
        for (String part : parts) {
            String token = part.trim().toLowerCase();
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    private void promptRatingDialog(PlaceDocument place, int adapterPosition) {
        if (adapterPosition == RecyclerView.NO_POSITION) {
            return;
        }
        String placeId = place.getId();
        if (TextUtils.isEmpty(placeId)) {
            Toast.makeText(context, "장소 ID가 없어 별점을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
                        recalculateRecommendations();
                        notifyDataSetChanged();
                        Toast.makeText(context, "별점 저장됨: " + rating, Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private static class Neighbor {
        final double similarity;
        final int rating;

        Neighbor(double similarity, int rating) {
            this.similarity = similarity;
            this.rating = rating;
        }
    }
}
