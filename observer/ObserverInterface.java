package observer;

/**
 * Observer (관찰자) 인터페이스
 * Pull 방식을 위해 update 메서드에서 Subject를 통째로 받습니다.
 */
public interface ObserverInterface {
    void update(SubjectInterface subject);
}