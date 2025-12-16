package app.dku.searchmabzip;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PlaceRating.class}, version = 3, exportSchema = false)
public abstract class RatingDatabase extends RoomDatabase {

    private static final String DB_NAME = "place_ratings.db";
    private static volatile RatingDatabase instance;

    public abstract RatingDao ratingDao();

    public static RatingDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (RatingDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    RatingDatabase.class,
                                    DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
