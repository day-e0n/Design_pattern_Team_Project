package observer;

/**
 * Subject (주제) 인터페이스
 * Pull 방식을 위해 Subject 자신을 넘기도록 update에 인자를 정의하지 않습니다.
 */
interface Subject {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}

/**
 * Observer (관찰자) 인터페이스
 * Pull 방식을 위해 update 메서드에서 Subject를 통째로 받습니다.
 */
interface Observer {
    void update(Subject subject);
}