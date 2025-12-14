package kr.co.ohgoodfood.user.controller;

import jakarta.validation.Valid;
import kr.co.ohgoodfood._legacy.dto.*;
import kr.co.ohgoodfood.user.service.UserMainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * UsersController
 *
 * 사용자 메인 페이지 전용 기능을 처리하는 컨트롤러입니다.
 * - GET  /user/main           : 사용자 메인 화면 조회
 * - POST /user/filter/store   : AJAX 기반 가게 목록 필터링
 * - GET  /user/map/pin        : AJAX 기반 핀으로 선택한 스토어 fragment 조회
 */

@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserMainController {
    private final UserMainService userMainService;

    // 지도 사용을 위한 앱키
    @Value("${kakao.map.appKey}")
    private String kakaoMapAppKey;

    /**
     * 사용자 메인 화면을 조회하고, 가게 목록을 뷰에 바인딩한다.
     *
     * @param model          뷰에 전달할 데이터(Model)
     * @return               포워딩할 JSP 뷰 이름 ("users/userMain")
     */
    @GetMapping("/main")
    public String userMain(Model model){

        UserMainFilter userMainFilter = new UserMainFilter(); //초기에는 filter 값이 없으므로 빈값 생성
        List<MainStoreDTO> mainStoreList = userMainService.getMainStoreList(userMainFilter);
        model.addAttribute("kakaoMapAppKey", kakaoMapAppKey);
        model.addAttribute("mainStoreList", mainStoreList);

        return "users/userMain";
    }

    /**
     * AJAX 필터링 결과에 따른 가게 목록을 조회하고 뷰 프래그먼트만 반환한다.
     *
     * @param userMainFilter JSON 바디로 전달된 필터 정보 (카테고리, 예약 가능 여부, 픽업 날짜, 검색어, 위도&경도 필터링)
     * @param br             유효성 검증을 위한 바인딩 값들을 저장
     * @param model          뷰에 전달할 데이터(Model)
     * @return               가게 카드 목록만 포함한 JSP 프래그먼트 ("users/fragment/userMainStoreList")
     */
    @PostMapping("/filter/store")
    public String filterStoreList(@Valid @RequestBody UserMainFilter userMainFilter,
                                  BindingResult br,
                                  Model model){

        if(br.hasErrors()){
            for (FieldError fe : br.getFieldErrors()) {
                switch (fe.getField()) {
                    case "amount":
                        userMainFilter.setAmount(0); // 이상값일 경우 0으로 처리
                        break;
                    case "pickup_start":
                        userMainFilter.setPickup_start(LocalDate.now());
                        break;
                    case "category_bakery":
                        userMainFilter.setCategory_bakery(YesNo.N);  // 필터 적용을 막기 위해 N으로 처리
                    case "category_fruit":
                        userMainFilter.setCategory_bakery(YesNo.N);  // 필터 적용을 막기 위해 N으로 처리
                    case "category_salad":
                        userMainFilter.setCategory_salad(YesNo.N);  // 필터 적용을 막기 위해 N으로 처리
                    case "category_others":
                        userMainFilter.setCategory_others(YesNo.N);  // 필터 적용을 막기 위해 N으로 처리
                    case "store_status":
                        userMainFilter.setStore_status(YesNo.N); //이상값일 경우 마감처리
                    case "latitude" :
                        userMainFilter.setLatitude(37.5593799298988); //설정해둔 기본 위도로 지정
                    case "longitude" :
                        userMainFilter.setLongitude(126.922667641634); //설정해둔 기본 경도로 지정
                        break;
                }
            }
        }

        List<MainStoreDTO> mainStoreList = userMainService.getMainStoreList(userMainFilter);
        model.addAttribute("mainStoreList", mainStoreList);

        // JSP fragment만 리턴
        return "users/fragment/userMainStoreList";
    }

    /**
     * map에서 pin 선택한 가게의 정보를 AJAX로 조회하고 뷰 프래그먼트만 반환한다.
     * 이미 선택한 정보 안에서 화면에 띄울 정보만 있으면 되므로, 위&경도 필터링은 제외한다.
     *
     * @param userMainFilter JSON 바디로 전달된 필터 정보 (카테고리, 예약 가능 여부, 픽업 날짜, 검색어 필터링)
     * @param br             유효성 검증을 위한 바인딩 값들을 저장
     * @return               가게 정보를 포함한 JSP 프래그먼트 ("users/fragment/userMapPinStore")
     */
    @GetMapping("/map/pin")
    public String getMapPinStore(@Valid @ModelAttribute UserMainFilter userMainFilter,
                                 BindingResult br,
                                 Model model){

        if(br.hasErrors()){
            for (FieldError fe : br.getFieldErrors()) {
                switch (fe.getField()) {
                    case "amount":
                        userMainFilter.setAmount(0); // 이상값일 경우 0으로 처리
                        break;
                    case "pickup_start":
                        userMainFilter.setPickup_start(LocalDate.now());
                        break;
                    case "category_bakery":
                        userMainFilter.setCategory_bakery(YesNo.N);  // 필터 적용을 막기 위해 N으로 처리
                    case "category_fruit":
                        userMainFilter.setCategory_bakery(YesNo.N);  // 필터 적용을 막기 위해 N으로 처리
                    case "category_salad":
                        userMainFilter.setCategory_salad(YesNo.N);  // 필터 적용을 막기 위해 N으로 처리
                    case "category_others":
                        userMainFilter.setCategory_others(YesNo.N);  // 필터 적용을 막기 위해 N으로 처리
                    case "store_status":
                        userMainFilter.setStore_status(YesNo.N); //이상값일 경우 마감처리
                        break;
                }
            }
        }

        MainStoreDTO mainStore = userMainService.getMainStoreOne(userMainFilter);
        model.addAttribute("mainStore", mainStore);

        // JSP fragment만 리턴
        return "users/fragment/userMapPinStore";
    }
}
