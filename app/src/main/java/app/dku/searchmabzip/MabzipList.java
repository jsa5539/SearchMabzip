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
import java.util.List;

public class MabzipList extends BaseActivity {

    private List<PlaceDocument> restaurantList;
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;

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

        // 예산 및 선호도 표시
        AppDataManager dm = AppDataManager.getInstance();
        int budget = dm.getUserBudget();
        TextView budgetText = findViewById(R.id.textView12);
        if (budgetText != null) {
            budgetText.setText("예산: " + budget + "원");
        }

        double rating = dm.getRatingFilterValue();
        int progress = dm.getSeekBarProgress();
        boolean switchOn = dm.getSwitchState();
        TextView prefText = findViewById(R.id.text_preferences_summary);
        if (prefText != null) {
            String summary = "선호도: 평점 " + String.format("%.1f", rating)
                    + ", 일동안 먹은 음식 제외 " + progress
                    + ", 동일 계열 메뉴 제외 " + (switchOn ? "켜짐" : "꺼짐");
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
        adapter = new RestaurantAdapter(this, restaurantList);
        recyclerView.setAdapter(adapter);
    }
}

