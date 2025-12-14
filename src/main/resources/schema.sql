-- ================================================
-- OhgoodFood 마감할인 서비스 스키마 (H2 버전)
-- ================================================

-- ------------------------------------------------
-- 1. 회원 (users) - Account + Admin 통합
-- ------------------------------------------------
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    login_id VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    nickname VARCHAR(20),
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'OWNER', 'ADMIN')),
    point INT DEFAULT 0,
    location_agreement BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- ------------------------------------------------
-- 2. 카테고리 마스터 (store_categories)
-- ------------------------------------------------
CREATE TABLE store_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------
-- 3. 가게 (stores)
-- ------------------------------------------------
CREATE TABLE stores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    business_number VARCHAR(20),
    address VARCHAR(200),
    phone VARCHAR(20),
    description VARCHAR(500),
    menu VARCHAR(100),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    open_time TIME,
    close_time TIME,
    is_confirmed BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'CLOSED' CHECK (status IN ('OPEN', 'CLOSED', 'SUSPENDED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- ------------------------------------------------
-- 4. 가게-카테고리 매핑 (store_category_map) - M:N
-- ------------------------------------------------
CREATE TABLE store_category_map (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES store_categories(id) ON DELETE CASCADE,
    CONSTRAINT uk_store_category UNIQUE (store_id, category_id)
);

-- ------------------------------------------------
-- 5. 가게 이미지 (store_images)
-- ------------------------------------------------
CREATE TABLE store_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
);

-- ------------------------------------------------
-- 6. 상품 (goods)
-- ------------------------------------------------
CREATE TABLE goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    origin_price INT NOT NULL,
    sale_price INT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    pickup_start_at TIMESTAMP NOT NULL,
    pickup_end_at TIMESTAMP NOT NULL,
    reservation_end_at TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'SELLING' CHECK (status IN ('SELLING', 'SOLD_OUT', 'CLOSED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    FOREIGN KEY (store_id) REFERENCES stores(id)
);

-- ------------------------------------------------
-- 7. 예약 (reservations)
-- ------------------------------------------------
CREATE TABLE reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_price INT NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING_PAYMENT' CHECK (status IN ('PENDING_PAYMENT', 'PAID', 'PICKED_UP', 'CANCELED', 'NO_SHOW')),
    canceled_by VARCHAR(20),
    canceled_at TIMESTAMP NULL,
    picked_up_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (goods_id) REFERENCES goods(id)
);

-- ------------------------------------------------
-- 8. 결제 (payments) - Toss Payments 연동
-- ------------------------------------------------
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    payment_key VARCHAR(200),
    order_id VARCHAR(100) NOT NULL UNIQUE,
    amount INT NOT NULL,
    method VARCHAR(20),
    status VARCHAR(20) DEFAULT 'READY' CHECK (status IN ('READY', 'SUCCESS', 'FAILED', 'CANCELED', 'REFUND')),
    fail_reason CLOB,
    paid_at TIMESTAMP NULL,
    refunded_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ------------------------------------------------
-- 9. 리뷰 (reviews)
-- ------------------------------------------------
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    reservation_id BIGINT NOT NULL,
    content VARCHAR(500),
    rating INT,
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (store_id) REFERENCES stores(id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

-- ------------------------------------------------
-- 10. 리뷰 이미지 (review_images)
-- ------------------------------------------------
CREATE TABLE review_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE
);

-- ------------------------------------------------
-- 11. 알림 (notifications)
-- ------------------------------------------------
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('ORDER_STATUS', 'PROMOTION', 'SYSTEM')),
    title VARCHAR(100) NOT NULL,
    content VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,

    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ------------------------------------------------
-- 12. 즐겨찾기 (bookmarks)
-- ------------------------------------------------
CREATE TABLE bookmarks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_store UNIQUE (user_id, store_id)
);

-- ------------------------------------------------
-- 13. 정산 (settlements)
-- ------------------------------------------------
CREATE TABLE settlements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    total_sales INT NOT NULL DEFAULT 0,
    total_orders INT NOT NULL DEFAULT 0,
    commission INT NOT NULL DEFAULT 0,
    settlement_amount INT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    settled_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (store_id) REFERENCES stores(id)
);

-- ================================================
-- 초기 데이터
-- ================================================

-- 카테고리
INSERT INTO store_categories (name, display_order) VALUES
    ('베이커리', 1),
    ('과일', 2),
    ('샐러드', 3),
    ('도시락', 4),
    ('반찬', 5),
    ('기타', 99);

-- 테스트 유저 (password: test1234)
INSERT INTO users (login_id, password, name, nickname, phone, role) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.wF5gH1C5MNKJsWqE.m', '관리자', 'admin', '010-0000-0000', 'ADMIN'),
    ('owner1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.wF5gH1C5MNKJsWqE.m', '사장님1', 'owner1', '010-1111-1111', 'OWNER'),
    ('user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.wF5gH1C5MNKJsWqE.m', '테스트유저', 'tester', '010-2222-2222', 'USER');

-- 테스트 가게
INSERT INTO stores (owner_id, name, business_number, address, phone, description, latitude, longitude, open_time, close_time, is_confirmed, status) VALUES
    (2, '맛있는 베이커리', '123-45-67890', '서울시 강남구 테헤란로 123', '02-1234-5678', '매일 신선한 빵을 구워요', 37.5012, 127.0396, '07:00', '22:00', TRUE, 'OPEN');

-- 가게 카테고리 매핑
INSERT INTO store_category_map (store_id, category_id) VALUES
    (1, 1);

-- 테스트 상품
INSERT INTO goods (store_id, name, description, origin_price, sale_price, stock, pickup_start_at, pickup_end_at, reservation_end_at, status) VALUES
    (1, '마감 빵 세트', '오늘 남은 빵 모음', 15000, 7500, 10, '2025-12-11 20:00:00', '2025-12-11 22:00:00', '2025-12-11 19:30:00', 'SELLING');