package kr.co.ohgoodfood.user.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kr.co.ohgoodfood.global.config.AwsS3Config;
import kr.co.ohgoodfood._legacy.dao.StoreMapper;
import kr.co.ohgoodfood._legacy.dao.UserMapper;
import kr.co.ohgoodfood._legacy.dto.Alarm;
import kr.co.ohgoodfood._legacy.dto.Image;
import kr.co.ohgoodfood._legacy.dto.Orders;
import kr.co.ohgoodfood._legacy.dto.Product;
import kr.co.ohgoodfood._legacy.dto.Review;
import kr.co.ohgoodfood._legacy.dto.Store;
import kr.co.ohgoodfood._legacy.dto.StoreSales;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

	private final AwsS3Config awsS3Config;
	private final StoreMapper mapper;
	private final UserMapper userMapper;

	// 회원가입
	@Override
	public int insert(Store vo) {
		return mapper.insert(vo);
	}

	// 아이디 중복확인
	@Override
	public boolean isDuplicateId(String store_id) {
		Store store = mapper.findById(store_id);
		int count = userMapper.countByUserId(store_id);
		return store != null || count > 0;
	}

	// 회원가입 처리 (주소/이미지 포함)
	@Override
	public void registerStore(Store vo, MultipartFile[] storeImageFiles, String storeAddressDetail, String store_menu2, String store_menu3,
							  HttpServletRequest request) throws Exception {

		// 비밀번호 암호화
		String rawPwd = vo.getStore_pwd();
		if (rawPwd != null && !rawPwd.isEmpty()) {
			vo.setStore_pwd(md5(rawPwd));
		}

		// 주소 합치기 (주소가 null일 가능성도 체크)
		if (vo.getStore_address() == null) {
			vo.setStore_address("");
		}
		if (storeAddressDetail != null && !storeAddressDetail.trim().isEmpty()) {
			vo.setStore_address(vo.getStore_address() + " " + storeAddressDetail);
		}

		// 카테고리 기본값 N (null 방지)
		vo.setCategory_bakery(vo.getCategory_bakery() == null ? "N" : vo.getCategory_bakery());
		vo.setCategory_salad(vo.getCategory_salad() == null ? "N" : vo.getCategory_salad());
		vo.setCategory_fruit(vo.getCategory_fruit() == null ? "N" : vo.getCategory_fruit());
		vo.setCategory_others(vo.getCategory_others() == null ? "N" : vo.getCategory_others());

		// confirmed, store_status 기본값 N
		vo.setConfirmed("N");
		vo.setStore_status("N");

		// 대표메뉴 합치기
		String menu1 = vo.getStore_menu() != null ? vo.getStore_menu().trim() : "";
		String menu2 = store_menu2 != null ? store_menu2.trim() : "";
		String menu3 = store_menu3 != null ? store_menu3.trim() : "";

		// 빈 문자열 제거 후 " | "로 join
		String combinedMenu = Stream.of(menu1, menu2, menu3)
				.map(String::trim)
				.filter(s -> s != null && !s.isEmpty())
				.collect(Collectors.joining(" | "))
				.trim();

		vo.setStore_menu(combinedMenu);

		// insert 실행
		mapper.insert(vo);

		// 이미지 저장
		if (storeImageFiles != null) {
			for (MultipartFile file : storeImageFiles) {
				if (!file.isEmpty()) {
					saveImage(vo.getStore_id(), file, request);
				}
			}
		}

	}

	// 이미지 AWS S3에 업로드하고 DB에 기록
	public void saveImage(String storeId, MultipartFile file, HttpServletRequest request) throws Exception {
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());

		// s3에 이미지 업로그
		amazonS3().putObject(new PutObjectRequest(awsS3Config.getBucket(), fileName, file.getInputStream(), metadata)); // 공개 읽기 설정

		// DB에 이미지 정보 저장
		Image image = new Image();
		image.setStore_id(storeId);
		image.setStore_img(fileName);
		mapper.insertImage(image);
	}

	// AWS S3 인스턴스 반환
	private AmazonS3 amazonS3() {
		return awsS3Config.amazonS3();
	}

	// MD5 암호화 메서드 
	private String md5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : messageDigest) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 가게별 이미지 리스트 조회
	@Override
	public List<Image> getImagesByStoreId(String store_id) {
		return mapper.findImagesByStoreId(store_id);
	}

	// 매장 상품 조회 (메인화면용) 
	@Override
	public Product getProductByStoreId(String store_id) {
		return mapper.findLatestProductByStoreId(store_id);
	}

	// 미확정 주문 개수 조회
	@Override
	public int checkOrderStatus(String storeId) {
		return mapper.checkOrderStatus(storeId);
	}

	// 가게 상태 업데이트 (오픈/마감)
	@Override
	public void updateStoreStatus(String store_id, String status) {
		Map<String, Object> param = new HashMap<>();
		param.put("store_id", store_id);
		param.put("status", status);

		mapper.updateStoreStatus(param);
	}

	// 상품 등록 및 오픈 처리
	public void createProduct(Store store, String productExplain, String pickupDateType, String pickupStartTime, String pickupEndTime,
							  int originPrice, int salePrice, int amount) {

		// 픽업 날짜 계산 (오늘/내일)
		LocalDate pickupDate = pickupDateType.equals("today")
				? LocalDate.now()
				: LocalDate.now().plusDays(1);

		// pickup_start(픽업 시작 시간), pickup_end(픽업 종료 시간)
		LocalDateTime pickupStart = LocalDateTime.of(pickupDate, LocalTime.parse(pickupStartTime));
		LocalDateTime pickupEnd = LocalDateTime.of(pickupDate, LocalTime.parse(pickupEndTime));

		// reservation_end(예약 마감 시간) 계산
		LocalDateTime reservationEnd;
		if (pickupDateType.equals("today")) {
			reservationEnd = pickupStart.minusHours(1);
		} else {
			reservationEnd = LocalDateTime.of(LocalDate.now(), store.getClosed_at().toLocalTime().minusHours(1));
		}

		// 상품 객체 생성 및 저장
		Product product = new Product();
		product.setStore_id(store.getStore_id());
		product.setPickup_start(Timestamp.valueOf(pickupStart));
		product.setPickup_end(Timestamp.valueOf(pickupEnd));
		product.setReservation_end(Timestamp.valueOf(reservationEnd));
		product.setOrigin_price(originPrice);
		product.setSale_price(salePrice);
		product.setAmount(amount);
		product.setProduct_explain(productExplain);

		mapper.insertProduct(product);
	}

	// 오늘 이미 마감된 내역이 있는지 확인
	@Override
	public boolean isTodayReservationClosed(String storeId) {
		int count = mapper.checkTodayReservationEnd(storeId);
		return count > 0;
	}

	// 마이페이지 가게 상세 정보 조회
	@Override
	public Store getStoreDetail(String store_id) {
		return mapper.findById(store_id);
	}

	// 마이페이지 카테고리 정보 수정
	@Override
	public void updateStoreCategory(Store store) {
		// 카테고리 체크박스 null 처리 (체크 안되면 null로 넘어옴)
		store.setCategory_bakery(store.getCategory_bakery() != null ? "Y" : "N");
		store.setCategory_salad(store.getCategory_salad() != null ? "Y" : "N");
		store.setCategory_fruit(store.getCategory_fruit() != null ? "Y" : "N");
		store.setCategory_others(store.getCategory_others() != null ? "Y" : "N");

		mapper.updateStore(store);

	}

	//가게 리뷰조회
	@Override
	public List<Review> getReviews(String storeId) {
		return mapper.getReviews(storeId);
	}

	//가게 주문내역 조회(미확정, 확정, 취소)
	@Override
	public List<Orders> getOrders(String storeId, String type, String selectedDate) {
		List<Orders> list = mapper.getOrders(storeId, type, selectedDate);
		for (Orders order : list) {
			// 확정 시작시간 구하는 로직
			if (order.getReservation_end() != null) {
				Timestamp end = order.getReservation_end();
				Timestamp start = new Timestamp(end.getTime() - 60 * 60 * 1000); // reservation_end - 1시간
				order.setReservation_start(start);
			}
		}
		return list;
	}

	//미확정 주문 -> 확정 주문으로 바꾸기
	@Override
	public int confirmOrders(int id, String type) {
		return mapper.confirmOrders(id, type);
	}

	//미확정 주문 -> 주문 취소
	@Override
	public int cancleOrders(int id, String type) {
		mapper.refundUserPoint(id);
		mapper.increaseProductAmount(id);
		return mapper.cancleOrders(id, type);
	}

	//사용자에게 알림 보내는 로직
	@Override
	public int createUserAlarm(int no, String type) {
		Orders order = mapper.getOrderById(no);
		Store store = mapper.getStoreNameByOrderNo(no);
		String storeName = store.getStore_name();
		String userId = order.getUser_id();
		String title = "";
		String content = "";

		// type에 따라서 알람 제목, 내용 분기처리
		if("confirmed".equals(type)) {
			title = "확정 완료";
			content = storeName + " 예약이 확정되었습니다.";
		}else if("cancel".equals(type)) {
			title = "예약 취소";
			content = storeName + " 이(가) 예약을 취소하셨습니다.";
		}else if("pickup".equals(type)) {
			title = "픽업 완료";
			content = storeName + " 픽업을 완료하였습니다.";
		}

		Alarm alarm = new Alarm();
		alarm.setAlarm_title(title);
		alarm.setAlarm_contents(content);
		alarm.setSended_at(new java.sql.Timestamp(System.currentTimeMillis()));
		alarm.setAlarm_displayed("Y");
		alarm.setReceive_id(userId);
		alarm.setAlarm_read("N");
		return mapper.insertAlarm(alarm);
	}

	//가게 사장에게 알림 보내는 로직
	@Override
	public int createStoreAlarm(int no, String type) {
		Orders order = mapper.getOrderById(no);
		String userId = order.getUser_id();
		String storeId = order.getStore_id();
		String title = "";
		String content = "";

		if("confirmed".equals(type)) {
			title = "확정 완료";
			content = order.getOrdered_at() + " " + order.getUser_id() +
					" 님의 오굿백" + order.getQuantity() + "개 예약";
		}else if("cancel".equals(type)) {
			title = "취소 완료";
			content = order.getOrdered_at() + " " + order.getUser_id() +
					" 님의 오굿백" + order.getQuantity() + "개 예약";
		}else if("pickup".equals(type)) {
			title = "픽업 완료";
			content = order.getOrdered_at() + " " + order.getUser_id() +
					" 님의 오굿백" + order.getQuantity() + "개 예약";
		}

		Alarm alarm = new Alarm();
		alarm.setAlarm_title(title);
		alarm.setAlarm_contents(content);
		alarm.setSended_at(new java.sql.Timestamp(System.currentTimeMillis()));
		alarm.setAlarm_displayed("Y");
		alarm.setReceive_id(storeId);
		alarm.setAlarm_read("N");
		return mapper.insertAlarm(alarm);
	}

	//확정 주문내역에서 주문건 픽업완료 처리
	@Override
	public int pickupOrders(int id, String type) {
		return mapper.pickupOrders(id, type);
	}

	// 확정 주문내역에서 주문을 다시 'confirmed'로 바꾸는 로직
	@Override
	public int confirmPickupOrders(int id, String type) {
		return mapper.confirmOrders(id, type);
	}

	// // 'confirmed', 'pickup'인 주문 조회
	// @Override
	// public List<Orders> getConfirmedOrPickupOrders(String id) {
	// 	return mapper.getConfirmedOrPickupOrders(id);
	// }

	// 기간 매출 조회
	@Override
	public StoreSales getSales(String store_id, String start, String end) {
		return mapper.getSales(store_id, start, end);
	}

	// 주문코드 랜덤으로 생성
	@Override
	public int createOrderCode(int id, String type) {
		Random rand = new Random();
		int randomCode = rand.nextInt(900000) + 100000;
		return mapper.createOrderCode(id, type, randomCode);
	}
}
