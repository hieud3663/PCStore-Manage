package com.pcstore.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Enum chứa các thông báo lỗi được sử dụng trong toàn bộ ứng dụng
 * Sử dụng caching để tối ưu performance khi đổi ngôn ngữ
 */
public enum ErrorMessage {
    
    // Common validation messages
    FIELD_EMPTY("FIELD_EMPTY"),                                    //"Phần tử không được để trống"
    FIELD_NEGATIVE("FIELD_NEGATIVE"),                              //"Phần tử không được âm"
    INVALID_VALUE("INVALID_VALUE"),                                //"Giá trị không hợp lệ. Vui lòng nhập lại."
    VALUE_MUST_BE_POSITIVE("VALUE_MUST_BE_POSITIVE"),              //"Giá trị phải là lớn hơn 0"
    ERROR_TITLE("ERROR_TITLE"),                                    //"Lỗi"
    
    // User messages
    USERNAME_EMPTY("USERNAME_EMPTY"),                              //"Tên đăng nhập không được để trống"
    USERNAME_TOO_SHORT("USERNAME_TOO_SHORT"),                      //"Tên đăng nhập phải có ít nhất 3 ký tự"
    PASSWORD_EMPTY("PASSWORD_EMPTY"),                              //"Mật khẩu không được để trống"
    PASSWORD_TOO_SHORT("PASSWORD_TOO_SHORT"),                      //"Mật khẩu phải có ít nhất 6 ký tự"
    ROLE_EMPTY("ROLE_EMPTY"),                                      //"Vai trò không được để trống"
    USERNAME_EXISTS("USERNAME_EXISTS"),                            //"Tên đăng nhập %s đã tồn tại"
    USER_NOT_EXISTS("USER_NOT_EXISTS"),                            //"Người dùng với tên đăng nhập %s không tồn tại"
    USERNAME_EXISTS_EN("USERNAME_EXISTS_EN"),                      //"Username %s existed"
    
    // Employee messages
    EMPLOYEE_ID_FORMAT("EMPLOYEE_ID_FORMAT"),                      //"Mã nhân viên phải có định dạng NVxx"
    EMPLOYEE_NAME_TOO_SHORT("EMPLOYEE_NAME_TOO_SHORT"),            //"Tên nhân viên phải có ít nhất 2 ký tự"
    INVALID_EMPLOYEE_POSITION("INVALID_EMPLOYEE_POSITION"),        //"Chức vụ không hợp lệ"
    EMPLOYEE_AGE_18("EMPLOYEE_AGE_18"),                            //"Nhân viên phải từ 18 tuổi trở lên"
    EMPLOYEE_AGE_70("EMPLOYEE_AGE_70"),                            //"Nhân viên phải nhỏ hơn 70 tuổi"

