package com.pcstore.utils;

/**
 * Class chứa các thông báo lỗi được sử dụng trong toàn bộ ứng dụng
 */
public class ErrorMessage {
    // Common validation messages
    public static final String FIELD_EMPTY = "%s không được để trống";
    public static final String FIELD_NEGATIVE = "%s không được âm";
    
    // User messages
    public static final String USERNAME_EMPTY = "Tên đăng nhập không được để trống";
    public static final String USERNAME_TOO_SHORT = "Tên đăng nhập phải có ít nhất 3 ký tự";
    public static final String PASSWORD_EMPTY = "Mật khẩu không được để trống";
    public static final String PASSWORD_TOO_SHORT = "Mật khẩu phải có ít nhất 6 ký tự";
    public static final String ROLE_EMPTY = "Vai trò không được để trống";

    //Employee messages
    public static final String EMPLOYEE_ID_FORMAT = "Mã nhân viên phải có định dạng NVxx";
    // public static final String EMPLOYEE_PHONE_INVALID = "Số điện thoại không hợp lệ";
    public static final String EMPLOYEE_NAME_TOO_SHORT = "Tên nhân viên phải có ít nhất 2 ký tự";

    //customer messages
    public static final String CUSTOMER_NAME_TOO_SHORT = "Tên khách hàng phải có ít nhất 2 ký tự";
    public static final String CUSTOMER_ID_FORMAT = "Mã khách hàng phải có định dạng KHxx";

    // Category error messages
    public static final String CATEGORY_NAME_EMPTY = "Tên danh mục không được để trống";
    public static final String CATEGORY_SELF_REFERENCE = "Danh mục không thể là danh mục cha của chính nó";
    public static final String CATEGORY_CIRCULAR_REFERENCE = "Không thể tạo vòng lặp trong cấu trúc danh mục";
    public static final String SUBCATEGORY_NULL = "Danh mục con không được để trống";
    public static final String PRODUCT_NULL = "Sản phẩm không được để trống";
    public static final String PARENT_CATEGORY_NULL = "Danh mục cha không được để trống";

    // Discount error messages
    public static final String DISCOUNT_CODE_EMPTY = "Mã khuyến mãi không được để trống";
    public static final String DISCOUNT_AMOUNT_NULL = "Số tiền giảm giá không được để trống";
    public static final String DISCOUNT_AMOUNT_NEGATIVE = "Số tiền giảm giá không được âm";
    public static final String MIN_PURCHASE_NEGATIVE = "Số tiền mua tối thiểu không được âm";
    public static final String DISCOUNT_PERCENTAGE_INVALID = "Phần trăm giảm giá phải nằm trong khoảng 0-100%";
    public static final String START_DATE_NULL = "Ngày bắt đầu không được để trống";
    public static final String END_DATE_NULL = "Ngày kết thúc không được để trống";
    public static final String START_DATE_AFTER_END_DATE = "Ngày bắt đầu không thể sau ngày kết thúc";
    public static final String END_DATE_BEFORE_START_DATE = "Ngày kết thúc không thể trước ngày bắt đầu";
    public static final String USAGE_LIMIT_NEGATIVE = "Giới hạn sử dụng không được âm";
    public static final String USAGE_COUNT_NEGATIVE = "Số lần sử dụng không được âm";
    public static final String CATEGORY_NULL = "Danh mục không được để trống";
    public static final String DISCOUNT_INVALID = "Khuyến mãi không hợp lệ hoặc đã hết hiệu lực";
    public static final String USAGE_LIMIT_EXCEEDED = "Đã vượt quá giới hạn sử dụng";
    
    // Discount specific messages
    public static final String DISCOUNT_PERCENTAGE_RANGE = "Phần trăm giảm giá phải nằm trong khoảng 0-100%%";
    public static final String DISCOUNT_START_DATE_AFTER_END_DATE = "Ngày bắt đầu không thể sau ngày kết thúc";
    public static final String DISCOUNT_END_DATE_BEFORE_START_DATE = "Ngày kết thúc không thể trước ngày bắt đầu";
    public static final String DISCOUNT_USAGE_LIMIT_EXCEEDED = "Đã vượt quá giới hạn sử dụng";

