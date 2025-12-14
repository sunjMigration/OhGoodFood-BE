package kr.co.ohgoodfood._legacy.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Product {
    private int product_no;
    private String store_id;
    private Timestamp pickup_start;
    private Timestamp pickup_end;
    private Timestamp reservation_end;
    private int origin_price;
    private int sale_price;
    private String product_explain;
    private int amount;

    private int s_product_no;
    private String s_store_id;
}
