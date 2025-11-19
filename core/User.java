package core;

/**
 * 사용자 정보 클래스
 * 사용자 ID, 비밀번호 해시, 이름, 전화번호, 위치 정보를 포함
 */

public class User {
    private String userId;
    private String passwordHash;
    private String name;
    private String phoneNumber;
    private String location;
    private String userType; // "regular" 또는 "student"

    public User(String userId, String passwordHash, String name, String phoneNumber, String location) {
        this(userId, passwordHash, name, phoneNumber, location, "regular");
    }

    public User(String userId, String passwordHash, String name, String phoneNumber, String location, String userType) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    // CSV 한 줄로 변환
    public String toCsvRow() {
        // 간단히 쉼표 기준으로만 나눌 거라, 쉼표는 제거해 둔다.
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
                userType
        );
    }
}
