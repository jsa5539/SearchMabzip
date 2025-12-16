package app.dku.searchmabzip;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;

public class Setting extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ⭐ AppDataManager 인스턴스를 가져옵니다.
        final AppDataManager dataManager = AppDataManager.getInstance();

        // === TextView들 ===
        TextView textView = findViewById(R.id.textView);
        TextView textView4 = findViewById(R.id.textView4);
        TextView textView5 = findViewById(R.id.textView5);
        TextView textView6 = findViewById(R.id.textView6);
        TextView textView7 = findViewById(R.id.textView7);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);
        TextView textView13 = findViewById(R.id.textView13);
        Button pre = findViewById(R.id.pre);
        // === SeekBar ===
        SeekBar seekBar2 = findViewById(R.id.seekBar2);

        // === RatingBar ===
        RatingBar ratingBar2 = findViewById(R.id.ratingBar2);

        // === Switch ===
        Switch switch1 = findViewById(R.id.switch1);

        // --- 초기 값 설정 (사용자가 설정 화면에 들어왔을 때, 이전에 저장된 값으로 UI를 업데이트) ---
        seekBar2.setProgress(dataManager.getSeekBarProgress());
        ratingBar2.setRating((float) dataManager.getRatingFilterValue());
        switch1.setChecked(dataManager.getSwitchState());

        // 1. SeekBar 리스너 수정: AppDataManager에 저장
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // ⭐ SharedPreferences 대신 AppDataManager에 저장
                dataManager.setSeekBarProgress(progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 2. RatingBar 리스너 수정: AppDataManager에 저장
        ratingBar2.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            // ⭐ SharedPreferences 대신 AppDataManager에 저장
            dataManager.setRatingFilterValue(rating);
        });

        // 3. Switch 리스너 수정: AppDataManager에 저장
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // ⭐ SharedPreferences 대신 AppDataManager에 저장
            dataManager.setSwitchState(isChecked);
        });


        // 4. 이전 버튼 (Pre) 로직
        pre.setOnClickListener(v -> {
            Intent intent = new Intent(Setting.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
