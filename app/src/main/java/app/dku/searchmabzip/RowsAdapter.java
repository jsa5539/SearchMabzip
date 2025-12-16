package app.dku.searchmabzip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RowsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Row> items;
    private final Context context; // Glide 등을 사용하기 위해 Context 저장 (현재는 사용 안 함)

    public RowsAdapter(Context context, List<Row> items) {
        this.context = context;
        this.items = items;
    }

    // 1. 아이템 위치에 따른 뷰 타입 반환 (핵심)
    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == Row.TYPE_HEADER) {
            // item_header.xml 레이아웃 사용
            View v = inflater.inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            // item_menu.xml 레이아웃 사용
            View v = inflater.inflate(R.layout.item_menu, parent, false);
            return new MenuViewHolder(v);
        }
    }

    // 2. 뷰 홀더에 데이터 바인딩 (핵심)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = items.get(position);

        if (row.getType() == Row.TYPE_HEADER) {
            Row.Header header = (Row.Header) row;
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.tvRestaurantName.setText(header.getRestaurantName());
            headerHolder.tvCategories.setText(header.getCategories()); // 카테고리 바인딩

            // TODO: 추후 사진 URL이 있다면 Glide/Picasso 등을 사용하여 headerHolder.ivRestaurantPhoto에 로딩

        } else if (row.getType() == Row.TYPE_MENU) {
            Row.Menu menu = (Row.Menu) row;
            MenuViewHolder menuHolder = (MenuViewHolder) holder;

            menuHolder.tvMenuName.setText("    - " + menu.getName()); // 메뉴는 들여쓰기

            // 가격 포맷팅 (예: 15000 -> 15,000원)
            String priceText = NumberFormat.getNumberInstance(Locale.KOREA).format(menu.getPrice()) + "원";
            menuHolder.tvPrice.setText(priceText);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 3. Header ViewHolder: item_header.xml 요소 연결
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRestaurantPhoto;
        TextView tvRestaurantName;
        TextView tvCategories; // 카테고리 TextView

        public HeaderViewHolder(View itemView) {
            super(itemView);
            // item_header.xml의 ID와 연결
            ivRestaurantPhoto = itemView.findViewById(R.id.iv_restaurant_photo);
            tvRestaurantName = itemView.findViewById(R.id.tv_restaurant_name);
            tvCategories = itemView.findViewById(R.id.tv_categories);
        }
    }

    // 4. Menu ViewHolder: item_menu.xml 요소 연결
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvMenuName;
        TextView tvPrice;

        public MenuViewHolder(View itemView) {
            super(itemView);
            // item_menu.xml의 ID와 연결
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}