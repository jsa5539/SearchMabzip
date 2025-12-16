package app.dku.searchmabzip;

// 메뉴 이름과 가격을 담는 클래스
public class MenuItem {
    private String name;
    private int price; // 가격

    public MenuItem(String name, int price) {
        this.name = name;
        this.price = price;
    }

    // Getters
    public String getName() { return name; }
    public int getPrice() { return price; }
}