package core;

/**
 * 비밀번호 해시 유틸리티 클래스
 * 평문 비밀번호 → SHA-256 해시 변환
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    // SHA-256으로 비밀번호 해시
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b)); // 16진수 문자열로 변환
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // 이 예제에서는 단순 처리
            throw new RuntimeException("해시 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}

