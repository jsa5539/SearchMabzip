package app.dku.searchmabzip;

// RecyclerView 행 모델: 헤더(음식점)와 메뉴 아이템
public abstract class Row {
    public static final int TYPE_HEADER = 1; // 음식점 헤더
    public static final int TYPE_MENU = 2;   // 메뉴 아이템

    public abstract int getType();

    // Header: 음식점 이름/사진/카테고리
    public static class Header extends Row {
        private final String restaurantName;
        private final String photoUrl;
        private final String categories;

        public Header(String restaurantName, String photoUrl, String categories) {
            this.restaurantName = restaurantName;
            this.photoUrl = photoUrl;
            this.categories = categories;
        }

        @Override
        public int getType() { return TYPE_HEADER; }
        public String getRestaurantName() { return restaurantName; }
        public String getPhotoUrl() { return photoUrl; }
        public String getCategories() { return categories; }
    }

    // Menu: 메뉴 이름과 가격
    public static class Menu extends Row {
        private final String name;
        private final int price;

        public Menu(String name, int price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public int getType() { return TYPE_MENU; }
        public String getName() { return name; }
        public int getPrice() { return price; }
    }
}
