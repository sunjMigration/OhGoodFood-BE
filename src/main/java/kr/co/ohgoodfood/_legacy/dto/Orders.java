package kr.co.ohgoodfood._legacy.dto;


import java.sql.Timestamp;
import lombok.Data;

@Data
public class Orders {
    private int order_no;

    private Timestamp ordered_at;
    private int quantity;
    private String order_status;
    private Timestamp picked_at;
    private String user_id;
    private String store_id;
    private String order_code;
    private String canceld_from ;
    private int product_no;

    private int s_order_no;
    private Timestamp s_ordered_at;
    private String s_order_status;

    private String s_price;

    private String s_type;
    private String s_value;

    private int page;
    private int startIdx;

    private String store_img;
    private Timestamp pickup_start;
    private Timestamp pickup_end;
    private Timestamp reservation_end;
    private String pickup_status; 
    private int sale_price;
    private Timestamp reservation_start; // reservation.jsp 에서 reservation_end -1시간 위해서
    
    private int paid_no;
    private String paid_type;
    private int paid_price;
    private int paid_point;
    private String user_nickname;
    
    public Orders() {
        this.page = 1;
    }

    public int getStartIdx() {
        return (page - 1) * 7;
    }
}   

