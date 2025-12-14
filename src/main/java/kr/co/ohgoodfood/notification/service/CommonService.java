package kr.co.ohgoodfood.notification.service;

import java.util.List;
import kr.co.ohgoodfood._legacy.dto.Account;
import kr.co.ohgoodfood._legacy.dto.KakaoUser;
import kr.co.ohgoodfood._legacy.dto.Alarm;
import kr.co.ohgoodfood._legacy.dto.Store;

public interface CommonService {

	// 유저 로그인 입력 정보 조회
	public Account loginAccount(String id, String pwd);

	// 가게 사장 로그인 입력 정보 조회
	public Store loginStore(String id, String pwd);

	// 알람 가져오기
	public List<Alarm> getAlarm(String id);

	// 알람 읽음 처리
	public int updateAlarm(String id);

	// 알람 디스플레이 숨김 처리
	public int hideAlarm(int alarm_no);

	// 안 읽은 알람 확인
	public int checkUnreadAlarm(String id);

	// 네이버 신규 유저 DB 등록
	public void insertNaverUser(Account account);

	// userId로 DB에서 유저 정보 가져오기
	public Account getAccountById(String userId);

	// 네이버 로그인 전체 처리 (state 검증 → 토큰 발급 → 프로필 조회 → 회원 등록)
	public Account processNaverLogin(String code, String state, String sessionState) throws Exception;
	
	public void updateNaverUser(Account account);

	// code로 access_token 받고 사용자 객체정보 가져옴
	KakaoUser getKakaoUserInfo(String code);

	// 사용자가 없으면 자동 회원가입, 있으면 해당 객체 정보 리턴
	Account autoLoginOrRegister(KakaoUser kakaoUser);
}
