package observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 공통 Subject 로직 (옵저버 관리)
 * Subject 인터페이스를 구현하여 중복 코드를 방지합니다.
 */
public abstract class AbstractSubject implements SubjectInterface {
    private List<ObserverInterface> observers = new ArrayList<>();

    @Override
    public void addObserver(ObserverInterface o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(ObserverInterface o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        // Pull 방식이므로 Subject 자신(this)을 전달
        for (ObserverInterface observer : observers) {
            observer.update(this);
        }
    }
}