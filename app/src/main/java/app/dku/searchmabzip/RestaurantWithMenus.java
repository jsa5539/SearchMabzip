package app.dku.searchmabzip;

import java.util.List;

public class RestaurantWithMenus {
    private String restaurantName;
    private String photoUrl;
    private List<MenuItem> menuItems;
    private String categories; // 새로 추가

    public RestaurantWithMenus(String restaurantName, String photoUrl, List<MenuItem> menuItems, String categories) { // 생성자 수정
        this.restaurantName = restaurantName;
        this.photoUrl = photoUrl;
        this.menuItems = menuItems;
        this.categories = categories; // 값 설정
    }

    // Getters
    public String getRestaurantName() { return restaurantName; }
    public String getPhotoUrl() { return photoUrl; }
    public List<MenuItem> getMenuItems() { return menuItems; }
    public String getCategories() { return categories; } // Getter 추가
}