    // Employee controller specific messages
    EMPLOYEE_LOAD_ERROR("EMPLOYEE_LOAD_ERROR"),                    //"Lỗi khi tải danh sách nhân viên: %s"
    EMPLOYEE_FILTER_ERROR("EMPLOYEE_FILTER_ERROR"),                //"Lỗi khi lọc danh sách nhân viên: %s"
    EMPLOYEE_GET_INFO_ERROR("EMPLOYEE_GET_INFO_ERROR"),            //"Lỗi khi lấy thông tin nhân viên: %s"
    EMPLOYEE_SELECTION_REQUIRED("EMPLOYEE_SELECTION_REQUIRED"),    //"Vui lòng chọn một nhân viên"
    INIT_CONTROLLER_ERROR("INIT_CONTROLLER_ERROR"),                //"Lỗi khởi tạo controller: "
    LOAD_EMPLOYEES_ERROR("LOAD_EMPLOYEES_ERROR"),                  //"Lỗi khi tải danh sách nhân viên: "
    LOAD_EMPLOYEE_DETAILS_ERROR("LOAD_EMPLOYEE_DETAILS_ERROR"),    //"Lỗi khi tải thông tin nhân viên: "
    INVALID_DATE_FORMAT("INVALID_DATE_FORMAT"),                    //"Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng dd-MM-yyyy"
    EMPTY_FIELDS_ERROR("EMPTY_FIELDS_ERROR"),                      //"Các trường không được để trống"
    INVALID_POSITION_ERROR("INVALID_POSITION_ERROR"),              //"Chức vụ không hợp lệ"
    DUPLICATE_EMPLOYEE_ID_ERROR("DUPLICATE_EMPLOYEE_ID_ERROR"),    //"Mã nhân viên này đã tồn tại"
    CONFIRM_ADD_EMPLOYEE("CONFIRM_ADD_EMPLOYEE"),                  //"Bạn có chắc chắn muốn thêm nhân viên mới?"
    ADD_EMPLOYEE_SUCCESS("ADD_EMPLOYEE_SUCCESS"),                  //"Thêm nhân viên mới thành công"
    SELECT_EMPLOYEE_TO_UPDATE("SELECT_EMPLOYEE_TO_UPDATE"),        //"Vui lòng chọn nhân viên cần cập nhật"
    CONFIRM_UPDATE_EMPLOYEE("CONFIRM_UPDATE_EMPLOYEE"),            //"Bạn có chắc chắn muốn cập nhật thông tin nhân viên?"
    UPDATE_EMPLOYEE_SUCCESS("UPDATE_EMPLOYEE_SUCCESS"),            //"Cập nhật thông tin nhân viên thành công"
    UPDATE_EMPLOYEE_ERROR("UPDATE_EMPLOYEE_ERROR"),                //"Lỗi khi cập nhật nhân viên: "
    SELECT_EMPLOYEE_TO_DELETE("SELECT_EMPLOYEE_TO_DELETE"),        //"Vui lòng chọn nhân viên cần xóa"
    CONFIRM_DELETE_EMPLOYEE("CONFIRM_DELETE_EMPLOYEE"),            //"Bạn có chắc chắn muốn xóa nhân viên "
    DELETE_EMPLOYEE_SUCCESS("DELETE_EMPLOYEE_SUCCESS"),            //"Xóa nhân viên thành công"
    DELETE_EMPLOYEE_ERROR("DELETE_EMPLOYEE_ERROR"),                //"Lỗi khi xóa nhân viên: "
    SEARCH_EMPLOYEE_ERROR("SEARCH_EMPLOYEE_ERROR"),                //"Lỗi khi tìm kiếm nhân viên: "
    NO_DATA_TO_EXPORT("NO_DATA_TO_EXPORT"),                        //"Không có dữ liệu để xuất"
    EXPORT_EXCEL_SUCCESS("EXPORT_EXCEL_SUCCESS"),                  //"Xuất Excel thành công!"
    EXPORT_EXCEL_FAILURE("EXPORT_EXCEL_FAILURE"),                  //"Xuất Excel không thành công!"
    EXPORT_EXCEL_ERROR("EXPORT_EXCEL_ERROR"),                      //"Lỗi khi xuất Excel: "
    CONFIRM_CANCEL_ADD_EMPLOYEE("CONFIRM_CANCEL_ADD_EMPLOYEE"),    //"Bạn có muốn hủy thao tác thêm nhân viên mới không?"
    INVALID_IMAGE_FILE("INVALID_IMAGE_FILE"),                      //"Tệp hình ảnh không hợp lệ"
    PROCESS_IMAGE_ERROR("PROCESS_IMAGE_ERROR"),                    //"Lỗi khi xử lý hình ảnh: "
    DISPLAY_AVATAR_ERROR("DISPLAY_AVATAR_ERROR"),                  //"Lỗi khi hiển thị ảnh đại diện: "
    DISPLAY_DEFAULT_AVATAR_ERROR("DISPLAY_DEFAULT_AVATAR_ERROR"),  //"Lỗi khi hiển thị ảnh đại diện mặc định: "
    CURRENT_USER_EMPLOYEE_NOT_FOUND("CURRENT_USER_EMPLOYEE_NOT_FOUND"), //"Không tìm thấy thông tin nhân viên của người dùng hiện tại"
    LOAD_CURRENT_USER_EMPLOYEE_ERROR("LOAD_CURRENT_USER_EMPLOYEE_ERROR"), //"Lỗi khi tải thông tin nhân viên của người dùng hiện tại: "
    CONFIRM_CONTINUE_ADD_EMPLOYEE("CONFIRM_CONTINUE_ADD_EMPLOYEE"), //"Bạn đang thêm nhân viên mới. Bạn có muốn tiếp tục thêm không?"
    CONFIRM_TITLE("CONFIRM_TITLE"),                                //"Xác nhận"

    // Customer messages
    CUSTOMER_NAME_TOO_SHORT("CUSTOMER_NAME_TOO_SHORT"),            //"Tên khách hàng phải có ít nhất 2 ký tự"
    CUSTOMER_ID_FORMAT("CUSTOMER_ID_FORMAT"),                      //"Mã khách hàng phải có định dạng KHxx"
    
