package app.dku.searchmabzip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChartActivity extends BaseActivity {

    private static final String TAG = "ChartActivity";
    private static final String[] DEFAULT_KEYWORDS = new String[]{
            "용인시 죽전동 단국대 근처 음식점",
            "단국대",
            "단국대학교",
            "경기 용인시 수지구 죽전로"
    };

    private KakaoLocalApiService apiService;
    private RatingRepository ratingRepository;
    private RatedRestaurantAdapter ratedAdapter;
    private RecyclerView ratedRecyclerView;
    private TextView ratedEmptyText;
    private View ratedListContainer;

    private final List<PlaceDocument> combinedRestaurantList = new ArrayList<>();
    private int searchCompletedCount = 0;
    private int totalSearches = DEFAULT_KEYWORDS.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentLayout(R.layout.activity_chart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ratingRepository = new RatingRepository(this);
        renderCategoryChart();
        setupRatedListBox();

        initRetrofit();

        Button nextButton = findViewById(R.id.newbtn);
        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    combinedRestaurantList.clear();
                    searchCompletedCount = 0;
                    totalSearches = DEFAULT_KEYWORDS.length;

                    Toast.makeText(ChartActivity.this, "음식점 정보를 검색합니다...", Toast.LENGTH_SHORT).show();

                    for (String keyword : DEFAULT_KEYWORDS) {
                        searchRestaurantList(keyword);
                    }
                }
            });
        }
    }

    private void setupRatedListBox() {
        View box = findViewById(R.id.ratedListBox);
        ratedListContainer = findViewById(R.id.ratedListContainer);
        ratedRecyclerView = findViewById(R.id.ratedRecyclerViewInline);
        ratedEmptyText = findViewById(R.id.ratedInlineEmpty);

        if (ratedRecyclerView != null) {
            ratedAdapter = new RatedRestaurantAdapter(this, new RatedRestaurantAdapter.OnRatingEditedListener() {
                @Override
                public void onRatingChanged(RatedRestaurantAdapter.RatedPlaceItem item, int rating) {
                    ratingRepository.saveRating(item.getPlaceId(), item.getCategoryName(), rating, () -> {
                        RatedPlaceStore.savePlaceMeta(ChartActivity.this, item.getPlaceId(), item.getDisplayName(), item.getCategoryName());
                        ratedAdapter.updateRating(item.getPlaceId(), rating);
                        renderCategoryChart();
                        Toast.makeText(ChartActivity.this, "별점이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
            ratedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            ratedRecyclerView.setAdapter(ratedAdapter);
        }

        if (box == null) {
            return;
        }
        box.setOnClickListener(v -> ratingRepository.loadAllRatings(ratings -> {
            if (ratedListContainer != null && ratedListContainer.getVisibility() == View.VISIBLE) {
                toggleRatedVisibility(false);
                return;
            }
            if (ratings.isEmpty()) {
                toggleRatedVisibility(false);
                toggleRatedEmpty(true);
                return;
            }
            List<RatedRestaurantAdapter.RatedPlaceItem> items = new ArrayList<>();
            for (PlaceRating rating : ratings.values()) {
                RatedPlaceStore.RatedPlaceMeta meta = RatedPlaceStore.getMeta(this, rating.getPlaceId());
                String name = meta != null && !TextUtils.isEmpty(meta.getPlaceName()) ? meta.getPlaceName() : "이름 정보 없음";
                String categoryName = meta != null && !TextUtils.isEmpty(meta.getCategoryName())
                        ? meta.getCategoryName()
                        : rating.getCategoryName();
                items.add(new RatedRestaurantAdapter.RatedPlaceItem(
                        rating.getPlaceId(),
                        name,
                        categoryName,
                        rating.getRating(),
                        rating.getSavedAt()
                ));
            }
            items.sort(Comparator.comparingLong(RatedRestaurantAdapter.RatedPlaceItem::getSavedAt).reversed());
            if (ratedAdapter != null) {
                ratedAdapter.setItems(items);
            }
            toggleRatedEmpty(items.isEmpty());
            toggleRatedVisibility(true);
        }));
    }

    private void toggleRatedEmpty(boolean empty) {
        if (ratedEmptyText != null) {
            ratedEmptyText.setVisibility(empty ? View.VISIBLE : View.GONE);
        }
        if (ratedRecyclerView != null) {
            ratedRecyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        }
    }

    private void toggleRatedVisibility(boolean show) {
        if (ratedListContainer != null) {
            ratedListContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void renderCategoryChart() {
        LinearLayout container = findViewById(R.id.ratingChartContainer);
        if (container == null) return;

        ratingRepository.loadAllRatings(ratings -> {
            container.removeAllViews();
            if (ratings.isEmpty()) {
                TextView empty = new TextView(this);
                empty.setText("저장된 별점이 없습니다.");
                container.addView(empty);
                return;
            }

            Map<String, CategoryStat> statMap = new HashMap<>();
            for (PlaceRating rating : ratings.values()) {
                String cat = extractMainCategory(rating.getCategoryName());
                if (cat.isEmpty()) cat = "기타";
                CategoryStat stat = statMap.getOrDefault(cat, new CategoryStat());
                stat.count += 1;
                stat.sum += rating.getRating();
                statMap.put(cat, stat);
            }

            int maxCount = 1;
            for (CategoryStat stat : statMap.values()) {
                if (stat.count > maxCount) maxCount = stat.count;
            }

            for (Map.Entry<String, CategoryStat> entry : statMap.entrySet()) {
                String cat = entry.getKey();
                CategoryStat stat = entry.getValue();
                double avg = stat.sum / stat.count;

                View row = getLayoutInflater().inflate(R.layout.view_chart_row, container, false);
                TextView title = row.findViewById(R.id.chart_row_title);
                TextView meta = row.findViewById(R.id.chart_row_meta);
                View bar = row.findViewById(R.id.chart_row_bar);
                View spacer = row.findViewById(R.id.chart_row_spacer);

                title.setText(cat);
                meta.setText(String.format(Locale.getDefault(), "평균 %.1f점 · %d곳", avg, stat.count));

                int percent = (int) (100f * stat.count / maxCount);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(12), percent);
                bar.setLayoutParams(params);
                LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(0, dpToPx(12), 100 - percent);
                spacer.setLayoutParams(spacerParams);

                container.addView(row);
            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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

    private static class CategoryStat {
        int count = 0;
        double sum = 0.0;
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(KakaoLocalApiService.class);
    }

    private void searchRestaurantList(String keyword) {
        String authHeader = "KakaoAK " + getString(R.string.kakao_rest_api_key);
        String restaurantCode = "FD6";

        apiService.searchPlaces(authHeader, keyword, restaurantCode)
                .enqueue(new Callback<KakaoPlaceResponse>() {
                    @Override
                    public void onResponse(Call<KakaoPlaceResponse> call, Response<KakaoPlaceResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<PlaceDocument> resultList = response.body().getDocuments();
                            processResult(resultList);
                        } else {
                            Log.e(TAG, "응답 실패: " + response.code() + " - 검색어: " + keyword);
                            processResult(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<KakaoPlaceResponse> call, Throwable t) {
                        Log.e(TAG, "네트워크 실패: " + t.getMessage() + " - 검색어: " + keyword);
                        processResult(new ArrayList<>());
                    }
                });
    }

    private synchronized void processResult(List<PlaceDocument> resultList) {
        combinedRestaurantList.addAll(resultList);
        searchCompletedCount++;

        if (searchCompletedCount == totalSearches) {
            Log.d(TAG, "모든 검색 완료. 총 결과: " + combinedRestaurantList.size());

            ResultCache.lastResults = new ArrayList<>(combinedRestaurantList);

            Intent intent = new Intent(ChartActivity.this, MabzipList.class);
            startActivity(intent);
        }
    }
}
