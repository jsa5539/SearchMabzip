package app.dku.searchmabzip;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MabzipList extends BaseActivity {

    private List<PlaceDocument> restaurantList;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private RatingRepository ratingRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentLayout(R.layout.activity_mabzip_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 선호도 표시
        AppDataManager dm = AppDataManager.getInstance();
        double rating = dm.getRatingFilterValue();
        int progress = dm.getSeekBarProgress();
        boolean switchOn = dm.getSwitchState();
        TextView prefText = findViewById(R.id.text_preferences_summary);
        if (prefText != null) {
            String summary = "평점 " + String.format("%.1f", rating) +"미만 제외\n"
                    + progress +"일동안 먹은 음식 제외 " + (switchOn ? "켜짐" : "꺼짐") +"\n"
                    + "동일 계열 메뉴 제외 " + (switchOn ? "켜짐" : "꺼짐");
            prefText.setText(summary);
        }

        // 결과 리스트 로드 (ResultCache에서 가져옴)
        if (ResultCache.lastResults != null) {
            restaurantList = ResultCache.lastResults;
        } else {
            restaurantList = new ArrayList<>();
        }

        // RecyclerView 설정
        recyclerView = findViewById(R.id.restaurantRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingRepository = new RatingRepository(this);
        adapter = new RestaurantAdapter(this, restaurantList, ratingRepository);
        recyclerView.setAdapter(adapter);

        ratingRepository.loadAllRatings(ratings -> {
            applyPreferencesFilter(ratings);
            adapter.setRatingCache(ratings);
        });
    }

    private void applyPreferencesFilter(Map<String, PlaceRating> ratings) {
        AppDataManager dm = AppDataManager.getInstance();
        double minRating = dm.getRatingFilterValue();
        int days = dm.getSeekBarProgress();
        boolean excludeSameMenu = dm.getSwitchState();
        long cutoff = System.currentTimeMillis() - (long) days * 24L * 60L * 60L * 1000L;

        Iterator<PlaceDocument> iterator = restaurantList.iterator();
        while (iterator.hasNext()) {
            PlaceDocument place = iterator.next();
            PlaceRating saved = ratings.get(place.getId());
            if (saved == null) {
                continue;
            }
            if (saved.getRating() < minRating) {
                iterator.remove();
                continue;
            }
            if (excludeSameMenu && days > 0) {
                String placeCat = extractMainCategory(place.getCategoryName());
                String savedCat = extractMainCategory(saved.getCategoryName());
                if (!placeCat.isEmpty() && placeCat.equals(savedCat) && saved.getSavedAt() >= cutoff) {
                    iterator.remove();
                }
            }
        }
    }

    private String extractMainCategory(String fullCategoryName) {
        if (fullCategoryName == null || fullCategoryName.isEmpty()) {
            return "";
        }
        String[] parts = fullCategoryName.split(">");
        if (parts.length < 2) {
            return parts[0].trim();
        }
        return parts[1].trim();
    }
}
