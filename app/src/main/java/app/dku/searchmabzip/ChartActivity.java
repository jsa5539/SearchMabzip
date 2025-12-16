package app.dku.searchmabzip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChartActivity extends BaseActivity {

    private static final String TAG = "ChartActivity";

    // Kakao REST API service
    private KakaoLocalApiService apiService;

    // 결과 취합 리스트
    private List<PlaceDocument> combinedRestaurantList = new ArrayList<>();
    private int searchCompletedCount = 0;
    private final int TOTAL_SEARCHES = 4; // MainActivity와 동일하게 4회 검색

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

        // 예산/선호도 텍스트 표기
        AppDataManager dm = AppDataManager.getInstance();
        int budget = dm.getUserBudget();

        TextView textBudget = findViewById(R.id.textBudget);
        if (textBudget != null) {
            textBudget.setText("예산: " + budget + "원");
        }

        double rating = dm.getRatingFilterValue();
        int progress = dm.getSeekBarProgress();
        boolean switchOn = dm.getSwitchState();

        TextView textPreferences = findViewById(R.id.textPreferences);
        if (textPreferences != null) {
            String summary = "선호도: 평점 " + String.format("%.1f", rating)
                    + ", 일동안 먹은 음식 제외 " + progress
                    + ",  동일 계열 메뉴 제외" + (switchOn ? "켜짐" : "꺼짐");
            textPreferences.setText(summary);
        }

        // Retrofit 초기화
        initRetrofit();

        // NEXT 버튼: API 호출 후 MabzipList로 이동
        Button nextButton = findViewById(R.id.newbtn);
        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    combinedRestaurantList.clear();
                    searchCompletedCount = 0;

                    Toast.makeText(ChartActivity.this, "음식점 정보를 검색합니다...", Toast.LENGTH_SHORT).show();

                    // MainActivity와 비슷한 키워드로 4회 검색
                    searchRestaurantList("단국대 죽전 캠퍼스 근처 음식점");
                    searchRestaurantList("죽전 맛집");
                    searchRestaurantList("단국대 앞 맛집");
                    searchRestaurantList("경기 용인시 수지구 죽전동 맛집");
                }
            });
        }
    }

    // --- Retrofit 초기화 ---
    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/") // Kakao Local API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(KakaoLocalApiService.class);
    }

    // --- API 호출 ---
    private void searchRestaurantList(String keyword) {
        String authHeader = "KakaoAK " + getString(R.string.kakao_rest_api_key);
        String restaurantCode = "FD6"; // 음식점 카테고리

        apiService.searchPlaces(authHeader, keyword, restaurantCode)
                .enqueue(new Callback<KakaoPlaceResponse>() {
                    @Override
                    public void onResponse(Call<KakaoPlaceResponse> call, Response<KakaoPlaceResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<PlaceDocument> resultList = response.body().getDocuments();
                            processResult(resultList);
                        } else {
                            Log.e(TAG, "응답 실패: " + response.code() + " - 키워드: " + keyword);
                            processResult(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<KakaoPlaceResponse> call, Throwable t) {
                        Log.e(TAG, "네트워크 실패: " + t.getMessage() + " - 키워드: " + keyword);
                        processResult(new ArrayList<>());
                    }
                });
    }

    // --- 결과 취합 후 다음 화면 이동 ---
    private synchronized void processResult(List<PlaceDocument> resultList) {
        combinedRestaurantList.addAll(resultList);
        searchCompletedCount++;

        if (searchCompletedCount == TOTAL_SEARCHES) {
            Log.d(TAG, "모든 검색 완료. 총 결과: " + combinedRestaurantList.size());

            // Cache results to avoid serialization issues
            ResultCache.lastResults = new ArrayList<>(combinedRestaurantList);

            Intent intent = new Intent(ChartActivity.this, MabzipList.class);
            startActivity(intent);
        }
    }
}

