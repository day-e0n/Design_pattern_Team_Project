package core;

/**
 * 사용자 관리 클래스
 * 사용자 정보를 CSV 파일에 저장 및 로그인 인증
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class UserManager {

    private static final String USER_CSV_FILE = "users.csv"; // 프로젝트 루트에 생성

    public void saveUser(User user) {
        Path path = Path.of(USER_CSV_FILE);
        boolean exists = Files.exists(path);

        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
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

        try (BufferedReader reader = Files.newBufferedReader(path)) {
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

        try (BufferedReader reader = Files.newBufferedReader(path)) {
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