    // Category error messages
    CATEGORY_NAME_EMPTY("CATEGORY_NAME_EMPTY"),                    //"Tên danh mục không được để trống"
    CATEGORY_SELF_REFERENCE("CATEGORY_SELF_REFERENCE"),            //"Danh mục không thể là danh mục cha của chính nó"
    CATEGORY_CIRCULAR_REFERENCE("CATEGORY_CIRCULAR_REFERENCE"),    //"Không thể tạo vòng lặp trong cấu trúc danh mục"
    SUBCATEGORY_NULL("SUBCATEGORY_NULL"),                          //"Danh mục con không được để trống"
    PRODUCT_NULL("PRODUCT_NULL"),                                  //"Sản phẩm không được để trống"
    PARENT_CATEGORY_NULL("PARENT_CATEGORY_NULL"),                  //"Danh mục cha không được để trống"
    
    // Discount error messages
    DISCOUNT_CODE_EMPTY("DISCOUNT_CODE_EMPTY"),                    //"Mã khuyến mãi không được để trống"
    DISCOUNT_AMOUNT_NULL("DISCOUNT_AMOUNT_NULL"),                  //"Số tiền giảm giá không được để trống"
    DISCOUNT_AMOUNT_NEGATIVE("DISCOUNT_AMOUNT_NEGATIVE"),          //"Số tiền giảm giá không được âm"
    MIN_PURCHASE_NEGATIVE("MIN_PURCHASE_NEGATIVE"),                //"Số tiền mua tối thiểu không được âm"
    DISCOUNT_PERCENTAGE_INVALID("DISCOUNT_PERCENTAGE_INVALID"),    //"Phần trăm giảm giá phải nằm trong khoảng 0-100%"
    START_DATE_NULL("START_DATE_NULL"),                            //"Ngày bắt đầu không được để trống"
    END_DATE_NULL("END_DATE_NULL"),                                //"Ngày kết thúc không được để trống"
    START_DATE_AFTER_END_DATE("START_DATE_AFTER_END_DATE"),        //"Ngày bắt đầu không thể sau ngày kết thúc"
    END_DATE_BEFORE_START_DATE("END_DATE_BEFORE_START_DATE"),      //"Ngày kết thúc không thể trước ngày bắt đầu"
    USAGE_LIMIT_NEGATIVE("USAGE_LIMIT_NEGATIVE"),                  //"Giới hạn sử dụng không được âm"
    USAGE_COUNT_NEGATIVE("USAGE_COUNT_NEGATIVE"),                  //"Số lần sử dụng không được âm"
    CATEGORY_NULL("CATEGORY_NULL"),                                //"Danh mục không được để trống"
    DISCOUNT_INVALID("DISCOUNT_INVALID"),                          //"Khuyến mãi không hợp lệ"
    USAGE_LIMIT_EXCEEDED("USAGE_LIMIT_EXCEEDED"),                  //"Đã vượt quá giới hạn sử dụng"
    
    // Discount specific messages
    DISCOUNT_PERCENTAGE_RANGE("DISCOUNT_PERCENTAGE_RANGE"),        //"Phần trăm giảm giá phải nằm trong khoảng 0-100%%"
    DISCOUNT_START_DATE_AFTER_END_DATE("DISCOUNT_START_DATE_AFTER_END_DATE"), //"Ngày bắt đầu không thể sau ngày kết thúc"
    DISCOUNT_END_DATE_BEFORE_START_DATE("DISCOUNT_END_DATE_BEFORE_START_DATE"), //"Ngày kết thúc không thể trước ngày bắt đầu"
    DISCOUNT_USAGE_LIMIT_EXCEEDED("DISCOUNT_USAGE_LIMIT_EXCEEDED"), //"Đã vượt quá giới hạn sử dụng"
    
    // Product error messages
    PRODUCT_NAME_EMPTY("PRODUCT_NAME_EMPTY"),                      //"Tên sản phẩm không được để trống"
    PRODUCT_CODE_EMPTY("PRODUCT_CODE_EMPTY"),                      //"Mã sản phẩm không được để trống"
    PRODUCT_PRICE_NEGATIVE("PRODUCT_PRICE_NEGATIVE"),              //"Giá sản phẩm không được âm"
    PRODUCT_CATEGORY_NULL("PRODUCT_CATEGORY_NULL"),                //"Danh mục sản phẩm không được để trống"
    PRODUCT_QUANTITY_NEGATIVE("PRODUCT_QUANTITY_NEGATIVE"),        //"Số lượng sản phẩm không được âm"
    PRODUCT_DESCRIPTION_EMPTY("PRODUCT_DESCRIPTION_EMPTY"),        //"Mô tả sản phẩm không được để trống"
    PRODUCT_SUPPLIER_NULL("PRODUCT_SUPPLIER_NULL"),                //"Nhà cung cấp không được để trống"
    PRODUCT_WARRANTY_NULL("PRODUCT_WARRANTY_NULL"),                //"Thông tin bảo hành không được để trống"
    PRODUCT_INSUFFICIENT_STOCK("PRODUCT_INSUFFICIENT_STOCK"),      //"Số lượng tồn kho không đủ (%d < %d)"
    PRODUCT_QUANTITY_NOT_POSITIVE("PRODUCT_QUANTITY_NOT_POSITIVE"), //"Số lượng sản phẩm phải lớn hơn 0"
    ENTER_PRODUCT_QUANTITY("ENTER_PRODUCT_QUANTITY"),              //"Nhập số lượng sản phẩm:"

