package kr.co.ohgoodfood._legacy.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Account {
    private String user_id;
    private String user_nickname;
    private String user_name;
    private String user_pwd;
    private String phone_number;
    private Timestamp join_date;
    private String user_status;
    private String location_agreement;
    private int user_point; 

    private String s_type;
    private String s_value;
    private String s_user_id;
    private String s_user_nickname;
    private String s_user_name;

    private int page;
    private int startIdx;

    public Account(){
        this.page = 1;
    }

    public int getStartIdx(){
        return (page - 1) * 7;
    }
}
