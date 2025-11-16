package observer;

/**
 * 옵저버 패턴
 */
import java.util.*;

// 간단한 옵저버 인터페이스 (Push 방식)
interface SimpleObserver {
    void update(String message);
}

// 자전거 상태 알림 클래스 (옵저버 패턴용)
class BicycleStatusNotifier {
    private List<SimpleObserver> observers = new ArrayList<>();
    private String bicycleId;
    
    public BicycleStatusNotifier(String id) {
        this.bicycleId = id;
    }
    
    public void addObserver(SimpleObserver observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(String message) {
        for (SimpleObserver observer : observers) {
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
class UserObserver implements SimpleObserver {
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
class AdminObserver implements SimpleObserver {
    @Override
    public void update(String message) {
        System.out.println("[관리자] " + message);
    }
}