/**
 * 옵저버 패턴
 */
import java.util.*;

// 옵져버 인터페이스
interface Observer {
    void update(String message);
}

// 옵져버 클래스
class BicycleStatus {
    private List<Observer> observers = new ArrayList<>();
    private String bicycleId;
    
    public BicycleStatus(String id) {
        this.bicycleId = id;
    }
    
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
    
    public void rent() {
        notifyObservers(bicycleId + " 대여됨");
    }
    
    public void returnBike() {
        notifyObservers(bicycleId + " 반납됨");
    }
}

// 사용자 알림
class UserObserver implements Observer {
    private String name;
    
    public UserObserver(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String message) {
        System.out.println("[사용자 " + name + "] " + message);
    }
}

// 관리자 알림
class AdminObserver implements Observer {
    @Override
    public void update(String message) {
        System.out.println("[관리자] " + message);
    }
}