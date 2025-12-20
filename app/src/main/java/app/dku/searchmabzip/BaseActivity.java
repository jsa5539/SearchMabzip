package app.dku.searchmabzip;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_base);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton home = findViewById(R.id.homebtn);
        ImageButton chart = findViewById(R.id.chartbtn);
        Button btn_next = findViewById(R.id.newbtn);

        home.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class))
        );


        chart.setOnClickListener(v ->
                startActivity(new Intent(this, ChartActivity.class))
        );




    }
    public void setContentLayout(int layoutId) {
        FrameLayout container = findViewById(R.id.container);
        getLayoutInflater().inflate(layoutId, container, true);
    }

    protected String getKakaoRestKey() {
        try {
            ApplicationInfo appInfo = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData == null) {
                return "";
            }
            String key = appInfo.metaData.getString("KAKAO_REST_API_KEY");
            return key == null ? "" : key.trim();
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

}
