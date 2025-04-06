package com.pcstore.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Lớp quản lý thiết lập Locale toàn cục trong ứng dụng
 */
public class LocaleManager {
    
    private static final double pointRate = 0.1; // Tỷ lệ điểm quy đổi

    // Singleton instance
    private static LocaleManager instance;
    
    public static double getPointRate() {
        return pointRate;
    }

    // Locale hiện tại (mặc định là Việt Nam)
    @SuppressWarnings("deprecation")
    private Locale currentLocale = new Locale("vi", "VN");
    
    // Các Locale được hỗ trợ
    @SuppressWarnings("deprecation")
    public static final Locale LOCALE_VIETNAM = new Locale("vi", "VN");
    @SuppressWarnings("deprecation")
    public static final Locale LOCALE_US = new Locale("en", "US");
    
    // Định dạng số
    private NumberFormat currencyFormatter;
    private NumberFormat numberFormatter;
    
    /**
     * Constructor riêng tư để đảm bảo Singleton
     */
    private LocaleManager() {
        updateFormatters();
    }
    
    /**
     * Lấy instance của LocaleManager
     */
    public static synchronized LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }
    
    /**
     * Cập nhật các định dạng dựa trên Locale hiện tại
     */
    private void updateFormatters() {
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
        numberFormatter = NumberFormat.getNumberInstance(currentLocale);
    }
    
    /**
     * Thay đổi Locale hiện tại
     * @param locale Locale mới
     */
    public void setLocale(Locale locale) {
        if (locale != null) {
            this.currentLocale = locale;
            updateFormatters();
        }
    }
    
    /**
     * Lấy Locale hiện tại
     * @return Locale hiện tại
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }
    
    /**
     * Lấy định dạng tiền tệ hiện tại
     * @return NumberFormat cho tiền tệ
     */
    public NumberFormat getCurrencyFormatter() {
        return currencyFormatter;
    }
    
    /**
     * Lấy định dạng số hiện tại
     * @return NumberFormat cho số
     */
    public NumberFormat getNumberFormatter() {
        return numberFormatter;
    }
    
    /**
     * Định dạng một số thành chuỗi tiền tệ
     * @param amount Số tiền cần định dạng
     * @return Chuỗi tiền tệ đã định dạng
     */
    public String formatCurrency(Number amount) {
        return currencyFormatter.format(amount);
    }
    
    /**
     * Định dạng một số thành chuỗi số
     * @param number Số cần định dạng
     * @return Chuỗi số đã định dạng
     */
    public String formatNumber(Number number) {
        return numberFormatter.format(number);
    }
    
    /**
     * Định dạng một số thành chuỗi tiền tệ với ký hiệu tiền tệ tùy chỉnh
     * @param amount Số tiền cần định dạng
     * @param currencySymbol Ký hiệu tiền tệ
     * @return Chuỗi tiền tệ đã định dạng
     */
    public String formatCurrency(Number amount, String currencySymbol) {
        return numberFormatter.format(amount) + " " + currencySymbol;
    }
}