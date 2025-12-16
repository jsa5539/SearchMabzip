package app.dku.searchmabzip;

// RecyclerView에 표시될 아이템의 타입을 정의하는 추상 클래스
public abstract class Row {
    public static final int TYPE_HEADER = 1; // 음식점 (헤더)
    public static final int TYPE_MENU = 2;   // 메뉴 아이템

    public abstract int getType();

    // 3-1. Header 클래스: 음식점 이름과 사진 URL
    public static class Header extends Row {
        private String restaurantName;
        private String photoUrl;
        private String categories; // 새로 추가: 카테고리 태그 문자열

        public Header(String restaurantName, String photoUrl, String categories) { // 생성자 수정
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

    // 3-2. Menu 클래스: 메뉴 이름과 가격
    public static class Menu extends Row {
        private String name;
        private int price;

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