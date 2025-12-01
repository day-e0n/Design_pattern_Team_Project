package core;

/**
 * 사용자 정보 클래스
 * [수정됨]: userType(등급)과 balance(잔액) 필드 및 관련 생성자 추가
 */
public class User {
    private String userId;
    private String passwordHash;
    private String name;
    private String phoneNumber;
    private String location;
    private String userType; // "general", "student" 등
    private boolean renting = false; 
    private String rentedBicycleId = null; 
    private int balance;    

    // 생성자 1: 기본 (잔액 0)
    public User(String userId, String passwordHash, String name, String phoneNumber, String location) {
        this(userId, passwordHash, name, phoneNumber, location, "regular", 0);
    }

    // 생성자 2: 타입 지정 (잔액 0)
    public User(String userId, String passwordHash, String name, String phoneNumber, String location, String userType) {
        this(userId, passwordHash, name, phoneNumber, location, userType, 0);
    }

    // 생성자 3
    public User(String userId, String passwordHash, String name, String phoneNumber, String location, String userType, int balance) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.userType = userType;
        this.balance = balance;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getLocation() { return location; }
    public String getUserType() { return userType; }
    
    // ★ 잔액 관련 Getter/Setter
    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    // 대여 상태 관련
    public boolean isRenting() { return renting; }
    public String getRentedBicycleId() { return rentedBicycleId; }
    
    public void startRental(String bicycleId) {
        this.renting = true;
        this.rentedBicycleId = bicycleId;
    }

    public void endRental() {
        this.renting = false;
        this.rentedBicycleId = null;
    }

    // CSV 한 줄로 변환 (잔액 포함)
    public String toCsvRow() {
        String safeUserId = userId.replace(",", " ");
        String safeName = name.replace(",", " ");
        String safePhoneNumber = phoneNumber.replace(",", " ");
        String safeLocation = location.replace(",", " ");

        return String.join(",",
                safeUserId,
                passwordHash,
                safeName,
                safePhoneNumber,
                safeLocation,
                userType,
                String.valueOf(balance) 
        );
    }
}