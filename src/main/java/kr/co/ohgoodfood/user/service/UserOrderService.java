package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dto.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * UserOrderService interface
 * - UserOrderService 기능 틀 interface
 * - 유지 보수 편의성 및, DIP 원칙을 준수하기 위해 interface로 구성한다.
 */

public interface UserOrderService {
    //[Controller 로직] 주문내역 Controller 연결 로직
    List<UserOrderDTO> getUserOrderList(UserOrderFilter userOrderFilter);

    //[판별 로직] UserOrder에서는 마감과 매진은 필요 없으므로, 오늘 픽업, 내일 픽업만 판별하는 로직
    PickupStatus getOrderPickupDateStatus(UserOrderDTO userOrder);

    //[판별 로직] 구매 금액별 point를 계산하기 위한 메서드
    int getOrderPoint(UserOrderDTO userOrder);

    //[판별 로직] reservation_end 한 시간 전에 주문취소를 막아두기 위한 상태 판별 로직
    boolean getOrderBlockCancel(String order_status, Timestamp reservation_end);

    //[Controller 로직] 주문내역 취소 Controller 연결 로직
    boolean updateUserOrderCancel(UserOrderRequest userOrderRequest);
}
