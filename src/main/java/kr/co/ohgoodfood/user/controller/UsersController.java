package kr.co.ohgoodfood.user.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import kr.co.ohgoodfood._legacy.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.ohgoodfood.notification.service.CommonService;
import kr.co.ohgoodfood.payment.service.PayService;
import kr.co.ohgoodfood.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UsersController
 *
 * 사용자 페이지 전용 기능을 처리하는 컨트롤러입니다.
 * - POST /user/signup	              : 사용자 회원가입 페이지
 * - GET  /user/mypage                : 유저 mypage 이동
 * - GET  /user/reviewList            : 하단 메뉴바 Review탭 이동시 전체 리뷰 목록 조회
 *
 */
@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    private final CommonService commonService;
    private final PayService payService;

    // 지도 사용을 위한 앱키
    @Value("${kakao.map.appKey}")
    private String kakaoMapAppKey;

    /**
     *  사용자 회원가입
     */
    /**  회원가입 폼 보여주기 */
    @GetMapping("/signup")
    public String showSignupForm() {
        return "users/userSignup"; 
    }

    /** 아이디 중복 확인 */
    @GetMapping("/checkId")
    @ResponseBody
    public boolean checkId(@RequestParam("user_id") String userId) {
        return usersService.isDuplicateId(userId);
    }
    /** 회원가입 처리 */
    @PostMapping("/signup")
    public String signup(
            @ModelAttribute Account account,
            Model model
    ) {
        // 서버 측 중복 재검증
        if (usersService.isDuplicateId(account.getUser_id())) {
            model.addAttribute("msg", "이미 사용 중인 아이디입니다.");
            model.addAttribute("url", "/user/signup");
            return "users/alert";
        }

        try {
        	usersService.registerUser(account);
            model.addAttribute("msg", "회원가입이 성공적으로 완료되었습니다.");
            model.addAttribute("url", "/login");
        } catch (Exception e) {
            model.addAttribute("msg", "회원가입 중 오류가 발생했습니다.");
            model.addAttribute("url", "/user/signup");
        }
        return "users/alert";
    }

    /**
     *  사용자 마이페이지 조회
     *  
     */
    
    @GetMapping("/mypage")
    public String userMypage(Model model, HttpSession session) {
    	
        // String userId = (String) session.getAttribute("user_id");
        // if (userId == null) userId = "u10"; // 임시 하드코딩값
        // 세션에서 user_id 가져오기
        Account loginUser = (Account) session.getAttribute("user");
        String user_id = loginUser.getUser_id();

        UserMypage page = usersService.getMypage(user_id);
        model.addAttribute("userMypage", page);
        return "users/userMypage";
        
    }
    /**
     * 제품 상세보기
     */
    @GetMapping("/productDetail")
    public String productDetail(
            @RequestParam("product_no") int product_no,
            HttpSession session,
            Model model
    ) {
    	
        ProductDetail detail = usersService.getProductDetail(product_no);
        
        List<String> images = usersService.getProductImages(product_no);
        model.addAttribute("images", images);
        
        // 세션에서 로그인한 사용자 정보 가져오기
        Account loginUser = (Account) session.getAttribute("user");

        boolean isBookmarked = false;
        if (loginUser != null) {
            String user_id = loginUser.getUser_id();
            String store_id = detail.getStore_id();
            isBookmarked = usersService.isBookmarked(user_id, store_id);  // 북마크 여부 조회
        }
        detail.setBookmarked(isBookmarked);  // 실제 여부 세팅
        model.addAttribute("productDetail", detail);
        
        List<Review> reviews = usersService.getReviewsByProductNo(product_no);
        model.addAttribute("reviews", reviews);
        return "users/userProductDetail";
        
    }

    /**
     * 하단 메뉴바 Review 페이지
     */
    @GetMapping("/reviewList")
    public String listReviews(Model model) {
        List<Review> reviews = usersService.getAllReviews(1, Integer.MAX_VALUE);
        model.addAttribute("reviews", reviews);
        return "users/userReviewList";  
    }
    
    /**
     * 확정 주문내역 -> 리뷰쓰기
     */
    // GET : 주문번호로 화면용 DTO 꺼내서 JSP에 바인딩
    @GetMapping("/reviewWrite")
    public String showReviewForm(@RequestParam("order_no") int orderNo,
                                 Model model) {
        ReviewForm form = usersService.getReviewForm(orderNo);
        model.addAttribute("reviewForm", form);
        return "users/userReviewWrite";
    }


    /**
     * 결제 페이지
     */

    @PostMapping("/userPaid")
    public String userPaid(@RequestParam("productNo") int productNo, Model model, HttpSession session) {
        ProductDetail detail = usersService.getProductDetail(productNo);
        detail.setStore_img(usersService.getStoreImg(detail.getStore_id()));
        model.addAttribute("productDetail", detail);
        model.addAttribute("userPoint", usersService.getUserPoint(((Account)session.getAttribute("user")).getUser_id()));
        return "users/userPaid";
    }

    /**
     * 결제 실패 페이지
     */
    @GetMapping("/paidfail")
    public String paidfail(@RequestParam("orderId") String orderId, Model model, HttpSession session) {
        int orderNo = payService.getOrderNoByPaidCode(orderId);
        model.addAttribute("orderNo", orderNo);
        return "users/paidfail";
    }


    // POST : 폼 제출 → DTO에 user_id 세팅 → 서비스 호출 → 마이페이지로 리다이렉트
    @PostMapping("/review/submit")
    public String submitReview(@ModelAttribute ReviewForm reviewForm,
                               HttpSession session) {
        String userId = ((Account)session.getAttribute("user")).getUser_id();
        reviewForm.setUser_id(userId);
        usersService.writeReview(reviewForm, userId);
        return "redirect:/user/mypage";
    }

    /**
     * 알람 페이지
     */
    @GetMapping("/alarm")
    public String showAlarm(Model model, HttpSession session) {
        Account loginUser = (Account) session.getAttribute("user");
        String user_id = loginUser.getUser_id();
        List<Alarm> alarms = commonService.getAlarm(user_id);
        model.addAttribute("alarms", alarms);
        return "users/alarm";
    }

    // 알람 읽음 처리
	@PostMapping("/alarmread")
	@ResponseBody
	public boolean readAlarm(HttpSession sess, Model model) {
		Account login = (Account) sess.getAttribute("user");
		if(commonService.updateAlarm(login.getUser_id()) > 0){
			return true;
		}
		return false;
	}

	// 알람 디스플레이 숨김 처리
	@PostMapping("/alarmhide")
	@ResponseBody
	public boolean hideAlarm(@RequestParam("alarm_no") int alarm_no) {
		if(commonService.hideAlarm(alarm_no) > 0){
			return true;
		}
		return false;
	}

    // 안 읽은 알람 확인
	@PostMapping("/alarmcheck")
	@ResponseBody
	public boolean checkUnreadAlarm(HttpSession sess, Model model) {
		Account login = (Account) sess.getAttribute("user");
		return commonService.checkUnreadAlarm(login.getUser_id()) > 0;
	}
}
