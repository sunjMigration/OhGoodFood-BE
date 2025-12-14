package kr.co.ohgoodfood._legacy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KakaoUser {
	private String id; // kakao_1234567
	private String nickname;
	private String email;
	private String kakao_token;

	public KakaoUser(String id, String nickname, String email, String kakao_token) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.kakao_token = kakao_token;
    }
}

