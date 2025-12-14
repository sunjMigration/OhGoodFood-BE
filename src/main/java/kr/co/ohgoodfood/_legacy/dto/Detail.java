package kr.co.ohgoodfood._legacy.dto;

import java.sql.Time;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Detail {
	//Store table에서 가져오는 정보들
    private String store_id;
    private String store_name;
    private String store_menu;
    private String store_status;
    private String category_bakery;
    private String category_fruit;
    private String category_salad;
    private String category_others;
    private Time closed_at;
    private Time opened_at;
    

    //Product table에서 가져오는 정보들
    private int product_no;
    private Timestamp pickup_start;
    private Timestamp pickup_end;
    int origin_price;
    int sale_price;
    int amount;
}
