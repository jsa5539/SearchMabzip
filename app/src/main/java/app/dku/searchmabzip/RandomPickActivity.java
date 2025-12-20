package app.dku.searchmabzip;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomPickActivity extends BaseActivity {

    private RatingRepository ratingRepository;
    private TextView nameView;
    private TextView categoryView;
    private TextView ratingView;
    private TextView emptyView;
    private final Random random = new Random();
    private final List<PlaceRating> candidates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentLayout(R.layout.activity_random_pick);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ratingRepository = new RatingRepository(this);
        nameView = findViewById(R.id.tv_random_name);
        categoryView = findViewById(R.id.tv_random_category);
        ratingView = findViewById(R.id.tv_random_rating);
        emptyView = findViewById(R.id.tv_random_empty);
        Button reroll = findViewById(R.id.btn_random_again);

        if (reroll != null) {
            reroll.setOnClickListener(v -> pickOne());
        }

        loadCandidates();
    }

    private void loadCandidates() {
        ratingRepository.loadAllRatings(this::handleRatingsLoaded);
    }

    private void handleRatingsLoaded(Map<String, PlaceRating> ratings) {
        candidates.clear();
        for (PlaceRating pr : ratings.values()) {
            if (pr.getRating() >= 4) {
                candidates.add(pr);
            }
        }
        if (candidates.isEmpty()) {
            showEmpty();
        } else {
            hideEmpty();
            pickOne();
        }
    }

    private void pickOne() {
        if (candidates.isEmpty()) {
            showEmpty();
            return;
        }
        int idx = random.nextInt(candidates.size());
        PlaceRating pick = candidates.get(idx);

        RatedPlaceStore.RatedPlaceMeta meta = RatedPlaceStore.getMeta(this, pick.getPlaceId());
        String name = meta != null && !TextUtils.isEmpty(meta.getPlaceName())
                ? meta.getPlaceName()
                : "이름 정보 없음";
        String categoryName = meta != null && !TextUtils.isEmpty(meta.getCategoryName())
                ? meta.getCategoryName()
                : pick.getCategoryName();

        if (nameView != null) nameView.setText(name);
        if (categoryView != null) categoryView.setText(categoryName != null ? categoryName : "");
        if (ratingView != null) ratingView.setText("별점: " + pick.getRating() + "/5");
    }

    private void showEmpty() {
        if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
        if (nameView != null) nameView.setVisibility(View.GONE);
        if (categoryView != null) categoryView.setVisibility(View.GONE);
        if (ratingView != null) ratingView.setVisibility(View.GONE);
    }

    private void hideEmpty() {
        if (emptyView != null) emptyView.setVisibility(View.GONE);
        if (nameView != null) nameView.setVisibility(View.VISIBLE);
        if (categoryView != null) categoryView.setVisibility(View.VISIBLE);
        if (ratingView != null) ratingView.setVisibility(View.VISIBLE);
    }
}
