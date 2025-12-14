//package kr.co.ohgoodfood._legacy.dao;
//
//import java.util.List;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//import kr.co.ohgoodfood._legacy.dto.Account;
//import kr.co.ohgoodfood._legacy.dto.KakaoUser;
//import kr.co.ohgoodfood._legacy.dto.Alarm;
//import kr.co.ohgoodfood._legacy.dto.Store;
//
//@Mapper
//public interface CommonMapper {
//
//	// 유저 로그인 검증 로직
//	public Account loginAccount(@Param("id") String id, @Param("pwd") String pwd);
//
//	// 가게 사장 로그인 검증 로직
//	public Store loginStore(@Param("id") String id, @Param("pwd") String pwd);
//
//	// 알람 가져오기
//	public List<Alarm> getAlarm(@Param("id") String id);
//
//	// 알람 읽음 처리
//	public int updateAlarm(@Param("id") String id);
//
//	// 알람 디스플레이 숨김 처리
//	public int hideAlarm(@Param("alarm_no") int alarm_no);
//
//	// 안 읽은 알람 확인
//	public int checkUnreadAlarm(@Param("id") String id);
//
//	// 네이버 신규 유저 DB 등록
//	public int insertNaverUser(Account account);
//
//	// userId로 계정 조회
//	public Account getAccountById(String userId);
//
//	// 이름, 닉네임, 전화번호 업데이트 확인
//	public void updateNaverUser(Account account);
//
//	// kakao_12345 형식으로 객체 찾음
//	public Account findById(String userId);
//
//	// 카카오 회원가입
//	public void insertKakaoUser(KakaoUser user);
//
//	// 카카오 로그인시 사용자 정보 업데이트
//	public int updateInfo(@Param("id") String id, @Param("nickname") String nickname, @Param("email") String email);
//}
