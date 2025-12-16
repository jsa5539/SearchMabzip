package app.dku.searchmabzip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private final List<PlaceDocument> restaurantList;
    private final Context context;

    public RestaurantAdapter(Context context, List<PlaceDocument> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    // --- 1. ViewHolder 정의 (아이템 뷰의 구성 요소를 저장) ---
    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName;
        public TextView tvCategories; // ⭐ 카테고리 TextView 추가
        public ImageView restaurantPhoto;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            tvCategories = itemView.findViewById(R.id.tv_categories); // ⭐ ID 연결
            restaurantPhoto = itemView.findViewById(R.id.iv_restaurant_photo);
        }
    }

    // --- 2. 뷰 홀더 생성 (레이아웃 이름이 list_item_restaurant라고 가정) ---
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ⭐ 레이아웃 파일 이름 확인: list_item_restaurant 사용
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    // --- 3. 데이터 바인딩 ---
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        PlaceDocument place = restaurantList.get(position);

        // 음식점 이름 바인딩
        holder.restaurantName.setText(place.getPlaceName());

        // ⭐ 카테고리 포맷팅 및 바인딩 (태그가 표시되는 핵심 로직)
        String formattedCategories = formatCategories(place.getCategoryName());
        holder.tvCategories.setText(formattedCategories);
    }

    // --- 4. 아이템 개수 반환 ---
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // ⭐ 5. 카테고리 포맷팅 함수 (카테고리 문자열을 #태그 형태로 변환)
    private String formatCategories(String fullCategoryName) {
        if (fullCategoryName == null || fullCategoryName.isEmpty()) {
            return "";
        }
        String[] parts = fullCategoryName.split(">");
        StringBuilder tags = new StringBuilder();

        // 가장 큰 카테고리("음식점")를 제외하고 2번째 요소부터 시작
        for (int i = 1; i < parts.length; i++) {
            String tag = parts[i].trim();
            if (!tag.isEmpty()) {
                tags.append("#").append(tag).append(" ");
            }
        }
        return tags.toString().trim();
    }
}