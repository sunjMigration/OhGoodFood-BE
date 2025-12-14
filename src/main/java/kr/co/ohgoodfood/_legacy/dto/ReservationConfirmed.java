package kr.co.ohgoodfood._legacy.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ReservationConfirmed {

	private String store_id;
	private int order_no;
	private int product_no;
	private String user_id;
	private String order_status;
	private String store_status;
	private int order_code;

	private Timestamp reservation_end;
	private Timestamp pickup_end;
	private Timestamp pickup_start;
}