    // Customer error messages
    CUSTOMER_NAME_EMPTY("CUSTOMER_NAME_EMPTY"),                    //"Tên khách hàng không được để trống"
    CUSTOMER_PHONE_EMPTY("CUSTOMER_PHONE_EMPTY"),                  //"Số điện thoại khách hàng không được để trống"
    CUSTOMER_PHONE_INVALID("CUSTOMER_PHONE_INVALID"),              //"Số điện thoại không hợp lệ"
    CUSTOMER_EMAIL_INVALID("CUSTOMER_EMAIL_INVALID"),              //"Email không hợp lệ"
    
    // Employee error messages
    EMPLOYEE_NAME_EMPTY("EMPLOYEE_NAME_EMPTY"),                    //"Tên nhân viên không được để trống"
    EMPLOYEE_PHONE_EMPTY("EMPLOYEE_PHONE_EMPTY"),                  //"Số điện thoại nhân viên không được để trống"
    EMPLOYEE_PHONE_INVALID("EMPLOYEE_PHONE_INVALID"),              //"Số điện thoại không hợp lệ"
    EMPLOYEE_EMAIL_INVALID("EMPLOYEE_EMAIL_INVALID"),              //"Email không hợp lệ"
    EMPLOYEE_POSITION_EMPTY("EMPLOYEE_POSITION_EMPTY"),            //"Chức vụ không được để trống"
    
    // Invoice error messages
    INVOICE_CUSTOMER_NULL("INVOICE_CUSTOMER_NULL"),                //"Khách hàng không được để trống"
    INVOICE_EMPLOYEE_NULL("INVOICE_EMPLOYEE_NULL"),                //"Nhân viên không được để trống"
    INVOICE_TOTAL_NEGATIVE("INVOICE_TOTAL_NEGATIVE"),              //"Tổng tiền hóa đơn không được âm"
    INVOICE_DETAILS_EMPTY("INVOICE_DETAILS_EMPTY"),                //"Hóa đơn phải có ít nhất một chi tiết"
    
    // Invoice Detail error messages
    INVOICE_DETAIL_PRODUCT_NULL("INVOICE_DETAIL_PRODUCT_NULL"),    //"Sản phẩm không được để trống"
    INVOICE_DETAIL_QUANTITY_NEGATIVE("INVOICE_DETAIL_QUANTITY_NEGATIVE"), //"Số lượng không được âm hoặc bằng 0"
    INVOICE_DETAIL_PRICE_NEGATIVE("INVOICE_DETAIL_PRICE_NEGATIVE"), //"Đơn giá không được âm hoặc bằng 0"
    
    // Invoice Detail controller specific messages
    INVOICE_DETAIL_CONTROLLER_INIT_ERROR("INVOICE_DETAIL_CONTROLLER_INIT_ERROR"), //"Lỗi khởi tạo controller: %s"
    INVOICE_DETAIL_ADD_ERROR("INVOICE_DETAIL_ADD_ERROR"),          //"Lỗi khi thêm chi tiết hóa đơn: %s"
    INVOICE_DETAIL_UPDATE_ERROR("INVOICE_DETAIL_UPDATE_ERROR"),    //"Lỗi khi cập nhật chi tiết hóa đơn: %s"
    INVOICE_DETAIL_DELETE_ERROR("INVOICE_DETAIL_DELETE_ERROR"),    //"Lỗi khi xóa chi tiết hóa đơn: %s"
    INVOICE_DETAIL_FIND_ERROR("INVOICE_DETAIL_FIND_ERROR"),        //"Lỗi khi tìm chi tiết hóa đơn: %s"
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND"),                        //"Không tìm thấy sản phẩm"
    
