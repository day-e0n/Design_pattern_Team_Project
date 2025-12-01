package core;

import command.*;
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
        
        // RepairServiceObserver 생성
        // (수정됨) 생성자 인자 수정 (3개로 변경)
        this.repairObserver = new RepairServiceObserver(bicycleManager, scheduler, new RepairStrategy());
    }

    // Command Pattern_Invoker 역할
    private void executeCommand(Command command) {
        if (command != null) {
            command.execute();
        }
    }

    public void start() {
        System.out.println("=======================================");
        System.out.println("     자전거 공유 시스템 ");
        System.out.println("=======================================");

        while (true) {
            isAdminMode = false;
            
            if (currentUser == null) {
                // 맨 처음에 메인 메뉴를 먼저 보여주고, 이후 로그인/회원가입 진행
                showMainMenu();
                int mainChoice = getMenuChoice(0, 2);
                if (mainChoice == 0) {
                    System.out.println("시스템을 종료합니다. 안녕히 가세요!");
                    scheduler.shutdownNow();
                    return;
                }

                if (mainChoice == 1) { // 관리자 모드 선택
                    // 바로 로그인 화면으로
                    System.out.println("\n==== 관리자 로그인 ====");
                    loginUser();
                    if (currentUser != null) {
                        if ("admin".equals(currentUser.getUserType())) {
                            adminMode();
                        } else {
                            System.out.println("관리자 권한이 없습니다.");
                            currentUser = null;
                        }
                    }
                } else { // 사용자 모드 선택
                    showLoginMenu();
                    int choice = getMenuChoice(0, 2);
                    switch (choice) {
                        case 1:
                            loginUser();
                            if (currentUser != null) {
                                userMode();
                            }
                            break;
                        case 2:
                            registerUser();
                            break;
                        case 0:
                            System.out.println("시스템을 종료합니다. 안녕히 가세요!");
                            scheduler.shutdownNow();
                            return;
                    }
                }
            } else {
                // 로그인된 사용자는 기존처럼 메인 메뉴에서 모드 선택
                showMainMenu();
                int choice = getMenuChoice(0, 2);
                switch (choice) {
                    case 1:
                        if ("admin".equals(currentUser.getUserType())) {
                            adminMode();
                        } else {
                            System.out.println("관리자 권한이 없습니다. 사용자 모드로 이동합니다.");
                            userMode();
                        }
                        break;
                    case 2:
                        userMode();
                        break;
                    case 0:
                        currentUser = null;
                        System.out.println("로그아웃 되었습니다.");
                        break;
                }
            }
        }
    }

    /**
     * 로그인 및 회원가입 로직
     */

    private void showLoginMenu() {
        System.out.println("\n==== 접속 메뉴 ====");
        System.out.println("1. 로그인");
        System.out.println("2. 회원가입");
        System.out.println("0. 종료");
        System.out.print("선택하세요: ");
    }

    private void loginUser() {
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("PW: ");
        String pw = scanner.nextLine().trim();
        
        User user = userManager.login(id, pw);
        if (user != null) {
            currentUser = user;
            System.out.println("\n[환영합니다, " + user.getName() + "님!]");
            System.out.println("현재 잔액: " + user.getBalance() + "원");
        } else {
            System.out.println("!!! 로그인 실패: ID 또는 비밀번호를 확인하세요.");
        }
    }

    private void registerUser() {
        System.out.println("\n==== 회원가입 ====");
        System.out.print("사용자 ID를 입력하세요: ");
        String userId = scanner.nextLine().trim();
        
        if (userManager.isUserIdExists(userId)) {
            System.out.println("이미 존재하는 ID입니다.");
            return;
        }

        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();
        String passwordHash = PasswordUtil.hashPassword(password);

        System.out.print("이름을 입력하세요: ");
        String name = scanner.nextLine().trim();
        System.out.print("전화번호를 입력하세요: ");
        String phoneNumber = scanner.nextLine().trim();
        System.out.print("거주 지역을 입력하세요: ");
        String location = scanner.nextLine().trim();
        
        System.out.print("회원 유형을 선택하세요 (1: 일반, 2: 학생): ");
        int userTypeChoice = getMenuChoice(1, 2);
        String userType = (userTypeChoice == 2) ? "student" : "regular";
        // 초기 잔액 10000원 지급
        User user = new User(userId, passwordHash, name, phoneNumber, location, userType, 10000);
        userManager.saveUser(user);
        System.out.println("회원가입 완료! (초기 잔액 10,000원 지급됨)");
    }
    
    /**
     * 메뉴 모드
     */

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
                case 1: addBicycle(); break;
                case 2: removeBicycle(); break;
                case 3: executeCommand(new ListAllBicyclesCommand(bicycleManager)); 
                        break;
                case 4: viewBicyclesByStatus(); break;
                case 5: reportBrokenBicycle(); break;
                case 6: changeBicycleLocation(); break;
                case 7: viewBicycleDetails(); break;
                case 8: bicycleManager.showStatistics(); break;
                case 0:
                    System.out.println("관리자 모드를 종료합니다.");
                    isAdminMode = false;
                    currentUser = null;
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
        System.out.println("0. 로그아웃");
        System.out.print("선택하세요: ");
    }

    private void userMode() {
        System.out.println("\n 사용자 모드에 들어갑니다.");
        System.out.println("현재 잔액: " + currentUser.getBalance() + "원");
        
        while (true) {
            showUserMenu();
            int choice = getMenuChoice(0, 4); 
            
            switch (choice) {
                case 1: viewAvailableBicycles(); break;
                case 2: rentBicycle(); break;
                case 3: returnBicycle(); break;
                case 4: chargeBalance(); break;
                case 0:
                    currentUser = null;
                    System.out.println("로그아웃 되었습니다.");
                    return;
            }
        }
    }

    private void showUserMenu() {
        System.out.println("\n==== 사용자 메뉴 ====");
        System.out.println("1. 대여 가능한 자전거 보기");
        System.out.println("2. 자전거 대여");
        System.out.println("3. 자전거 반납");
        System.out.println("4. 잔액 충전");
        System.out.println("0. 로그아웃");
        System.out.print("선택하세요: ");
    }
    
    /**
     * 관리자 기능
     */

    // 1번
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
        Command cmd = new AddBicycleCommand(bicycleManager, id, type, stationName);
        executeCommand(cmd);
    }

    // 2번
    private void removeBicycle() {
        System.out.print("삭제할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        core.Bicycle bike = bicycleManager.getBicycle(id);
        if (bike != null && bike.getBikeState().canDelete()) {
            Command cmd = new RemoveBicycleCommand(bicycleManager, id);
            executeCommand(cmd);
        }
    }

    // 3번 (전체 자전거 조회) : 전부 bicycleManager로 위임 

    // 4번
    private void viewBicyclesByStatus() {
        System.out.println("\n상태를 선택하세요:");
        System.out.println("1. 대여 가능");
        System.out.println("2. 대여 중");
        System.out.println("3. 수리 중");
        System.out.println("4. 고장");

        int choice = getMenuChoice(1, 4);
        BicycleStatus status = BicycleStatus.values()[choice - 1];

        Command cmd = new ListByStatusCommand(bicycleManager, status);
        executeCommand(cmd);
    }

    // 5번
    private void reportBrokenBicycle() {
        System.out.print("신고할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        core.Bicycle bike = bicycleManager.getBicycle(id);
        if (bike == null) {
            System.out.println("오류: 존재하지 않는 자전거 ID입니다.");
            return;
        }
        if (!bike.getBikeState().canReport()) return;
        
        List<BreakdownReason> reasons = new ArrayList<>();
        boolean isElectric = "전기자전거".equals(bike.getType());
        BreakdownReason[] values = BreakdownReason.values();
        
        while (true) {
            System.out.println("\n고장 사유를 선택하세요 (완료 시 0):");
            for (int i = 0; i < values.length; i++) System.out.printf("%d. %s\n", i + 1, values[i]);
            System.out.print("선택하세요: ");
            int c = getMenuChoice(0, values.length);
            if (c == 0) break;
            BreakdownReason reason = values[c - 1];
            if (!isElectric && reason == BreakdownReason.BATTERY) {
                System.out.println("오류: 일반 자전거는 배터리 문제를 선택할 수 없습니다. 다시 선택해주세요.");
                continue;
            }
            reasons.add(reason);
        }

        if (!reasons.isEmpty()) {
            bike.getBikeState().reportBroken(reasons);
            BreakdownReportSubject subject = new BreakdownReportSubject(id, reasons, bike.getLocation(), isElectric);
            subject.addObserver(repairObserver);
            subject.report();
            System.out.println("수리 신고가 접수되었습니다.");
        }
    }

    // 6번
    private void changeBicycleLocation() {
        System.out.print("위치를 변경할 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        core.Bicycle bike = bicycleManager.getBicycle(id);
        if (bike != null && !bike.getBikeState().canMove()) return;
        
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
        
        Command cmd = new ChangeLocationCommand(bicycleManager, id, stationName);
        executeCommand(cmd);
    }

    // 7번
    private void viewBicycleDetails() {
        System.out.print("상세 정보를 볼 자전거 ID를 입력하세요: ");
        String id = scanner.nextLine();
        bicycleManager.showBicycleDetails(id);
    }

    // 8번 (통계 보기) : 전부 bicycleManager로 위임

    /**
     * 사용자 기능
     */

    // 1번
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

    // 2번
    private void rentBicycle() {
        if (currentUser.isRenting()) {
            System.out.println("이미 대여 중인 자전거가 있습니다 (" + currentUser.getRentedBicycleId() + ")");
            return;
        }

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

        core.Bicycle bike = bicycleManager.getBicycle(id);
        if (bike != null && !bike.getBikeState().canRent()) {
            return;
        }

        // 대여 전 최종 확인 추가
        System.out.print("대여하시겠습니까? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("대여가 취소되었습니다.");
            return;
        }

        if (bicycleManager.rentBicycle(id)) {
            currentUser.startRental(id);
        }
    }
    
    // 3번
    private void returnBicycle() {
        String id = currentUser.getRentedBicycleId();
        
        if (id == null) {
            System.out.println("대여 중인 자전거가 없습니다.");
            System.out.print("반납할 자전거 ID를 직접 입력하시겠습니까? (y/n): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y")) {
                System.out.print("자전거 ID: ");
                id = scanner.nextLine();
            } else {
                return;
            }
        } else {
            System.out.println(">> 반납 자전거: " + id);
        }
        
        LocationManager loc = LocationManager.getInstance();
        System.out.println("반납 스테이션 선택:");
        loc.showStationList();
        System.out.print("번호: ");
        int num = getMenuChoice(1, 4);
        String station = loc.getStationNameByNumber(num);
        
        if (station == null) return;

        core.Bicycle bike = bicycleManager.getBicycle(id);
        if (bike == null) {
            System.out.println("오류: 자전거를 찾을 수 없습니다.");
            return;
        }
        String type = bike.getType();
        
        int minutes = bicycleManager.returnBicycle(id, station);
        
        if (minutes >= 0) {
            currentUser.endRental();
            System.out.println("\n--- 반납 완료 (" + minutes + "분 이용) ---");
            
            PricingStrategy strategy = strategyFactory.getStrategy(currentUser.getUserType(), type);
            pricingContext.setStrategy(strategy);
            int fee = pricingContext.calculatePrice(minutes);

            System.out.println("---------------------------------");
            System.out.println("        결제 명세서");
            System.out.println("---------------------------------");
            System.out.println("사용자: " + currentUser.getName());
            System.out.println("자전거: " + type + " (" + id + ")");
            System.out.println("요금제: " + pricingContext.getStrategyName());
            System.out.println("청구 금액: " + fee + "원");
            
            boolean success = userManager.deductBalance(currentUser.getUserId(), fee);
            if (success) {
                System.out.println("[결제 성공] 잔액 차감 완료");
                System.out.println("남은 잔액: " + currentUser.getBalance() + "원");
            } else {
                System.out.println("[결제 실패] 잔액 부족!");
                System.out.println("현재 잔액: " + currentUser.getBalance() + "원");
            }
            System.out.println("---------------------------------");
        }
    }

    // 4번
    private void chargeBalance() {
        System.out.println("\n==== 잔액 충전 ====");
        System.out.println("현재 잔액: " + currentUser.getBalance() + "원");
        System.out.print("충전할 금액을 입력하세요: ");
        int amount = getIntInput();

        if (amount <= 0) {
            System.out.println("0원보다 큰 금액을 입력해주세요.");
            return;
        }

        // UserManager.rechargeBalance()가 users.csv에 저장합니다.
        userManager.rechargeBalance(currentUser.getUserId(), amount);
    }
    
    // 유틸리티 메서드들
    private int getMenuChoice(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                // 공백 입력 처리
                if (input.isEmpty()) continue;
                
                int choice = Integer.parseInt(input);
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