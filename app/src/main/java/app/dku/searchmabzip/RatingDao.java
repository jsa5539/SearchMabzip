package app.dku.searchmabzip;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Upsert;

import java.util.List;

@Dao
public interface RatingDao {

    @Upsert
    void upsertRating(PlaceRating rating);

    @Query("SELECT rating FROM place_ratings WHERE place_id = :placeId LIMIT 1")
    Integer getRatingForPlace(String placeId);

    @Query("SELECT * FROM place_ratings")
    List<PlaceRating> getAllRatings();
}
