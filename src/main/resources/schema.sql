-- ohgoodfood.Account definition

CREATE TABLE `Account` (
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `user_name` varchar(20) NOT NULL,
  `user_nickname` varchar(10) NOT NULL,
  `user_pwd` text NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `join_date` datetime DEFAULT NULL,
  `user_status` varchar(1) DEFAULT NULL,
  `location_agreement` varchar(1) DEFAULT NULL,
  `user_point` int(10) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Store definition

CREATE TABLE `Store` (
  `store_id` varchar(15) NOT NULL,
  `confirmed` varchar(1) DEFAULT NULL,
  `business_number` varchar(10) DEFAULT NULL,
  `store_address` varchar(100) DEFAULT NULL,
  `store_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `store_menu` varchar(30) DEFAULT NULL,
  `store_explain` varchar(50) DEFAULT NULL,
  `store_telnumber` varchar(20) DEFAULT NULL,
  `store_status` varchar(1) DEFAULT NULL,
  `opened_at` time DEFAULT NULL,
  `closed_at` time DEFAULT NULL,
  `store_pwd` varchar(255) DEFAULT NULL,
  `owner_name` varchar(20) DEFAULT NULL,
  `category_bakery` varchar(1) DEFAULT NULL,
  `category_fruit` varchar(1) DEFAULT NULL,
  `category_salad` varchar(1) DEFAULT NULL,
  `category_others` varchar(1) DEFAULT NULL,
  `latitude` decimal(15,12) DEFAULT NULL,
  `longitude` decimal(15,12) DEFAULT NULL,
  `join_date` datetime DEFAULT NULL,
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Product definition

CREATE TABLE `Product` (
  `product_no` int(11) NOT NULL AUTO_INCREMENT,
  `store_id` varchar(15) NOT NULL,
  `pickup_start` datetime DEFAULT NULL,
  `pickup_end` datetime DEFAULT NULL,
  `reservation_end` datetime DEFAULT NULL,
  `origin_price` int(11) DEFAULT NULL,
  `sale_price` int(11) DEFAULT NULL,
  `product_explain` varchar(50) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  PRIMARY KEY (`product_no`),
  KEY `store_id` (`store_id`),
  CONSTRAINT `Product_ibfk_1` FOREIGN KEY (`store_id`) REFERENCES `Store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Admin definition

CREATE TABLE `Admin` (
  `admin_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `admin_pwd` varchar(255) NOT NULL,
  `admin_name` varchar(20) NOT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Alarm definition

CREATE TABLE `Alarm` (
  `alarm_no` int(11) NOT NULL AUTO_INCREMENT,
  `alarm_title` varchar(20) NOT NULL,
  `alarm_contents` varchar(50) NOT NULL,
  `sended_at` datetime DEFAULT NULL,
  `alarm_displayed` varchar(1) DEFAULT 'Y',
  `receive_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `alarm_read` varchar(1) DEFAULT 'N',
  PRIMARY KEY (`alarm_no`),
  KEY `receive_id` (`receive_id`)
) ENGINE=InnoDB AUTO_INCREMENT=300 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Bookmark definition

CREATE TABLE `Bookmark` (
  `bookmark_no` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `store_id` varchar(15) NOT NULL,
  PRIMARY KEY (`bookmark_no`),
  KEY `user_id` (`user_id`),
  KEY `store_id` (`store_id`),
  CONSTRAINT `FK_Bookmark_Account` FOREIGN KEY (`user_id`) REFERENCES `Account` (`user_id`),
  CONSTRAINT `FK_Bookmark_Store` FOREIGN KEY (`store_id`) REFERENCES `Store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Image definition

CREATE TABLE `Image` (
  `img_no` int(11) NOT NULL AUTO_INCREMENT,
  `store_img` varchar(255) DEFAULT NULL,
  `store_id` varchar(15) NOT NULL,
  PRIMARY KEY (`img_no`),
  KEY `store_id` (`store_id`),
  CONSTRAINT `Image_ibfk_1` FOREIGN KEY (`store_id`) REFERENCES `Store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Orders definition

CREATE TABLE `Orders` (
  `order_no` int(11) NOT NULL AUTO_INCREMENT,
  `ordered_at` datetime DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `order_status` varchar(15) DEFAULT NULL,
  `picked_at` datetime DEFAULT NULL,
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `store_id` varchar(15) NOT NULL,
  `order_code` int(6) DEFAULT NULL,
  `canceld_from` varchar(10) CHARACTER SET armscii8 COLLATE armscii8_general_ci DEFAULT NULL,
  `product_no` int(10) DEFAULT NULL,
  PRIMARY KEY (`order_no`),
  KEY `user_id` (`user_id`),
  KEY `store_id` (`store_id`),
  KEY `product_no` (`product_no`),
  CONSTRAINT `FK_Orders_Account` FOREIGN KEY (`user_id`) REFERENCES `Account` (`user_id`),
  CONSTRAINT `FK_Orders_Product` FOREIGN KEY (`product_no`) REFERENCES `Product` (`product_no`),
  CONSTRAINT `FK_Orders_Store` FOREIGN KEY (`store_id`) REFERENCES `Store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Paid definition

CREATE TABLE `Paid` (
  `paid_no` int(11) NOT NULL AUTO_INCREMENT,
  `paid_type` varchar(20) DEFAULT NULL,
  `paid_price` int(11) DEFAULT NULL,
  `paid_point` int(11) DEFAULT NULL,
  `paid_time` datetime DEFAULT NULL,
  `paid_status` varchar(1) DEFAULT NULL,
  `fail_reason` text DEFAULT NULL,
  `refund_request` varchar(2) DEFAULT 'N',
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `order_no` int(11) NOT NULL,
  `paid_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`paid_no`),
  KEY `user_id` (`user_id`),
  KEY `order_no` (`order_no`),
  CONSTRAINT `FK_Paid_Account` FOREIGN KEY (`user_id`) REFERENCES `Account` (`user_id`),
  CONSTRAINT `FK_Paid_Orders` FOREIGN KEY (`order_no`) REFERENCES `Orders` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ohgoodfood.Review definition

CREATE TABLE `Review` (
  `review_no` int(11) NOT NULL AUTO_INCREMENT,
  `review_content` varchar(100) NOT NULL,
  `writed_at` datetime DEFAULT NULL,
  `is_blocked` varchar(1) DEFAULT NULL,
  `review_img` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `store_id` varchar(15) NOT NULL,
  `order_no` int(11) NOT NULL,
  PRIMARY KEY (`review_no`),
  KEY `user_id` (`user_id`),
  KEY `store_id` (`store_id`),
  KEY `order_no` (`order_no`),
  CONSTRAINT `FK_Review_Account` FOREIGN KEY (`user_id`) REFERENCES `Account` (`user_id`),
  CONSTRAINT `FK_Review_Orders` FOREIGN KEY (`order_no`) REFERENCES `Orders` (`order_no`),
  CONSTRAINT `FK_Review_Store` FOREIGN KEY (`store_id`) REFERENCES `Store` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
