package kr.co.ohgoodfood._legacy.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data

public class UserSignup {
    private String user_id;
    private String user_pwd;
	private String nickname;
	private String name;
	private String phone;
	private Timestamp join_date;
    private String location_agreement;

}
