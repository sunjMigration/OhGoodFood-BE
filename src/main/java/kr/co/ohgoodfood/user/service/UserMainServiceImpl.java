package kr.co.ohgoodfood.user.service;

import kr.co.ohgoodfood._legacy.dao.UserMainMapper;
import kr.co.ohgoodfood._legacy.dto.*;
import kr.co.ohgoodfood.global.exception.InvalidPickupDataException;
import kr.co.ohgoodfood.util.StringSplitUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserMainServiceImpl.java - UsersMainService interface 구현체
 *
 * @see UserMainService - 세부 기능은 해당 클래스인 UserMainServiceImpl에 구현한다.
 * - 의존성 주입은 생성자 주입으로 구성한다.
 * - 스프링은 기본 빈 주입이 싱글톤이기 때문에, 따로 싱글톤 처리 없이 @Service로 해결한다.
 */

@Slf4j
@RequiredArgsConstructor
@Service
public class UserMainServiceImpl implements UserMainService {
    private final UserMainMapper userMainMapper;
    private final UserCommonService userCommonService;

    /**
     * 메인 화면에 뿌릴 DTO리스트를 가져오는 method
     *
     * @param userMainFilter    : 필터링을 위한 객체가 담겨있다. ()
     * @return                  : MainStoreDTOList (MainStore DTO의 리스트 객체)
     */
    @Override
    public List<MainStoreDTO> getMainStoreList(UserMainFilter userMainFilter) {
        List<MainStoreDTO> mainStoreList = userMainMapper.selectAllStore(userMainFilter);

        // 카테고리 이름과 pickup 상태를 저장
        // 이상 데이터 값이 있어도 , 이는 로그에 남기고 정상 데이터들은 잘 보여주기 위해 continue 처리한다.
        for(MainStoreDTO mainStore : mainStoreList){
            PickupStatus pickup_status;
            try{
                //product가 없음, 마감 상태 getProduct().으로 접근할때 nullpointerException을 막기 위함이다.
                if(mainStore.getProduct() == null){
                    pickup_status = PickupStatus.CLOSED;
                }else{
                    pickup_status = userCommonService.getPickupDateStatus(
                            mainStore.getStore().getStore_status(),
                            mainStore.getProduct().getPickup_start(),
                            mainStore.getProduct().getAmount()
                    );
                }
            } catch (InvalidPickupDataException e){
                log.info("픽업 상태 계산 실패(storeId={}): {}",
                        mainStore.getStore().getStore_id(), e.getMessage());
                //이상 데이터 값의 경우, continue로 숨김처리 및 pickup_status 계산 안함
                continue;
            }
            mainStore.setPickup_status(pickup_status);
            mainStore.setCategory_list(userCommonService.getCategoryList(mainStore.getStore()));
            mainStore.setMainmenu_list(StringSplitUtils.splitMenu(mainStore.getStore().getStore_menu(), "\\s*\\|\\s*"));
        }

        return mainStoreList;
    }

    /**
     * 지도에 표시할 가게 정보를 가져오는 method
     *
     * @param userMainFilter    : 필터링을 위한 객체가 담겨있다. main에서 사용하는걸 그대로 사용한다
     * @return                  : MainStoreDTO
     */
    //selectOneStoreByStoreId
    @Override
    public MainStoreDTO getMainStoreOne(UserMainFilter userMainFilter){
        MainStoreDTO mainStore = userMainMapper.selectOneStoreByStoreId(userMainFilter);
        PickupStatus pickup_status;

        if(mainStore.getProduct() == null){
            pickup_status = PickupStatus.CLOSED;
        }else{
            //단일건의 경우, 따로 예외처리 하거나 null을 return 하지 않고 ControllerAdvice에서 처리하도록 한다.
            pickup_status = userCommonService.getPickupDateStatus(
                    mainStore.getStore().getStore_status(),
                    mainStore.getProduct().getPickup_start(),
                    mainStore.getProduct().getAmount()
            );
        }

        mainStore.setPickup_status(pickup_status);
        mainStore.setCategory_list(userCommonService.getCategoryList(mainStore.getStore()));
        mainStore.setMainmenu_list(StringSplitUtils.splitMenu(mainStore.getStore().getStore_menu(), "\\s*\\|\\s*"));

        return mainStore;
    }
}
