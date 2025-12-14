package kr.co.ohgoodfood.global.common;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;

import kr.co.ohgoodfood._legacy.dto.Account;
import kr.co.ohgoodfood._legacy.dto.KakaoUser;
import kr.co.ohgoodfood._legacy.dto.Store;
import kr.co.ohgoodfood.notification.service.CommonService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@ControllerAdvice // 전역 예외처리
@PropertySource("classpath:db.properties")
public class CommonController {

	private final CommonService commonService;

	@Value("${kakao.rest.apiKey}")
	private String kakaoClientId;

	@Value("${kakao.redirect-uri}")
	private String kakaoRedirectUri;

	// 로그인 페이지 리턴
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("kakaoClientId", kakaoClientId);
		model.addAttribute("kakaoRedirectUri", kakaoRedirectUri);
		return "common/login";
	}

	@GetMapping("/")
	public String toLogin() {
		return "redirect:/login";
	}

	@GetMapping("/home")
	public String toLogin2() {
		return "redirect:/login";
	}

	// 로그인 페이지 접속 시 카카오와 네이버 키 값 전송
	@PostMapping("/login/kakao")
	public Map<String, Object> loginKakao(Model model) {
		Map<String, Object> result = new HashMap<>();
		result.put("kakaoClientId", kakaoClientId);
		result.put("kakaoRedirectUri", kakaoRedirectUri);
		return result;
	}

	@PostMapping("/login")
	public String login(HttpServletRequest request, HttpSession sess, Model model) {
		String id = request.getParameter("id"); // 아이디 파라미터로
		String pwd = request.getParameter("pwd"); // 비번 파라미터로 가져옴
		sess.invalidate();
		sess = request.getSession(true);
		Account account = commonService.loginAccount(id, pwd);
		if (account != null) {
			sess.setAttribute("user", account);
			model.addAttribute("user", account);
			return "/common/intro";
		}

		Store store = commonService.loginStore(id, pwd);
		if (store != null) {
			if ("N".equals(store.getConfirmed())) {
				sess.setAttribute("store", store);
				model.addAttribute("showConfirmationModal", true);
				return "common/login";
			}
			sess.setAttribute("store", store);
			model.addAttribute("store", store);
			return "/common/intro";
		}

		model.addAttribute("msg", "아이디 또는 비밀번호가 일치하지 않습니다.");
		model.addAttribute("url", "/login");
		return "store/alert";
	}

	// 로그아웃
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();                    // 세션 무효화
		return "redirect:/login";                // 로그인 페이지로 리다이렉트
	}

	@GetMapping("/jointype") // 회원가입 유형 선택 페이지
	public String jointype() {
		return "/common/jointype";
	}

	@GetMapping("/intro") // 인트로 페이지
	public String intro() {
		return "/common/intro";
	}

	// 상태 코드에 따른 에러 페이지 반환
	@RequestMapping("/error")
	public String handleException(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

		if (statusCode != null) {
			if (statusCode == 404) {
				return "common/error404";
			} else if (statusCode == 500) {
				return "common/error500";
			} else if (statusCode == 403) {
				return "common/error403";
			} else if (statusCode == 400) {
				return "common/error400";
			}
		}

		return "common/error";
	}


	@GetMapping("/oauth/kakaocallback")
	public String kakaoCallback(@RequestParam("code") String code, HttpSession session) {
		// 카카오 로그인 클릭시 리다이렉트uri로 이동하고 code 받음
		//이 code로 가지고 access_token 요청
		KakaoUser kakaoUser = commonService.getKakaoUserInfo(code);
		//code 넘겨서 access_token 받고 사용자 객체 저장
		if (kakaoUser == null) {
			return "redirect:/login";
		}
		Account account = commonService.autoLoginOrRegister(kakaoUser);
		//회원정보가 없으면 자동 회원가입, 있으면 해당 객체정보 리턴
		session.setAttribute("user", account);
		return "redirect:/user/main";
	}

	@Value("${naver.client_id}")
	private String clientId;

	// 네이버 소셜로그인(네이버 인증 URL 생성 후 리다이렉트)
	@GetMapping("/naver/login")
	public String naverLogin(HttpSession session) throws Exception {

	    // 네이버에 등록된 Redirect URI
	    String redirectUri = "https://ohgoodfood.com/naver/callback";

		// CSRF 방지를 위한 임의의 상태(state) 값 생성
		String state = UUID.randomUUID().toString();

		// 생성한 state를 세션에 저장 (콜백에서 검증)
		session.setAttribute("naver_state", state);

		// Redirect URI를 URL 인코딩
		String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString());

		// 네이버 인증 URL 생성
		String naverAuthUrl = "https://nid.naver.com/oauth2.0/authorize"
				+ "?response_type=code"
				+ "&client_id=" + clientId
				+ "&redirect_uri=" + encodedRedirectUri
				+ "&state=" + state;

		// 확인용 로그
		System.out.println("Naver Login URL: " + naverAuthUrl);

		// 7) 네이버 로그인 페이지로 리다이렉트
		return "redirect:" + naverAuthUrl;
	}

	// 네이버 소셜로그인 콜백 처리 (사용자 정보 요청 및 세션 저장)
	@GetMapping("/naver/callback")
	public String naverCallback(@RequestParam String code,     // 네이버가 보내준 인증 코드
								@RequestParam String state,    // 네이버가 보내준 state
								HttpSession session,           // 현재 세션
								Model model) {                 // 에러 메시지 출력용
		// 세션에 저장된 state 가져오기
		String sessionState = (String) session.getAttribute("naver_state");

		try {
			// 서비스 로직 호출: Access Token → 사용자 정보 → DB 처리
			Account account = commonService.processNaverLogin(code, state, sessionState);

			// 로그인 성공 시 세션에 유저 정보 저장
			session.setAttribute("user", account);

			// 메인 인트로 페이지로 리다이렉트
			return "redirect:/intro";

		} catch (IllegalStateException e) {
			// state 값 불일치 (CSRF 의심)
			model.addAttribute("msg", "잘못된 접근입니다. state 값이 일치하지 않습니다.");
			model.addAttribute("url", "/login");
			return "store/alert";

		} catch (Exception e) {
			// 기타 예외 발생
			e.printStackTrace();
			model.addAttribute("msg", "네이버 로그인 처리 중 오류가 발생했습니다.");
			model.addAttribute("url", "/login");
			return "store/alert";
		}
	}


}

