package kr.co.ohgoodfood.notification.service;

public interface ScheduleService {
    public void reservationCheck();
    public void reservationCheckBeforeOneHour();
    public void pickupCheck();
    public void pickupStartCheck();
}
