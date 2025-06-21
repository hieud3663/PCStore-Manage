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
    RETURN_INVOICE_NULL("RETURN_INVOICE_NULL"),                    //"Hóa đơn trả hàng không được để trống"
    RETURN_REASON_EMPTY("RETURN_REASON_EMPTY"),                    //"Lý do trả hàng không được để trống"
    RETURN_DATE_INVALID("RETURN_DATE_INVALID"),                    //"Ngày trả hàng không hợp lệ"
    RETURN_APPROVE_PENDING_ONLY("RETURN_APPROVE_PENDING_ONLY"),    //"Chỉ có thể chấp nhận đơn trả hàng đang chờ xử lý"
    RETURN_REJECT_PENDING_ONLY("RETURN_REJECT_PENDING_ONLY"),      //"Chỉ có thể từ chối đơn trả hàng đang chờ xử lý"
    RETURN_COMPLETE_APPROVED_ONLY("RETURN_COMPLETE_APPROVED_ONLY"), //"Chỉ có thể hoàn thành đơn trả hàng đã được chấp nhận"
    RETURN_QUANTITY_POSITIVE("RETURN_QUANTITY_POSITIVE"),          //"Số lượng trả phải lớn hơn 0"
    RETURN_CANNOT_TRANSITION("RETURN_CANNOT_TRANSITION"),          //"Không thể chuyển sang trạng thái %s"
    
    // RepairService error messages
    REPAIR_CUSTOMER_NULL("REPAIR_CUSTOMER_NULL"),                  //"Khách hàng không được để trống"
    REPAIR_PRODUCT_NULL("REPAIR_PRODUCT_NULL"),                    //"Sản phẩm cần sửa chữa không được để trống"
    REPAIR_DESCRIPTION_EMPTY("REPAIR_DESCRIPTION_EMPTY"),          //"Mô tả lỗi không được để trống"
    REPAIR_STATUS_INVALID("REPAIR_STATUS_INVALID"),                //"Trạng thái sửa chữa không hợp lệ"
    REPAIR_COST_NEGATIVE("REPAIR_COST_NEGATIVE"),                  //"Chi phí sửa chữa không được âm"
    
    // Validation messages
    INVALID_PHONE_NUMBER("INVALID_PHONE_NUMBER"),                  //"Số điện thoại không hợp lệ"
    INVALID_EMAIL("INVALID_EMAIL"),                                //"Email không hợp lệ"
    
    // Login related errors
    LOGIN_ERROR("LOGIN_ERROR"),                                    //"Lỗi đăng nhập"
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED"),               //"Tên đăng nhập hoặc mật khẩu không đúng"
    LOGIN_SERVICE_ERROR("LOGIN_SERVICE_ERROR"),                    //"Lỗi khởi tạo dịch vụ đăng nhập: %s"
    USER_SERVICE_ERROR("USER_SERVICE_ERROR"),                      //"Lỗi dịch vụ người dùng: %s"
    
    // Forgot password related messages
    EMAIL_EMPTY("EMAIL_EMPTY"),                                    //"Vui lòng nhập địa chỉ email!"
    EMAIL_INVALID("EMAIL_INVALID"),                                //"Địa chỉ email không hợp lệ!"
    EMAIL_NOT_FOUND("EMAIL_NOT_FOUND"),                            //"Email không tồn tại trong hệ thống!"
    OTP_SENT_SUCCESS("OTP_SENT_SUCCESS"),                          //"Mã OTP đã được gửi đến email của bạn!"
    OTP_SENT_FAILED("OTP_SENT_FAILED"),                            //"Không thể gửi mã OTP. Vui lòng thử lại sau!"
    FIELDS_REQUIRED("FIELDS_REQUIRED"),                            //"Vui lòng nhập đầy đủ thông tin!"
    OTP_INVALID("OTP_INVALID"),                                    //"Mã OTP không chính xác!"
    PASSWORD_MISMATCH("PASSWORD_MISMATCH"),                        //"Mật khẩu xác nhận không khớp với mật khẩu mới!"
    PASSWORD_REQUIREMENT("PASSWORD_REQUIREMENT"),                  //"Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!"
    PASSWORD_RESET_SUCCESS("PASSWORD_RESET_SUCCESS"),              //"Đặt lại mật khẩu thành công!"
    PASSWORD_RESET_FAILED("PASSWORD_RESET_FAILED"),                //"Không thể cập nhật mật khẩu. Vui lòng thử lại sau!"
    DATABASE_CONNECTION_ERROR("DATABASE_CONNECTION_ERROR"),        //"Lỗi kết nối đến cơ sở dữ liệu: %s"
    SYSTEM_ERROR("SYSTEM_ERROR"),                                  //"Lỗi hệ thống: %s"
    
    // Database error messages
    DB_CONNECTION_ERROR("DB_CONNECTION_ERROR"),                    //"Không thể kết nối đến database"
    DB_DRIVER_ERROR("DB_DRIVER_ERROR"),                            //"Không tìm thấy JDBC driver"
    
    // Database specific errors
    DB_CONNECTION_INIT_ERROR("DB_CONNECTION_INIT_ERROR"),          //"Lỗi khởi tạo DatabaseConnection: %s"
    DB_CONNECTION_CHECK_ERROR("DB_CONNECTION_CHECK_ERROR"),        //"Lỗi khi kiểm tra kết nối: %s"
    DB_CONNECTION_RECREATE_ERROR("DB_CONNECTION_RECREATE_ERROR"),  //"Không thể tạo lại kết nối: %s"
    DB_CONNECTION_CLOSE_ERROR("DB_CONNECTION_CLOSE_ERROR"),        //"Lỗi khi đóng kết nối: %s"
    DB_CONNECTION_INVALID("DB_CONNECTION_INVALID"),                //"Kết nối không hợp lệ, tạo mới..."
    DB_CONNECTION_NULL_OR_CLOSED("DB_CONNECTION_NULL_OR_CLOSED"),  //"Kết nối null hoặc đã đóng, tạo mới..."
    DB_CONNECTION_SUCCESSFUL("DB_CONNECTION_SUCCESSFUL"),          //"Tạo kết nối database thành công"
    DB_CONNECTION_CLOSED("DB_CONNECTION_CLOSED"),                  //"Đã đóng kết nối database"
    
    // Encryption errors
    PASSWORD_HASH_ERROR("PASSWORD_HASH_ERROR"),                    //"Lỗi khi mã hóa mật khẩu: %s"
    PASSWORD_VERIFY_ERROR("PASSWORD_VERIFY_ERROR"),                //"Lỗi khi xác thực mật khẩu: %s"
    AUTHENTICATION_ERROR("AUTHENTICATION_ERROR"),                  //"Đã xảy ra lỗi khi xác thực người dùng: %s"
    
    // Dashboard related messages
    LOGOUT_CONFIRM("LOGOUT_CONFIRM"),                              //"Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?"
    LOGOUT_TITLE("LOGOUT_TITLE"),                                  //"Xác nhận đăng xuất"
    LOGIN_REQUIRED("LOGIN_REQUIRED"),                              //"Bạn cần đăng nhập để sử dụng hệ thống"
    LOGIN_REQUIRED_TITLE("LOGIN_REQUIRED_TITLE"),                  //"Yêu cầu đăng nhập"
    LOGIN_REDIRECT_ERROR("LOGIN_REDIRECT_ERROR"),                  //"Đã xảy ra lỗi khi mở trang đăng nhập: %s"
    
    // Language change messages
    LANGUAGE_CHANGE_CONFIRM_VI("LANGUAGE_CHANGE_CONFIRM_VI"),      //"Cần khởi động lại ứng dụng để áp dụng thay đổi ngôn ngữ. Bạn có muốn tiếp tục không?"
    LANGUAGE_CHANGE_CONFIRM_EN("LANGUAGE_CHANGE_CONFIRM_EN"),      //"The application needs to restart to apply language changes. Do you want to continue?"
    LANGUAGE_CHANGE_TITLE_VI("LANGUAGE_CHANGE_TITLE_VI"),          //"Thay đổi ngôn ngữ"
    LANGUAGE_CHANGE_TITLE_EN("LANGUAGE_CHANGE_TITLE_EN"),          //"Language Change"
    RESTART_ERROR_VI("RESTART_ERROR_VI"),                          //"Lỗi khởi động lại ứng dụng: "
    RESTART_ERROR_EN("RESTART_ERROR_EN"),                          //"Error restarting application:"
    
    // Customer controller specific messages
    CUSTOMER_CONTROLLER_INIT_ERROR("CUSTOMER_CONTROLLER_INIT_ERROR"), //"Lỗi khởi tạo controller: %s"
    CUSTOMER_LOAD_ERROR("CUSTOMER_LOAD_ERROR"),                    //"Lỗi khi tải danh sách khách hàng: %s"
    CUSTOMER_DETAILS_LOAD_ERROR("CUSTOMER_DETAILS_LOAD_ERROR"),    //"Lỗi khi tải thông tin khách hàng: %s"
    CUSTOMER_REQUIRED_FIELDS("CUSTOMER_REQUIRED_FIELDS"),          //"Mã khách hàng, họ tên và số điện thoại không được để trống"
    CUSTOMER_POINTS_NEGATIVE("CUSTOMER_POINTS_NEGATIVE"),          //"Điểm tích lũy không được âm"
    CUSTOMER_POINTS_INTEGER("CUSTOMER_POINTS_INTEGER"),            //"Điểm tích lũy phải là số nguyên"
    CUSTOMER_ID_EXISTS("CUSTOMER_ID_EXISTS"),                      //"Mã khách hàng đã tồn tại"
    CUSTOMER_ADD_CONFIRM("CUSTOMER_ADD_CONFIRM"),                  //"Xác nhận thêm mới khách hàng?"
    CUSTOMER_ADD_SUCCESS("CUSTOMER_ADD_SUCCESS"),                  //"Thêm khách hàng mới thành công"
    CUSTOMER_UPDATE_SUCCESS("CUSTOMER_UPDATE_SUCCESS"),            //"Cập nhật thông tin khách hàng thành công"
    CUSTOMER_ADD_ERROR("CUSTOMER_ADD_ERROR"),                      //"Lỗi khi thêm khách hàng: %s"
    CUSTOMER_UPDATE_ERROR("CUSTOMER_UPDATE_ERROR"),                //"Lỗi khi cập nhật khách hàng: %s"
    CUSTOMER_SELECT_UPDATE("CUSTOMER_SELECT_UPDATE"),              //"Vui lòng chọn khách hàng cần cập nhật"
    CUSTOMER_SELECT_DELETE("CUSTOMER_SELECT_DELETE"),              //"Vui lòng chọn khách hàng cần xóa"
    CUSTOMER_DELETE_CONFIRM("CUSTOMER_DELETE_CONFIRM"),            //"Bạn có chắc chắn muốn xóa khách hàng %s?"
    CUSTOMER_DELETE_TITLE("CUSTOMER_DELETE_TITLE"),                //"Xác nhận xóa"
    CUSTOMER_DELETE_SUCCESS("CUSTOMER_DELETE_SUCCESS"),            //"Xóa khách hàng thành công"
    CUSTOMER_DELETE_ERROR("CUSTOMER_DELETE_ERROR"),                //"Lỗi khi xóa khách hàng: %s"
    CUSTOMER_DELETE_CONSTRAINT("CUSTOMER_DELETE_CONSTRAINT"),      //"Không thể xóa khách hàng. Khách hàng có thể đã có giao dịch trong hệ thống."
    CUSTOMER_SEARCH_ERROR("CUSTOMER_SEARCH_ERROR"),                //"Lỗi khi tìm kiếm khách hàng: %s"
    CUSTOMER_EXPORT_NO_DATA("CUSTOMER_EXPORT_NO_DATA"),            //"Không có dữ liệu để xuất"
    CUSTOMER_EXPORT_SUCCESS("CUSTOMER_EXPORT_SUCCESS"),            //"Xuất Excel thành công!"
    CUSTOMER_EXPORT_FAILED("CUSTOMER_EXPORT_FAILED"),              //"Xuất Excel không thành công!"
    CUSTOMER_EXPORT_ERROR("CUSTOMER_EXPORT_ERROR"),                //"Lỗi khi xuất Excel: %s"
    CUSTOMER_ADD_CONTINUE("CUSTOMER_ADD_CONTINUE"),                //"Bạn có muốn tiếp tục thêm khách hàng không?"
    CUSTOMER_ADD_CANCEL("CUSTOMER_ADD_CANCEL"),                    //"Bạn có muốn hủy thao tác thêm khách hàng mới không?"
    INFO_TITLE("INFO_TITLE"),                                      //"Thông báo"
    INSUFFICIENT_POINTS("INSUFFICIENT_POINTS"),                    //"Số điểm tích lũy không đủ để trừ"
    CUSTOMER_FIND_BY_ID_ERROR("CUSTOMER_FIND_BY_ID_ERROR"),        //"Lỗi khi tìm khách hàng theo ID: %s"
    CUSTOMER_FIND_BY_PHONE_ERROR("CUSTOMER_FIND_BY_PHONE_ERROR"),  //"Lỗi khi tìm khách hàng theo số điện thoại: %s"
    CUSTOMER_POINTS_UPDATE_ERROR("CUSTOMER_POINTS_UPDATE_ERROR"),  //"Lỗi khi cập nhật điểm tích lũy: %s"
    CUSTOMER_POINTS_ADD_ERROR("CUSTOMER_POINTS_ADD_ERROR"),        //"Lỗi khi cộng điểm tích lũy: %s"
    CUSTOMER_POINTS_DEDUCT_ERROR("CUSTOMER_POINTS_DEDUCT_ERROR"),  //"Lỗi khi trừ điểm tích lũy: %s"
    
    // Home controller messages
    HOME_CONTROLLER_INIT_ERROR("HOME_CONTROLLER_INIT_ERROR"),      //"Lỗi khi khởi tạo controller: %s"
    USER_INFO_LOAD_ERROR("USER_INFO_LOAD_ERROR"),                  //"Lỗi khi tải thông tin người dùng: %s"
    TODAY_STATS_LOAD_ERROR("TODAY_STATS_LOAD_ERROR"),              //"Lỗi khi tải thống kê hôm nay: %s"
    MONTH_STATS_LOAD_ERROR("MONTH_STATS_LOAD_ERROR"),              //"Lỗi khi tải thống kê tháng: %s"
    WEEKLY_CHART_LOAD_ERROR("WEEKLY_CHART_LOAD_ERROR"),            //"Lỗi khi tải biểu đồ doanh thu theo tuần: %s"
    DAILY_REVENUE_DATA_ERROR("DAILY_REVENUE_DATA_ERROR"),          //"Lỗi khi lấy dữ liệu doanh thu theo ngày: %s"
    TOTAL_REVENUE_ERROR("TOTAL_REVENUE_ERROR"),                    //"Lỗi khi lấy tổng doanh thu: %s"
    
    // User controller specific messages
    USER_CONTROLLER_INIT_ERROR("USER_CONTROLLER_INIT_ERROR"),      //"Lỗi khởi tạo controller: %s"
    USER_LOAD_ERROR("USER_LOAD_ERROR"),                            //"Lỗi khi tải danh sách người dùng: %s"
    USER_FILTER_ERROR("USER_FILTER_ERROR"),                        //"Lỗi khi lọc danh sách người dùng: %s"
    USER_DETAILS_LOAD_ERROR("USER_DETAILS_LOAD_ERROR"),            //"Lỗi khi tải thông tin người dùng: %s"
    USER_SELECT_EMPLOYEE("USER_SELECT_EMPLOYEE"),                  //"Vui lòng chọn nhân viên"
    USER_COMPLETE_INFORMATION("USER_COMPLETE_INFORMATION"),        //"Vui lòng điền đầy đủ thông tin tài khoản"
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS"),            //"Tên đăng nhập đã tồn tại"
    USER_ADD_CONFIRM("USER_ADD_CONFIRM"),                          //"Xác nhận thêm mới người dùng?"
    USER_ADD_CONFIRM_TITLE("USER_ADD_CONFIRM_TITLE"),              //"Xác nhận thêm"
    USER_ADD_SUCCESS("USER_ADD_SUCCESS"),                          //"Thêm người dùng thành công"
    USER_ADD_ERROR("USER_ADD_ERROR"),                              //"Lỗi khi thêm người dùng: %s"
    USER_SELECT_TO_UPDATE("USER_SELECT_TO_UPDATE"),                //"Vui lòng chọn người dùng cần cập nhật"
    USER_UPDATE_OWN_INFO_ONLY("USER_UPDATE_OWN_INFO_ONLY"),        //"Bạn chỉ có thể cập nhật thông tin của mình"
    USER_COMPLETE_INFO("USER_COMPLETE_INFO"),                      //"Vui lòng điền đầy đủ thông tin"
    USER_UPDATE_CONFIRM("USER_UPDATE_CONFIRM"),                    //"Xác nhận cập nhật thông tin?"
    USER_UPDATE_CONFIRM_TITLE("USER_UPDATE_CONFIRM_TITLE"),        //"Xác nhận cập nhật"
    USER_UPDATE_SUCCESS("USER_UPDATE_SUCCESS"),                    //"Cập nhật người dùng thành công"
    USER_UPDATE_ERROR("USER_UPDATE_ERROR"),                        //"Lỗi khi cập nhật người dùng: %s"
    USER_SELECT_TO_DELETE("USER_SELECT_TO_DELETE"),                //"Vui lòng chọn người dùng cần xóa"
    USER_DELETE_CONFIRM("USER_DELETE_CONFIRM"),                    //"Bạn có chắc chắn muốn xóa người dùng này?"
    USER_DELETE_CONFIRM_TITLE("USER_DELETE_CONFIRM_TITLE"),        //"Xác nhận xóa"
    USER_DELETE_SUCCESS("USER_DELETE_SUCCESS"),                    //"Xóa người dùng thành công"
    USER_DELETE_ERROR("USER_DELETE_ERROR"),                        //"Lỗi khi xóa người dùng: %s"
    USER_SELECT_TO_RESET_PASSWORD("USER_SELECT_TO_RESET_PASSWORD"), //"Vui lòng chọn người dùng cần đặt lại mật khẩu"
    USER_RESET_PASSWORD_CONFIRM("USER_RESET_PASSWORD_CONFIRM"),    //"Bạn có chắc chắn muốn đặt lại mật khẩu cho User: %s?"
    USER_RESET_PASSWORD_CONFIRM_TITLE("USER_RESET_PASSWORD_CONFIRM_TITLE"), //"Xác nhận đặt lại mật khẩu"
    USER_RESET_PASSWORD_SUCCESS("USER_RESET_PASSWORD_SUCCESS"),    //"Đặt lại mật khẩu thành công"
    USER_RESET_PASSWORD_NEW("USER_RESET_PASSWORD_NEW"),            //"Mật khẩu mới: %s"
    USER_RESET_PASSWORD_ERROR("USER_RESET_PASSWORD_ERROR"),        //"Lỗi khi đặt lại mật khẩu: %s"
    USER_CHANGE_PASSWORD_ERROR("USER_CHANGE_PASSWORD_ERROR"),      //"Lỗi khi mở hộp thoại đổi mật khẩu: %s"
    USER_LOAD_CURRENT_ERROR("USER_LOAD_CURRENT_ERROR"),            //"Lỗi khi tải thông tin người dùng"
    PASSWORD_CHANGE_REQUIRED_FIELDS("PASSWORD_CHANGE_REQUIRED_FIELDS"), //"Vui lòng nhập đầy đủ thông tin"
    PASSWORD_NEW_MISMATCH("PASSWORD_NEW_MISMATCH"),                //"Mật khẩu mới không khớp với xác nhận mật khẩu"
    PASSWORD_MIN_LENGTH("PASSWORD_MIN_LENGTH"),                    //"Mật khẩu mới phải có ít nhất 6 ký tự"
    PASSWORD_CURRENT_INCORRECT("PASSWORD_CURRENT_INCORRECT"),      //"Mật khẩu hiện tại không đúng"
    PASSWORD_CHANGE_SUCCESS("PASSWORD_CHANGE_SUCCESS"),            //"Đổi mật khẩu thành công"
    PASSWORD_CHANGE_FAILED("PASSWORD_CHANGE_FAILED"),              //"Đổi mật khẩu thất bại"
    PASSWORD_CHANGE_ERROR("PASSWORD_CHANGE_ERROR"),                //"Lỗi khi đổi mật khẩu: %s"
    
    // Payment controller specific messages
    PAYMENT_PROCESSING_ERROR("PAYMENT_PROCESSING_ERROR"),          //"Lỗi khi xử lý thanh toán: %s"
    PAYMENT_SUCCESS_CHANGE("PAYMENT_SUCCESS_CHANGE"),              //"Thanh toán thành công! Số tiền thối lại: %s đ"
    PAYMENT_METHOD_MAINTENANCE("PAYMENT_METHOD_MAINTENANCE"),      //"Chức năng này đang bảo trì! Vui lòng chọn phương thức thanh toán khác"
    PAYMENT_CONFIRM("PAYMENT_CONFIRM"),                            //"Xác nhận đã thanh toán thành công?"
    PAYMENT_CONFIRM_TITLE("PAYMENT_CONFIRM_TITLE"),                //"Xác nhận thanh toán"

    // File related messages
    FILE_EXISTS_OVERWRITE("FILE_EXISTS_OVERWRITE"),                //"File đã tồn tại. Bạn có muốn ghi đè không?"
    FILE_PATH("FILE_PATH"),                                        //"Đường dẫn: "
    CANNOT_OPEN_FILE("CANNOT_OPEN_FILE"),                          //"Không thể tự động mở file: %s"

    // Sell controller messages
    DEFAULT_CUSTOMER_NOT_FOUND("DEFAULT_CUSTOMER_NOT_FOUND"),      //"Không tìm thấy khách hàng mặc định. Vui lòng kiểm tra lại."
    CART_UPDATE_ERROR("CART_UPDATE_ERROR"),                        //"Lỗi khi cập nhật bảng giỏ hàng: %s"
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK"),                      //"Không đủ số lượng tồn kho. Chỉ còn %d sản phẩm."
    INVALID_INVOICE_SAVE("INVALID_INVOICE_SAVE"),                  //"Không thể lưu hóa đơn. Giỏ hàng trống hoặc thông tin hóa đơn không hợp lệ."
    INVOICE_SAVE_ERROR("INVOICE_SAVE_ERROR"),                      //"Lỗi khi lưu hóa đơn. Vui lòng thử lại."
    INVOICE_DETAIL_SAVE_ERROR("INVOICE_DETAIL_SAVE_ERROR"),        //"Lỗi khi lưu chi tiết hóa đơn. Vui lòng thử lại."
    UNKNOWN_ERROR("UNKNOWN_ERROR"),                                //"Lỗi không xác định: %s"
    INVALID_INVOICE_COMPLETE("INVALID_INVOICE_COMPLETE"),          //"Không thể hoàn thành giao dịch. Hóa đơn không hợp lệ."
    EXPORT_INVOICE_ERROR("EXPORT_INVOICE_ERROR"),                  //"Lỗi khi xuất hóa đơn: %s"
    EXPORT_INVOICE_FAILED("EXPORT_INVOICE_FAILED"),                //"Xuất hóa đơn thất bại. Vui lòng thử lại."
    POINTS_ALREADY_USED("POINTS_ALREADY_USED"),                    //"Hóa đơn đã sử dụng điểm."

    // SellForm specific messages
    FORM_INIT_ERROR("FORM_INIT_ERROR"),                            //"Lỗi khởi tạo form bán hàng: %s"
    SELECT_PRODUCT_TO_DELETE("SELECT_PRODUCT_TO_DELETE"),          //"Vui lòng chọn sản phẩm để xóa khỏi giỏ hàng."
    CONFIRM_DELETE_PRODUCT("CONFIRM_DELETE_PRODUCT"),              //"Bạn có chắc chắn muốn xóa sản phẩm đã chọn khỏi giỏ hàng không?"
    CUSTOMER_POINTS_INSUFFICIENT("CUSTOMER_POINTS_INSUFFICIENT"),  //"Khách hàng chưa đủ điểm để áp dụng ưu đãi. Cần ít nhất 10,000 điểm."
    CONFIRM_USE_POINTS("CONFIRM_USE_POINTS"),                      //"Khách hàng có %d điểm tích lũy.\nBạn có muốn sử dụng điểm để giảm giá không?"
    CUSTOMER_SELECT_REQUIRED("CUSTOMER_SELECT_REQUIRED"),          //"Vui lòng chọn khách hàng trước khi áp dụng ưu đãi điểm."
    EMPTY_CART("EMPTY_CART"),                                      //"Giỏ hàng trống. Vui lòng thêm sản phẩm trước khi thanh toán."
    INVALID_QUANTITY("INVALID_QUANTITY"),                          //"Số lượng không hợp lệ"
    QUANTITY_MUST_BE_POSITIVE("QUANTITY_MUST_BE_POSITIVE"),        //"Số lượng phải lớn hơn 0"
    SELECT_PRODUCT_TO_ADD("SELECT_PRODUCT_TO_ADD"),                //"Vui lòng chọn sản phẩm để thêm vào giỏ hàng"
    QUANTITY_UPDATE_ERROR("QUANTITY_UPDATE_ERROR"),                //"Không thể cập nhật số lượng. Số lượng vượt quá tồn kho hoặc có lỗi khác."
    CONFIRM_SAVE_INVOICE("CONFIRM_SAVE_INVOICE"),                  //"Bạn có chắc chắn muốn thanh toán hóa đơn không?"
    SAVE_INVOICE_ERROR("SAVE_INVOICE_ERROR"),                      //"Lỗi khi lưu hóa đơn. Vui lòng thử lại."
    SAVE_INVOICE_SUCCESS("SAVE_INVOICE_SUCCESS"),                  //"Lưu hóa đơn thành công! ID: %s"
    PAYMENT_SUCCESS("PAYMENT_SUCCESS"),                            //"Thanh toán thành công!"
    PAYMENT_FAILED("PAYMENT_FAILED"),                              //"Thanh toán không thành công!"
    CONFIRM_RESET_CART("CONFIRM_RESET_CART"),                      //"Bạn có chắc chắn muốn làm mới giỏ hàng không?"
    CUSTOMER_NAME_REQUIRED("CUSTOMER_NAME_REQUIRED"),              //"Vui lòng nhập tên khách hàng"

    // Bank payment related messages
    BROWSER_OPEN_ERROR("BROWSER_OPEN_ERROR"),                      //"Không thể mở trình duyệt để thanh toán"
    PAYMENT_WAITING_MESSAGE("PAYMENT_WAITING_MESSAGE"),            //"Vui lòng hoàn tất thanh toán trong trình duyệt.\nBấm OK khi đã thanh toán xong hoặc Cancel để hủy."
    PAYMENT_VERIFICATION_FAILED("PAYMENT_VERIFICATION_FAILED"),    //"Không thể xác nhận thanh toán. Vui lòng kiểm tra lại sau."
    WARNING_TITLE("WARNING_TITLE"),                                //"Cảnh báo"
    PAYMENT_LINK_CREATE_ERROR("PAYMENT_LINK_CREATE_ERROR"),      //"Không thể tạo link thanh toán"

    // Inventory Check Controller messages
    INVENTORY_CHECK_CONTROLLER_INIT_ERROR("INVENTORY_CHECK_CONTROLLER_INIT_ERROR"), //"Lỗi kết nối cơ sở dữ liệu: %s"
    INVENTORY_CHECK_LOAD_DATA_ERROR("INVENTORY_CHECK_LOAD_DATA_ERROR"),//"Lỗi tải dữ liệu: %s"
    INVENTORY_CHECK_NOT_FOUND("INVENTORY_CHECK_NOT_FOUND"),//"Không tìm thấy phiếu kiểm kê với mã: %s"
    INVENTORY_CHECK_OPEN_ADD_FORM_ERROR("INVENTORY_CHECK_OPEN_ADD_FORM_ERROR"),//"Lỗi mở form thêm: %s"
    INVENTORY_CHECK_OPEN_DETAIL_FORM_ERROR("INVENTORY_CHECK_OPEN_DETAIL_FORM_ERROR"), //"Lỗi mở form chi tiết: %s"
    INVENTORY_CHECK_NOT_EXISTS("INVENTORY_CHECK_NOT_EXISTS"),//"Phiếu kiểm kê không tồn tại!"
    INVENTORY_CHECK_CANNOT_DELETE_STATUS("INVENTORY_CHECK_CANNOT_DELETE_STATUS"),//"Không thể xóa phiếu kiểm kê với trạng thái: %s"
    INVENTORY_CHECK_DELETE_CONFIRM("INVENTORY_CHECK_DELETE_CONFIRM"),//"Bạn có chắc chắn muốn xóa phiếu kiểm kê '%s' (Mã: %s)?\n\nHành động này không thể hoàn tác!"
    INVENTORY_CHECK_DELETE_CONFIRM_TITLE("INVENTORY_CHECK_DELETE_CONFIRM_TITLE"),//"Xác nhận xóa"
    INVENTORY_CHECK_DELETE_SUCCESS("INVENTORY_CHECK_DELETE_SUCCESS"),//"Xóa phiếu kiểm kê thành công!"
    INVENTORY_CHECK_DELETE_FAILED("INVENTORY_CHECK_DELETE_FAILED"),  //"Xóa phiếu kiểm kê thất bại!"
    INVENTORY_CHECK_DELETE_ERROR("INVENTORY_CHECK_DELETE_ERROR"),    //"Lỗi xóa phiếu kiểm kê: %s"
    INVENTORY_CHECK_FILTER_ERROR("INVENTORY_CHECK_FILTER_ERROR"),    //"Lỗi lọc dữ liệu: %s"
    INVENTORY_CHECK_STATISTICS_ERROR("INVENTORY_CHECK_STATISTICS_ERROR"),//"Lỗi lấy thống kê: %s"
    INVENTORY_CHECK_EXPORT_DEVELOPING("INVENTORY_CHECK_EXPORT_DEVELOPING"),//"Chức năng xuất Excel đang được phát triển"
    INVENTORY_CHECK_STATISTICS_TITLE("INVENTORY_CHECK_STATISTICS_TITLE"),//"Thống kê"
    INVENTORY_CHECK_STATISTICS_MESSAGE("INVENTORY_CHECK_STATISTICS_MESSAGE"),//"Thống kê phiếu kiểm kê:\n\n• %s: %d\n• %s: %d\n• %s: %d\n• %s: %d\n\nTổng cộng: %d"

    // Add Inventory Check Controller messages
    ADD_INVENTORY_CHECK_INIT_ERROR("ADD_INVENTORY_CHECK_INIT_ERROR"),                       //"Lỗi khởi tạo dịch vụ: %s"
    ADD_INVENTORY_CHECK_DB_ERROR("ADD_INVENTORY_CHECK_DB_ERROR"),                           //"Lỗi kết nối cơ sở dữ liệu: %s"
    ADD_INVENTORY_CHECK_LOAD_CATEGORIES_ERROR("ADD_INVENTORY_CHECK_LOAD_CATEGORIES_ERROR"), //"Lỗi tải danh mục: %s"
    ADD_INVENTORY_CHECK_LOAD_EMPLOYEES_ERROR("ADD_INVENTORY_CHECK_LOAD_EMPLOYEES_ERROR"),   //"Lỗi tải nhân viên: %s"
    ADD_INVENTORY_CHECK_LOAD_PRODUCTS_ERROR("ADD_INVENTORY_CHECK_LOAD_PRODUCTS_ERROR"),     //"Lỗi tải sản phẩm: %s"
    ADD_INVENTORY_CHECK_LOAD_PRODUCTS_BY_CATEGORY_ERROR("ADD_INVENTORY_CHECK_LOAD_PRODUCTS_BY_CATEGORY_ERROR"), //"Lỗi tải sản phẩm theo danh mục: %s"
    ADD_INVENTORY_CHECK_ADD_PRODUCT_ERROR("ADD_INVENTORY_CHECK_ADD_PRODUCT_ERROR"),         //"Lỗi thêm sản phẩm: %s"
    ADD_INVENTORY_CHECK_PRODUCT_ALREADY_ADDED("ADD_INVENTORY_CHECK_PRODUCT_ALREADY_ADDED"), //"Sản phẩm '%s' đã được thêm vào danh sách kiểm kê"
    ADD_INVENTORY_CHECK_REMOVE_PRODUCT_CONFIRM("ADD_INVENTORY_CHECK_REMOVE_PRODUCT_CONFIRM"), //"Bạn có chắc chắn muốn xóa sản phẩm '%s' khỏi danh sách kiểm kê?"
    ADD_INVENTORY_CHECK_REMOVE_PRODUCT_TITLE("ADD_INVENTORY_CHECK_REMOVE_PRODUCT_TITLE"),   //"Xác nhận xóa"
    ADD_INVENTORY_CHECK_SELECT_ALL_ERROR("ADD_INVENTORY_CHECK_SELECT_ALL_ERROR"),           //"Lỗi tải tất cả sản phẩm: %s"
    ADD_INVENTORY_CHECK_SELECT_ALL_SUCCESS("ADD_INVENTORY_CHECK_SELECT_ALL_SUCCESS"),       //"Đã thêm tất cả %d sản phẩm vào danh sách kiểm kê"
    ADD_INVENTORY_CHECK_NAME_EMPTY("ADD_INVENTORY_CHECK_NAME_EMPTY"),                       //"Vui lòng nhập tên phiếu kiểm kê!"
    ADD_INVENTORY_CHECK_EMPLOYEE_EMPTY("ADD_INVENTORY_CHECK_EMPLOYEE_EMPTY"),               //"Vui lòng chọn nhân viên thực hiện kiểm kê!"
    ADD_INVENTORY_CHECK_PRODUCTS_EMPTY("ADD_INVENTORY_CHECK_PRODUCTS_EMPTY"),               //"Vui lòng chọn ít nhất một sản phẩm để kiểm kê!"
    ADD_INVENTORY_CHECK_EMPLOYEE_NOT_FOUND("ADD_INVENTORY_CHECK_EMPLOYEE_NOT_FOUND"),       //"Không tìm thấy nhân viên được chọn!"
    ADD_INVENTORY_CHECK_CREATE_CONFIRM("ADD_INVENTORY_CHECK_CREATE_CONFIRM"),               //"Bạn có chắc chắn muốn tạo phiếu kiểm kê với %d sản phẩm?"
    ADD_INVENTORY_CHECK_CREATE_CONFIRM_TITLE("ADD_INVENTORY_CHECK_CREATE_CONFIRM_TITLE"),   //"Xác nhận tạo phiếu"
    ADD_INVENTORY_CHECK_CREATE_ERROR("ADD_INVENTORY_CHECK_CREATE_ERROR"),                   //"Lỗi tạo phiếu kiểm kê: %s"
    ADD_INVENTORY_CHECK_CREATE_SUCCESS("ADD_INVENTORY_CHECK_CREATE_SUCCESS"),               //"Tạo phiếu kiểm kê thành công!\nMã phiếu: %s\nSố sản phẩm: %d"
    ADD_INVENTORY_CHECK_CANCEL_CONFIRM("ADD_INVENTORY_CHECK_CANCEL_CONFIRM"),               //"Bạn có chắc chắn muốn hủy? Tất cả dữ liệu sẽ bị mất!"
    ADD_INVENTORY_CHECK_CANCEL_CONFIRM_TITLE("ADD_INVENTORY_CHECK_CANCEL_CONFIRM_TITLE"),   //"Xác nhận hủy"
    ADD_INVENTORY_CHECK_EXPORT_NO_DATA("ADD_INVENTORY_CHECK_EXPORT_NO_DATA"),               //"Không có dữ liệu để xuất!"
    ADD_INVENTORY_CHECK_EXPORT_DEVELOPING("ADD_INVENTORY_CHECK_EXPORT_DEVELOPING"),         //"Chức năng xuất Excel đang được phát triển"

    // Detail Inventory Check Controller messages
    DETAIL_INVENTORY_CHECK_INIT_ERROR("DETAIL_INVENTORY_CHECK_INIT_ERROR"),                       //"Lỗi khởi tạo services: %s"
    DETAIL_INVENTORY_CHECK_NOT_FOUND("DETAIL_INVENTORY_CHECK_NOT_FOUND"),                         //"Không tìm thấy phiếu kiểm kê!"
    DETAIL_INVENTORY_CHECK_LOAD_ERROR("DETAIL_INVENTORY_CHECK_LOAD_ERROR"),                       //"Lỗi tải phiếu kiểm kê: %s"
    DETAIL_INVENTORY_CHECK_LOAD_DETAILS_ERROR("DETAIL_INVENTORY_CHECK_LOAD_DETAILS_ERROR"),       //"Lỗi tải chi tiết kiểm kê: %s"
    DETAIL_INVENTORY_CHECK_LOAD_EMPLOYEES_ERROR("DETAIL_INVENTORY_CHECK_LOAD_EMPLOYEES_ERROR"),   //"Lỗi tải danh sách nhân viên: %s"
    DETAIL_INVENTORY_CHECK_QUANTITY_EMPTY("DETAIL_INVENTORY_CHECK_QUANTITY_EMPTY"),               //"Số lượng thực tế không được để trống!"
    DETAIL_INVENTORY_CHECK_QUANTITY_INVALID("DETAIL_INVENTORY_CHECK_QUANTITY_INVALID"),           //"Số lượng phải là một số nguyên hợp lệ!"
    DETAIL_INVENTORY_CHECK_QUANTITY_NEGATIVE("DETAIL_INVENTORY_CHECK_QUANTITY_NEGATIVE"),         //"Số lượng thực tế không được âm!"
    DETAIL_INVENTORY_CHECK_QUANTITY_TOO_LARGE("DETAIL_INVENTORY_CHECK_QUANTITY_TOO_LARGE"),       //"Số lượng thực tế quá lớn (tối đa 999,999)!"
    DETAIL_INVENTORY_CHECK_UPDATE_QUANTITY_ERROR("DETAIL_INVENTORY_CHECK_UPDATE_QUANTITY_ERROR"), //"Lỗi cập nhật số lượng: %s"
    DETAIL_INVENTORY_CHECK_VALIDATION_NO_DATA("DETAIL_INVENTORY_CHECK_VALIDATION_NO_DATA"),       //"Chưa có dữ liệu kiểm kê!"
    DETAIL_INVENTORY_CHECK_VALIDATION_CONFIRM("DETAIL_INVENTORY_CHECK_VALIDATION_CONFIRM"),       //"Còn %d sản phẩm chưa nhập số lượng thực tế.\nBạn có muốn tiếp tục hoàn thành kiểm kê?\n(Các sản phẩm chưa nhập sẽ được tính bằng 0)"
    DETAIL_INVENTORY_CHECK_UPDATE_CONFIRM("DETAIL_INVENTORY_CHECK_UPDATE_CONFIRM"),               //"Bạn có chắc chắn muốn cập nhật phiếu kiểm kê?"
    DETAIL_INVENTORY_CHECK_UPDATE_CONFIRM_TITLE("DETAIL_INVENTORY_CHECK_UPDATE_CONFIRM_TITLE"),   //"Xác nhận cập nhật"
    DETAIL_INVENTORY_CHECK_UPDATE_SUCCESS("DETAIL_INVENTORY_CHECK_UPDATE_SUCCESS"),               //"Cập nhật phiếu kiểm kê thành công!"
    DETAIL_INVENTORY_CHECK_UPDATE_ERROR("DETAIL_INVENTORY_CHECK_UPDATE_ERROR"),                   //"Lỗi cập nhật phiếu kiểm kê: %s"
    DETAIL_INVENTORY_CHECK_UPDATE_DETAILS_WARNING("DETAIL_INVENTORY_CHECK_UPDATE_DETAILS_WARNING"), //"Cập nhật thành công phiếu kiểm kê, nhưng có lỗi khi lưu chi tiết!"
    DETAIL_INVENTORY_CHECK_COMPLETE_NO_DATA("DETAIL_INVENTORY_CHECK_COMPLETE_NO_DATA"),           //"Phiếu kiểm kê chưa có chi tiết. Vui lòng thêm sản phẩm kiểm kê!"
    DETAIL_INVENTORY_CHECK_COMPLETE_SUMMARY("DETAIL_INVENTORY_CHECK_COMPLETE_SUMMARY"),           //"Thông tin tổng kết:\n- Tổng số sản phẩm: %d\n- Số lượng tăng: %s\n- Số lượng giảm: %s\n- Tổng chênh lệch: %s\n\nBạn có chắc chắn muốn hoàn thành kiểm kê?\nSau khi hoàn thành, tồn kho sẽ được cập nhật theo số liệu thực tế."
    DETAIL_INVENTORY_CHECK_COMPLETE_CONFIRM_TITLE("DETAIL_INVENTORY_CHECK_COMPLETE_CONFIRM_TITLE"), //"Xác nhận hoàn thành kiểm kê"
    DETAIL_INVENTORY_CHECK_COMPLETE_SAVE_ERROR("DETAIL_INVENTORY_CHECK_COMPLETE_SAVE_ERROR"),     //"Có lỗi khi lưu chi tiết kiểm kê. Vui lòng thử lại!"
    DETAIL_INVENTORY_CHECK_COMPLETE_SUCCESS("DETAIL_INVENTORY_CHECK_COMPLETE_SUCCESS"),           //"Hoàn thành kiểm kê thành công!\nTồn kho đã được cập nhật theo số liệu thực tế."
    DETAIL_INVENTORY_CHECK_COMPLETE_ERROR("DETAIL_INVENTORY_CHECK_COMPLETE_ERROR"),               //"Lỗi hoàn thành kiểm kê. Vui lòng thử lại!"
    DETAIL_INVENTORY_CHECK_COMPLETE_EXCEPTION("DETAIL_INVENTORY_CHECK_COMPLETE_EXCEPTION"),       //"Lỗi hoàn thành kiểm kê: %s"
    DETAIL_INVENTORY_CHECK_IMPORT_NO_CURRENT("DETAIL_INVENTORY_CHECK_IMPORT_NO_CURRENT"),         //"Không có phiếu kiểm kê hiện tại!"
    DETAIL_INVENTORY_CHECK_IMPORT_STATUS_INVALID("DETAIL_INVENTORY_CHECK_IMPORT_STATUS_INVALID"), //"Chỉ có thể import dữ liệu trong trạng thái nháp hoặc đang kiểm kê!"
    DETAIL_INVENTORY_CHECK_IMPORT_FILE_TITLE("DETAIL_INVENTORY_CHECK_IMPORT_FILE_TITLE"),         //"Chọn file Excel chứa dữ liệu kiểm kê"
    DETAIL_INVENTORY_CHECK_IMPORT_FILE_INVALID("DETAIL_INVENTORY_CHECK_IMPORT_FILE_INVALID"),     //"File không có dữ liệu hoặc không đúng định dạng!"
    DETAIL_INVENTORY_CHECK_IMPORT_RESULT("DETAIL_INVENTORY_CHECK_IMPORT_RESULT"),                 //"Import hoàn tất!\n- Số lượng sản phẩm đã cập nhật: %d\n- Số lượng dòng không hợp lệ: %d"
    DETAIL_INVENTORY_CHECK_IMPORT_RESULT_TITLE("DETAIL_INVENTORY_CHECK_IMPORT_RESULT_TITLE"),     //"Kết quả import"
    DETAIL_INVENTORY_CHECK_IMPORT_ERROR("DETAIL_INVENTORY_CHECK_IMPORT_ERROR"),                   //"Lỗi khi import file Excel: %s"
    DETAIL_INVENTORY_CHECK_EXPORT_NO_DATA("DETAIL_INVENTORY_CHECK_EXPORT_NO_DATA"),               //"Không có dữ liệu để xuất!"
    DETAIL_INVENTORY_CHECK_EXPORT_SUCCESS("DETAIL_INVENTORY_CHECK_EXPORT_SUCCESS"),               //"Xuất Excel thành công!\nFile đã được lưu tại: %s"
    DETAIL_INVENTORY_CHECK_EXPORT_ERROR("DETAIL_INVENTORY_CHECK_EXPORT_ERROR"),                   //"Xuất Excel không thành công!"
    DETAIL_INVENTORY_CHECK_EXPORT_EXCEPTION("DETAIL_INVENTORY_CHECK_EXPORT_EXCEPTION"),           //"Lỗi khi Xuất Excel: %s"
    DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_CONFIRM("DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_CONFIRM"), //"Xác nhận xóa sản phẩm có mã: \"%s\" khỏi phiếu kiểm kê?"
    DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_CONFIRM_TITLE("DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_CONFIRM_TITLE"), //"Xác nhận xóa"
    DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_STATUS_INVALID("DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_STATUS_INVALID"), //"Không thể xóa sản phẩm khỏi phiếu kiểm kê với trạng thái: %s"
    DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_SUCCESS("DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_SUCCESS"), //"Xóa sản phẩm khỏi phiếu kiểm kê thành công!"
    DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_FAILED("DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_FAILED"), //"Xóa sản phẩm khỏi phiếu kiểm kê thất bại!"
    DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_ERROR("DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_ERROR"),   //"Lỗi xóa sản phẩm khỏi phiếu kiểm kê: %s"
    DETAIL_INVENTORY_CHECK_NAME_EMPTY("DETAIL_INVENTORY_CHECK_NAME_EMPTY"),                       //"Tên phiếu kiểm kê không được để trống!"
    DETAIL_INVENTORY_CHECK_EMPLOYEE_EMPTY("DETAIL_INVENTORY_CHECK_EMPLOYEE_EMPTY"),               //"Vui lòng chọn nhân viên thực hiện kiểm kê!"
    DETAIL_INVENTORY_CHECK_START_ERROR("DETAIL_INVENTORY_CHECK_START_ERROR"),                     //"Lỗi bắt đầu kiểm kê: %s"
    DETAIL_INVENTORY_CHECK_CANCEL_CONFIRM("DETAIL_INVENTORY_CHECK_CANCEL_CONFIRM"),               //"Bạn có chắc chắn muốn hủy phiếu kiểm kê?\nThao tác này không thể hoàn tác!"
    DETAIL_INVENTORY_CHECK_CANCEL_CONFIRM_TITLE("DETAIL_INVENTORY_CHECK_CANCEL_CONFIRM_TITLE"),   //"Xác nhận hủy"
    DETAIL_INVENTORY_CHECK_CANCEL_ERROR("DETAIL_INVENTORY_CHECK_CANCEL_ERROR"),                   //"Lỗi hủy phiếu kiểm kê: %s"

        // Invoice Controller specific messages
    INVOICE_LOAD_ALL_ERROR("INVOICE_LOAD_ALL_ERROR"),                       //"Lỗi khi tải danh sách hóa đơn: %s"
    INVOICE_SEARCH_ERROR("INVOICE_SEARCH_ERROR"),                           //"Lỗi khi tìm kiếm hóa đơn: %s"
    INVOICE_DELETE_SELECTED_NO_SELECTION("INVOICE_DELETE_SELECTED_NO_SELECTION"), //"Vui lòng tích chọn ít nhất một hóa đơn để xóa!"
    INVOICE_DELETE_SELECTED_CONFIRM("INVOICE_DELETE_SELECTED_CONFIRM"),     //"Bạn có chắc chắn muốn xóa %d hóa đơn đã chọn?"
    INVOICE_DELETE_SELECTED_TITLE("INVOICE_DELETE_SELECTED_TITLE"),         //"Xác nhận xóa"
    INVOICE_DELETE_SELECTED_SUCCESS("INVOICE_DELETE_SELECTED_SUCCESS"),     //"Đã xóa thành công %d hóa đơn.\n"
    INVOICE_DELETE_SELECTED_FAILED("INVOICE_DELETE_SELECTED_FAILED"),       //"Không thể xóa %d hóa đơn.\n\n"
    INVOICE_DELETE_SELECTED_DETAILS("INVOICE_DELETE_SELECTED_DETAILS"),     //"Chi tiết lỗi:\n"
    INVOICE_DELETE_SELECTED_PAID_OR_DELIVERED("INVOICE_DELETE_SELECTED_PAID_OR_DELIVERED"), //"Hóa đơn #%d: Không thể xóa hóa đơn đã thanh toán hoặc đã giao hàng!"
    INVOICE_DELETE_SELECTED_FAIL_DETAIL("INVOICE_DELETE_SELECTED_FAIL_DETAIL"), //"Hóa đơn #%d: Xóa thất bại!"
    INVOICE_DELETE_SELECTED_RESULT_TITLE("INVOICE_DELETE_SELECTED_RESULT_TITLE"), //"Kết quả xóa hóa đơn"
    INVOICE_PAYMENT_NO_SELECTION("INVOICE_PAYMENT_NO_SELECTION"),           //"Vui lòng chọn hóa đơn cần thanh toán!"
    INVOICE_PAYMENT_ALREADY_PAID("INVOICE_PAYMENT_ALREADY_PAID"),           //"Hóa đơn đã được thanh toán!"
    INVOICE_UPDATE_STATUS_ERROR("INVOICE_UPDATE_STATUS_ERROR"),             //"Lỗi cập nhật trạng thái hóa đơn: %s"
    INVOICE_PROCESS_PAYMENT_ERROR("INVOICE_PROCESS_PAYMENT_ERROR"),         //"Lỗi xử lý thanh toán: %s"
    INVOICE_EXPORT_EXCEL_NO_DATA("INVOICE_EXPORT_EXCEL_NO_DATA"),           //"Không có dữ liệu hóa đơn để xuất"
    INVOICE_RETURNABLE_DETAILS_ERROR("INVOICE_RETURNABLE_DETAILS_ERROR"),   //"Lỗi khi lấy chi tiết hóa đơn có thể trả: %s"
    INVOICE_WARRANTY_DETAILS_ERROR("INVOICE_WARRANTY_DETAILS_ERROR"),       //"Lỗi khi lấy chi tiết hóa đơn cho bảo hành: %s"
    INVOICE_INVOICES_FOR_WARRANTY_ERROR("INVOICE_INVOICES_FOR_WARRANTY_ERROR"), //"Lỗi khi lấy danh sách hóa đơn cho bảo hành: %s"
    INVOICE_INVOICES_FOR_RETURN_ERROR("INVOICE_INVOICES_FOR_RETURN_ERROR"),     //"Lỗi khi lấy hóa đơn cho trả hàng: %s"
    INVOICE_INVOICES_SIMPLE_ERROR("INVOICE_INVOICES_SIMPLE_ERROR"),             //"Lỗi khi lấy danh sách hóa đơn đơn giản: %s"
    INVOICE_CALCULATE_REVENUE_ERROR("INVOICE_CALCULATE_REVENUE_ERROR"),         //"Lỗi khi tính doanh thu: %s"
    INVOICE_SEARCH_BY_CUSTOMER_ERROR("INVOICE_SEARCH_BY_CUSTOMER_ERROR"),       //"Lỗi khi tìm hóa đơn theo khách hàng: %s"
    INVOICE_SEARCH_BY_CUSTOMER_PHONE_ERROR("INVOICE_SEARCH_BY_CUSTOMER_PHONE_ERROR"), //"Lỗi khi lấy hóa đơn theo số điện thoại: %s"
    INVOICE_SEARCH_BY_EMPLOYEE_ERROR("INVOICE_SEARCH_BY_EMPLOYEE_ERROR"),       //"Lỗi khi tìm hóa đơn theo nhân viên: %s"
    INVOICE_SEARCH_BY_DATE_RANGE_ERROR("INVOICE_SEARCH_BY_DATE_RANGE_ERROR"),   //"Lỗi khi tìm hóa đơn theo khoảng thời gian: %s"
    INVOICE_CREATE_MAIN_ERROR("INVOICE_CREATE_MAIN_ERROR"),                     //"Lỗi khi tạo hóa đơn mới: %s"
    INVOICE_ADD_PRODUCT_STOCK_ERROR("INVOICE_ADD_PRODUCT_STOCK_ERROR"),         //"Số lượng sản phẩm không đủ. Hiện chỉ còn %d"
    INVOICE_ADD_PRODUCT_MAIN_ERROR("INVOICE_ADD_PRODUCT_MAIN_ERROR"),           //"Lỗi khi thêm sản phẩm vào hóa đơn: %s"
    INVOICE_UPDATE_QUANTITY_INSUFFICIENT("INVOICE_UPDATE_QUANTITY_INSUFFICIENT"), //"Số lượng sản phẩm không đủ. Hiện chỉ còn %d"
    INVOICE_UPDATE_QUANTITY_MAIN_ERROR("INVOICE_UPDATE_QUANTITY_MAIN_ERROR"),   //"Lỗi khi cập nhật số lượng sản phẩm: %s"
    INVOICE_REMOVE_PRODUCT_MAIN_ERROR("INVOICE_REMOVE_PRODUCT_MAIN_ERROR"),     //"Lỗi khi xóa sản phẩm khỏi hóa đơn: %s"
    INVOICE_COMPLETE_NO_DETAILS("INVOICE_COMPLETE_NO_DETAILS"),                 //"Hóa đơn không có sản phẩm nào"
    INVOICE_COMPLETE_MAIN_ERROR("INVOICE_COMPLETE_MAIN_ERROR"),                 //"Lỗi khi hoàn thành hóa đơn: %s"
    INVOICE_CANCEL_INVALID_STATUS("INVOICE_CANCEL_INVALID_STATUS"),             //"Không thể hủy hóa đơn ở trạng thái %s"
    INVOICE_CANCEL_MAIN_ERROR("INVOICE_CANCEL_MAIN_ERROR"),                     //"Lỗi khi hủy hóa đơn: %s"
    INVOICE_DELETE_INVALID_STATUS("INVOICE_DELETE_INVALID_STATUS"),             //"Không thể xóa hóa đơn đã thanh toán hoặc đã giao"
    INVOICE_DELETE_MAIN_ERROR("INVOICE_DELETE_MAIN_ERROR"),                     //"Lỗi khi xóa hóa đơn: %s"
    INVOICE_SEARCH_INVOICES_ERROR("INVOICE_SEARCH_INVOICES_ERROR"),             //"Lỗi khi tìm kiếm hóa đơn: %s"
    INVOICE_DELETE_SINGLE_ERROR("INVOICE_DELETE_SINGLE_ERROR"),                 //"Lỗi khi xóa hóa đơn: %s"
    INVOICE_RETURNABLE_INVOICE_DETAILS_ERROR("INVOICE_RETURNABLE_INVOICE_DETAILS_ERROR"); //"Lỗi khi xử lý chi tiết hóa đơn %d: %s"
    ;
    private final String key;
    private static LocaleManager LM = LocaleManager.getInstance();
    private static Map<String, String> cache = new ConcurrentHashMap<>();
    
    ErrorMessage(String key) {
        this.key = key;
    }
    
    @Override
    public String toString() {
        // Cache key với current locale
        String cacheKey = key + "_" + LM.getCurrentLocale().toString();
        return cache.computeIfAbsent(cacheKey, k -> 
            LM.getMessageResourceBundle().getString(key)
        );
    }
    
    /**
     * Get method (alias cho toString)
     */
    public String get() {
        return toString();
    }
    
    /**
     * Format message với parameters
     */
    public String format(Object... args) {
        return String.format(toString(), args);
    }
    
    /**
     * Refresh khi đổi ngôn ngữ - xóa cache và cập nhật LocaleManager
     */
    public static void refresh() {
        LM = LocaleManager.getInstance();
        cache.clear(); // Xóa cache để load message mới
    }
}