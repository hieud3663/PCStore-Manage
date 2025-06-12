package com.pcstore.utils;

import org.thymeleaf.context.Context;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * Utility class cho việc xử lý dữ liệu bill/phiếu
 */
public class BillDataUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,###");
    
    /**
     * Tạo Context cơ bản với thông tin công ty
     */
    public static Context createBaseContext() {
        Context context = new Context();
        
        context.setVariable("companyName", "CÔNG TY TNHH PCSTORE");
        context.setVariable("companyAddress", "123 Nguyễn Văn Cừ, Quận 5, TP.HCM");
        context.setVariable("companyPhone", "Tel: 0333458584 | Email: pcstorehal@gmail.com");
        
        return context;
    }
    
    /**
     * Tạo Context với thông tin công ty tùy chỉnh
     */
    public static Context createContextWithCompanyInfo(String companyName, String address, String phone) {
        Context context = new Context();
        
        context.setVariable("companyName", companyName);
        context.setVariable("companyAddress", address);
        context.setVariable("companyPhone", phone);
        
        return context;
    }
    
    /**
     */
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }
    
    /**
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }
    
    /**
     */
    public static String formatCurrency(Object amount) {
        if (amount == null) return "0";
        
        if (amount instanceof Number) {
            return CURRENCY_FORMAT.format(((Number) amount).doubleValue());
        }
        
        try {
            double value = Double.parseDouble(amount.toString());
            return CURRENCY_FORMAT.format(value);
        } catch (NumberFormatException e) {
            return "0";
        }
    }
    
    /**
     */
    public static String formatNumber(Object number) {
        if (number == null) return "0";
        
        if (number instanceof Number) {
            return NUMBER_FORMAT.format(((Number) number).longValue());
        }
        
        try {
            long value = Long.parseLong(number.toString());
            return NUMBER_FORMAT.format(value);
        } catch (NumberFormatException e) {
            return "0";
        }
    }
    
    /**
     */
    public static Map<String, Object> createSummaryData(int totalItems, Object totalIncrease, 
                                                       Object totalDecrease, Object totalDifference) {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("totalProducts", totalItems);
        summary.put("totalIncrease", formatNumber(totalIncrease));
        summary.put("totalDecrease", formatNumber(totalDecrease));
        summary.put("totalDifference", formatNumber(totalDifference));
        
        return summary;
    }
    
    /**
     * Chuyển đổi trạng thái thành text hiển thị
     */
    public static String getStatusDisplayText(String status) {
        if (status == null) return "";
        
        switch (status.toUpperCase()) {
            case "DRAFT":
                return "Nháp";
            case "IN_PROGRESS":
                return "Đang thực hiện";
            case "COMPLETED":
                return "Hoàn thành";
            case "CANCELLED":
                return "Đã hủy";
            case "PENDING":
                return "Chờ xử lý";
            case "APPROVED":
                return "Đã duyệt";
            case "REJECTED":
                return "Từ chối";
            default:
                return status;
        }
    }
    
    /**
     * Thêm common variables vào context
     */
    public static void addCommonVariables(Context context, String code, String name, 
                                        LocalDateTime createdDate, String employeeName, String status) {
        context.setVariable("documentCode", code);
        context.setVariable("documentName", name);
        context.setVariable("createdDate", formatDate(createdDate));
        context.setVariable("employeeName", employeeName != null ? employeeName : "");
        context.setVariable("status", getStatusDisplayText(status));
    }
    
}