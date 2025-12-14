package kr.co.ohgoodfood._legacy.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Review {
    private int review_no;
    private String review_content;
    private Timestamp writed_at;
    private String is_blocked;
    private String review_img;
    private String user_id;
    private String store_id;
    private int order_no; // [gaeun] oreder_no로 되어있어 수정했습니다.

    private int s_review_no;
    private int s_user_id;
    private int s_sotre_id;
    
    // --- 가격 정보 추가 필요 ---
    // Product table
    private int    origin_price;
    private int    sale_price;

    // Store table
    private String store_name;
    private String store_menu;

    // 대표 이미지 (Image 서브쿼리)
    private String store_img;

    // 유저 닉네임
    private String user_nickname;
    
    private String s_type;
    private String s_value;

    private int page;
    private int startIdx;

    public Review() {
        this.page = 1;
    }

    public int getStartIdx(){
        return (page - 1) * 7;
    }
}
