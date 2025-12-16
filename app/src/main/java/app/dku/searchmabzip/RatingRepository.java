package app.dku.searchmabzip;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RatingRepository {

    public interface RatingsLoadCallback {
        void onLoaded(Map<String, PlaceRating> ratings);
    }

    public interface RatingsStatsCallback {
        void onStats(int count, double average, long lastSavedAt);
    }

    private final RatingDao ratingDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public RatingRepository(Context context) {
        ratingDao = RatingDatabase.getInstance(context).ratingDao();
    }

    public void saveRating(String placeId, String categoryName, int rating, Runnable onComplete) {
        executor.execute(() -> {
            long now = System.currentTimeMillis();
            ratingDao.upsertRating(new PlaceRating(placeId, rating, categoryName, now));
            if (onComplete != null) {
                mainHandler.post(onComplete);
            }
        });
    }

    public void loadAllRatings(RatingsLoadCallback callback) {
        executor.execute(() -> {
            List<PlaceRating> ratings = ratingDao.getAllRatings();
            Map<String, PlaceRating> map = new HashMap<>();
            for (PlaceRating item : ratings) {
                map.put(item.getPlaceId(), item);
            }
            if (callback != null) {
                mainHandler.post(() -> callback.onLoaded(map));
            }
        });
    }

    public void loadRatingStats(RatingsStatsCallback callback) {
        executor.execute(() -> {
            List<PlaceRating> ratings = ratingDao.getAllRatings();
            int count = ratings.size();
            double average = 0.0;
            long lastSaved = 0L;
            if (count > 0) {
                int sum = 0;
                for (PlaceRating item : ratings) {
                    sum += item.getRating();
                    if (item.getSavedAt() > lastSaved) {
                        lastSaved = item.getSavedAt();
                    }
                }
                average = (double) sum / count;
            }
            long finalLastSaved = lastSaved;
            double finalAverage = average;
            if (callback != null) {
                mainHandler.post(() -> callback.onStats(count, finalAverage, finalLastSaved));
            }
        });
    }
}