    // Invoice controller specific messages
    INVOICE_CONTROLLER_INIT_ERROR("INVOICE_CONTROLLER_INIT_ERROR"), //"Lỗi khởi tạo controller: %s"
    INVOICE_CREATE_ERROR("INVOICE_CREATE_ERROR"),                  //"Lỗi khi tạo hóa đơn: %s"
    INVOICE_LOAD_ERROR("INVOICE_LOAD_ERROR"),                      //"Lỗi khi tải danh sách hóa đơn: %s"
    INVOICE_DETAIL_LOAD_ERROR("INVOICE_DETAIL_LOAD_ERROR"),        //"Lỗi khi tải chi tiết hóa đơn: %s"
    INVOICE_ADD_PRODUCT_ERROR("INVOICE_ADD_PRODUCT_ERROR"),        //"Lỗi khi thêm sản phẩm vào hóa đơn: %s"
    INVOICE_UPDATE_QUANTITY_ERROR("INVOICE_UPDATE_QUANTITY_ERROR"), //"Lỗi khi cập nhật số lượng sản phẩm: %s"
    INVOICE_REMOVE_PRODUCT_ERROR("INVOICE_REMOVE_PRODUCT_ERROR"),  //"Lỗi khi xóa sản phẩm khỏi hóa đơn: %s"
    INVOICE_COMPLETE_ERROR("INVOICE_COMPLETE_ERROR"),              //"Lỗi khi hoàn thành hóa đơn: %s"
    INVOICE_CANCEL_ERROR("INVOICE_CANCEL_ERROR"),                  //"Lỗi khi hủy hóa đơn: %s"
    INVOICE_DELETE_ERROR("INVOICE_DELETE_ERROR"),                  //"Lỗi khi xóa hóa đơn: %s"
    INVOICE_PAYMENT_ERROR("INVOICE_PAYMENT_ERROR"),                //"Lỗi khi xử lý thanh toán: %s"
    INVOICE_STATUS_UPDATE_ERROR("INVOICE_STATUS_UPDATE_ERROR"),    //"Lỗi khi cập nhật trạng thái hóa đơn: %s"
    INVOICE_PRINT_ERROR("INVOICE_PRINT_ERROR"),                    //"Lỗi khi in hóa đơn: %s"
    INVOICE_EXPORT_EXCEL_ERROR("INVOICE_EXPORT_EXCEL_ERROR"),      //"Lỗi khi xuất Excel: %s"
    INVOICE_SELECT_TO_PRINT("INVOICE_SELECT_TO_PRINT"),            //"Vui lòng chọn hóa đơn cần in!"
    INVOICE_SELECT_TO_PAY("INVOICE_SELECT_TO_PAY"),                //"Vui lòng chọn hóa đơn cần thanh toán!"
    INVOICE_ALREADY_PAID("INVOICE_ALREADY_PAID"),                  //"Hóa đơn đã được thanh toán!"
    INVOICE_NOT_FOUND("INVOICE_NOT_FOUND"),                        //"Không tìm thấy hóa đơn!"
    INVOICE_SELECT_TO_DELETE("INVOICE_SELECT_TO_DELETE"),          //"Vui lòng tích chọn ít nhất một hóa đơn để xóa!"
    INVOICE_DELETE_CONFIRM("INVOICE_DELETE_CONFIRM"),              //"Bạn có chắc chắn muốn xóa %d hóa đơn đã chọn?"
    INVOICE_DELETE_RESULT("INVOICE_DELETE_RESULT"),                //"Đã xóa thành công %d hóa đơn.\n"
    INVOICE_DELETE_FAILED("INVOICE_DELETE_FAILED"),                //"Không thể xóa %d hóa đơn.\n\n"
    INVOICE_DELETE_DETAILS("INVOICE_DELETE_DETAILS"),              //"Chi tiết lỗi:\n"
    INVOICE_PAID_OR_DELIVERED("INVOICE_PAID_OR_DELIVERED"),        //"Hóa đơn #%d: Không thể xóa hóa đơn đã thanh toán hoặc đã giao hàng!"
    INVOICE_DELETE_FAIL_DETAIL("INVOICE_DELETE_FAIL_DETAIL"),      //"Hóa đơn #%d: Xóa thất bại!"
    INVOICE_NOT_EXISTS("INVOICE_NOT_EXISTS"),                      //"Hóa đơn không tồn tại"
    INVOICE_NO_PRODUCTS("INVOICE_NO_PRODUCTS"),                    //"Hóa đơn không có sản phẩm nào"
    INVOICE_CANNOT_CANCEL("INVOICE_CANNOT_CANCEL"),                //"Không thể hủy hóa đơn ở trạng thái %s"
    INVOICE_PRINT_INCOMPLETE("INVOICE_PRINT_INCOMPLETE"),          //"Hóa đơn chưa hoàn thành thanh toán!"
    INVOICE_PRINT_CONFIRM("INVOICE_PRINT_CONFIRM"),                //"Bạn có muốn in hóa đơn này không?"
    INVOICE_PRINT_SUCCESS("INVOICE_PRINT_SUCCESS"),                //"In hóa đơn thành công!"
    INVOICE_PRINT_FAILED("INVOICE_PRINT_FAILED"),                  //"In hóa đơn thất bại!"
    INVOICE_PAYMENT_SUCCESS("INVOICE_PAYMENT_SUCCESS"),            //"Thanh toán hóa đơn thành công!"
    INVOICE_EXPORT_NO_DATA("INVOICE_EXPORT_NO_DATA"),              //"Không có dữ liệu hóa đơn để xuất"
    INVOICE_EXPORT_SUCCESS("INVOICE_EXPORT_SUCCESS"),              //"Xuất Excel thành công!"
    INVOICE_EXPORT_FAILED("INVOICE_EXPORT_FAILED"),                //"Xuất Excel không thành công!"
    PRODUCT_INSUFFICIENT_QUANTITY("PRODUCT_INSUFFICIENT_QUANTITY"), //"Số lượng sản phẩm không đủ. Hiện chỉ còn %d"
    CANNOT_DELETE_INVOICE("CANNOT_DELETE_INVOICE"),                //"Không thể xóa hóa đơn đã thanh toán hoặc đã giao"
    
