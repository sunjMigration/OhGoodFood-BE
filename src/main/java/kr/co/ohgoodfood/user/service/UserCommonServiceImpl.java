package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dto.PickupStatus;
import kr.co.ohgoodfood._legacy.dto.Store;
import kr.co.ohgoodfood._legacy.dto.StoreCategory;
import kr.co.ohgoodfood.global.exception.InvalidPickupDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * UserCommonServiceImpl.java - UsersMainService interface 구현체
 *
 * @see UserCommonService - 세부 기능은 해당 클래스인 UserCommonServiceImpl에 구현한다.
 * - 공통적으로 사용하는 판별 로직(오늘 픽업 & 내일픽업 여부, categoryList 구현 등..)을 commonservice로 분리한다.
 * - 의존성 주입은 생성자 주입으로 구성한다.
 * - 스프링은 기본 빈 주입이 싱글톤이기 때문에, 따로 싱글톤 처리 없이 @Service로 해결한다.
 */

@Slf4j
@Service
public class UserCommonServiceImpl implements UserCommonService {
    /**
     * LocalDate.now()로 오늘픽업, 내일픽업, 매진, 마감 상태를 판별합니다.
     *
     * @param store_status      : store가 현재 오픈 상태인지 판단
     * @param pickup_start      : store가 오늘 픽업인지, 내일 픽업인지를 판단
     * @param amount            : store가 매진인지 판단, nullable 이므로 컬렉션 객체로 만든다.
     *
     * @return                  : PickupStatus ENUM 객체
     */
    @Override
    public PickupStatus getPickupDateStatus(String store_status, Timestamp pickup_start, Integer amount) {
        //parameter가 null인경우, nullpointerException이 발생하므로, Integer로 감싼다.
        LocalDate today = LocalDate.now();

        // [마감] - store_status = N
        if(store_status.equals("N")){
            return PickupStatus.CLOSED;
        }else{
            LocalDate pickupDate;
            if (pickup_start == null) {
                throw new InvalidPickupDataException(store_status, pickup_start, amount);
            }
            //한번 null 위험 처리를 하고나면, 그 다음부터는 time_stamp 형식 예외 등의 로직만 잡으므로, 더 안전하게 체크가 가능하다.
            try{
                pickupDate = pickup_start.toLocalDateTime().toLocalDate();
            } catch (Exception e){
                //에러 로그를 보존한채 넘긴다.
                throw new InvalidPickupDataException(store_status, pickup_start, amount, e);
            }

            // [매진] - amount = 0
            if(amount == null || amount == 0){
                return PickupStatus.SOLD_OUT;
            }else{
                // [오늘픽업] 현재 날짜와 같음
                if (pickupDate.isEqual(today)) {
                    return PickupStatus.TODAY;
                } else if (pickupDate.isEqual(today.plusDays(1))) { //[내일픽업] 현재 날짜 + 1과 같음
                    return PickupStatus.TOMORROW;
                }
            }
        }
        throw new InvalidPickupDataException(store_status, pickup_start, amount); //custom exception 던지기
    }

    /**
     * |(구분자) 구분은 확장성을 위해 프론트 단에 위임
     * 서버에서는 리스트에 담아서 보내도록 한다.
     *
     * @param store             : store dto 내부에 있는 cateogory_ 값에 따라 list를 구현하기 위함이다.
     * @return                  : 카테고리 이름이 담긴 List
     */
    @Override
    public List<String> getCategoryList(Store store) {
        List<String> category_list = new ArrayList<>();

        if(store.getCategory_bakery().equals("Y")){
            category_list.add(StoreCategory.BAKERY.getDisplayName());
        }

        if(store.getCategory_fruit().equals("Y")){
            category_list.add(StoreCategory.FRUIT.getDisplayName());
        }

        if(store.getCategory_salad().equals("Y")){
            category_list.add(StoreCategory.SALAD.getDisplayName());
        }

        if(store.getCategory_others().equals("Y")){
            category_list.add(StoreCategory.ETC.getDisplayName());
        }

        return category_list;
    }
}
