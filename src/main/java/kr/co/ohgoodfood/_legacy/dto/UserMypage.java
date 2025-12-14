package kr.co.ohgoodfood._legacy.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *  Mypage용 DTO
 */
@Data
@NoArgsConstructor
public class UserMypage {
    // Account table에서 가져오는 정보들
    private String       user_id;
    private String       user_nickname;
    private int 		 user_point;

    // Review table에서 가져오는 정보들
    private List<Review> reviews;
    private int review_no;
    private String review_content;
    private Timestamp writed_at;
    private String is_blocked;
    private String review_img;
    private String store_id;
    private int oreder_no;

    // Product table에서 가져오는 정보들
    private int    origin_price;
    private int    sale_price;

    // Store table에서 가져오는 정보들
    private String store_name;
    private String store_menu;

    // Image table에서 가져오는 정보들
    private String store_img; // 가게 이미지1개만 가져옴




}