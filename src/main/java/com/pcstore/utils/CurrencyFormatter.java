package com.pcstore.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lớp tiện ích để định dạng tiền tệ
 */
public class CurrencyFormatter {
    
    private static final NumberFormat FORMAT = new DecimalFormat("#,###.##");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    /**
     * Định dạng số thành chuỗi tiền tệ (VND)
     * @param value Số cần định dạng
     * @return Chuỗi tiền tệ đã định dạng
     */
    public static String format(BigDecimal value) {
        if (value == null) {
            return "0 ₫";
        }
        return FORMAT.format(value) + " ₫";
    }
    
    /**
     * Định dạng số thành chuỗi tiền tệ (VND)
     * @param value Số cần định dạng
     * @return Chuỗi tiền tệ đã định dạng
     */
    public static String format(double value) {
        return FORMAT.format(value) + " ₫";
    }
    
    /**
     * Định dạng số thành chuỗi tiền tệ (VND) theo định dạng tiền tệ Việt Nam
     * @param value Số cần định dạng
     * @return Chuỗi tiền tệ đã định dạng
     */
    public static String formatCurrency(BigDecimal value) {
        if (value == null) {
            return CURRENCY_FORMAT.format(0);
        }
        return CURRENCY_FORMAT.format(value);
    }
}