    // Supplier error messages
    SUPPLIER_NAME_EMPTY("SUPPLIER_NAME_EMPTY"),                    //"Tên nhà cung cấp không được để trống"
    SUPPLIER_PHONE_EMPTY("SUPPLIER_PHONE_EMPTY"),                  //"Số điện thoại nhà cung cấp không được để trống"
    SUPPLIER_PHONE_INVALID("SUPPLIER_PHONE_INVALID"),              //"Số điện thoại không hợp lệ"
    SUPPLIER_EMAIL_INVALID("SUPPLIER_EMAIL_INVALID"),              //"Email không hợp lệ"
    
    // Purchase Order error messages
    PURCHASE_ORDER_SUPPLIER_NULL("PURCHASE_ORDER_SUPPLIER_NULL"),  //"Nhà cung cấp không được để trống"
    PURCHASE_ORDER_EMPLOYEE_NULL("PURCHASE_ORDER_EMPLOYEE_NULL"),  //"Nhân viên không được để trống"
    PURCHASE_ORDER_TOTAL_NEGATIVE("PURCHASE_ORDER_TOTAL_NEGATIVE"), //"Tổng tiền đơn hàng không được âm"
    PURCHASE_ORDER_DETAILS_EMPTY("PURCHASE_ORDER_DETAILS_EMPTY"),  //"Đơn đặt hàng phải có ít nhất một chi tiết"

    // Return error messages
    public static final String RETURN_NOT_FOUND = "Đơn trả hàng không tồn tại";
    public static final String RETURN_NOT_FOUND_WITH_ID = "Không tìm thấy thông tin đơn trả hàng với ID: %s";
    public static final String RETURN_NOT_FOUND_WITH_KEYWORD = "Không tìm thấy đơn trả hàng nào phù hợp với từ khóa: %s";
    public static final String RETURN_CANNOT_UPDATE = "Không thể cập nhật đơn trả hàng ở trạng thái hiện tại";
    public static final String RETURN_QUANTITY_EXCEED = "Số lượng trả không thể lớn hơn số lượng trong hóa đơn";
    public static final String RETURN_UPDATE_ERROR = "Lỗi khi cập nhật đơn trả hàng";
    public static final String RETURN_CANNOT_EXCHANGE = "Không thể đổi sản phẩm với đơn trả hàng ở trạng thái hiện tại";
    public static final String PRODUCT_NOT_FOUND = "Sản phẩm mới không tồn tại";
    public static final String RETURN_EXCHANGE_ERROR = "Lỗi khi đổi sản phẩm";
    public static final String RETURN_STATUS_INVALID = "Không thể chuyển từ trạng thái 'Đã từ chối' về 'Đang chờ xử lý'";
    public static final String RETURN_LOAD_ERROR = "Lỗi khi tải dữ liệu đơn trả hàng";
    public static final String RETURN_SEARCH_ERROR = "Lỗi khi tìm kiếm đơn trả hàng";
    public static final String RETURN_DETAIL_LOAD_ERROR = "Lỗi khi tải chi tiết đơn trả hàng";
    public static final String RETURN_SELECT_ONE = "Vui lòng chọn một đơn trả hàng để thực hiện thao tác";
    public static final String RETURN_DELETE_ONLY_PENDING = "Chỉ có thể xóa đơn trả hàng ở trạng thái 'Đang chờ xử lý'.";
    public static final String RETURN_CURRENT_STATUS = "Đơn trả hàng này đang ở trạng thái: ";
    public static final String RETURN_DELETE_CONFIRM = "Bạn có chắc chắn muốn xóa đơn trả hàng này?\n- Mã đơn: %s\n- Sản phẩm: %s";
    public static final String RETURN_DELETE_SUCCESS = "Đã xóa đơn trả hàng thành công!";
    public static final String RETURN_DELETE_FAIL = "Không thể xóa đơn trả hàng. Vui lòng thử lại sau!";
    public static final String RETURN_DELETE_ERROR = "Lỗi khi xóa đơn trả hàng";
    public static final String RETURN_STATUS_CONVERT_ERROR = "Lỗi chuyển đổi trạng thái";
    public static final String RETURN_STATUS_UPDATE_SUCCESS = "Cập nhật trạng thái thành công!";
    public static final String RETURN_STATUS_UPDATE_FAIL = "Không thể cập nhật trạng thái. Vui lòng thử lại sau!";
    public static final String RETURN_STATUS_UPDATE_ERROR = "Lỗi khi cập nhật trạng thái";
    public static final String DB_CONNECTION_ERROR = "Không thể kết nối đến cơ sở dữ liệu";

