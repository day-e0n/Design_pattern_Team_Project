package core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import observer.BreakdownReason;
import observer.BreakdownReportSubject;
import observer.ObserverInterface;

/*
 * 자전거 관리 시스템
 * - 자전거 추가, 삭제, 조회, 수정 기능
 * - 상태 관리 (정상/고장/대여중/정비중)
 * - JSON 파일로 자전거 목록 관리
 * - 대여 시간 측정 로직 추가 
*/

public class BicycleManager {
    private Map<String, Bicycle> bicycles;
    private BicycleFactory regularFactory;
    private BicycleFactory electricFactory;
    private LocationManager locationManager; // 위치 관리자 추가
    private Map<String, Long> rentalStartTimes; // 대여 시작 시간 기록용 Map 추가
    private static final String BICYCLE_JSON_FILE = "bicycles.json";

    public BicycleManager() {
        this.bicycles = new HashMap<>();
        this.regularFactory = new RegularBicycleFactory();
        this.electricFactory = new ElectricBicycleFactory();
        this.locationManager = LocationManager.getInstance(); // 싱글톤 인스턴스
        this.rentalStartTimes = new HashMap<>(); // Map 초기화

        // 프로그램 시작 시 JSON에서 자전거 목록 로딩
        loadBicyclesFromJson();
    }

    // JSON 파일에서 자전거 목록을 읽어와 등록
    private void loadBicyclesFromJson() {
        Path path = Path.of(BICYCLE_JSON_FILE);
        if (!Files.exists(path)) {
            System.out.println("초기 JSON 파일 없음");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            String id = null;
            String type = null;
            String station = null;
            String statusStr = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("\"id\"")) {
                    id = extractJsonValue(line);
                } else if (line.startsWith("\"type\"")) {
                    type = extractJsonValue(line);
                } else if (line.startsWith("\"station\"")) {
                    station = extractJsonValue(line);
                } else if (line.startsWith("\"status\"")) {
                    statusStr = extractJsonValue(line);
                }

                // 네 필드가 다 채워지면 자전거 한 대 생성
                if (id != null && type != null && station != null && statusStr != null) {
                    boolean added = addBicycleInternal(id, type, station, false); // 로그/저장 X
                    if (added) {
                        Bicycle b = bicycles.get(id);
                        try {
                            BicycleStatus status = BicycleStatus.valueOf(statusStr);
                            b.setStatus(status);
                        } catch (IllegalArgumentException e) {
                            // 잘못된 값이면 안전하게 대여가능으로
                            b.setStatus(BicycleStatus.AVAILABLE);
                        }
                    }

                    // 다음 자전거를 위해 초기화
                    id = null;
                    type = null;
                    station = null;
                    statusStr = null;
                }
            }

