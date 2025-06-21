package com.pcstore.utils;

/**
 * Lớp quản lý cấu hình từ biến môi trường
 * Sử dụng lớp này để truy cập các thông tin nhạy cảm thay vì đọc từ file
 */
public class EnvironmentConfig {
    
    /**
     * Lấy giá trị từ biến môi trường, nếu không tìm thấy sẽ trả về giá trị mặc định
     * @param key Tên biến môi trường
     * @param defaultValue Giá trị mặc định nếu không tìm thấy
     * @return Giá trị của biến môi trường hoặc giá trị mặc định
     */
    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Lấy giá trị từ biến môi trường
     * @param key Tên biến môi trường
     * @return Giá trị của biến môi trường
     * @throws IllegalArgumentException nếu biến môi trường không được thiết lập
     */
    public static String getRequiredEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            // System.out.println(System.getenv());
            throw new IllegalArgumentException("Biến môi trường bắt buộc không được thiết lập: " + key);
        }
        return value;
    }
    
    // PayOS Configuration
    public static String getPayOSClientId() {
        return getRequiredEnv("PAYOS_CLIENT_ID");
    }
    
    public static String getPayOSApiKey() {
        return getRequiredEnv("PAYOS_API_KEY");
    }
    
    public static String getPayOSChecksumKey() {
        return getRequiredEnv("PAYOS_CHECKSUM_KEY");
    }
    
    // ZaloPay Configuration
    public static String getZaloPayAppId() {
        return getRequiredEnv("ZALOPAY_APP_ID");
    }
    
    public static String getZaloPayKey1() {
        return getRequiredEnv("ZALOPAY_KEY1");
    }
    
    public static String getZaloPayKey2() {
        return getRequiredEnv("ZALOPAY_KEY2");
    }
    
    // Database Configuration
    public static String getDbUrl() {
        return getRequiredEnv("DB_URL");
    }
    
    public static String getDbUsername() {
        return getRequiredEnv("DB_USERNAME");
    }
    
    public static String getDbPassword() {
        return getRequiredEnv("DB_PASSWORD");
    }
    
    // Thêm các phương thức khác nếu cần
}