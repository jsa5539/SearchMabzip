package app.dku.searchmabzip;
import java.util.List;
import com.google.gson.annotations.SerializedName;
public class KakaoPlaceResponse {
    @SerializedName("documents")
    private List<PlaceDocument> documents;

    public List<PlaceDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PlaceDocument> documents) {
        this.documents = documents;
    }
}