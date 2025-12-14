package kr.co.ohgoodfood.notification.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import kr.co.ohgoodfood._legacy.dao.CommonMapper;
import kr.co.ohgoodfood._legacy.dto.Account;
import kr.co.ohgoodfood._legacy.dto.KakaoUser;
import kr.co.ohgoodfood._legacy.dto.Alarm;
import kr.co.ohgoodfood._legacy.dto.Store;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

	private final CommonMapper commonMapper;

	// 사용자 로그인
	@Override
	public Account loginAccount(String id, String pwd) {
		return commonMapper.loginAccount(id, md5(pwd));
	}

	// 가게 사장 로그인
	@Override
	public Store loginStore(String id, String pwd) {
		return commonMapper.loginStore(id, md5(pwd));
	}

	// MD5 암호화 메서드 추가
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

	@Value("${naver.client_id}")
	private String clientId;

	@Value("${naver.client_secret}")
	private String clientSecret;

	// 네이버 소셜로그인 관련 신규 유저 삽입
	@Override
	public void insertNaverUser(Account account) {
		commonMapper.insertNaverUser(account);
	}

	// 유저 ID로 계정 조회
	@Override
	public Account getAccountById(String userId) {
		return commonMapper.getAccountById(userId);
	}

	// 네이버 소셜로그인
	@Override
	public Account processNaverLogin(String code, String state, String sessionState) throws Exception {

		// CSRF 방지용: 콜백으로 받은 state와 세션에 저장된 state가 일치하는지 검증
		if (!state.equals(sessionState)) {
			throw new IllegalStateException("State mismatch");
		}

		// 콜백 URI (네이버 API 등록 시 설정한 redirect URI)
		String redirectUri = "https://ohgoodfood.com/naver/callback";

		// 네이버 OAuth 서버에 Access Token 요청 URL 구성
		String tokenUrl = "https://nid.naver.com/oauth2.0/token" + "?grant_type=authorization_code" + "&client_id="
				+ clientId + "&client_secret=" + clientSecret + "&code=" + code + "&state=" + state;

		// Access Token 요청
		URL url = new URL(tokenUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		// 응답 받기
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		StringBuilder res = new StringBuilder();
		while ((line = br.readLine()) != null) {
			res.append(line);
		}
		br.close();

		// JSON 파싱하여 Access Token 추출
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(res.toString());
		String accessToken = node.get("access_token").asText();

		// 추출한 Access Token으로 사용자 프로필 API 호출
		URL profileUrl = new URL("https://openapi.naver.com/v1/nid/me");
		HttpURLConnection profileCon = (HttpURLConnection) profileUrl.openConnection();
		profileCon.setRequestMethod("GET");
		profileCon.setRequestProperty("Authorization", "Bearer " + accessToken);

		// 사용자 프로필 응답 읽기
		BufferedReader profileBr = new BufferedReader(new InputStreamReader(profileCon.getInputStream()));
		StringBuilder profileRes = new StringBuilder();
		while ((line = profileBr.readLine()) != null) {
			profileRes.append(line);
		}
		profileBr.close();

		// JSON 파싱하여 유저 정보 추출
		JsonNode profileNode = mapper.readTree(profileRes.toString());
		JsonNode response = profileNode.get("response");

		// 네이버 고유 ID
		String rawNaverId = response.get("id").asText();
		// 내부 시스템 유저 ID는 'naver_id_' 붙여서 생성
		String userId = "naver_id_" + rawNaverId;
		String name = response.get("name").asText();
		String nickname = response.get("nickname").asText();
		String mobile = response.get("mobile").asText();

		// DB에 동일 유저 ID가 이미 존재하는지 확인
		Account account = getAccountById(userId);

		// 없으면 신규 계정 등록
		if (account == null) {
			Account newAccount = new Account();
			newAccount.setUser_id(userId);
			newAccount.setUser_name(name);
			// 닉네임은 최대 10자 제한
			newAccount.setUser_nickname(nickname.length() > 10 ? nickname.substring(0, 10) : nickname);
			newAccount.setPhone_number(mobile);
			// 소셜로그인은 비밀번호를 따로 받지 않으므로 식별값 저장
			newAccount.setUser_pwd("naver_login");
			insertNaverUser(newAccount);
			account = newAccount;
		}else {
	        // 있으면 기존 값과 비교해서 다르면 갱신
	        boolean needsUpdate = false;

	        if (!account.getUser_name().equals(name)) {
	            account.setUser_name(name);
	            needsUpdate = true;
	        }

	        String newNickname = nickname.length() > 10 ? nickname.substring(0, 10) : nickname;
	        if (!account.getUser_nickname().equals(newNickname)) {
	            account.setUser_nickname(newNickname);
	            needsUpdate = true;
	        }

	        if (!account.getPhone_number().equals(mobile)) {
	            account.setPhone_number(mobile);
	            needsUpdate = true;
	        }

	        if (needsUpdate) {
	            updateNaverUser(account);
	        }
	    }

		// 로그인 완료 Account 리턴
		return account;
	}

	@Override
	public void updateNaverUser(Account account) {
	    commonMapper.updateNaverUser(account);
	}
	
	@Value("${kakao.rest.apiKey}")
	private String kakaoClientId;

	@Value("${kakao.redirect-uri}")
	private String kakaoRedirectUri;

	private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
	private final String KAKAO_USER_URL = "https://kapi.kakao.com/v2/user/me";
	private final ObjectMapper mapper = new ObjectMapper();

	// access_token만 사용하여 사용자 정보를 가져오는 로직
	@Override
	public KakaoUser getKakaoUserInfo(String code) {
		try {
			// 전송할 파라미터 만들기
			String params = "grant_type=authorization_code" + "&client_id=" + kakaoClientId + "&redirect_uri="
					+ URLEncoder.encode(kakaoRedirectUri, "UTF-8") + "&code=" + code;

			HttpURLConnection conn = (HttpURLConnection) new URL(KAKAO_TOKEN_URL).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8"); // 헤더 설정

			// 파라미터 전송
			try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
				bw.write(params);
				bw.flush();
			}

			// access_token을 받아옴
			String accessToken;
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String response = br.readLine();
				Map<String, Object> tokenMap = mapper.readValue(response, Map.class); // json -> java 파싱
				accessToken = (String) tokenMap.get("access_token");
			}

			// access_token 으로 사용자 정보 조회
			HttpURLConnection userConn = (HttpURLConnection) new URL(KAKAO_USER_URL).openConnection();
			userConn.setRequestMethod("GET");
			userConn.setRequestProperty("Authorization", "Bearer " + accessToken);

			int responseCode = userConn.getResponseCode();
			if (responseCode == 200) {
			    try (BufferedReader br = new BufferedReader(new InputStreamReader(userConn.getInputStream()))) {
			        StringBuilder sb = new StringBuilder();
			        String line;
			        while ((line = br.readLine()) != null) {
			            sb.append(line);
			        }
			        String userInfo = sb.toString();
			        Map<String, Object> jsonMap = mapper.readValue(userInfo, Map.class);

			        // user_id = kakao_id_12345로 만드는 로직
			        String id = "kakao_id_" + jsonMap.get("id").toString();
			        Map<String, Object> properties = (Map<String, Object>) jsonMap.get("properties");

			        // nickname 받아옴
			        String nickname = (String) properties.get("nickname");
			        Map<String, Object> kakaoAccount = (Map<String, Object>) jsonMap.get("kakao_account");

			        // email 받아옴, email이 없으면 기본값으로 none@example.com
			        String email = kakaoAccount.getOrDefault("email", "none@example.com").toString();

			        // 카카오 로그인 시 매번 nickname, email 들고와서 갱신
			        int r = commonMapper.updateInfo(id, nickname, email);
			        if (r == 0) {
			            // UPDATE 실패 → INSERT 실행
			            commonMapper.insertKakaoUser(new KakaoUser(id, nickname, email, accessToken));
			        }

			        // UPDATE든 INSERT든 KakaoUser 리턴
			        return new KakaoUser(id, nickname, email, accessToken);

			    }
			} else {
			    throw new RuntimeException("카카오 사용자 정보 요청 실패. 응답 코드: " + responseCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// DB에 해당 계정이 없으면 가입, 있으면 바로 로그인
	@Override
	public Account autoLoginOrRegister(KakaoUser kakaoUser) {
		Account existing = commonMapper.findById(kakaoUser.getId());
		if (existing != null)
			return existing;
		commonMapper.insertKakaoUser(kakaoUser);
		return commonMapper.findById(kakaoUser.getId());
	}

	// 알람 가져오기
	@Override
	public List<Alarm> getAlarm(String id) {
		return commonMapper.getAlarm(id);
	}

	// 알람 읽음 처리
	@Override
	public int updateAlarm(String id) {
		return commonMapper.updateAlarm(id);
	}

	// 알람 디스플레이 숨김 처리
	@Override
	public int hideAlarm(int alarm_no) {
		return commonMapper.hideAlarm(alarm_no);
	}

	// 안 읽은 알람 확인
	@Override
	public int checkUnreadAlarm(String id) {
		return commonMapper.checkUnreadAlarm(id);
	}
}
