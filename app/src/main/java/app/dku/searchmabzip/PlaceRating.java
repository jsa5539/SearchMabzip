package app.dku.searchmabzip;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "place_ratings")
public class PlaceRating {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "place_id")
    private String placeId;

    @ColumnInfo(name = "rating")
    private int rating;

    @ColumnInfo(name = "category_name")
    private String categoryName;

    @ColumnInfo(name = "saved_at")
    private long savedAt;

    public PlaceRating(@NonNull String placeId, int rating, String categoryName, long savedAt) {
        this.placeId = placeId;
        this.rating = rating;
        this.categoryName = categoryName;
        this.savedAt = savedAt;
    }

    @NonNull
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(@NonNull String placeId) {
        this.placeId = placeId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(long savedAt) {
        this.savedAt = savedAt;
    }
}
