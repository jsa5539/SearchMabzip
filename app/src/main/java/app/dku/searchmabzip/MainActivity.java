package app.dku.searchmabzip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

// BaseActivity 를 상속받아 사용
public class MainActivity extends BaseActivity {

    // Kakao REST API service
    private KakaoLocalApiService apiService;

    // 결과 취합 리스트
    private List<PlaceDocument> combinedRestaurantList = new ArrayList<>();
    private int searchCompletedCount = 0;
    private final int TOTAL_SEARCHES = 4; // 총 2개의 키워드 검색
    private static final String TAG = "MainActivity"; // 로그 태그

    private void getAppKeyHash() {
        try {
            android.content.pm.PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), android.content.pm.PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                android.util.Log.e("KeyHash", android.util.Base64.encodeToString(md.digest(), android.util.Base64.DEFAULT));
            }
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppKeyHash();
        EdgeToEdge.enable(this);
        setContentLayout(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrofit 초기화
        initRetrofit();

        // --- UI 요소 초기화 ---
        Button newbtn = findViewById(R.id.newbtn);
        ImageButton setting = findViewById(R.id.set);

        // --- 설정 버튼 ---
        setting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Setting.class);
            startActivity(intent);
        });

        // NEXT 버튼: API 호출 후 MabzipList로 이동
        newbtn.setOnClickListener(v-> {
            combinedRestaurantList.clear();
            searchCompletedCount = 0;

            Toast.makeText(MainActivity.this, "음식점 정보를 검색합니다...", Toast.LENGTH_SHORT).show();

            // 1. 용인시 죽전동 단국대 근처 음식점
            searchRestaurantList("용인시 죽전동 단국대 근처 음식점");

            searchRestaurantList("단국대");
            
            searchRestaurantList("단국대학교");

            searchRestaurantList("경기 용인시 수지구 죽전로");
        });
    }
    //나중에 gps로 구현으로 바꿀 예정.

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
                            String err = "";
                            try { if (response.errorBody() != null) { err = response.errorBody().string(); } } catch (Exception e) { /* ignore */ }
                            Log.e(TAG, "Kakao error body: " + err);
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

    // --- 결과 취합 및 다음 화면 이동 ---
    private synchronized void processResult(List<PlaceDocument> resultList) {
        combinedRestaurantList.addAll(resultList);
        searchCompletedCount++;

        if (searchCompletedCount == TOTAL_SEARCHES) {

            Log.d(TAG, "모든 검색 완료. 총 음식점 수: " + combinedRestaurantList.size());

            if (combinedRestaurantList.isEmpty()) {
                Toast.makeText(MainActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
            }

            // Cache results to avoid serialization issues
            ResultCache.lastResults = new ArrayList<>(combinedRestaurantList);

            Intent intent = new Intent(MainActivity.this, MabzipList.class);
            startActivity(intent);
        }
    }
}
