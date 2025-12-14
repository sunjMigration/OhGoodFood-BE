package kr.co.ohgoodfood.store.controller;


import java.beans.PropertyEditorSupport;
import java.sql.Time;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import kr.co.ohgoodfood._legacy.dto.Orders;
import kr.co.ohgoodfood._legacy.dto.Review;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.co.ohgoodfood._legacy.dto.Alarm;

import kr.co.ohgoodfood._legacy.dto.Image;
import kr.co.ohgoodfood._legacy.dto.Product;
import kr.co.ohgoodfood._legacy.dto.Store;
import kr.co.ohgoodfood._legacy.dto.StoreSales;
import kr.co.ohgoodfood.notification.service.CommonService;
import kr.co.ohgoodfood.user.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/store")
@Slf4j
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;
	private final CommonService commonService;

	// 회원가입 페이지 이동
	@GetMapping("/signup")
	public String showSignup() {
		return "store/signup";
	}

	// 내 가게 리뷰 페이지 보기
	@GetMapping("/review")
	public String getReviews(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");
		String storeId = login.getStore_id();
		List<Review> lists = storeService.getReviews(storeId);
		model.addAttribute("reviews", lists);
		return "store/review";
	}

	// main에서 order 탭을 눌렀을때 기본 미확정 주문 조회
	@GetMapping("/reservation")
	public String getReservationOrders(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");
		return "/store/order";
	}

	// 이 밑에가 ajax 동적 처리 컨트롤러
	@PostMapping("/order/{status}")
	public String loadOrderByStatus(@RequestParam("month") String month,
									@RequestParam("year") String year,
									@PathVariable("status") String status,
									HttpSession session,
									Model model) {
		Store store = (Store) session.getAttribute("store");
		// 이 부분은 store 유효성 없으면 NullPointerException 에러가 나서 추가
		if (store == null) {
			return "redirect:/store/login";
		}
		String selectedDate = year + "-" + month;
		List<Orders> orders = storeService.getOrders(store.getStore_id(), status, selectedDate);
		model.addAttribute("order", orders);
		switch (status) { // fragment 에서 ajax 로 div 붙이기
			case "reservation":
				return "/store/fragments/reservation";
			case "confirmed":
				return "/store/fragments/confirmed";
			case "cancel":
				return "/store/fragments/cancel";
			default:
				return "/store/fragments/reservation";
		}
	}

	// 미확정 탭에서 확정 버튼 클릭시 -> 확정으로 바꿈
	@PostMapping("/reservation/{id}/confirm")
	@ResponseBody
	public String confirmOrders(@PathVariable("id") int id ,HttpSession sess, Model model) {
		int r = storeService.confirmOrders(id, "confirmed");
		if(r > 0) {
			int a = storeService.createUserAlarm(id, "confirmed");
			int b = storeService.createOrderCode(id, "confirmed");
			if(a > 0 && b > 0) {
				return "success";
			}
			return "failed";

		}else {
			return "failed";
		}
	}

	// 미확정 탭에서 취소 버튼 클릭시 -> 취소 상태로 바꿈
	@PostMapping("/reservation/{id}/cancel")
	@ResponseBody
	public String cancleOrders(@PathVariable("id") int id, HttpSession sess, Model model) {
		int r = storeService.cancleOrders(id, "cancel");
		if(r > 0) {
			int a = storeService.createUserAlarm(id, "cancel");
			if(a > 0) {
				return "success";
			}
			return "failed";

		}else {
			return "failed";
		}
	}

	// 토글에서 확정주문 클릭시 -> 확정 주문 내역 조회
	// @GetMapping("/confirmed")
	// public String getConfirmedOrders(HttpSession sess, Model model) {
	// 	Store login = (Store) sess.getAttribute("store");
	// 	if (login == null) {
	// 		model.addAttribute("msg", "로그인이 필요합니다.");
	// 		model.addAttribute("url", "/login");
	// 		return "store/alert";
	// 	}

	// 	// List<Orders> lists = storeService.getConfirmedOrPickupOrders(login.getStore_id());

	// 	for(Orders order : lists) {
	// 		if("pickup".equals(order.getOrder_status())) {
	// 			order.setPickup_status("complete");
	// 		}else {
	// 			order.setPickup_status("today");
	// 		}
	// 	}
	// 	model.addAttribute("order", lists);
	// 	return "/store/confirmedorder";
	// }

	// 확정 주문 내역에서 체크 표시 클릭시 픽업 상태로 변경
	@PostMapping("/confirmed/{id}/pickup")
	@ResponseBody
	public String pickupOrders(@PathVariable("id") int id, HttpSession sess, Model model) {
		int r = storeService.pickupOrders(id, "pickup");
		if(r > 0) {
			int a = storeService.createUserAlarm(id, "pickup");
			if(a > 0) {
				return "success";
			}
			return "failed";
		}else {
			return "failed";
		}
	}

	// 확정 주문내역에서 픽업완료로 상태 변경 후 다시 오늘픽업으로 변경
	@PostMapping("/confirmed/{id}/confirmed")
	@ResponseBody
	public String confirmPickupOrders(@PathVariable("id") int id, HttpSession sess, Model model) {
		int r = storeService.confirmPickupOrders(id, "confirmed");
		if(r > 0) {
			int a = storeService.createUserAlarm(id, "confirmed");
			if(a > 0) {
				return "success";
			}
			return "failed";
		}else {
			return "failed";
		}
	}

	// 회원가입 처리
	@PostMapping("/signup")
	public String signup(Store vo,
						 @RequestParam("storeImage") MultipartFile[] storeImageFiles,
						 @RequestParam("storeAddressDetail") String storeAddressDetail,
						 @RequestParam("store_menu2") String store_menu2,
						 @RequestParam("store_menu3") String store_menu3,
						 HttpServletRequest request,
						 Model model) {
		try {
			storeService.registerStore(vo, storeImageFiles, storeAddressDetail, store_menu2, store_menu3, request);
			model.addAttribute("msg", "회원가입이 성공적으로 완료되었습니다.");
			model.addAttribute("url", "/login");
			return "store/alert";
		} catch (Exception e) {
			model.addAttribute("msg", "회원가입 중 오류가 발생했습니다.");
			model.addAttribute("url", "/store/signup");
			e.printStackTrace();
			return "store/alert";
		}
	}

	// Time 문자열 → Time 타입 변환 (HH:mm 또는 HH:mm:ss 지원)
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Time.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				try {
					// "HH:mm" → "HH:mm:ss"로 보정
					if (text != null && text.length() == 5) {
						text += ":00";
					}
					setValue(Time.valueOf(text));
				} catch (Exception e) {
					setValue(null);
				}
			}
		});
	}

	// 아이디 중복확인 (true면 중복, false면 사용가능)
	@GetMapping("/checkId")
	@ResponseBody
	public boolean checkId(@RequestParam("store_id") String store_id) {
		// true면 중복, false면 사용가능
		return storeService.isDuplicateId(store_id);
	}

	// 메인화면
	@GetMapping("/main")
	public String showMain(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");

		// 가게 이미지 목록
		List<Image> images = storeService.getImagesByStoreId(login.getStore_id());
		model.addAttribute("images", images);

		// 가게 상세 정보
		Store store = storeService.getStoreDetail(login.getStore_id());
		model.addAttribute("store", store);

		// 오늘 등록된 상품 정보
		Product product = storeService.getProductByStoreId(login.getStore_id());
		model.addAttribute("product", product);

		// 픽업 날짜가 오늘인지 여부
		boolean isToday = false;
		if (product != null && product.getPickup_start() != null) {
			LocalDate pickupDate = product.getPickup_start().toLocalDateTime().toLocalDate();
			LocalDate today = LocalDate.now();
			isToday = pickupDate.equals(today);
		}
		model.addAttribute("isToday", isToday);

		return "store/main";
	}

	//가게 상태 변경 (오픈/마감)
	@PostMapping("/updateStatus")
	@ResponseBody
	public String updateStatus(HttpSession session, @RequestParam("status") String status) {
		Store store = (Store) session.getAttribute("store");
		String store_id = store.getStore_id();

		// 가게 상태 업데이트
		storeService.updateStoreStatus(store_id, status);

		store.setStore_status(status);
		session.setAttribute("store", store);

		return "success";
	}
	//오픈하기 (상품 등록 및 가게 오픈)
	@PostMapping("/createProduct")
	@ResponseBody
	public String createProduct(
			@RequestParam String productExplain,
			@RequestParam String pickupDateType,
			@RequestParam String pickupStartTime,
			@RequestParam String pickupEndTime,
			@RequestParam int originPrice,
			@RequestParam int salePrice,
			@RequestParam int amount,
			HttpSession sess) {

		Store store = (Store) sess.getAttribute("store");

		// 오늘 날짜에 이미 마감된 내역 있는지 체크 (서비스 호출)
		boolean isClosedToday = storeService.isTodayReservationClosed(store.getStore_id());

		if (isClosedToday) {
			// 오늘 이미 마감되었으면 오픈 막기
			return "closedToday";
		}

		try {
			// 상품 등록 및 가게 오픈 처리
			storeService.createProduct(
					store,
					productExplain,
					pickupDateType,
					pickupStartTime,
					pickupEndTime,
					originPrice,
					salePrice,
					amount
			);
			storeService.updateStoreStatus(store.getStore_id(), "Y");
			store.setStore_status("Y");
			sess.setAttribute("store", store);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	//마감하기 (미확정 주문 개수 반환)
	@GetMapping("/checkOrderStatus")
	@ResponseBody
	public int checkUnconfirmedOrders(HttpSession session) {
		Store store = (Store) session.getAttribute("store");
		if (store == null) return 0;

		// 미확정 주문 개수 반환
		int count = storeService.checkOrderStatus(store.getStore_id());
		return count;
	}

	// 상품 정보 조회 (AJAX)
	@GetMapping("/product")
	@ResponseBody
	public Product getProduct(HttpSession sess) {
		Store login = (Store) sess.getAttribute("store");
		if (login == null) {
			return null;
		}
		// 오늘 등록된 상품 정보 반환
		return storeService.getProductByStoreId(login.getStore_id());
	}

	// 매출확인 -> 이번달 매출 조회
	@GetMapping("/viewsales")
	public String showViewSales(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");
		LocalDate now = LocalDate.now();
		LocalDate start = now.withDayOfMonth(1);
		LocalDate end = now.withDayOfMonth(now.lengthOfMonth()).plusDays(1);
		StoreSales vo = storeService.getSales(login.getStore_id(), start.toString(), end.toString());
		vo.setStart_date(start.toString());
		vo.setEnd_date(end.toString());
		String month = vo.getStart_date().substring(5,7);
		model.addAttribute("month", month);
		model.addAttribute("vo", vo);
		model.addAttribute("store", login);
		return "store/viewsales";
	}

	//달력 연, 월 바귈 때 해당 월 매출조회
	@PostMapping("/monthsales")
	@ResponseBody
	public StoreSales getMonthSales(@RequestParam("year") int year, @RequestParam("month") int month, HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");
		YearMonth ym = YearMonth.of(year, month);
		String start = ym.atDay(1).toString();
		String end = ym.atEndOfMonth().toString();
		StoreSales saleVO = storeService.getSales(login.getStore_id(), start, end);
		saleVO.setStart_date(start);
		saleVO.setEnd_date(end);
		String salesMonth = saleVO.getStart_date().substring(5,7); // 월 추출
		model.addAttribute("saleVO", saleVO);
		model.addAttribute("salesMonth", salesMonth);
		return saleVO;
	}

	//당일 매출 조회
	@PostMapping("/viewsales/{date}")
	@ResponseBody
	public StoreSales getDailySales(@PathVariable("date") String date, HttpSession session) {
		Store login = (Store) session.getAttribute("store");
		StoreSales vo = storeService.getSales(login.getStore_id(), date, date);
		return vo;
	}

	// 알람
	@GetMapping("/alarm")
	public String showAlarm(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");

		List<Alarm> alarms = commonService.getAlarm(login.getStore_id());
		model.addAttribute("alarms", alarms);

		// 로그인 되어 있으면 alarm.jsp로
		return "store/alarm";
	}

	// 알람 읽음 처리
	@PostMapping("/alarmread")
	@ResponseBody
	public boolean readAlarm(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");
		commonService.updateAlarm(login.getStore_id());
		return true;
	}

	// 알람 디스플레이 숨김 처리
	@PostMapping("/alarmhide")
	@ResponseBody
	public boolean hideAlarm(@RequestParam("alarm_no") int alarm_no) {
		commonService.hideAlarm(alarm_no);
		return true;
	}

	// 안 읽은 알람 확인
	@PostMapping("/alarmcheck")
	@ResponseBody
	public boolean checkUnreadAlarm(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");
		return commonService.checkUnreadAlarm(login.getStore_id()) > 0;
	}

	// 마이페이지 조회
	@GetMapping("/mypage")
	public String showMypage(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");

		// 가게 상세 정보
		Store store = storeService.getStoreDetail(login.getStore_id());
		model.addAttribute("store", store);

		// 가게 이미지 목록
		List<Image> images = storeService.getImagesByStoreId(login.getStore_id());
		model.addAttribute("images", images);

		// 오픈시간, 마감시간(시,분) 포맷팅
		Time openedTime = store.getOpened_at();
		Time closedTime = store.getClosed_at();

		String openedStr = openedTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
		String closedStr = closedTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

		model.addAttribute("openedTime", openedStr);
		model.addAttribute("closedTime", closedStr);

		// 카테고리 리스트 생성
		List<String> categories = new ArrayList<>();
		if ("Y".equals(store.getCategory_bakery()))
			categories.add("빵 & 디저트");
		if ("Y".equals(store.getCategory_salad()))
			categories.add("샐러드");
		if ("Y".equals(store.getCategory_fruit()))
			categories.add("과일");
		if ("Y".equals(store.getCategory_others()))
			categories.add("그 외");

		model.addAttribute("categories", categories);

		return "store/mypage";
	}

	// 마이페이지 수정 페이지 이동
	@GetMapping("/updatemypage")
	public String updateMyPage(HttpSession sess, Model model) {
		Store login = (Store) sess.getAttribute("store");

		// 가게 상세 정보
		Store store = storeService.getStoreDetail(login.getStore_id());
		model.addAttribute("store", store);

		// 가게 이미지 목록
		List<Image> images = storeService.getImagesByStoreId(login.getStore_id());
		model.addAttribute("images", images);

		// 오픈시간, 마감시간(시,분) 포맷팅
		Time openedTime = store.getOpened_at();
		Time closedTime = store.getClosed_at();

		String openedStr = openedTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
		String closedStr = closedTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

		model.addAttribute("openedTime", openedStr);
		model.addAttribute("closedTime", closedStr);

		return "store/updatemypage";
	}

	// 수정된 마이페이지 내용 반영
	@PostMapping("/updatemypage")
	public String updateMyPagePost(HttpSession sess,
								   @RequestParam String store_menu,
								   @RequestParam(required = false) String store_menu2,
								   @RequestParam(required = false) String store_menu3,
								   @ModelAttribute Store store,
								   Model model) {

		// 로그인한 점주 정보 가져오기
		Store login = (Store) sess.getAttribute("store");
		store.setStore_id(login.getStore_id());

		// 대표 메뉴 값 3개를 합쳐서 하나의 문자열로 구성 (|로 구분)
		String combinedMenu = Stream.of(store_menu, store_menu2, store_menu3)
				.filter(s -> s != null && !s.trim().isEmpty())
				.map(String::trim)
				.collect(Collectors.joining(" | "));
		store.setStore_menu(combinedMenu);

		// DB 업데이트
		storeService.updateStoreCategory(store);

		// 알림 메시지와 이동할 URL 설정
		model.addAttribute("msg", "정보가 수정되었습니다.");
		model.addAttribute("url", "/store/mypage");

		return "store/alert";
	}

}