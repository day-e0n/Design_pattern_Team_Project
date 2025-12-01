package core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * 사용자 관리 클래스
 */
public class UserManager {

    private static final String USER_CSV_FILE = "users.csv";
    private Map<String, User> users = new HashMap<>();

    private static final String ADMIN_USER_ENV = "ADMIN_USER";
    private static final String ADMIN_PASS_ENV = "ADMIN_PASS";
    private static final String ADMIN_PASS_HASH_ENV = "ADMIN_PASS_HASH";

    public UserManager() {
        loadUsers();
        ensureAdminUser();
    }

    private void loadUsers() {
        Path path = Path.of(USER_CSV_FILE);
        if (!Files.exists(path)) return;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); 
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                
                if (parts.length >= 6) {
                    String uid = parts[0].trim();
                    String pwd = parts[1].trim();
                    String name = parts[2].trim();
                    String phone = parts[3].trim();
                    String loc = parts[4].trim();
                    String type = parts[5].trim();
                    
                    int balance = 0;
                    if (parts.length > 6) {
                        try {
                            balance = Integer.parseInt(parts[6].trim());
                        } catch (NumberFormatException e) {
                            balance = 0;
                        }
                    }

                    User user = new User(uid, pwd, name, phone, loc, type, balance);
                    users.put(uid, user);
                }
            }
        } catch (IOException e) {
            System.out.println("유저 정보 로딩 중 오류 발생: " + e.getMessage());
        }
    }

    private void saveAllUsers() {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(USER_CSV_FILE), 
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, 
                StandardOpenOption.TRUNCATE_EXISTING)) {
            
            writer.write("userid,passwordhash,name,phoneNumber,location,userType,balance");
            writer.newLine();
            
            for (User user : users.values()) {
                writer.write(user.toCsvRow());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUser(User user) {
        users.put(user.getUserId(), user);
        saveAllUsers();
        System.out.println("사용자 정보가 저장되었습니다.");
    }

    public User login(String userId, String password) {
        User user = users.get(userId);
        if (user == null) return null;

        String inputHash = PasswordUtil.hashPassword(password);
        if (user.getPasswordHash().equals(inputHash)) {
            return user;
        }
        return null;
    }

    public boolean isUserIdExists(String userId) {
        return users.containsKey(userId);
    }

    public boolean deductBalance(String userId, int amount) {
        User user = users.get(userId);
        if (user == null) return false;

        int currentBalance = user.getBalance();
        if (currentBalance >= amount) {
            user.setBalance(currentBalance - amount);
            saveAllUsers();
            return true;
        } else {
            return false;
        }
    }
    public void rechargeBalance(String userId, int amount) {
        User user = users.get(userId);
        if (user != null && amount > 0) {
            user.setBalance(user.getBalance() + amount);
            saveAllUsers();
            System.out.println(">> 충전 성공! 현재 잔액: " + user.getBalance() + "원");
        } else {
            System.out.println(">> 충전 실패: 사용자 정보가 없거나 금액이 올바르지 않습니다.");
        }
    }

    private void ensureAdminUser() {
        for (User u : users.values()) {
            if ("admin".equals(u.getUserType())) return;
        }

        String adminId = System.getenv(ADMIN_USER_ENV);
        if (adminId == null || adminId.isBlank()) adminId = "admin";

        if (users.containsKey(adminId)) return;

        String adminPass = System.getenv(ADMIN_PASS_ENV);
        String passwordHash;
        
        if (adminPass != null && !adminPass.isBlank()) {
            passwordHash = PasswordUtil.hashPassword(adminPass);
        } else {
            String generated = generateRandomPassword(12);
            passwordHash = PasswordUtil.hashPassword(generated);
            System.out.println("[UserManager] 관리자 계정 생성됨 (" + adminId + " / " + generated + ")");
        }

        User admin = new User(adminId, passwordHash, "Administrator", "", "", "admin", 0);
        saveUser(admin);
    }

    private String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*-_";
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}