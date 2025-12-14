package kr.co.ohgoodfood.user.service;

import java.util.List;
import kr.co.ohgoodfood._legacy.dto.Orders;
import kr.co.ohgoodfood._legacy.dto.Review;
import kr.co.ohgoodfood._legacy.dto.Image;
import kr.co.ohgoodfood._legacy.dto.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import kr.co.ohgoodfood._legacy.dto.Store;
import kr.co.ohgoodfood._legacy.dto.StoreSales;

public interface StoreService {

	// 가게 회원 정보 등록
	public int insert(Store vo);

	// 내 가게 리뷰보기
	public List<Review> getReviews(String storeId);

	// 주문내역 조회(미확정, 확정, 취소)
	public List<Orders> getOrders(String storeId, String type, String selectedDate);

	// 주문을 확정
	public int confirmOrders(int id, String type);

	// 주문을 취소
	public int cancleOrders(int id, String type);

	// 유저에게 알람생성
	public int createUserAlarm(int no, String type);

	// 사장님에게 알람생성
	public int createStoreAlarm(int no, String type);

	// 주문을 픽업상태로 변경
	public int pickupOrders(int id, String type);

	// 픽업상태 주문을 다시 확정으로 변경
	public int confirmPickupOrders(int id, String type);

	// 주문상태가 확정 혹은 픽업을 조회
	// public List<Orders> getConfirmedOrPickupOrders(String id);

	// 내 가게 매출 조회
	public StoreSales getSales(String store_id, String start, String end);

	// 주문 코드 생성
	public int createOrderCode(int id, String string);

	// 아이디 중복 여부 확인
	public boolean isDuplicateId(String store_id);

	// 가게 회원가입 처리 (주소, 이미지 포함)
	public void registerStore(Store vo, MultipartFile[] storeImageFiles, String storeAddressDetail, String store_menu2, String store_menu3, HttpServletRequest request) throws Exception;

	// 단일 이미지 AWS S3 업로드 및 DB 저장
	public void saveImage(String storeId, MultipartFile file, HttpServletRequest request) throws Exception;

	// 가게 상세 정보 조회 (마이페이지)
	public Store getStoreDetail(String store_id);

	// 가게 카테고리 정보 수정
	public void updateStoreCategory(Store vo);

	// 가게 이미지 리스트 조회
	public List<Image> getImagesByStoreId(String store_id);

	// 가게별 최신 상품 조회(메인용)
	public Product getProductByStoreId(String store_id);

	// 가게 상태값 업데이트 (오픈/마감)
	public void updateStoreStatus(String store_id, String status);

	// 상품 등록 및 픽업 정보 포함 오픈 처리
	public void createProduct(Store store, String productExplain, String pickupDateType, String pickupStartTime, String pickupEndTime, int originPrice, int salePrice, int amount);

	// 가게 미확정 주문 수 확인
	public int checkOrderStatus(String store_id);

	// 오늘 픽업 마감 여부
	public boolean isTodayReservationClosed(String store_id);
}