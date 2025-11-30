package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import observer.*;
import strategy.*;

/**
 * 콘솔 기반 사용자 인터페이스
 * 관리자 모드와 사용자 모드를 제공
 */
public class ConsoleInterface {
    private Scanner scanner;
    private BicycleManager bicycleManager;
    private PricingContext pricingContext;
    private PricingStrategyFactory strategyFactory;
    private UserManager userManager;
    private User currentUser; // 현재 로그인한 사용자

    public static boolean isAdminMode = false; 
    private ScheduledExecutorService scheduler;
    private RepairServiceObserver repairObserver;
    
    public ConsoleInterface() {
        this.scanner = new Scanner(System.in);
        this.bicycleManager = new BicycleManager();
        this.strategyFactory = new PricingStrategyFactory();
        // Factory를 통해 기본 전략 생성
        this.pricingContext = new PricingContext(strategyFactory.getStrategy("regular", "일반자전거"));
        this.userManager = new UserManager();
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.repairObserver = new RepairServiceObserver(bicycleManager, scheduler, new RepairStrategy());
    }
    
    public void start() {
        System.out.println("=======================================");
        System.out.println("     자전거 공유 시스템 ");
        System.out.println("=======================================");
        
        while (true) {
            isAdminMode = false;
            showMainMenu();
            int choice = getMenuChoice(0, 2);
            
            switch (choice) {
                case 1:
                    adminMode();
                    break;
                case 2:
                    userMode();
                    break;
                case 0:
                    System.out.println("시스템을 종료합니다. 안녕히 가세요!");
                    scheduler.shutdownNow();
                    return;
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n==== 메인 메뉴 ====");
        System.out.println("1. 관리자 모드");
        System.out.println("2. 사용자 모드");
        System.out.println("0. 종료");
        System.out.print("선택하세요: ");
    }
    
    private void adminMode() {
        isAdminMode = true;
        System.out.println("\n 관리자 모드에 들어갑니다.");
        
        while (true) {
            showAdminMenu();
            int choice = getMenuChoice(0, 8);
            
            switch (choice) {
                case 1:
                    addBicycle();
                    break;
                case 2:
                    removeBicycle();
                    break;
                case 3:
                    bicycleManager.listAllBicycles();
                    break;
                case 4:
                    viewBicyclesByStatus();
                    break;
                case 5:
                    reportBrokenBicycle();
                    break;
                case 6:
                    changeBicycleLocation();
                    break;
                case 7:
                    viewBicycleDetails();
                    break;
                case 8:
                    bicycleManager.showStatistics();
                    break;
                case 0:
                    System.out.println("관리자 모드를 종료합니다.");
                    isAdminMode = false;
                    return;
            }
        }
    }
    
    private void showAdminMenu() {
        System.out.println("\n==== 관리자 메뉴 ====");
        System.out.println("1. 자전거 추가");
        System.out.println("2. 자전거 삭제");
        System.out.println("3. 전체 자전거 목록");
        System.out.println("4. 상태별 자전거 조회");
        System.out.println("5. 자전거 고장 신고");
        System.out.println("6. 자전거 위치 변경");
        System.out.println("7. 자전거 상세 정보");
        System.out.println("8. 통계 보기");
        System.out.println("0. 메인 메뉴로");
        System.out.print("선택하세요: ");
    }
    
    private void userMode() {
        System.out.println("\n 사용자 모드에 들어갑니다.");
        
        // 로그인 확인
        if (currentUser == null) {
            System.out.println("\n사용자 모드를 사용하려면 로그인이 필요합니다.");
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("0. 메인 메뉴로");
            System.out.print("선택하세요: ");
            
            int choice = getMenuChoice(0, 2);
            
            if (choice == 1) {
                loginUser();
                if (currentUser == null) {
                    return; // 로그인 실패 시 메인 메뉴로
                }
            } else if (choice == 2) {
                registerUser();
                System.out.println("회원가입 후 로그인해주세요.");
                loginUser();
                if (currentUser == null) {
                    return;
                }
            } else {
                return;
            }
        }
        
        System.out.println("\n환영합니다, " + currentUser.getName() + "님!");
        System.out.println("회원 유형: " + (currentUser.getUserType().equals("student") ? "학생" : "일반"));
        
        while (true) {
            showUserMenu();
            int choice = getMenuChoice(0, 5);
            
            switch (choice) {
                case 1:
                    viewAvailableBicycles();
                    break;
                case 2:
                    rentBicycle();
                    break;
                case 3:
                    returnBicycle();
                    break;
                case 4:
                    calculatePrice();
                    break;
                case 5:
                    if (currentUser.isRenting()) {
                        System.out.println("현재 대여 중인 자전거("
                            + currentUser.getRentedBicycleId() + ")가 있습니다. 반납 후 로그아웃 해주세요.");
                    } else {
                        currentUser = null; // 로그아웃
                        System.out.println("로그아웃되었습니다.");
                        return;
                    }
                    break;
                case 0:
                    System.out.println("사용자 모드를 종료합니다.");
                    return;
            }
        }
    }
    
    private void showUserMenu() {
        System.out.println("\n==== 사용자 메뉴 ====");
        System.out.println("1. 대여 가능한 자전거 보기");
        System.out.println("2. 자전거 대여");
        System.out.println("3. 자전거 반납");
        System.out.println("4. 요금 계산");
        System.out.println("5. 로그아웃");
        System.out.println("0. 메인 메뉴로");
        System.out.print("선택하세요: ");
    }
    
    // 관리자 기능들
    private void addBicycle() {
        System.out.print("자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        
        System.out.print("자전거 유형을 선택하세요 (regular/electric): ");
        String type = scanner.nextLine().toLowerCase();
        
        LocationManager locationManager = LocationManager.getInstance();
        
        System.out.println("\n초기 위치(스테이션)를 선택하세요:");
        locationManager.showStationList();
        System.out.print("번호를 입력하세요: ");
        
        int stationNum = getMenuChoice(1, 4);
        String stationName = locationManager.getStationNameByNumber(stationNum);
        
        if (stationName == null) {
            System.out.println("잘못된 스테이션 번호입니다.");
            return;
        }
        
        bicycleManager.addBicycle(id, type, stationName);
    }
    
    private void removeBicycle() {
        System.out.print("삭제할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();

        Bicycle bike = bicycleManager.getBicycle(id);
        // 삭제 가능한 상태인지 조회
        if (bike != null) {
            if (!bike.getBikeState().canDelete()) return;
        }

        bicycleManager.removeBicycle(id);
    }

    private void reportBrokenBicycle() {
        System.out.print("신고할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        Bicycle bike = bicycleManager.getBicycle(id);
        if (bike == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return;
        }
        // 신고 가능한 상태인지 조회 
        if (!bike.getBikeState().canReport()) return;
        
        List<BreakdownReason> reasons = new ArrayList<>();
        boolean isElectric = "electric".equals(bike.getType());
        
        while (true) {
            System.out.println("\n고장 사유를 선택하세요 (완료 시 0):");
            int idx = 1;
            for (BreakdownReason r : BreakdownReason.values()) {
                System.out.printf("%d. %s\n", idx++, r);
            }
            System.out.print("선택하세요: ");
            int choice = getMenuChoice(0, BreakdownReason.values().length);
            if (choice == 0) break;
            
            BreakdownReason reason = BreakdownReason.values()[choice - 1];
            if (!isElectric && reason == BreakdownReason.BATTERY) {
                System.out.println("오류 메시지: 일반 자전거는 배터리 문제를 선택할 수 없습니다. 다시 선택해주세요.");
                continue;
            }
            reasons.add(reason);
        }
        
        if (reasons.isEmpty()) {
            System.out.println("신고가 취소되었습니다.");
            return;
        }
        
        bike.getBikeState().reportBroken(reasons);
        BreakdownReportSubject subject = new BreakdownReportSubject(id, reasons, bike.getLocation(), isElectric);
        subject.addObserver(repairObserver);
        subject.report();
        System.out.println("수리 신고가 접수되었습니다.");
    }
    
    private void viewBicyclesByStatus() {
        System.out.println("\n상태를 선택하세요:");
        System.out.println("1. 대여 가능");
        System.out.println("2. 대여 중");
        System.out.println("3. 수리 중");
        System.out.println("4. 고장");
        
        int choice = getMenuChoice(1, 4);
        BicycleStatus status = BicycleStatus.values()[choice - 1];
        bicycleManager.listBicyclesByStatus(status);
    }
    
    private void changeBicycleLocation() {
        System.out.print("위치를 변경할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();

        // 위치 이동 가능한 상태인지 조회
        Bicycle bike = bicycleManager.getBicycle(id);
        if (bike != null) {
            if (!bike.getBikeState().canMove()) return;
        }
        
        LocationManager locationManager = LocationManager.getInstance();
        
        System.out.println("\n새로운 위치(스테이션)를 선택하세요:");
        locationManager.showStationList();
        System.out.print("번호를 입력하세요: ");
        
        int stationNum = getMenuChoice(1, 4);
        String stationName = locationManager.getStationNameByNumber(stationNum);
        
        if (stationName == null) {
            System.out.println("잘못된 스테이션 번호입니다.");
            return;
        }
        
        bicycleManager.changeBicycleLocation(id, stationName);
    }
    
    private void viewBicycleDetails() {
        System.out.print("상세 정보를 볼 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        bicycleManager.showBicycleDetails(id);
    }
    
    // 사용자 기능들
    private void loginUser() {
        System.out.println("\n==== 로그인 ====");
        
        System.out.print("사용자 ID: ");
        String userId = scanner.nextLine().trim();
        
        System.out.print("비밀번호: ");
        String password = scanner.nextLine();
        
        currentUser = userManager.login(userId, password);
        
        if (currentUser != null) {
            System.out.println("로그인 성공! 환영합니다, " + currentUser.getName() + "님!");
            
            // 사용자 유형에 따라 요금 전략 설정
            String bicycleType = "일반자전거"; // 기본값
            pricingContext.setStrategy(strategyFactory.getStrategy(currentUser.getUserType(), bicycleType));
        } else {
            System.out.println("로그인 실패!");
        }
    }
    
    private void registerUser() {
        System.out.println("\n==== 회원가입 ====");
        
        System.out.print("사용자 ID를 입력하세요: ");
        String userId = scanner.nextLine().trim();
        
        // ID 중복 확인
        if (userManager.isUserIdExists(userId)) {
            System.out.println("이미 존재하는 ID입니다. 다른 ID를 사용해주세요.");
            return;
        }

        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();
        String passwordHash = PasswordUtil.hashPassword(password);  // 해시 처리

        System.out.print("이름을 입력하세요: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("회원 유형을 선택하세요 (1: 일반, 2: 학생): ");
        int userTypeChoice = getMenuChoice(1, 2);
        String userType = (userTypeChoice == 2) ? "student" : "regular";

        User user = new User(userId, passwordHash, name, "", "", userType);
        userManager.saveUser(user);

        System.out.println("회원가입이 완료되었습니다!");
        System.out.println("회원 유형: " + (userType.equals("student") ? "학생" : "일반"));
    }


    private void viewAvailableBicycles() {
        LocationManager locationManager = LocationManager.getInstance();
        
        System.out.println("\n현재 위치를 선택하세요:");
        locationManager.showStationList();
        System.out.print("번호를 입력하세요: ");
        
        int stationNum = getMenuChoice(1, 4);
        String stationName = locationManager.getStationNameByNumber(stationNum);
        
        if (stationName == null) {
            System.out.println("잘못된 스테이션 번호입니다.");
            return;
        }
        
        locationManager.showAvailableBicyclesAtStation(stationName, bicycleManager);
    }
    
    private void rentBicycle() {
        LocationManager locationManager = LocationManager.getInstance();
        
        System.out.println("\n현재 위치(자전거를 빌릴 스테이션)를 선택하세요:");
        locationManager.showStationList();
        System.out.print("번호를 입력하세요: ");
        
        int stationNum = getMenuChoice(1, 4);
        String stationName = locationManager.getStationNameByNumber(stationNum);
        
        if (stationName == null) {
            System.out.println("잘못된 스테이션 번호입니다.");
            return;
        }
        
        // 해당 스테이션의 대여 가능한 자전거 보기
        locationManager.showAvailableBicyclesAtStation(stationName, bicycleManager);
        
        List<String> bikesAtStation = locationManager.getBicyclesAtStation(stationName);
        if (bikesAtStation.isEmpty()) {
            System.out.println("이 스테이션에는 대여 가능한 자전거가 없습니다.");
            return;
        }
        
        System.out.print("\n대여할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        
        // 해당 스테이션에 자전거가 있는지 확인
        if (!bikesAtStation.contains(id)) {
            System.out.println("이 스테이션에 해당 자전거가 없습니다.");
            return;
        }

        // 대여 가능 여부 확인
        Bicycle bike = bicycleManager.getBicycle(id);
        if (bike != null) {
            if (!bike.getBikeState().canRent()) {
                return;
            }
        }
        
        if (bicycleManager.rentBicycle(id)) { // 자전거 대여 처리
            currentUser.startRental(id); 
        }   // 사용자 대여 상태 업데이트
    }
    
    private void returnBicycle() {
        System.out.print("반납할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        
        LocationManager locationManager = LocationManager.getInstance();
        
        System.out.println("\n반납할 스테이션을 선택하세요:");
        locationManager.showStationList();
        System.out.print("번호를 입력하세요: ");
        
        int stationNum = getMenuChoice(1, 4);
        String stationName = locationManager.getStationNameByNumber(stationNum);
        
        if (stationName == null) {
            System.out.println("잘못된 스테이션 번호입니다.");
            return;
        }
        
        Bicycle bicycle = bicycleManager.getBicycle(id);
        if (bicycle == null) {
            System.out.println("오류: 자전거를 찾을 수 없습니다.");
            return;
        }
        
        String bicycleType = bicycle.getType();
        
        int minutes = bicycleManager.returnBicycle(id, stationName);
        
        // 반납이 성공적으로 이뤄졌을 때만 (minutes >= 0) 요금 계산을 진행
        if (minutes >= 0) {
            
            currentUser.endRental(); // 사용자 대여 상태 업데이트(반납 완료)
            System.out.println("\n--- 반납 완료! (" + minutes + "초 이용) ---");
            System.out.println("--- 요금 계산을 시작합니다. (테스트: 1초 = 1분) ---");
        
            // 로그인한 사용자 정보 사용
            String userType = currentUser.getUserType();
            
            // 팩토리를 통해 전략 선택
            PricingStrategy strategy = strategyFactory.getStrategy(userType, bicycleType);
            
            // Context를 통해 요금 계산 실행
            pricingContext.setStrategy(strategy);
            int fee = pricingContext.calculatePrice(minutes);
            
            System.out.println("---------------------------------");
            System.out.println("        최종 결제 요금");
            System.out.println("---------------------------------");
            System.out.println("사용자: " + currentUser.getName() + " (" + currentUser.getUserId() + ")");
            System.out.println("사용자 유형: " + (userType.equals("student") ? "학생" : "일반"));
            System.out.println("자전거 종류: " + bicycleType);
            System.out.println("이용 시간: " + minutes + "분 (테스트: " + minutes + "초)");
            
            //PricingStrategy.java에 추가된 getStrategyName() 사용
            System.out.println("적용 요금제: " + pricingContext.getStrategyName());
            
            System.out.println("최종 요금: " + fee + "원");
            System.out.println("---------------------------------");
            
            // (여기에 잔액 관리 기능도 넣으면 될 것 같아요.)
        }
    }
    
    private void calculatePrice() {
        System.out.println("\n==== 요금 계산기 ====");
        System.out.println("자전거 유형을 선택하세요:");
        System.out.println("1. 일반자전거");
        System.out.println("2. 전기자전거");
        
        int bikeChoice = getMenuChoice(1, 2);
        String bicycleType = (bikeChoice == 1) ? "일반자전거" : "전기자전거";
        
        System.out.print("이용 시간(분)을 입력하세요: ");
        int minutes = getIntInput();
        
        // 로그인한 사용자 정보 사용
        String userType = currentUser.getUserType();
        
        // 팩토리를 통해 전략 선택
        PricingStrategy strategy = strategyFactory.getStrategy(userType, bicycleType);
        pricingContext.setStrategy(strategy);
        
        int fee = pricingContext.calculatePrice(minutes);
        
        System.out.println("\n---------------------------------");
        System.out.println("        요금 계산 결과");
        System.out.println("---------------------------------");
        System.out.println("사용자: " + currentUser.getName());
        System.out.println("회원 유형: " + (userType.equals("student") ? "학생" : "일반"));
        System.out.println("자전거 종류: " + bicycleType);
        System.out.println("이용 시간: " + minutes + "분");
        System.out.println("적용 요금제: " + pricingContext.getStrategyName());
        System.out.println("예상 요금: " + fee + "원");
        System.out.println("---------------------------------");
    }
    
    
    // 유틸리티 메소드들
    private int getMenuChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.printf("잘못된 선택입니다. %d~%d 사이의 숫자를 입력하세요: ", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.print("숫자를 입력해주세요: ");
            }
        }
    }
    
    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("올바른 숫자를 입력해주세요: ");
            }
        }
    }
}