    // Product error messages
    public static final String PRODUCT_NAME_EMPTY = "Tên sản phẩm không được để trống";
    public static final String PRODUCT_CODE_EMPTY = "Mã sản phẩm không được để trống";
    public static final String PRODUCT_PRICE_NEGATIVE = "Giá sản phẩm không được âm";
    public static final String PRODUCT_CATEGORY_NULL = "Danh mục sản phẩm không được để trống";
    public static final String PRODUCT_QUANTITY_NEGATIVE = "Số lượng sản phẩm không được âm";
    public static final String PRODUCT_DESCRIPTION_EMPTY = "Mô tả sản phẩm không được để trống";
    public static final String PRODUCT_SUPPLIER_NULL = "Nhà cung cấp không được để trống";
    public static final String PRODUCT_WARRANTY_NULL = "Thông tin bảo hành không được để trống";
    public static final String PRODUCT_INSUFFICIENT_STOCK = "Số lượng tồn kho không đủ (%d < %d)";
    public static final String PRODUCT_QUANTITY_NOT_POSITIVE = "Số lượng sản phẩm phải lớn hơn 0";

    
    // Customer error messages
    public static final String CUSTOMER_NAME_EMPTY = "Tên khách hàng không được để trống";
    public static final String CUSTOMER_PHONE_EMPTY = "Số điện thoại khách hàng không được để trống";
    public static final String CUSTOMER_PHONE_INVALID = "Số điện thoại không hợp lệ";
    public static final String CUSTOMER_EMAIL_INVALID = "Email không hợp lệ";
    
    // Employee error messages
    public static final String EMPLOYEE_NAME_EMPTY = "Tên nhân viên không được để trống";
    public static final String EMPLOYEE_PHONE_EMPTY = "Số điện thoại nhân viên không được để trống";
    public static final String EMPLOYEE_PHONE_INVALID = "Số điện thoại không hợp lệ";
    public static final String EMPLOYEE_EMAIL_INVALID = "Email không hợp lệ";
    public static final String EMPLOYEE_POSITION_EMPTY = "Chức vụ không được để trống";
    
    // Invoice error messages
    public static final String INVOICE_CUSTOMER_NULL = "Khách hàng không được để trống";
    public static final String INVOICE_EMPLOYEE_NULL = "Nhân viên không được để trống";
    public static final String INVOICE_TOTAL_NEGATIVE = "Tổng tiền hóa đơn không được âm";
    public static final String INVOICE_DETAILS_EMPTY = "Hóa đơn phải có ít nhất một chi tiết";
    // public static final String INVOICE_DETAILS_EMPTY = "Hóa đơn phải có ít nhất một sản phẩm";
    
    // Invoice Detail error messages
    public static final String INVOICE_DETAIL_PRODUCT_NULL = "Sản phẩm không được để trống";
    public static final String INVOICE_DETAIL_QUANTITY_NEGATIVE = "Số lượng không được âm hoặc bằng 0";
    public static final String INVOICE_DETAIL_PRICE_NEGATIVE = "Đơn giá không được âm hoặc bằng 0";
    
    // Supplier error messages
    public static final String SUPPLIER_NAME_EMPTY = "Tên nhà cung cấp không được để trống";
    public static final String SUPPLIER_PHONE_EMPTY = "Số điện thoại nhà cung cấp không được để trống";
    public static final String SUPPLIER_PHONE_INVALID = "Số điện thoại không hợp lệ";
    public static final String SUPPLIER_EMAIL_INVALID = "Email không hợp lệ";
    
    // Purchase Order error messages
    public static final String PURCHASE_ORDER_SUPPLIER_NULL = "Nhà cung cấp không được để trống";
    public static final String PURCHASE_ORDER_EMPLOYEE_NULL = "Nhân viên không được để trống";
    public static final String PURCHASE_ORDER_TOTAL_NEGATIVE = "Tổng tiền đơn hàng không được âm";
    public static final String PURCHASE_ORDER_DETAILS_EMPTY = "Đơn đặt hàng phải có ít nhất một chi tiết";
    // public static final String PURCHASE_ORDER_DETAILS_EMPTY = "Đơn đặt hàng phải có ít nhất một sản phẩm";
    
    // Return error messages
    public static final String RETURN_INVOICE_NULL = "Hóa đơn trả hàng không được để trống";
    public static final String RETURN_REASON_EMPTY = "Lý do trả hàng không được để trống";
    public static final String RETURN_DATE_INVALID = "Ngày trả hàng không hợp lệ";
    
    // RepairService error messages
    public static final String REPAIR_CUSTOMER_NULL = "Khách hàng không được để trống";
    public static final String REPAIR_PRODUCT_NULL = "Sản phẩm cần sửa chữa không được để trống";
    public static final String REPAIR_DESCRIPTION_EMPTY = "Mô tả lỗi không được để trống";
    public static final String REPAIR_STATUS_INVALID = "Trạng thái sửa chữa không hợp lệ";
    public static final String REPAIR_COST_NEGATIVE = "Chi phí sửa chữa không được âm";

    //Validation messages
    public static final String INVALID_PHONE_NUMBER = "Số điện thoại không hợp lệ";
    public static final String INVALID_EMAIL = "Email không hợp lệ";
}