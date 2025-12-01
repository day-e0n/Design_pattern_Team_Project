package core;

/**
 * 사용자 관리 클래스
 * 사용자 정보를 CSV 파일에 저장 및 로그인 인증
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class UserManager {

    private static final String USER_CSV_FILE = "users.csv"; // 프로젝트 루트에 생성

    // 환경변수 이름
    private static final String ADMIN_USER_ENV = "ADMIN_USER";
    private static final String ADMIN_PASS_ENV = "ADMIN_PASS";
    private static final String ADMIN_PASS_HASH_ENV = "ADMIN_PASS_HASH";

    public UserManager() {
        ensureAdminUser();
    }

    public void saveUser(User user) {
        Path path = Path.of(USER_CSV_FILE);
        boolean exists = Files.exists(path);

        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,   // 파일 없으면 생성
                StandardOpenOption.APPEND    // 항상 맨 뒤에 추가
        )) {
            // 파일이 처음 생성될 때만 헤더 작성
            if (!exists) {
                writer.write("userid,passwordhash,name,phoneNumber,location,userType");
                writer.newLine();
            }

            writer.write(user.toCsvRow());
            writer.newLine();

            System.out.println("사용자 정보가 CSV 파일에 저장되었습니다. (" + USER_CSV_FILE + ")");
        } catch (IOException e) {
            System.out.println("사용자 정보를 CSV 파일에 저장하는 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    /**
     * 애플리케이션 시작 시 관리자 계정이 존재하는지 확인하고, 없으면 생성합니다.
     * - 관리자 아이디/비밀번호는 환경변수로 설정할 수 있습니다.
     * - 환경변수가 없으면 랜덤 비밀번호를 생성하여 콘솔에 출력합니다. (개발용)
     */
    private void ensureAdminUser() {
        Path path = Path.of(USER_CSV_FILE);

        // 이미 admin 타입의 사용자가 존재하면 아무 것도 하지 않음
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                reader.readLine(); // 헤더 건너뛰기
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length > 5 && "admin".equals(parts[5])) {
                        return; // 관리자 계정 이미 존재
                    }
                }
            } catch (IOException e) {
                System.out.println("관리자 계정 확인 중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        }

        // 관리자 정보 가져오기 (환경변수 우선)
        String adminId = System.getenv(ADMIN_USER_ENV);
        String adminPass = System.getenv(ADMIN_PASS_ENV);
        String adminHash = System.getenv(ADMIN_PASS_HASH_ENV);

        if (adminId == null || adminId.isBlank()) {
            adminId = "admin"; // 기본 관리자 아이디
        }

        String passwordHash;
        if (adminPass != null && !adminPass.isBlank()) {
            passwordHash = PasswordUtil.hashPassword(adminPass);
        } else if (adminHash != null && !adminHash.isBlank()) {
            passwordHash = adminHash;
        } else {
            // 환경변수가 없으면 랜덤 비밀번호 생성 (개발용). 콘솔에 출력하므로 github에 올려도 노출되지 않음.
            String generated = generateRandomPassword(12);
            passwordHash = PasswordUtil.hashPassword(generated);
            System.out.println("[UserManager] 관리자 계정이 없어서 새 계정을 생성합니다.");
            System.out.println("[UserManager] 관리자 아이디: " + adminId);
            System.out.println("[UserManager] 생성된 관리자 비밀번호(한번만 출력): " + generated);
            System.out.println("환경변수 ADMIN_USER/ADMIN_PASS 로 고정된 관리자 계정을 설정할 수 있습니다.");
        }

        // 이미 동일 아이디가 존재하면 덮어쓰지 않음
        if (isUserIdExists(adminId)) {
            System.out.println("[UserManager] 동일한 아이디가 존재하지만 관리자 계정이 없었습니다. 수동 확인 필요: " + adminId);
            return;
        }

        User admin = new User(adminId, passwordHash, "Administrator", "", "", "admin");
        saveUser(admin);
        System.out.println("[UserManager] 관리자 계정 생성 완료: " + adminId);
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

    /**
     * 사용자 로그인 인증
     * @param userId 사용자 ID
     * @param password 비밀번호 (평문)
     * @return 인증 성공 시 User 객체, 실패 시 null
     */
    public User login(String userId, String password) {
        Path path = Path.of(USER_CSV_FILE);
        
        if (!Files.exists(path)) {
            System.out.println("등록된 사용자가 없습니다. 회원가입을 먼저 진행해주세요.");
            return null;
        }

        String passwordHash = PasswordUtil.hashPassword(password);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // 헤더 건너뛰기
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                
                String csvUserId = parts[0];
                String csvPasswordHash = parts[1];
                String csvName = parts[2];
                String csvPhoneNumber = parts[3];
                String csvLocation = parts[4];
                String csvUserType = parts.length > 5 ? parts[5] : "regular";
                
                if (csvUserId.equals(userId) && csvPasswordHash.equals(passwordHash)) {
                    return new User(csvUserId, csvPasswordHash, csvName, csvPhoneNumber, csvLocation, csvUserType);
                }
            }
            
            System.out.println("아이디 또는 비밀번호가 일치하지 않습니다.");
            return null;
            
        } catch (IOException e) {
            System.out.println("사용자 정보를 읽는 중 오류가 발생했습니다.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 사용자 ID 중복 확인
     * @param userId 확인할 사용자 ID
     * @return 중복이면 true, 아니면 false
     */
    public boolean isUserIdExists(String userId) {
        Path path = Path.of(USER_CSV_FILE);
        
        if (!Files.exists(path)) {
            return false;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            reader.readLine(); // 헤더 건너뛰기
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(userId)) {
                    return true;
                }
            }
            return false;
            
        } catch (IOException e) {
            System.out.println("사용자 정보를 읽는 중 오류가 발생했습니다.");
            e.printStackTrace();
            return false;
        }
    }
}
