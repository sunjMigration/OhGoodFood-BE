//package kr.co.ohgoodfood._legacy.dao;
//
//import java.util.List;
//import org.apache.ibatis.annotations.Param;
//import kr.co.ohgoodfood._legacy.dto.Alarm;
//import kr.co.ohgoodfood._legacy.dto.Orders;
//import kr.co.ohgoodfood._legacy.dto.Review;
//import java.util.Map;
//import org.apache.ibatis.annotations.Mapper;
//import kr.co.ohgoodfood._legacy.dto.Image;
//import kr.co.ohgoodfood._legacy.dto.Product;
//import kr.co.ohgoodfood._legacy.dto.Store;
//import kr.co.ohgoodfood._legacy.dto.StoreSales;
//
//@Mapper
//public interface StoreMapper {
//	// 내 가게 리뷰보기
//	public List<Review> getReviews(String storeId);
//
//	// 회원가입(가게 등록)
//	public int insert(Store vo);
//
//	// 주문(미확정, 확정, 취소) 조회
//	public List<Orders> getOrders(@Param("storeId") String storeId, @Param("type") String type, @Param("selectedDate") String selectedDate);
//
//	// 내 가게 주문을 확정
//	public int confirmOrders(@Param("id") int id, @Param("type") String type);
//
//	// 내 가게 주문을 취소
//	public int cancleOrders(@Param("id") int id, @Param("type") String type);
//
//	// order_no로 주문 조회
//	public Orders getOrderById(int no);
//
//	// order_no로 스토어 이름 조회
//	public Store getStoreNameByOrderNo(@Param("no") int no);
//
//	// 알람 생성
//	public int insertAlarm(Alarm alarm);
//
//	// 주문을 픽업상태로 변경
//	public int pickupOrders(@Param("id") int id, @Param("type") String type);
//
//	// 내 가게 기간 매출 조회
//	public StoreSales getSales(@Param("store_id") String store_id, @Param("start") String start, @Param("end") String end);
//
//	// 주문 코드 생성
//	public int createOrderCode(@Param("id") int id, @Param("type") String type, @Param("randomCode") int randomCode);
//
//	// 아이디 중복 확인
//	public Store findById(String store_id);
//
//	// 가게 이미지 등록
//	public void insertImage(Image image);
//
//	// 가게 정보 수정
//	public void updateStore(Store vo);
//
//	// 가게별 이미지 목록 조회
//	public List<Image> findImagesByStoreId(String store_id);
//
//	// 가게별 상품 정보 조회(오늘)
//	public Product findProductByStoreId(String store_id);
//
//	// 가게 상태(오픈/마감) 변경
//	public void updateStoreStatus(Map<String, Object> param);
//
//	// 상품 등록
//	public int insertProduct(Product product);
//
//	// 가게별 최신 상품 조회
//	public Product findLatestProductByStoreId(String storeId);
//
//	// 미확정 주문 개수 조회
//	public int checkOrderStatus(String store_id);
//
//	// 오늘 마감 여부 확인
//	public int checkTodayReservationEnd(String storeId);
//
//	// User Point 환불
//	public void refundUserPoint(int id);
//
//	// 주문 취소 시 Product 의 amount 증가
//	public void increaseProductAmount(int id);
//}
