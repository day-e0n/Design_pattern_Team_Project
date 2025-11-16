package core;

import java.util.*;

/**
 * 위치 정보 관리 클래스 (싱글톤 + 옵저버 패턴)
 * - 자전거-스테이션 매핑 관리
 * - 위치 변경 시 자동 알림
 */
class LocationManager {
    // 싱글톤 인스턴스
    private static LocationManager instance;
    
    // 자전거 ID → 스테이션 이름 매핑
    private Map<String, String> bikeToStationMap;
    
    // 스테이션 이름 → 자전거 ID 리스트 매핑
    private Map<String, List<String>> stationToBikesMap;
    
    // 위치 변경 옵저버들
    private List<LocationObserver> observers;
    
    // private 생성자 - 외부에서 인스턴스 생성 방지
    private LocationManager() {
        this.bikeToStationMap = new HashMap<>();
        this.stationToBikesMap = new HashMap<>();
        this.observers = new ArrayList<>();
        
        // 초기 스테이션 생성
        initializeStations();
    }
    
    // 싱글톤 인스턴스 반환 (Thread-Safe)
    public static synchronized LocationManager getInstance() {
        if (instance == null) {
            instance = new LocationManager();
        }
        return instance;
    }
    
    // 초기 스테이션 설정
    private void initializeStations() {
        String[] stations = {"성복동", "상현동", "죽전동", "보정동"};
        for (String station : stations) {
            stationToBikesMap.put(station, new ArrayList<>());
        }
    }
    
    // 자전거 위치 등록
    public void registerBicycle(String bikeId, String station) {
        bikeToStationMap.put(bikeId, station);
        stationToBikesMap.computeIfAbsent(station, k -> new ArrayList<>()).add(bikeId);
    }
    
    // 자전거 위치 업데이트 (옵저버 패턴 적용)
    public void updateBicycleLocation(String bikeId, String newStation) {
        String oldStation = bikeToStationMap.get(bikeId);
        
        if (oldStation == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return;
        }
        
        if (oldStation.equals(newStation)) {
            System.out.println("이미 해당 스테이션에 있습니다.");
            return;
        }
        
        // 이전 스테이션에서 제거
        if (stationToBikesMap.containsKey(oldStation)) {
            stationToBikesMap.get(oldStation).remove(bikeId);
        }
        
        // 새 스테이션에 추가
        bikeToStationMap.put(bikeId, newStation);
        stationToBikesMap.computeIfAbsent(newStation, k -> new ArrayList<>()).add(bikeId);
        
        // 옵저버들에게 위치 변경 알림
        notifyLocationChange(bikeId, oldStation, newStation);
        
        System.out.println("자전거 " + bikeId + "의 위치가 '" + oldStation + "'에서 '" + newStation + "'로 변경되었습니다.");
    }
    
    // 자전거의 현재 위치 조회
    public String getBicycleLocation(String bikeId) {
        return bikeToStationMap.get(bikeId);
    }
    
    // 특정 스테이션의 자전거 목록 조회
    public List<String> getBicyclesAtStation(String station) {
        return stationToBikesMap.getOrDefault(station, new ArrayList<>());
    }
    
    // 모든 스테이션 목록 조회
    public Set<String> getAllStations() {
        return stationToBikesMap.keySet();
    }
    
    // 스테이션별 자전거 현황
    public void showStationStatus() {
        System.out.println("\n==== 스테이션별 자전거 현황 ====");
        int stationNum = 1;
        for (String station : stationToBikesMap.keySet()) {
            List<String> bikes = stationToBikesMap.get(station);
            System.out.printf("%d. %s: %d대\n", stationNum++, station, bikes.size());
            if (!bikes.isEmpty()) {
                System.out.println("   → " + String.join(", ", bikes));
            }
        }
    }
    
    // 스테이션 번호로 이름 가져오기
    public String getStationNameByNumber(int number) {
        String[] stations = {"성복동", "상현동", "죽전동", "보정동"};
        if (number >= 1 && number <= stations.length) {
            return stations[number - 1];
        }
        return null;
    }
    
    // 스테이션 목록 출력 (번호와 함께)
    public void showStationList() {
        System.out.println("\n==== 스테이션 목록 ====");
        String[] stations = {"성복동", "상현동", "죽전동", "보정동"};
        for (int i = 0; i < stations.length; i++) {
            System.out.printf("%d. %s\n", i + 1, stations[i]);
        }
    }
    
    // 특정 스테이션의 대여 가능한 자전거 보기
    public void showAvailableBicyclesAtStation(String station, BicycleManager bicycleManager) {
        List<String> bikes = getBicyclesAtStation(station);
        if (bikes.isEmpty()) {
            System.out.println(station + "에는 현재 대여 가능한 자전거가 없습니다.");
            return;
        }
        
        System.out.println("\n" + station + "의 대여 가능한 자전거:");
        for (String bikeId : bikes) {
            Bicycle bike = bicycleManager.getBicycle(bikeId);
            if (bike != null && bike.getStatus() == BicycleStatus.AVAILABLE) {
                System.out.println("  - " + bike);
            }
        }
    }
    
    // 옵저버 추가
    public void addObserver(LocationObserver observer) {
        observers.add(observer);
        System.out.println("[위치 관리자] 옵저버가 등록되었습니다: " + observer.getClass().getSimpleName());
    }
    
    // 옵저버 제거
    public void removeObserver(LocationObserver observer) {
        observers.remove(observer);
        System.out.println("[위치 관리자] 옵저버가 제거되었습니다: " + observer.getClass().getSimpleName());
    }
    
    // 옵저버들에게 위치 변경 알림
    private void notifyLocationChange(String bikeId, String fromStation, String toStation) {
        for (LocationObserver observer : observers) {
            observer.onLocationChanged(bikeId, fromStation, toStation);
        }
    }
    
    // 자전거 제거
    public void removeBicycle(String bikeId) {
        String station = bikeToStationMap.get(bikeId);
        if (station != null) {
            stationToBikesMap.get(station).remove(bikeId);
            bikeToStationMap.remove(bikeId);
        }
    }
    
    // 통계 정보
    public void showStatistics() {
        System.out.println("\n==== 위치 관리 통계 ====");
        System.out.println("전체 스테이션 수: " + stationToBikesMap.size());
        System.out.println("등록된 자전거 수: " + bikeToStationMap.size());
        System.out.println("등록된 옵저버 수: " + observers.size());
        
        // 가장 많은 자전거가 있는 스테이션
        String maxStation = null;
        int maxCount = 0;
        for (Map.Entry<String, List<String>> entry : stationToBikesMap.entrySet()) {
            if (entry.getValue().size() > maxCount) {
                maxCount = entry.getValue().size();
                maxStation = entry.getKey();
            }
        }
        if (maxStation != null) {
            System.out.println("자전거가 가장 많은 스테이션: " + maxStation + " (" + maxCount + "대)");
        }
    }
}

/**
 * 위치 변경 옵저버 인터페이스
 */
interface LocationObserver {
    void onLocationChanged(String bikeId, String fromStation, String toStation);
}

/**
 * 스테이션 통계 옵저버
 * 위치 변경 시 스테이션 통계 업데이트
 */
class StationStatisticsObserver implements LocationObserver {
    @Override
    public void onLocationChanged(String bikeId, String fromStation, String toStation) {
        System.out.println("[스테이션 통계] " + fromStation + " → " + toStation + " (자전거: " + bikeId + ")");
        // 실제로는 통계 데이터베이스 업데이트 등의 작업 수행
    }
}


