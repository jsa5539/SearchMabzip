package app.dku.searchmabzip;


public class PlaceDocument {

    @com.google.gson.annotations.SerializedName("id")
    private String id;

    @com.google.gson.annotations.SerializedName("place_name")
    private String placeName;

    @com.google.gson.annotations.SerializedName("address_name")
    private String addressName;

    @com.google.gson.annotations.SerializedName("phone")
    private String phone;

    @com.google.gson.annotations.SerializedName("category_name")
    private String categoryName; // Kakao API의 상세 카테고리 정보

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
