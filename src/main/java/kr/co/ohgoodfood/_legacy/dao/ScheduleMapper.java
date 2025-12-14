//package kr.co.ohgoodfood._legacy.dao;
//
//import org.apache.ibatis.annotations.Mapper;
//import java.util.List;
//import kr.co.ohgoodfood._legacy.dto.ReservationConfirmed;
//
//
//@Mapper
//public interface ScheduleMapper {
//    //금일 예약 가게 가져오기
//    List<ReservationConfirmed> todayReservation(String formattedDate);
//    //금일 예약 가게 상태 업데이트
//    void updateStoreStatus(ReservationConfirmed reservationConfirmed);
//    //금일 예약 가게 주문 가져오기
//    List<ReservationConfirmed> todayReservationOrder(String formattedDate);
//    //금일 예약 가게 주문 상태 확정 업데이트
//    void updateOrderStatus(ReservationConfirmed reservationConfirmed);
//    //금일 예약 가게 주문 상태 취소 업데이트
//    void updateOrderStatusCancel(ReservationConfirmed reservationConfirmed);
//    //픽업 시간 종료 가게 가져오기
//    List<ReservationConfirmed> pickupEnd(String formattedDate);
//    //픽업 안 된 주문 가져오기, 픽업 시간 종료 기준
//    List<ReservationConfirmed> pickupNotDone(String formattedDate);
//    //픽업 안 된 주문 가져오기, 픽업 시간 시작 기준
//    List<ReservationConfirmed> pickupNotDoneStart(String formattedDate);
//    //가게 이름 가져오기
//    String getStoreName(String store_id);
//    //사용자 닉네임 가져오기
//    String getUserNickname(String user_id);
//}
