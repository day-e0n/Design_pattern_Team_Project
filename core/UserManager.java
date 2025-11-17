package core;

/**
 * 사용자 관리 클래스
 * 사용자 정보를 CSV 파일에 저장
 */

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
                writer.write("userid,passwordhash,name,phoneNumber,location");
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
}
