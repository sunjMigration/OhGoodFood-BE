package kr.co.ohgoodfood._legacy.dto;

import java.sql.Time;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class Store {
    private String store_id;
    private String confirmed; 
    private String business_number;
    private String store_address;
    private String store_name;
    private String store_menu;
    private String store_explain;
    private String store_telnumber;
    private String store_status;
    private Time opened_at;
    private Time closed_at;

    private String store_pwd;
    private String owner_name;
    private String category_bakery;
    private String category_fruit;
    private String category_salad;
    private String category_others;
    private double latitude;
    private double longitude;
    private Timestamp join_date;

    private String s_store_id;
  
    private String s_confirmed;

    private String s_store_address;
    private String s_store_name;
    private String s_store_menu;
    private String s_store_explain;
    private String s_category_bakery;
    private String s_category_fruit;
    private String s_category_salad;
    private String s_category_others;

    private String s_type;
    private String s_value;
    private int page;
    private int startIdx;

    private long s_paid_price;

    public Store(){
        this.page = 1;
    }

    public int getStartIdx(){
        return (page - 1) * 7;
    }

}