    // Invoice
    public static final String INVOICE_SEARCH_ERROR = "Lỗi khi tìm kiếm hóa đơn";
    public static final String INVOICE_LIST_EMPTY = "Không tìm thấy hóa đơn nào trong hệ thống.";
    public static final String INVOICE_LOAD_ERROR = "Lỗi khi tải dữ liệu hóa đơn";

    // Return
//    public static final String RETURN_SELECT_ONE = "Vui lòng chọn sản phẩm để trả hàng";
    public static final String RETURN_NO_QUANTITY = "Sản phẩm này đã trả hết";
    public static final String RETURN_OVER_30_DAYS = "Sản phẩm này đã quá 30 ngày kể từ ngày mua (%d ngày).\nViệc trả hàng có thể bị từ chối hoặc áp dụng điều kiện đặc biệt.\nBạn vẫn muốn tiếp tục?";
    public static final String RETURN_INPUT_REASON = "Nhập lý do trả hàng:";
    public static final String RETURN_REASON_EMPTY = "Vui lòng nhập lý do trả hàng";
    public static final String RETURN_CREATE_SUCCESS = "Đã tạo đơn trả hàng thành công!";
    public static final String RETURN_CREATE_FAIL = "Không thể tạo đơn trả hàng";
    public static final String RETURN_CREATE_ERROR = "Lỗi khi tạo đơn trả hàng";

    // RepairService error messages
    public static final String REPAIR_CUSTOMER_NULL = "Khách hàng không được để trống";
    public static final String REPAIR_PRODUCT_NULL = "Sản phẩm cần sửa chữa không được để trống";
    public static final String REPAIR_DESCRIPTION_EMPTY = "Mô tả lỗi không được để trống";
    public static final String REPAIR_STATUS_INVALID = "Trạng thái sửa chữa không hợp lệ";
    public static final String REPAIR_COST_NEGATIVE = "Chi phí sửa chữa không được âm";

    // Repair error messages
    public static final String REPAIR_NOT_FOUND = "Dịch vụ sửa chữa không tồn tại";
    public static final String REPAIR_NOT_FOUND_WITH_ID = "Không tìm thấy dịch vụ sửa chữa với ID: %s";
    public static final String REPAIR_ID_INVALID = "ID dịch vụ sửa chữa không hợp lệ";
    public static final String REPAIR_CONTROLLER_NOT_SET = "Controller chưa được thiết lập. Thao tác không thể thực hiện.";
    public static final String REPAIR_FORM_INIT_ERROR = "Lỗi khi khởi tạo form dịch vụ sửa chữa";
    public static final String REPAIR_FORM_ADD_ERROR = "Lỗi khi mở form thêm mới dịch vụ sửa chữa";
    public static final String REPAIR_FORM_DETAIL_ERROR = "Lỗi khi hiển thị chi tiết dịch vụ sửa chữa";
    public static final String REPAIR_SELECT_ONE = "Vui lòng chọn một dịch vụ sửa chữa để xem chi tiết.";
    public static final String REPAIR_SELECT_ONE_DELETE = "Vui lòng chọn một dịch vụ sửa chữa để xóa.";
    public static final String REPAIR_SELECT_ONE_UPDATE_STATUS = "Vui lòng chọn một dịch vụ sửa chữa để cập nhật trạng thái";
    public static final String REPAIR_DELETE_CONFIRM = "Bạn có chắc chắn muốn xóa dịch vụ sửa chữa này không?";
    public static final String REPAIR_DELETE_SUCCESS = "Đã xóa dịch vụ sửa chữa thành công!";
    public static final String REPAIR_DELETE_FAIL = "Không thể xóa dịch vụ sửa chữa. Vui lòng thử lại sau.";
    public static final String REPAIR_DELETE_ERROR = "Lỗi khi xóa dịch vụ sửa chữa";
    public static final String REPAIR_STATUS_CONVERT_ERROR = "Lỗi chuyển đổi trạng thái";
    public static final String REPAIR_STATUS_UPDATE_SUCCESS = "Cập nhật trạng thái thành công!";
    public static final String REPAIR_STATUS_UPDATE_FAIL = "Không thể cập nhật trạng thái. Vui lòng thử lại sau!";
    public static final String REPAIR_STATUS_UPDATE_ERROR = "Lỗi khi cập nhật trạng thái";
    public static final String REPAIR_CREATE_ERROR = "Lỗi khi tạo dịch vụ sửa chữa";
    public static final String REPAIR_LIST_ERROR = "Lỗi khi lấy danh sách dịch vụ sửa chữa";
    public static final String REPAIR_FIND_ERROR = "Lỗi khi tìm dịch vụ sửa chữa";
    public static final String REPAIR_UPDATE_ERROR = "Lỗi khi cập nhật dịch vụ sửa chữa";
    public static final String REPAIR_ASSIGN_EMPLOYEE_ERROR = "Lỗi khi phân công nhân viên";
    public static final String REPAIR_UPDATE_FEE_ERROR = "Lỗi khi cập nhật phí dịch vụ";
    public static final String EMPLOYEE_NOT_FOUND = "Nhân viên không tồn tại";
    public static final String CUSTOMER_NOT_FOUND = "Khách hàng không tồn tại";

