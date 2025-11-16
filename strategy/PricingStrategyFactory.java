/*
 전략 패턴 어떻게 구현할지 보다보니까 코드 완성도를 위해서는 팩토리 패턴도 같이 써야한다길래 어쩔 수 없이 팩토리패턴도 같이 써봤습니다..
 consoleInterface.java에서 직접 요금제를 선택하지않고 이 클래스를 통해서 요금제에 따른 계산을 할 수 있도록 했습니다.
 */

 package strategy;

public class PricingStrategyFactory {
    /* 
    @param userType 사용자 유형 ("regular" 또는 "student")
    @param bicycleType 자전거 유형 ("regular" 또는 "electric")
    @return 해당하는 요금제 전략 객체
    */

    public PricingStrategy getStrategy (String userType, String bicycleType) {
        //자전거 타입으로 한 번 분기하고 switch문으로 사용자 타입에 따라 다시 분기
        boolean isElectric = "전기자전거".equals(bicycleType);

        switch (userType.toLowerCase()) {
            case "student": 
                return isElectric ? new StudentUserElectricBicycleStrategy() 
                                  : new StudentUserRegularBicycleStrategy();
            case "regular":
            default:
                return isElectric ? new RegularUserElectricBicycleStrategy() 
                                  : new RegularUserRegularBicycleStrategy();
           
 }
}
 }
                