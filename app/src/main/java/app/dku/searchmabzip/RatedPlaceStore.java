package app.dku.searchmabzip;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class RatedPlaceStore {

    private static final String PREF_NAME = "rated_place_meta";
    private static final String KEY_NAME_PREFIX = "name_";
    private static final String KEY_CATEGORY_PREFIX = "category_";

    public static void savePlaceMeta(Context context, String placeId, String placeName, String categoryName) {
        if (context == null || TextUtils.isEmpty(placeId)) {
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (!TextUtils.isEmpty(placeName)) {
            editor.putString(KEY_NAME_PREFIX + placeId, placeName);
        }
        if (!TextUtils.isEmpty(categoryName)) {
            editor.putString(KEY_CATEGORY_PREFIX + placeId, categoryName);
        }
        editor.apply();
    }

    public static RatedPlaceMeta getMeta(Context context, String placeId) {
        if (context == null || TextUtils.isEmpty(placeId)) {
            return null;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString(KEY_NAME_PREFIX + placeId, null);
        String category = prefs.getString(KEY_CATEGORY_PREFIX + placeId, null);
        if (name == null && category == null) {
            return null;
        }
        return new RatedPlaceMeta(name, category);
    }

    public static class RatedPlaceMeta {
        private final String placeName;
        private final String categoryName;

        public RatedPlaceMeta(String placeName, String categoryName) {
            this.placeName = placeName;
            this.categoryName = categoryName;
        }

        public String getPlaceName() {
            return placeName;
        }

        public String getCategoryName() {
            return categoryName;
        }
    }
}