            System.out.println("JSON에서 자전거 목록을 모두 로딩했습니다.");
        } catch (IOException e) {
            System.out.println("JSON을 읽는 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    // 같은 줄에서 값만 추출하는 유틸 함수
    private String extractJsonValue(String line) {
        int colonPos = line.indexOf(':');
        if (colonPos == -1)
            return null;

        int firstQuote = line.indexOf('"', colonPos);
        int lastQuote = line.indexOf('"', firstQuote + 1);
        if (firstQuote == -1 || lastQuote == -1)
            return null;

        return line.substring(firstQuote + 1, lastQuote);
    }

    // 자전거 목록 JSON 파일로 저장
    private void saveBicyclesToJson() {
        Path path = Path.of(BICYCLE_JSON_FILE);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("{\n");
            writer.write("  \"bicycle\": [\n");

            int index = 0;
            int size = bicycles.size();

            for (Bicycle bicycle : bicycles.values()) {

                String typeForJson;
                if ("일반자전거".equals(bicycle.getType())) {
                    typeForJson = "regular";
                } else if ("전기자전거".equals(bicycle.getType())) {
                    typeForJson = "electric";
                } else {
                    typeForJson = bicycle.getType(); // 예외적으로 다른 문자열이면 그대로
                }

                writer.write("    {\n");
                writer.write("      \"id\": \"" + bicycle.getId() + "\",\n");
                writer.write("      \"type\": \"" + typeForJson + "\",\n");
                writer.write("      \"station\": \"" + bicycle.getLocation() + "\",\n");
                writer.write("      \"status\": \"" + bicycle.getStatus().name() + "\"\n");
                writer.write("    }");
                index++;
                if (index < size) {
                    writer.write(",");
                }
                writer.write("\n");
            }

            writer.write("  ]\n");
            writer.write("}\n");

            System.out.println("현재 자전거 목록이 Json 파일로 저장되었습니다. (" + BICYCLE_JSON_FILE + ")");
        } catch (IOException e) {
            System.out.println("자전거 정보를 Json 파일로 저장하는 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    // 공통 내부 메서드: JSON 저장 여부를 플래그로 제어
    private boolean addBicycleInternal(String id, String type, String location, boolean saveJson) {
        if (bicycles.containsKey(id)) {
            System.out.println("오류: 이미 존재하는 자전거 ID입니다.");
            return false;
        }

        Bicycle bicycle;
        if ("regular".equals(type)) {
            bicycle = regularFactory.createBicycle(id);
        } else if ("electric".equals(type)) {
            bicycle = electricFactory.createBicycle(id);
        } else {
            if (saveJson) {
                System.out.println("오류: 잘못된 자전거 유형입니다. (regular 또는 electric)");
            }
            return false;
        }

        bicycle.setLocation(location);
        bicycles.put(id, bicycle);

        // 위치 관리자에 등록
        locationManager.registerBicycle(id, location);

        // 내부 플래그에 따라 JSON 저장
        if (saveJson) {
            saveBicyclesToJson();
            System.out.println("자전거가 성공적으로 추가되었습니다: " + bicycle);
        }

        return true;
    }

    // 자전거 추가 -> 관리자 1번
    public boolean addBicycle(String id, String type, String location) {
        return addBicycleInternal(id, type, location, true);
    }

    // 자전거 삭제 -> 관리자 2번
    public boolean removeBicycle(String id) {
        if (!bicycles.containsKey(id)) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }

        Bicycle bicycle = bicycles.get(id);
        // state 패턴 적용
        if (bicycle.getBikeState() != null && !bicycle.getBikeState().canDelete()) {
            return false; // 거부 메시지는 State 객체가 이미 출력했을 수 있음
        }

        bicycles.remove(id);

        // 위치 관리자에서도 제거
        locationManager.removeBicycle(id);

        // 삭제 후 JSON 업데이트
        saveBicyclesToJson();

        System.out.println("자전거가 성공적으로 삭제되었습니다: " + id);
        return true;
    }

    // 자전거 조회
    public Bicycle getBicycle(String id) {
        return bicycles.get(id);
    }

    // 모든 자전거 목록 조회 -> 관리자 3번
    public void listAllBicycles() {
        if (bicycles.isEmpty()) {
            System.out.println("등록된 자전거가 없습니다.");
            return;
        }

        System.out.println("\n==== 전체 자전거 목록 ====");
        for (Bicycle bicycle : bicycles.values()) {
            System.out.println(bicycle);
        }
    }

    // 상태별 자전거 조회 -> 관리자 4번
    public void listBicyclesByStatus(BicycleStatus status) {
        System.out.println("\n==== " + status.getDescription() + " 자전거 목록 ====");
        boolean found = false;
        for (Bicycle bicycle : bicycles.values()) {
            if (bicycle.getStatus() == status) {
                System.out.println(bicycle);
                found = true;
            }
        }
        if (!found) {
            System.out.println("해당 상태의 자전거가 없습니다.");
        }
    }

    // (추가) 고장 신고 처리 -> 관리자 5번
    public boolean reportBroken(String id, List<BreakdownReason> reasons, ObserverInterface observer) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }

        // State 패턴: 신고 가능 상태 2차 검증
        if (bicycle.getBikeState() != null && !bicycle.getBikeState().canReport()) {
            return false;
        }

        // 상태 객체에 신고 전달 (상태를 BROKEN 등으로 변경)
        bicycle.getBikeState().reportBroken(reasons);

        // Observer 패턴: 알림 전송
        boolean isElectric = "전기자전거".equals(bicycle.getType());
        BreakdownReportSubject subject = new BreakdownReportSubject(id, reasons, bicycle.getLocation(), isElectric);
        subject.addObserver(observer);
        subject.report();

        saveBicyclesToJson(); // 상태가 변경되었으므로 저장
        System.out.println("수리 신고가 접수되었습니다.");
        return true;
    }

    // 자전거 상태 변경
    public boolean changeBicycleStatus(String id, BicycleStatus newStatus) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }

        BicycleStatus oldStatus = bicycle.getStatus();
        bicycle.setStatus(newStatus);

        // 정비 완료 시 정비일 업데이트
        if (newStatus == BicycleStatus.AVAILABLE &&
                (oldStatus == BicycleStatus.MAINTENANCE || oldStatus == BicycleStatus.BROKEN)) {
            bicycle.setLastMaintenanceDate(LocalDate.now().toString());
        }

        // 상태 변경도 JSON에 업데이트
        saveBicyclesToJson();

        System.out.printf("자전거 %s의 상태가 '%s'에서 '%s'로 변경되었습니다.\n",
                id, oldStatus.getDescription(), newStatus.getDescription());
        return true;
    }

    // 자전거 위치 변경 -> 관리자 6번
    public boolean changeBicycleLocation(String id, String newLocation) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }

        // state 패턴으로 이동 가능 상태 2차 검증 
        if (bicycle.getBikeState() != null && !bicycle.getBikeState().canMove()) {
            return false;
        }

        String oldLocation = bicycle.getLocation();
        bicycle.setLocation(newLocation);

        // 위치 관리자에 업데이트 (옵저버들에게 자동 알림)
        locationManager.updateBicycleLocation(id, newLocation);

        // 위치 변경 JSON에 업데이트
        saveBicyclesToJson();

        return true;
    }

    // 자전거 상세 정보 조회 -> 관리자 7번
    public void showBicycleDetails(String id) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return;
        }

        System.out.println("\n==== 자전거 상세 정보 ====");
        System.out.println(bicycle.getDetailedInfo());
    }

    // 대여 가능한 자전거 목록 -> 사용자 1번
    public List<Bicycle> getAvailableBicycles() {
        List<Bicycle> available = new ArrayList<>();
        for (Bicycle bicycle : bicycles.values()) {
            if (bicycle.getStatus() == BicycleStatus.AVAILABLE) {
                available.add(bicycle);
            }
        }
        return available;
    }

    // 자전거 대여 + 자전거 대여 시 시간 기록 -> 사용자 2번
    public boolean rentBicycle(String id) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return false;
        }

        // state 패턴으로 대여 가능 상태 2차 검증 
        if (bicycle.getBikeState() != null && !bicycle.getBikeState().canRent()) {
            return false;
        }

        bicycle.setStatus(BicycleStatus.RENTED);

        // 대여 시작 시간 기록
        rentalStartTimes.put(id, System.currentTimeMillis());

        // 자전거 상태 변경 내용 JSON 파일에 업데이트
        saveBicyclesToJson();

        System.out.println("자전거 " + id + "가 대여되었습니다. (대여 시간 기록됨)");
        return true;

    }

    // 자전거 반납 + 대여 시간 계산 -> 사용자 3번
    public int returnBicycle(String id, String returnLocation) {
        Bicycle bicycle = bicycles.get(id);
        if (bicycle == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return -1; // <-- (수정) 실패 시 -1 반환
        }

        if (bicycle.getStatus() != BicycleStatus.RENTED) {
            System.out.println("오류: 대여 중이 아닌 자전거입니다.");
            return -1; // <-- (수정) 실패 시 -1 반환
        }

        // (추가) 대여 시작 시간 조회
        Long startTime = rentalStartTimes.get(id);
        if (startTime == null) {
            System.out.println("오류: 이 자전거의 대여 시작 기록을 찾을 수 없습니다.");
            return -1; // <-- (수정) 실패 시 -1 반환
        }

        // 이용 시간 계산
        long durationMillis = System.currentTimeMillis() - startTime;

        // (수정) 테스트를 위해 '초'를 '분'처럼 취급합니다. (1초 -> 1분)
        int durationMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(durationMillis);

        if (durationMinutes == 0) {
            durationMinutes = 1; // 최소 1분(1초)
        }

        // (추가) 기록 삭제
        rentalStartTimes.remove(id);

        bicycle.setStatus(BicycleStatus.AVAILABLE);
        bicycle.setLocation(returnLocation);

        // 위치 관리자에 업데이트 (옵저버들에게 자동 알림)
        locationManager.updateBicycleLocation(id, returnLocation);

        // 상태/위치 변경 내용 JSON 파일에 업데이트
        saveBicyclesToJson();

        // ★★★ (수정) return true; -> println()을 먼저 실행
        System.out.println("자전거 " + id + "가 " + returnLocation + "에 반납되었습니다.");

        // 2. 이용 시간(분)을 '맨 마지막에' 반환합니다.
        return durationMinutes; // <-- ★★★ (수정) boolean이 아닌 int 반환
    }

    // 통계 정보 -> 관리자 8번
    public void showStatistics() {
        System.out.println("\n==== 자전거 현황 통계 ====");
        Map<BicycleStatus, Integer> statusCount = new HashMap<>();
        Map<String, Integer> typeCount = new HashMap<>();

        for (Bicycle bicycle : bicycles.values()) {
            // 상태별 통계
            statusCount.put(bicycle.getStatus(),
                    statusCount.getOrDefault(bicycle.getStatus(), 0) + 1);
            // 유형별 통계
            typeCount.put(bicycle.getType(),
                    typeCount.getOrDefault(bicycle.getType(), 0) + 1);
        }

        System.out.println("총 자전거 수: " + bicycles.size());
        System.out.println("\n[상태별 현황]");
        for (BicycleStatus status : BicycleStatus.values()) {
            int count = statusCount.getOrDefault(status, 0);
            System.out.println(status.getDescription() + ": " + count + "대");
        }

        System.out.println("\n[유형별 현황]");
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "대");
        }
    }
}