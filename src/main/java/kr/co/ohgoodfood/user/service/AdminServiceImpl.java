package kr.co.ohgoodfood.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.ohgoodfood._legacy.dao.AdminMapper;
import kr.co.ohgoodfood._legacy.dto.Account;
import kr.co.ohgoodfood._legacy.dto.Admin;
import kr.co.ohgoodfood._legacy.dto.Alarm;
import kr.co.ohgoodfood._legacy.dto.Orders;
import kr.co.ohgoodfood._legacy.dto.Paid;
import kr.co.ohgoodfood._legacy.dto.Review;
import kr.co.ohgoodfood._legacy.dto.Store;
import kr.co.ohgoodfood._legacy.dto.StoreSales;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

	private final AdminMapper adminMapper;

	// 전년 전체 매출 조회
	@Override
	public int getLastYearSales() {
		return adminMapper.getLastYearSalesTotal();
	}

	// 금년 전체 매출 조회
	@Override
	public int getThisYearSales() {
		return adminMapper.getThisYearSalesTotal();
	}

	// 전월 전체 매출 조회
	@Override
	public int getPreviousMonthSales() {
		return adminMapper.getPreviousMonthSalesTotal();
	}

	// 이번 달 전체 매출 조회
	@Override
	public int getThisMonthSales() {
		return adminMapper.getThisMonthSalesTotal();
	}

	// 전년 전체 주문 건수 조회
	@Override
	public int getLastYearOrderCount() {
		return adminMapper.getLastYearOrderCountTotal();
	}

	// 금년 전체 주문 건수 조회
	@Override
	public int getThisYearOrderCount() {
		return adminMapper.getThisYearOrderCountTotal();
	}

	// 전월 전체 주문 건수 조회
	@Override
	public int getPreviousMonthOrderCount() {
		return adminMapper.getPreviousMonthOrderCountTotal();
	}

	// 이번 달 전체 주문 건수 조회
	@Override
	public int getThisMonthOrderCount() {
		return adminMapper.getThisMonthOrderCountTotal();
	}

	// 금일 전체 주문 건수 조회
	@Override
	public int getTodayOrderCount() {
		return adminMapper.getTodayOrderCountTotal();
	}

	// 미승인 점포 수 조회
	@Override
	public int getUnapprovedStoreCount() {
		return adminMapper.getUnapprovedStoreCountTotal();
	}

	// 단일 회원 목록 조회
	@Override
	public Account getUser(Account account) {
		return adminMapper.getUser(account);
	}

	// 단일 회원 정보 업데이트
	@Override
	public boolean updateUser(Account account) {
		if(adminMapper.updateUser(account) > 0) {
			return true;
		}
		return false;
	}

	// 회원 목록 조회
	@Override
	public Map<String, Object> usersList(Account account) {
		int count = adminMapper.countAccounts(account);
		int totalPage = count / 7;
		if (count % 7 != 0) {
			totalPage++;
		}
		List<Account> list = adminMapper.searchAccounts(account);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalPage", totalPage);
		map.put("count", count);

		int endPage = (int) Math.ceil(account.getPage() / 10.0) * 10;
		int startPage = endPage - 9;
		if (endPage > totalPage) {
			endPage = totalPage;
		}
		boolean isPrev = startPage > 1;
		boolean isNext = endPage < totalPage;
		map.put("isPrev", isPrev);
		map.put("isNext", isNext);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		return map;
	}

	// 가게 목록 조회
	@Override
	public Map<String, Object> storesList(Store store) {
		int count = adminMapper.countStores(store);
		int totalPage = count / 7;
		if (count % 7 != 0) {
			totalPage++;
		}
		List<Store> list = adminMapper.searchStores(store);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalPage", totalPage);
		map.put("count", count);

		int endPage = (int) Math.ceil(store.getPage() / 10.0) * 10;
		int startPage = endPage - 9;
		if (endPage > totalPage) {
			endPage = totalPage;
		}
		boolean isPrev = startPage > 1;
		boolean isNext = endPage < totalPage;
		map.put("isPrev", isPrev);
		map.put("isNext", isNext);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		return map;
	}

	// 미승인 가게 목록 가져오기
	@Override
	public Map<String, Object> unapprovedStoresList(Store store) {
		int count = adminMapper.getUnapprovedStoreCountTotal();
		int totalPage = count / 7;
		if (count % 7 != 0) {
			totalPage++;
		}
		List<Store> list = adminMapper.getUnapprovedStore(store);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalPage", totalPage);
		map.put("count", count);

		int endPage = (int) Math.ceil(store.getPage() / 10.0) * 10;
		int startPage = endPage - 9;
		if (endPage > totalPage) {
			endPage = totalPage;
		}
		boolean isPrev = startPage > 1;
		boolean isNext = endPage < totalPage;
		map.put("isPrev", isPrev);
		map.put("isNext", isNext);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		return map;
	}

	// 가게 승인
	@Override
	public boolean approveStore(Store store) {
		if(adminMapper.approveStore(store) > 0) {
			return true;
		}
		return false;
	}

	// 가게 전년 매출 조회
	@Override
	public Integer getLastYearSales(StoreSales storeSales) {
		return adminMapper.getLastYearSalesStore(storeSales);
	}

	// 가게 금년 매출 조회
	@Override
	public Integer getThisYearSales(StoreSales storeSales) {
		return adminMapper.getThisYearSalesStore(storeSales);
	}

	// 가게 전월 매출 조회
	@Override
	public Integer getPreviousMonthSales(StoreSales storeSales) {
		return adminMapper.getPreviousMonthSalesStore(storeSales);
	}

	// 가게 금월 매출 조회
	@Override
	public Integer getThisMonthSales(StoreSales storeSales) {
		return adminMapper.getThisMonthSalesStore(storeSales);
	}

	// 가게 기간 매출 조회
	@Override
	public Integer getPeriodSales(StoreSales storeSales) {
		return adminMapper.getPeriodSalesStore(storeSales);
	}

	// 가게 금일 주문 건수 조회
	@Override
	public Integer getTodayOrderCount(StoreSales storeSales) {
		return adminMapper.getTodayOrderCountStore(storeSales);
	}

	// 가게 금월 주문 건수 조회
	@Override
	public Integer getThisMonthOrderCount(StoreSales storeSales) {
		return adminMapper.getThisMonthOrderCountStore(storeSales);
	}

	// 가게 금년 주문 건수 조회
	@Override
	public Integer getThisYearOrderCount(StoreSales storeSales) {
		return adminMapper.getThisYearOrderCountStore(storeSales);
	}

	// 가게 전년 주문 건수 조회
	@Override
	public Integer getLastYearOrderCount(StoreSales storeSales) {
		return adminMapper.getLastYearOrderCountStore(storeSales);
	}

	// 가게 기간 지정 주문 건수 조회
	@Override
	public Integer getPeriodOrderCount(StoreSales storeSales) {
		return adminMapper.getPeriodOrderCountStore(storeSales);
	}

	// 주문 목록 가져오기
	@Override
	public Map<String, Object> ordersList(Orders orders) {
		int count = adminMapper.countOrders(orders);
		int totalPage = count / 7;
		if (count % 7 != 0) {
			totalPage++;
		}
		List<Orders> list = adminMapper.searchOrdersPersonal(orders);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalPage", totalPage);
		map.put("count", count);

		int endPage = (int) Math.ceil(orders.getPage() / 10.0) * 10;
		int startPage = endPage - 9;
		if (endPage > totalPage) {
			endPage = totalPage;
		}
		boolean isPrev = startPage > 1;
		boolean isNext = endPage < totalPage;
		map.put("isPrev", isPrev);
		map.put("isNext", isNext);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		return map;
	}

	// 주문 상태 변경
	@Override
	public boolean updateOrderStatus(Orders orders) {
		if(adminMapper.updateOrderStatusPersonal(orders) > 0) {
			return true;
		}
		return false;
	}

	// 결제 목록 가져오기
	@Override
	public Map<String, Object> paidList(Paid paid) {
		int count = adminMapper.countPaid(paid);
		int totalPage = count / 7;
		if (count % 7 != 0) {
			totalPage++;
		}
		List<Paid> list = adminMapper.searchPaidPersonal(paid);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalPage", totalPage);
		map.put("count", count);

		int endPage = (int) Math.ceil(paid.getPage() / 10.0) * 10;
		int startPage = endPage - 9;
		if (endPage > totalPage) {
			endPage = totalPage;
		}
		boolean isPrev = startPage > 1;
		boolean isNext = endPage < totalPage;
		map.put("isPrev", isPrev);
		map.put("isNext", isNext);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		return map;
	}

	// 결제 상태 변경
	@Override
	public boolean updatePaidStatus(Paid paid) {
		if(adminMapper.updatePaidStatusPersonal(paid) > 0) {
			return true;
		}
		return false;
	}

	// 결제 실패 이유 변경
	@Override
	public boolean updatePaidFailReason(Paid paid) {
		if(adminMapper.updatePaidFailReasonPersonal(paid) > 0) {
			return true;
		}
		return false;
	}

	// 알람 목록 가져오기
	@Override
	public Map<String, Object> alarmList(Alarm alarm) {
		int count = adminMapper.countAlarm(alarm);
		int totalPage = count / 7;
		if (count % 7 != 0) {
			totalPage++;
		}
		List<Alarm> list = adminMapper.searchAlarm(alarm);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalPage", totalPage);
		map.put("count", count);

		int endPage = (int) Math.ceil(alarm.getPage() / 10.0) * 10;
		int startPage = endPage - 9;
		if (endPage > totalPage) {
			endPage = totalPage;
		}
		boolean isPrev = startPage > 1;
		boolean isNext = endPage < totalPage;
		map.put("isPrev", isPrev);
		map.put("isNext", isNext);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		return map;
	}

	// 알람 상태 변경
	@Override
	public boolean updateAlarm(Alarm alarm) {
		if(adminMapper.readAlarm(alarm) > 0 && adminMapper.displayAlarm(alarm) > 0) {
			return true;
		}
		return false;
	}

	// 알람 수신자 체크
	@Override
	public boolean alarmCheckId(String receiveId) {
		return adminMapper.checkReceiverAccount(receiveId) > 0 || adminMapper.checkReceiverStore(receiveId) > 0;
	}

	// 알람 보내기
	@Override
	public boolean sendAlarm(Alarm alarm) {
		if(adminMapper.sendAlarm(alarm) > 0) {
			return true;
		}
		return false;
	}

	// 리뷰 목록 가져오기
	@Override
	public Map<String, Object> reviewList(Review review) {
		int count = adminMapper.countReview(review);
		int totalPage = count / 7;
		if (count % 7 != 0) {
			totalPage++;
		}
		List<Review> list = adminMapper.searchReview(review);
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalPage", totalPage);
		map.put("count", count);

		int endPage = (int) Math.ceil(review.getPage() / 10.0) * 10;
		int startPage = endPage - 9;
		if (endPage > totalPage) {
			endPage = totalPage;
		}
		boolean isPrev = startPage > 1;
		boolean isNext = endPage < totalPage;
		map.put("isPrev", isPrev);
		map.put("isNext", isNext);
		map.put("startPage", startPage);
		map.put("endPage", endPage);
		return map;
	}

	// 리뷰 블러드 처리
	@Override
	public boolean updateReview(Review review) {
		if(adminMapper.blockReview(review) > 0) {
			return true;
		}
		return false;
	}

	// Admin 로그인 체크
	@Override
	public int checkAdminLogin(Admin admin) {
		return adminMapper.checkAdminLogin(admin);
	}

	// 금월 신규 회원 수 조회
	@Override
	public int getThisMonthNewUserCount() {
		return adminMapper.getThisMonthNewUserCount();
	}

	// 금월 신규 매장 수 조회
	@Override
	public int getThisMonthNewStoreCount() {
		return adminMapper.getThisMonthNewStoreCount();
	}
}
