package core;

import java.util.Scanner;
import java.util.List;
import strategy.*;
import observer.*;

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
    
    public ConsoleInterface() {
        this.scanner = new Scanner(System.in);
        this.bicycleManager = new BicycleManager();
        this.pricingContext = new PricingContext(new RegularUserRegularBicycleStrategy());
        this.strategyFactory = new PricingStrategyFactory();
        this.userManager = new UserManager();
    }
    
    public void start() {
        System.out.println("=======================================");
        System.out.println("     자전거 공유 시스템 ");
        System.out.println("=======================================");
        
        while (true) {
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
                    changeBicycleStatus();
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
        System.out.println("5. 자전거 상태 변경");
        System.out.println("6. 자전거 위치 변경");
        System.out.println("7. 자전거 상세 정보");
        System.out.println("8. 통계 보기");
        System.out.println("0. 메인 메뉴로");
        System.out.print("선택하세요: ");
    }
    
    private void userMode() {
        System.out.println("\n 사용자 모드에 들어갑니다.");
        
        while (true) {
            showUserMenu();
            int choice = getMenuChoice(0, 4);
            
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    viewAvailableBicycles();
                    break;
                case 3:
                    rentBicycle();
                    break;
                case 4:
                    returnBicycle();
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
        bicycleManager.removeBicycle(id);
    }
    
    private void viewBicyclesByStatus() {
        System.out.println("\n상태를 선택하세요:");
        System.out.println("1. 대여가능");
        System.out.println("2. 대여중");
        System.out.println("3. 정비중");
        System.out.println("4. 고장");
        
        int choice = getMenuChoice(1, 4);
        BicycleStatus status = BicycleStatus.values()[choice - 1];
        bicycleManager.listBicyclesByStatus(status);
    }
    
    private void changeBicycleStatus() {
        System.out.print("상태를 변경할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        
        System.out.println("\n새로운 상태를 선택하세요:");
        System.out.println("1. 대여가능");
        System.out.println("2. 대여중");
        System.out.println("3. 정비중");
        System.out.println("4. 고장");
        
        int choice = getMenuChoice(1, 4);
        BicycleStatus newStatus = BicycleStatus.values()[choice - 1];
        bicycleManager.changeBicycleStatus(id, newStatus);
    }
    
    private void changeBicycleLocation() {
        System.out.print("위치를 변경할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        
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
    private void registerUser() {
    System.out.println("\n==== 회원가입 ====");
    
    System.out.print("사용자 ID를 입력하세요: ");
    String userId = scanner.nextLine().trim();

    System.out.print("비밀번호를 입력하세요: ");
    String password = scanner.nextLine();
    String passwordHash = PasswordUtil.hashPassword(password);  // 해시 처리

    System.out.print("이름을 입력하세요: ");
    String name = scanner.nextLine().trim();

    System.out.print("전화번호를 입력하세요 (예: 010-1234-5678): ");
    String phoneNumber = scanner.nextLine().trim();

    System.out.print("거주 지역(위치)을 입력하세요: ");
    String location = scanner.nextLine().trim();

    User user = new User(userId, passwordHash, name, phoneNumber, location);
    userManager.saveUser(user);

    System.out.println("사용자 정보 입력이 완료되었습니다.");
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
        
        bicycleManager.rentBicycle(id);
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
            
            System.out.println("\n--- 반납 완료! (" + minutes + "초 이용) ---");
            System.out.println("--- 요금 계산을 시작합니다. (테스트: 1초 = 1분) ---");
        
            // (임시) 사용자 정보 입력 (회의록 4.A - 유저 클래스 구현 전)
            System.out.println("요금 계산을 위해 사용자 유형을 선택하세요:");
            System.out.println("1. 일반 (general)");
            System.out.println("2. 학생 (student)");
            int userChoice = getMenuChoice(1, 2);
            String userType = (userChoice == 1) ? "general" : "student";
            
            // 팩토리를 통해 전략 선택
            PricingStrategy strategy = strategyFactory.getStrategy(userType, bicycleType);
            
            // Context를 통해 요금 계산 실행
            pricingContext.setStrategy(strategy);
            int fee = pricingContext.calculatePrice(minutes);
            
            System.out.println("---------------------------------");
            System.out.println("        최종 결제 요금");
            System.out.println("---------------------------------");
            System.out.println("사용자 유형: " + userType);
            System.out.println("자전거 종류: " + bicycleType);
            System.out.println("이용 시간: " + minutes + "분 (테스트: " + minutes + "초)");
            
            //PricingStrategy.java에 추가된 getStrategyName() 사용
            System.out.println("적용 요금제: " + pricingContext.getStrategyName());
            
            System.out.println("최종 요금: " + fee + "원");
            System.out.println("---------------------------------");

            
            // (여기에 잔액 관리 기능도 넣으면 될 것 같아요.)
          
        }
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