    //Validation messages
    public static final String INVALID_PHONE_NUMBER = "Số điện thoại không hợp lệ";
    public static final String INVALID_EMAIL = "Email không hợp lệ";

    // Warranty error messages
    public static final String WARRANTY_LOAD_ERROR = "Lỗi khi tải danh sách bảo hành";
    public static final String WARRANTY_SEARCH_ERROR = "Lỗi khi tìm kiếm bảo hành";
    public static final String WARRANTY_NOT_FOUND_WITH_ID = "Không tìm thấy bảo hành có mã %s";
    public static final String WARRANTY_DETAIL_ERROR = "Lỗi khi hiển thị chi tiết bảo hành";
    public static final String WARRANTY_CREATE_ERROR = "Lỗi khi tạo bảo hành";
    public static final String WARRANTY_ID_NULL = "Không thể xóa bảo hành với ID null";
    public static final String WARRANTY_DELETE_ERROR = "Lỗi khi xóa bảo hành";
    public static final String WARRANTY_FORM_INIT_ERROR = "Lỗi khởi tạo form bảo hành";
    public static final String WARRANTY_FORM_ADD_ERROR = "Lỗi khi mở form đăng ký bảo hành";
    public static final String WARRANTY_CONTROLLER_NOT_SET = "Controller chưa được khởi tạo.";
    public static final String WARRANTY_SELECT_ONE = "Vui lòng chọn sản phẩm để đăng ký bảo hành";
    public static final String WARRANTY_EXPIRED = "Sản phẩm này đã hết hạn bảo hành vào ngày %s.\nVui lòng liên hệ nhân viên để được hỗ trợ thêm!";
    public static final String WARRANTY_ALREADY_REGISTERED = "Sản phẩm này đã được đăng ký bảo hành trước đó!";
    public static final String WARRANTY_DETAIL_NOT_FOUND = "Không thể tìm thấy thông tin chi tiết của sản phẩm đã chọn";
    public static final String WARRANTY_REGISTER_SUCCESS = "Đã đăng ký bảo hành thành công với mã: %s";
    public static final String WARRANTY_REGISTER_ERROR = "Lỗi khi đăng ký bảo hành";
    public static final String WARRANTY_NOT_FOUND_BY_PHONE = "Không tìm thấy sản phẩm nào theo số điện thoại này";
    public static final String WARRANTY_SELECT_ONE_DELETE = "Vui lòng chọn một bảo hành để xóa.";
    public static final String WARRANTY_ID_INVALID = "Bảo hành không có ID hợp lệ.";
    public static final String WARRANTY_DELETE_CONFIRM = "Bạn có chắc chắn muốn xóa bảo hành này không?";
    public static final String WARRANTY_DELETE_SUCCESS = "Đã xóa bảo hành thành công!";
    public static final String WARRANTY_DELETE_FAIL = "Không thể xóa bảo hành. Vui lòng thử lại sau.";
    public static final String WARRANTY_SELECT_ONE_DETAIL = "Vui lòng chọn một bảo hành để xem chi tiết";
    public static final String INVOICE_DETAIL_NULL = "Chi tiết hóa đơn không được null";
    public static final String INVOICE_NULL = "Thông tin hóa đơn không được null";
}