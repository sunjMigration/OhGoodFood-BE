package kr.co.ohgoodfood.user.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.co.ohgoodfood._legacy.dto.Account;
import kr.co.ohgoodfood._legacy.dto.Admin;
import kr.co.ohgoodfood._legacy.dto.Alarm;
import kr.co.ohgoodfood._legacy.dto.Orders;
import kr.co.ohgoodfood._legacy.dto.Paid;
import kr.co.ohgoodfood._legacy.dto.Review;
import kr.co.ohgoodfood._legacy.dto.Store;
import kr.co.ohgoodfood._legacy.dto.StoreSales;
import kr.co.ohgoodfood.user.service.AdminService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@GetMapping("/admin")
	public String admin() {
		return "redirect:/admin/login";
	}

	@GetMapping("/admin/login")
	public String adminLogin(Model model) {
		return "admin/login";
	}

	@PostMapping("/admin/login")
	public String adminLogin(Model model, RedirectAttributes rttr, HttpServletRequest request,
			@ModelAttribute Admin admin) {
		if (adminService.checkAdminLogin(admin) == 1) {
			request.getSession().setAttribute("admin", admin);
			return "redirect:/admin/main";
		} else {
			rttr.addFlashAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
			return "redirect:/admin/login";
		}
	}

	// main 페이지 이동
	@GetMapping("/admin/main")
	public String adminMain(Model model) {
		model.addAttribute("lastYearSales", adminService.getLastYearSales());
		model.addAttribute("thisYearSales", adminService.getThisYearSales());
		model.addAttribute("previousMonthSales", adminService.getPreviousMonthSales());
		model.addAttribute("thisMonthSales", adminService.getThisMonthSales());
		model.addAttribute("lastYearOrderCount", adminService.getLastYearOrderCount());
		model.addAttribute("thisYearOrderCount", adminService.getThisYearOrderCount());
		model.addAttribute("previousMonthOrderCount", adminService.getPreviousMonthOrderCount());
		model.addAttribute("thisMonthOrderCount", adminService.getThisMonthOrderCount());
		model.addAttribute("todayOrderCount", adminService.getTodayOrderCount());
		model.addAttribute("unapprovedStoreCount", adminService.getUnapprovedStoreCount());
		model.addAttribute("thisMonthNewUserCount", adminService.getThisMonthNewUserCount());
		model.addAttribute("thisMonthNewStoreCount", adminService.getThisMonthNewStoreCount());
		return "admin/main";
	}

	// 회원 검색 이동
	@GetMapping("/admin/searchusers")
	public String searchUser(Model model, @ModelAttribute Account account) {
		model.addAttribute("map", adminService.usersList(account));
		return "admin/searchusers";
	}

	// 가게 검색 이동
	@GetMapping("/admin/searchstores")
	public String searchStore(Model model, @ModelAttribute Store store) {
		model.addAttribute("map", adminService.storesList(store));
		return "admin/searchstores";
	}

	// 회원 관리 이동
	@GetMapping("/admin/managedusers")
	public String manageUsers(Model model, @ModelAttribute Account account) {
		model.addAttribute("account", adminService.getUser(account));
		return "admin/managedusers";
	}

	// 회원 정보 수정
	@PostMapping("/admin/updateuser")
	public String updateUser(RedirectAttributes rttr, @ModelAttribute Account account) {
		if(adminService.updateUser(account)) {
			rttr.addFlashAttribute("success", "회원 정보가 성공적으로 수정되었습니다.");
		} else {
			rttr.addFlashAttribute("error", "회원 정보 수정에 실패했습니다.");
		}
		return "redirect:/admin/managedusers";
	}

	// 가게 관리 이동
	@GetMapping("/admin/managedstore")
	public String manageStores(Model model, @ModelAttribute Store store) {
		if (store.getS_value() == null || store.getS_value().equals("")) {
			model.addAttribute("map", adminService.unapprovedStoresList(store));
		} else {
			model.addAttribute("map", adminService.storesList(store));
		}
		return "admin/managedstore";
	}

	// 가게 승인
	@PostMapping("/admin/updatestore")
	public String updateStore(RedirectAttributes rttr, @RequestParam("s_store_id") String[] storeIds,
			@RequestParam(value = "s_confirmed", required = false) String[] confirmed) {
				
		try {
			// 체크박스가 체크된 가게 ID들만 Y로 업데이트
			for (int i = 0; i < storeIds.length; i++) {
				Store store = new Store();
				store.setStore_id(storeIds[i]);
				// checkbox가 체크되지 않으면 파라미터가 안 넘어오므로
				// 체크된 것만 Y로, 나머지는 N으로 처리
				store.setConfirmed("N");

				// 체크된 가게들 처리
				if (confirmed != null) {
					for (String checkedId : confirmed) {
						if (storeIds[i].equals(checkedId)) {
							store.setConfirmed("Y");
							break;
						}
					}
				}
				if(adminService.approveStore(store)) {
					rttr.addFlashAttribute("success", "가게 승인이 성공적으로 처리되었습니다.");
				} else {
					rttr.addFlashAttribute("error", "가게 승인 처리에 실패했습니다.");
				}
			}

		} catch (Exception e) {

		}

		// 같은 페이지로 리다이렉트 (새로고침 시 재전송 방지)
		return "redirect:/admin/managedstore";
	}

	// 주문 관리 이동
	@GetMapping("/admin/managedorders")
	public String manageOrders(Model model, @ModelAttribute Orders orders) {
		model.addAttribute("map", adminService.ordersList(orders));
		return "admin/managedorders";
	}

	// 주문 상태 변경
	@PostMapping("/admin/updateorders")
	public String updateOrders(RedirectAttributes rttr, @RequestParam("order_status") String[] orderStatus,
			@RequestParam("order_no") int[] orderNos) {
		try {
			for (int i = 0; i < orderNos.length; i++) {
				Orders orders = new Orders();
				orders.setOrder_no(orderNos[i]);
				orders.setOrder_status(orderStatus[i]);
				if(adminService.updateOrderStatus(orders)) {
					rttr.addFlashAttribute("success", "주문 상태가 성공적으로 변경되었습니다.");
				} else {
					rttr.addFlashAttribute("error", "주문 상태 변경에 실패했습니다.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/managedorders";
	}

	// 알람 관리 이동
	@GetMapping("/admin/managedalarm")
	public String manageAlarms(Model model, @ModelAttribute Alarm alarm) {
		model.addAttribute("map", adminService.alarmList(alarm));
		return "admin/managedalarm";
	}

	// 알람 상태 변경
	@PostMapping("/admin/updatealarm")
	public String updateAlarm(RedirectAttributes rttr, @RequestParam("alarm_read") String[] alarmRead, @RequestParam("alarm_no") int[] alarmNos,
			@RequestParam("alarm_displayed") String[] alarmDisplay) {
		try {
			for (int i = 0; i < alarmNos.length; i++) {
				Alarm alarm = new Alarm();
				alarm.setAlarm_no(alarmNos[i]);
				alarm.setAlarm_read(alarmRead[i]);
				alarm.setAlarm_displayed(alarmDisplay[i]);
				if(adminService.updateAlarm(alarm)) {
					rttr.addFlashAttribute("success", "알람 상태가 성공적으로 변경되었습니다.");
				} else {
					rttr.addFlashAttribute("error", "알람 상태 변경에 실패했습니다.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/managedalarm";
	}

	// 리뷰 관리 이동
	@GetMapping("/admin/managedreviews")
	public String manageCoupons(Model model, @ModelAttribute Review review) {
		model.addAttribute("map", adminService.reviewList(review));
		return "admin/managedreviews";
	}

	// 리뷰 블러드 처리
	@PostMapping("/admin/updatereviews")
	public String updateReviews(RedirectAttributes rttr, @RequestParam("review_no") int[] reviewNos,
			@RequestParam("is_blocked") String[] isBlocked) {
		try {
			for (int i = 0; i < reviewNos.length; i++) {
				Review review = new Review();
				review.setReview_no(reviewNos[i]);
				review.setIs_blocked(isBlocked[i]);
				if(adminService.updateReview(review)) {
					rttr.addFlashAttribute("success", "리뷰 블러드 처리가 성공적으로 처리되었습니다.");
				} else {
					rttr.addFlashAttribute("error", "리뷰 블러드 처리에 실패했습니다.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/managedreviews";
	}

	// 결제 관리 이동
	@GetMapping("/admin/managedpaids")
	public String managePaid(Model model, @ModelAttribute Paid paid) {
		model.addAttribute("map", adminService.paidList(paid));
		return "admin/managedpaids";
	}

	// 결제 상태 변경 , 실패 이유 변경
	@PostMapping("/admin/updatepaids")
	public String updatePaid(RedirectAttributes rttr, @RequestParam("paid_status") String[] paidStatus, @RequestParam("paid_no") int[] paidNos,
			@RequestParam("fail_reason") String[] failReason) {
		try {
			for (int i = 0; i < paidNos.length; i++) {
				Paid paid = new Paid();
				paid.setPaid_no(paidNos[i]);
				paid.setPaid_status(paidStatus[i]);
				paid.setFail_reason(failReason[i]);
				if(adminService.updatePaidStatus(paid) && adminService.updatePaidFailReason(paid)) {
					rttr.addFlashAttribute("success", "결제 상태가 성공적으로 변경되었습니다.");
				} else {
					rttr.addFlashAttribute("error", "결제 상태 변경에 실패했습니다.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/managedpaids";
	}

	// 알람 보내기 이동
	@GetMapping("/admin/sendalarm")
	public String sendAlarm(Model model) {
		return "admin/sendalarm";
	}

	// 알람 수신자 체크
	@PostMapping("/admin/alarmcheckid")
	@ResponseBody
	public boolean alarmCheckId(@RequestParam("receive_id") String receiveId) {
		return adminService.alarmCheckId(receiveId);
	}

	// 알람 보내기
	@PostMapping("/admin/sendalarmtouser")
	public String sendAlarmToUser(RedirectAttributes rttr, @RequestParam("receive_id") String receiveId,
			@RequestParam("alarm_title") String alarmTitle, @RequestParam("alarm_contents") String alarmContents) {
		try {
			Alarm alarm = new Alarm();
			alarm.setReceive_id(receiveId);
			alarm.setAlarm_title(alarmTitle);
			alarm.setAlarm_contents(alarmContents);
			if (alarm.getAlarm_title() == null || alarm.getAlarm_title().equals("")) {
				rttr.addFlashAttribute("error", "제목을 입력해주세요.");
				return "redirect:/admin/sendalarm";
			}
			if (alarm.getAlarm_contents() == null || alarm.getAlarm_contents().equals("")) {
				rttr.addFlashAttribute("error", "내용을 입력해주세요.");
				return "redirect:/admin/sendalarm";
			}
			if (adminService.sendAlarm(alarm)) {
				rttr.addFlashAttribute("success", "알람이 성공적으로 보내졌습니다.");
			} else {
				rttr.addFlashAttribute("error", "알람 보내기에 실패했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/sendalarm";
	}

	// 가게 매출 조회 이동
	@GetMapping("/admin/storesales")
	public String storeSales(Model model, @ModelAttribute StoreSales storeSales) {
		if (storeSales.getStore_id() != null && !(storeSales.getStore_id().equals(""))) {
			model.addAttribute("lastYearSales", adminService.getLastYearSales(storeSales));
			model.addAttribute("thisYearSales", adminService.getThisYearSales(storeSales));
			model.addAttribute("previousMonthSales", adminService.getPreviousMonthSales(storeSales));
			model.addAttribute("thisMonthSales", adminService.getThisMonthSales(storeSales));
			model.addAttribute("periodSales", adminService.getPeriodSales(storeSales));
			model.addAttribute("lastYearOrderCount", adminService.getLastYearOrderCount(storeSales));
			model.addAttribute("thisYearOrderCount", adminService.getThisYearOrderCount(storeSales));
			model.addAttribute("thisMonthOrderCount", adminService.getThisMonthOrderCount(storeSales));
			model.addAttribute("periodOrderCount", adminService.getPeriodOrderCount(storeSales));
			model.addAttribute("todayOrderCount", adminService.getTodayOrderCount(storeSales));
		}
		return "admin/storesales";
	}

}
