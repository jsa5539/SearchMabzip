package app.dku.searchmabzip;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoLocalApiService {

    @GET("v2/local/search/keyword.json")
    Call<KakaoPlaceResponse> searchPlaces(
            // 카카오 REST API 키 (예: "KakaoAK YOUR_REST_API_KEY")
            @Header("Authorization") String apiKey,

            // 검색할 키워드 (예: "강남구 역삼동 음식점")
            @Query("query") String query,

            // 카테고리 코드 (FD6: 음식점)
            @Query("category_group_code") String categoryCode
    );
}
