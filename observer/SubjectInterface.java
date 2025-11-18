package observer;

/**
 * Subject (주제) 인터페이스
 * Pull 방식을 위해 Subject 자신을 넘기도록 update에 인자를 정의하지 않습니다.
 */
public interface SubjectInterface {
    void addObserver(ObserverInterface o);
    void removeObserver(ObserverInterface o);
    void notifyObservers();
}