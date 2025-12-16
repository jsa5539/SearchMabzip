package app.dku.searchmabzip;

import java.util.HashMap;
import java.util.Map;

public class AppDataManager {

    // Singleton 인스턴스
    private static AppDataManager instance;

    // 1~7 값을 저장하는 데이터 (예: 카테고리 필터)
    private Map<String, Integer> categoryFilterData = new HashMap<>();

    // 0~5 값을 저장하는 데이터 (예: 평점 필터)
    private double ratingFilterValue = 0.0;

    // 예산을 저장하는 데이터
    private int userBudget = 0;

    // ⭐ 추가: SeekBar의 진행도 (Progress) 값
    private int seekBarProgress = 0;

    // ⭐ 추가: Switch의 상태 (boolean) 값
    private boolean switchState = false;

    // 최종 검색 결과 리스트 (기존 ResultCache 대체 가능)
    // private List<PlaceDocument> lastResults = new ArrayList<>();

    // 생성자를 private으로 만들어 외부에서 직접 인스턴스 생성 불가
    private AppDataManager() {
        // 초기 필터 데이터 설정 (예시)
        categoryFilterData.put("Korean", 1);
        categoryFilterData.put("Chinese", 7);
    }

    // 전역적으로 접근 가능한 유일한 메서드
    public static AppDataManager getInstance() {
        if (instance == null) {
            instance = new AppDataManager();
        }
        return instance;
    }

    // --- Getter와 Setter ---

    // 예산 데이터
    public int getUserBudget() {
        return userBudget;
    }

    public void setUserBudget(int userBudget) {
        this.userBudget = userBudget;
    }

    // 평점 데이터 (0.0 ~ 5.0)
    public double getRatingFilterValue() {
        return ratingFilterValue;
    }

    public void setRatingFilterValue(double ratingFilterValue) {
        this.ratingFilterValue = ratingFilterValue;
    }

    // 카테고리/기타 필터 데이터 (Map<String, Integer> 예시)
    public Map<String, Integer> getCategoryFilterData() {
        return categoryFilterData;
    }

    public void setCategoryFilterData(Map<String, Integer> categoryFilterData) {
        this.categoryFilterData = categoryFilterData;
    }

    // ⭐ 추가: SeekBar Progress Getter와 Setter
    public int getSeekBarProgress() {
        return seekBarProgress;
    }

    public void setSeekBarProgress(int seekBarProgress) {
        this.seekBarProgress = seekBarProgress;
    }

    // ⭐ 추가: Switch State Getter와 Setter
    public boolean getSwitchState() {
        return switchState;
    }

    public void setSwitchState(boolean switchState) {
        this.switchState = switchState;
    }
}