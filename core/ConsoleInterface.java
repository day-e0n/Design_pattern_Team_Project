package core;

import java.util.Scanner;
import java.util.List;
import strategy.*;
import observer.*;

/**
 * 콘솔 기반 사용자 인터페이스
 * 관리자 모드와 사용자 모드를 제공
 */
class ConsoleInterface {
    private Scanner scanner;
    private BicycleManager bicycleManager;
    private PricingContext pricingContext;
    private PricingStrategyFactory strategyFactory;
    
    public ConsoleInterface() {
        this.scanner = new Scanner(System.in);
        this.bicycleManager = new BicycleManager();
        this.pricingContext = new PricingContext(new RegularUserRegularBicycleStrategy());
        this.strategyFactory = new PricingStrategyFactory();
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
                    viewAvailableBicycles();
                    break;
                case 2:
                    rentBicycle();
                    break;
                case 3:
                    returnBicycle();
                    break;
                case 4:
                    calculateRentalFee();
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
        
        bicycleManager.returnBicycle(id, stationName);
    }
    
    private void calculateRentalFee() {
        System.out.println("요금제를 선택하세요:");
        System.out.println("1. 일반 요금");
        System.out.println("2. 학생 요금 (20% 할인)");
        int userchoice = getMenuChoice(1, 2);
        String userType = (userchoice == 2) ? "student" : "regular";
        System.out.println("자전거 유형을 선택하세요:");
        System.out.println("1. 일반 자전거");
        System.out.println("2. 전기 자전거");
        int bikechoice = getMenuChoice(1, 2);
        String bicycleType = (bikechoice == 2) ? "전기자전거" : "일반자전거";
        System.out.print("대여 시간을 분 단위로 입력하세요: ");
        int minutes = getIntInput();
        PricingStrategy strategy = strategyFactory.getStrategy(userType, bicycleType);
        pricingContext.setStrategy(strategy);
        int price = pricingContext.calculatePrice(minutes);
        System.out.printf("총 대여 요금은 %d원입니다.\n", price);
        
    
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