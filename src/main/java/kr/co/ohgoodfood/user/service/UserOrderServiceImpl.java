package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dao.UserOrderMapper;
import kr.co.ohgoodfood._legacy.dto.*;
import kr.co.ohgoodfood.global.exception.OrderCancelException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

/**
 * UserOrderServiceImpl.java - UserOrderService interface 구현체
 *
 * @see UserOrderService - 세부 기능은 해당 클래스인 UserOrderServiceImpl에 구현한다.
 * - 의존성 주입은 생성자 주입으로 구성한다.
 * - 스프링은 기본 빈 주입이 싱글톤이기 때문에, 따로 싱글톤 처리 없이 @Service로 해결한다.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class UserOrderServiceImpl implements UserOrderService{

    private final UserOrderMapper userOrderMapper;

    /**
     * 사용자의 OrderList를 가져오는 method
     *
     * @param userOrderFilter    : 세션에 접속한 사용자 id와 필터링을 위한 객체가 담겨있다.
     * @return                   : 조회한 UserOrderList
     */
    @Override
    public List<UserOrderDTO> getUserOrderList(UserOrderFilter userOrderFilter){
        List<UserOrderDTO> orderList = userOrderMapper.selectOrderList(userOrderFilter);

        log.info("-----------getUserOrderList: {}", orderList);

        // userOrder에 pickup_status와 block_cancel 상태를 저장.
        for(UserOrderDTO userOrder : orderList){
            userOrder.setPickup_status(getOrderPickupDateStatus(userOrder));
            //주문에 해당하는 포인트를 적립
            userOrder.setPoint(getOrderPoint(userOrder));
            userOrder.setBlock_cancel(getOrderBlockCancel(userOrder.getOrder().getOrder_status(), userOrder.getProduct().getReservation_end()));
        }
        return orderList;
    }

    /**
     * LocalDate.now()로 오늘픽업, 내일픽업만을 판별합니다.
     * Orders 페이지에서는 마감,매진 값은 필요 없기 때문에, 이것만을 판별하는 로직을 따로 만듭니다.
     *
     * @param userOrder        : 판별이 필요한 데이터가 담긴 객체
     * @return                 : PickupStatus ENUM 객체
     */
    @Override
    public PickupStatus getOrderPickupDateStatus(UserOrderDTO userOrder) {
        //order 정보에 product가 없는 일은 없으므로, bookmark나 main 에서처럼 null처리를 할 필요 없다.
        LocalDate today = LocalDate.now();

        Timestamp pickupStart = userOrder.getProduct().getPickup_start();
        LocalDate pickupDate = pickupStart.toLocalDateTime().toLocalDate();

        // [오늘픽업] 현재 날짜와 같음
        if (pickupDate.isEqual(today)) {
            return PickupStatus.TODAY;
        }
        // [내일픽업] 현재 날짜 + 1과 같음
        if (pickupDate.isEqual(today.plusDays(1))) {
            return PickupStatus.TOMORROW;
        }

        // 둘다 아닌 날짜 = 마감
        return PickupStatus.CLOSED;
    }

    /**
     * 사용자의 구매 금액별 포인트를 설정하기 위한 메서드
     *
     * @param userOrder          : orderList 화면에 뿌릴 객체
     * @return                   : 구매 금액의 1%에 해당하는 point
     */
    public int getOrderPoint(UserOrderDTO userOrder){
        return (int) (userOrder.getPaid().getPaid_price() * 0.01);
    }

    /**
     * pickup_status가 오늘픽업 혹은 내일 픽업인 경우에, (즉, confirmed 상태) 한 시간 전에 취소 block 상태를 만들기 위함입니다.
     * reservation_end -1이 NOW일때를 계산합니다.
     *
     * @param order_status       : order_status가 reservation인 경우에만 진행한다.
     * @param reservation_end    : 예약 마감 한시간 전을 계산하기 위한 reservation_end
     * @return                   : block_cancel 값을 설정하기 위해 boolean return
     */
    public boolean getOrderBlockCancel(String order_status, Timestamp reservation_end){
        if(order_status.equals("reservation")){
            Timestamp now = new Timestamp(System.currentTimeMillis());

            long oneHourInMillis = 60L * 60L * 1000L; //한시간 계산
            Timestamp reservationEndOneHourBefore = new Timestamp(reservation_end.getTime() - oneHourInMillis);

            //reservation_end -1h < now < reservation_end
            if (now.after(reservationEndOneHourBefore) && now.before(reservation_end)) {
                return true;  // 마감 1시간 전 이내이면, 취소 블록하고 막는다.
            }
            return false;
        }
        return false; //오늘 픽업, 내일 픽업이 아니라면 이 block 변수는 false
    }

    /**
     * 사용자가 선택한 order를 취소 처리 합니다.
     * tracsaction으로 묶은 두 개의 쿼리 중 하나라도 실패하면 롤백하고 OrderCancelException을 발생시킨다.
     *
     * @param userOrderRequest   : 사용자 주문 상태 변경 처리에 필요한 DTO
     * @return                   : UPDATE 쿼리가 잘 실행 되었는지 보기 위해 row return
     */
    @Override
    @Transactional // 트랜잭션을 걸어주어, 두 개의 쿼리 중 하나라도 실패하면 롤백되도록 한다.
    public boolean updateUserOrderCancel(UserOrderRequest userOrderRequest){
        userOrderRequest.setOrder_status("cancel");
        userOrderRequest.setCanceld_from("user");

        int updateOrderCnt = userOrderMapper.updateOrderStatus(userOrderRequest);
        int updateAmountCnt = userOrderMapper.restoreProductAmount(userOrderRequest);

        // 하나라도 오류가 발생할 경우, 롤백을 위해 exception throws
        if (updateOrderCnt != 1 || updateAmountCnt != 1) {
            throw new OrderCancelException(updateOrderCnt, updateAmountCnt);
        }
        return true;
    }
}
