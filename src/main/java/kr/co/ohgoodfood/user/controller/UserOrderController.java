package kr.co.ohgoodfood.user.controller;

import jakarta.validation.Valid;
import kr.co.ohgoodfood._legacy.dto.*;
import kr.co.ohgoodfood.global.exception.OrderCancelException;
import kr.co.ohgoodfood.user.service.UserOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * UserOrderController.java
 *
 * - GET  /user/orderList      : 유저가 가진 orderList 목록 조회
 * - POST /user/filter/order   : AJAX 기반 오더 목록 필터링
 * - POST /user/order/cancel   : 유저가 선택한 오더 주문 취소
*/

@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserOrderController {
    private final UserOrderService userOrderService;

    /**
     * 세션에 있는 유저가 가진 주문 목록을 조회한다.
     *
     * @param model                뷰에 전달할 데이터(Model)
     * @param session              현재 HTTP 세션(로그인된 사용자 정보)
     * @return                     users/userOrders.jsp로 포워딩
     */
    @GetMapping("/orderList")
    public String userOrderList(Model model,
                                HttpSession session){

        Account loginUser = (Account) session.getAttribute("user");
        String user_id = loginUser.getUser_id();

        UserOrderFilter userOrderFilter = new UserOrderFilter();

        userOrderFilter.setUser_id(user_id); //필터에 id값 추가
        List<UserOrderDTO> userOrderList = userOrderService.getUserOrderList(userOrderFilter);

        log.info("UserOrderServiceImpl.getUserOrderList() - userOrderList : {}", userOrderList.toString());

        model.addAttribute("userOrderList", userOrderList);

        return "users/userOrders";
    }

    /**
     * AJAX 필터링 결과에 따른 주문 목록을 조회하고 뷰 프래그먼트만 반환한다.
     *
     * @param userOrderFilter      JSON 바디로 전달된 필터 정보 (order_status 정보)
     * @param model                뷰에 전달할 데이터(Model)
     * @param session              현재 HTTP 세션(로그인된 사용자 정보)
     * @return                     필터링 된 가게 주문 목록만 포함한 JSP 프래그먼트 ("users/fragment/userOrderList")
     */
    @PostMapping("/filter/order")
    public String filterOrderList(@RequestBody UserOrderFilter userOrderFilter,
                                  Model model,
                                  HttpSession session){
        //세션에서 받아오는 로직
        Account loginUser = (Account) session.getAttribute("user");
        String user_id = loginUser.getUser_id();

        userOrderFilter.setUser_id(user_id); //필터에 id값 추가
        List<UserOrderDTO> userOrderList = userOrderService.getUserOrderList(userOrderFilter);
        model.addAttribute("userOrderList",userOrderList);

        return "users/fragment/userOrderList";
    }

    /**
     * 세션에 있는 유저가 가진 주문 목록중 선택한 것을 취소한다.
     *
     * @param userOrderRequest     요청 파라미터와 바인딩되어 뷰로 전달되는 DTO (order_no, product_no에 해당하는 주문의 order_status 번경 & quantity 복원)
     * @param session              현재 HTTP 세션(로그인된 사용자 정보)
     * @param redirectAttributes   리다이렉트 시에도 메세지를 담을 수 있도록 하는 객체
     * @return                     PRG : /user/orderList 로 리다이렉트
     */
    @PostMapping("/order/cancel")
    public String cancelOrder(@Valid @ModelAttribute UserOrderRequest userOrderRequest,
                              BindingResult br,
                              HttpSession session,
                              RedirectAttributes redirectAttributes){

        // 유효성 검증 결과가 있다면, error 메시지를 모델에 담아서 다시 뷰로 리다이렉트
        if (br.hasErrors()) {
            // redirectAttributes에 에러 메시지를 담는다.
            redirectAttributes.addFlashAttribute("errorMsg", "[ERROR!] 주문 취소에 실패했습니다.");
            // 다시 list로 리다이렉트, 취소 주문 요청 실패
            return "redirect:/user/orderList";
        }

        Account loginUser = (Account) session.getAttribute("user");
        String user_id = loginUser.getUser_id();

        userOrderRequest.setUser_id(user_id);

        //주문 취소의 경우는, 두 테이블을 UPDATE 하므로 @Transactional 처리, 그러므로 예외처리해준다.
        try {
            userOrderService.updateUserOrderCancel(userOrderRequest);
            redirectAttributes.addFlashAttribute("msg", "주문이 정상적으로 취소되었습니다.");
        } catch (OrderCancelException e) {
            log.error(e.getMessage()); // 로그에 에러 메시지 출력
            // 트랜잭션은 exception 던졌을 때 롤백됨
            redirectAttributes.addFlashAttribute("errorMsg", "[ERROR!] 주문 취소에 실패했습니다.");
        }
        return "redirect:/user/orderList";
    }
}
