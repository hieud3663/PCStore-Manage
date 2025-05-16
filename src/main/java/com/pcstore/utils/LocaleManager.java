package com.pcstore.utils;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
/**
 * Lớp quản lý thiết lập Locale toàn cục trong ứng dụng
 */
public class LocaleManager {
    
    private static final double pointRate = 0.1; // Tỷ lệ điểm quy đổi

    private final String fileNameVI = "/com/pcstore/resources/vi_VN.properties";
    private final String  fileNameEN = "/com/pcstore/resources/en_US.properties";

    private final String fileNameResourceVI = "com/pcstore/resources/vi_VN";
    private final String fileNameResourceEN = "com/pcstore/resources/en_US";

    private final String fileNameMessageVI = "com/pcstore/resources/message_vi_VN";
    private final String fileNameMessageEN = "com/pcstore/resources/message_en_US";

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
    

    //Lấy ngôn ngữ hiện tại
    public String getCurrentLanguage() {
        return currentLocale.getLanguage();
    }

    //Trả về bundle properties tương ứng với ngôn ngữ hiện tại
    //Trả về file properties dạng bundle tương ứng với ngôn ngữ hiện tại
    public Properties getProperties(){
        Properties properties = new Properties();
        try {
            if (currentLocale.getLanguage().equals("vi")) {
                properties.load(getClass().getResourceAsStream(fileNameVI));
            } else {
                properties.load(getClass().getResourceAsStream(fileNameEN));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    //Trả về resource bundle tương ứng với ngôn ngữ hiện tại
    public ResourceBundle getResourceBundle() {
        ResourceBundle resourceBundle = null;
        try {
            if (currentLocale.getLanguage().equals("vi")) {
                resourceBundle = ResourceBundle.getBundle(fileNameResourceVI);
            } else {
                resourceBundle = ResourceBundle.getBundle(fileNameResourceEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceBundle;
    }

    //trả về resource bundle tương ứng với ngôn ngữ hiện tại cho message
    public ResourceBundle getMessageResourceBundle() {
        ResourceBundle resourceBundle = null;
        try {
            if (currentLocale.getLanguage().equals("vi")) {
                resourceBundle = ResourceBundle.getBundle(fileNameMessageVI);
            } else {
                resourceBundle = ResourceBundle.getBundle(fileNameMessageEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resourceBundle;
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
            // Cập nhật lại tất cả các thông báo lỗi khi thay đổi ngôn ngữ
            ErrorMessage.refresh();
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

    //ĐỊnh dạng thời gian
    public String formatDate(java.util.Date date) {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", currentLocale);
        return dateFormat.format(date);
    }
    
    public String formatDateTime(java.util.Date date) {
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", currentLocale);
        return dateTimeFormat.format(date);
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", currentLocale);
    }

    public DateTimeFormatter getDateFormatter() {
        // Creates a formatter that first tries dd/MM/yyyy, then dd-MM-yyyy
        return new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            .toFormatter(currentLocale);
    }


}