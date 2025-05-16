package com.pcstore.utils;

/**
 * Class chứa các thông báo lỗi được sử dụng trong toàn bộ ứng dụng
 */
public class ErrorMessage {

  private static LocaleManager LM = LocaleManager.getInstance();    // Common validation messages    //"Phần tử không được để trống"
  public static String FIELD_EMPTY;
  //"Phần tử không được âm"
  public static String FIELD_NEGATIVE;
  //"Giá trị không hợp lệ. Vui lòng nhập lại."
  public static String INVALID_VALUE;
  //"Giá trị phải là lớn hơn 0"
  public static String VALUE_MUST_BE_POSITIVE;
  //"Lỗi"
  public static String ERROR_TITLE;
  
  // User messages
  //"Tên đăng nhập không được để trống"
  public static String USERNAME_EMPTY;
  //"Tên đăng nhập phải có ít nhất 3 ký tự"
  public static String USERNAME_TOO_SHORT;
  //"Mật khẩu không được để trống"
  public static String PASSWORD_EMPTY;
  //"Mật khẩu phải có ít nhất 6 ký tự"
  public static String PASSWORD_TOO_SHORT;
  //"Vai trò không được để trống"
  public static String ROLE_EMPTY;
  //"Tên đăng nhập %s đã tồn tại"
  public static String USERNAME_EXISTS;
  //"Người dùng với tên đăng nhập %s không tồn tại"
  public static String USER_NOT_EXISTS;
  //"Username %s existed"
  public static String USERNAME_EXISTS_EN;    //Employee messages
  //"Mã nhân viên phải có định dạng NVxx"
  public static String EMPLOYEE_ID_FORMAT;
  // public static String EMPLOYEE_PHONE_INVALID = "Số điện thoại không hợp lệ";
  //"Tên nhân viên phải có ít nhất 2 ký tự"
  public static String EMPLOYEE_NAME_TOO_SHORT;
  //"Chức vụ không hợp lệ"
  public static String INVALID_EMPLOYEE_POSITION;
  //"Nhân viên phải từ 18 tuổi trở lên"
  public static String EMPLOYEE_AGE_18;    // Employee controller specific messages
  //"Lỗi khi tải danh sách nhân viên: %s"
  public static String EMPLOYEE_LOAD_ERROR;
  //"Lỗi khi lọc danh sách nhân viên: %s"
  public static String EMPLOYEE_FILTER_ERROR;
  //"Lỗi khi lấy thông tin nhân viên: %s"
  public static String EMPLOYEE_GET_INFO_ERROR;    //"Vui lòng chọn một nhân viên"
  public static String EMPLOYEE_SELECTION_REQUIRED;
  //"Lỗi khởi tạo controller: "
  public static String INIT_CONTROLLER_ERROR;
  //"Lỗi khi tải danh sách nhân viên: "
  public static String LOAD_EMPLOYEES_ERROR;
  //"Lỗi khi tải thông tin nhân viên: "
  public static String LOAD_EMPLOYEE_DETAILS_ERROR;    //"Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng dd-MM-yyyy"
  public static String INVALID_DATE_FORMAT;
  //"Các trường không được để trống"
  public static String EMPTY_FIELDS_ERROR;
  //"Chức vụ không hợp lệ"
  public static String INVALID_POSITION_ERROR;
  //"Mã nhân viên này đã tồn tại"
  public static String DUPLICATE_EMPLOYEE_ID_ERROR;    //"Bạn có chắc chắn muốn thêm nhân viên mới?"
  public static String CONFIRM_ADD_EMPLOYEE;
  //"Thêm nhân viên mới thành công"
  public static String ADD_EMPLOYEE_SUCCESS;
  //"Vui lòng chọn nhân viên cần cập nhật"
  public static String SELECT_EMPLOYEE_TO_UPDATE;
  //"Bạn có chắc chắn muốn cập nhật thông tin nhân viên?"
  public static String CONFIRM_UPDATE_EMPLOYEE;    //"Cập nhật thông tin nhân viên thành công"
  public static String UPDATE_EMPLOYEE_SUCCESS;
  //"Lỗi khi cập nhật nhân viên: "
  public static String UPDATE_EMPLOYEE_ERROR;
  //"Vui lòng chọn nhân viên cần xóa"
  public static String SELECT_EMPLOYEE_TO_DELETE;
  //"Bạn có chắc chắn muốn xóa nhân viên "
  public static String CONFIRM_DELETE_EMPLOYEE;
  //"Xóa nhân viên thành công"
  public static String DELETE_EMPLOYEE_SUCCESS;
  //"Lỗi khi xóa nhân viên: "
  public static String DELETE_EMPLOYEE_ERROR;
  //"Lỗi khi tìm kiếm nhân viên: "
  public static String SEARCH_EMPLOYEE_ERROR;    //"Không có dữ liệu để xuất"
  public static String NO_DATA_TO_EXPORT;
  //"Xuất Excel thành công!"
  public static String EXPORT_EXCEL_SUCCESS;
  //"Xuất Excel không thành công!"
  public static String EXPORT_EXCEL_FAILURE;
  //"Lỗi khi xuất Excel: "
  public static String EXPORT_EXCEL_ERROR;
  //"Bạn có muốn hủy thao tác thêm nhân viên mới không?"
  public static String CONFIRM_CANCEL_ADD_EMPLOYEE;
  //"Tệp hình ảnh không hợp lệ"
  public static String INVALID_IMAGE_FILE;
  //"Lỗi khi xử lý hình ảnh: "
  public static String PROCESS_IMAGE_ERROR;
  //"Lỗi khi hiển thị ảnh đại diện: "
  public static String DISPLAY_AVATAR_ERROR;
  //"Lỗi khi hiển thị ảnh đại diện mặc định: "
  public static String DISPLAY_DEFAULT_AVATAR_ERROR;
  //"Không tìm thấy thông tin nhân viên của người dùng hiện tại"
  public static String CURRENT_USER_EMPLOYEE_NOT_FOUND;
  //"Lỗi khi tải thông tin nhân viên của người dùng hiện tại: "
  public static String LOAD_CURRENT_USER_EMPLOYEE_ERROR;
  //"Bạn đang thêm nhân viên mới. Bạn có muốn tiếp tục thêm không?"
  public static String CONFIRM_CONTINUE_ADD_EMPLOYEE;
  //"Xác nhận"
  public static String CONFIRM_TITLE;//customer messages
  //"Tên khách hàng phải có ít nhất 2 ký tự"
  public static String CUSTOMER_NAME_TOO_SHORT;
  //"Mã khách hàng phải có định dạng KHxx"
  public static String CUSTOMER_ID_FORMAT;
      // Category error messages
  //"Tên danh mục không được để trống"
  public static String CATEGORY_NAME_EMPTY;
  //"Danh mục không thể là danh mục cha của chính nó"
  public static String CATEGORY_SELF_REFERENCE;
  //"Không thể tạo vòng lặp trong cấu trúc danh mục"
  public static String CATEGORY_CIRCULAR_REFERENCE;
  //"Danh mục con không được để trống"
  public static String SUBCATEGORY_NULL;
  //"Sản phẩm không được để trống"
  public static String PRODUCT_NULL;
  //"Danh mục cha không được để trống"
  public static String PARENT_CATEGORY_NULL;    // Discount error messages
  //"Mã khuyến mãi không được để trống"
  public static String DISCOUNT_CODE_EMPTY;
  //"Số tiền giảm giá không được để trống"
  public static String DISCOUNT_AMOUNT_NULL;
  //"Số tiền giảm giá không được âm"
  public static String DISCOUNT_AMOUNT_NEGATIVE;
  //"Số tiền mua tối thiểu không được âm"
  public static String MIN_PURCHASE_NEGATIVE;
  //"Phần trăm giảm giá phải nằm trong khoảng 0-100%"
  public static String DISCOUNT_PERCENTAGE_INVALID;    //"Ngày bắt đầu không được để trống"
  public static String START_DATE_NULL;
  //"Ngày kết thúc không được để trống"
  public static String END_DATE_NULL;
  //"Ngày bắt đầu không thể sau ngày kết thúc"
  public static String START_DATE_AFTER_END_DATE;
  //"Ngày kết thúc không thể trước ngày bắt đầu"
  public static String END_DATE_BEFORE_START_DATE;
  //"Giới hạn sử dụng không được âm"
  public static String USAGE_LIMIT_NEGATIVE;
  public static String USAGE_COUNT_NEGATIVE;
  public static String CATEGORY_NULL;
  public static String DISCOUNT_INVALID;
  public static String USAGE_LIMIT_EXCEEDED;
    // Discount specific messages
  //"Phần trăm giảm giá phải nằm trong khoảng 0-100%%"
  public static String DISCOUNT_PERCENTAGE_RANGE;
  //"Ngày bắt đầu không thể sau ngày kết thúc"
  public static String DISCOUNT_START_DATE_AFTER_END_DATE;
  //"Ngày kết thúc không thể trước ngày bắt đầu"
  public static String DISCOUNT_END_DATE_BEFORE_START_DATE;
  //"Đã vượt quá giới hạn sử dụng"
  public static String DISCOUNT_USAGE_LIMIT_EXCEEDED;    // Product error messages
  //"Tên sản phẩm không được để trống"
  public static String PRODUCT_NAME_EMPTY;
  //"Mã sản phẩm không được để trống"
  public static String PRODUCT_CODE_EMPTY;
  //"Giá sản phẩm không được âm"
  public static String PRODUCT_PRICE_NEGATIVE;
  //"Danh mục sản phẩm không được để trống"
  public static String PRODUCT_CATEGORY_NULL;
  //"Số lượng sản phẩm không được âm"
  public static String PRODUCT_QUANTITY_NEGATIVE;    //"Mô tả sản phẩm không được để trống"
  public static String PRODUCT_DESCRIPTION_EMPTY;
  //"Nhà cung cấp không được để trống"
  public static String PRODUCT_SUPPLIER_NULL;
  //"Thông tin bảo hành không được để trống"
  public static String PRODUCT_WARRANTY_NULL;
  //"Số lượng tồn kho không đủ (%d < %d)"
  public static String PRODUCT_INSUFFICIENT_STOCK;
  //"Số lượng sản phẩm phải lớn hơn 0"
  public static String PRODUCT_QUANTITY_NOT_POSITIVE;
  //"Nhập số lượng sản phẩm:"
  public static String ENTER_PRODUCT_QUANTITY;

    // Customer error messages
  //"Tên khách hàng không được để trống"
  public static String CUSTOMER_NAME_EMPTY;
  //"Số điện thoại khách hàng không được để trống"
  public static String CUSTOMER_PHONE_EMPTY;
  //"Số điện thoại không hợp lệ"
  public static String CUSTOMER_PHONE_INVALID;
  //"Email không hợp lệ"
  public static String CUSTOMER_EMAIL_INVALID;
    // Employee error messages
  //"Tên nhân viên không được để trống"
  public static String EMPLOYEE_NAME_EMPTY;
  //"Số điện thoại nhân viên không được để trống"
  public static String EMPLOYEE_PHONE_EMPTY;
  //"Số điện thoại không hợp lệ"
  public static String EMPLOYEE_PHONE_INVALID;
  //"Email không hợp lệ"
  public static String EMPLOYEE_EMAIL_INVALID;
  //"Chức vụ không được để trống"
  public static String EMPLOYEE_POSITION_EMPTY;
    // Invoice error messages
  //"Khách hàng không được để trống"
  public static String INVOICE_CUSTOMER_NULL;
  //"Nhân viên không được để trống"
  public static String INVOICE_EMPLOYEE_NULL;
  //"Tổng tiền hóa đơn không được âm"
  public static String INVOICE_TOTAL_NEGATIVE;
  //"Hóa đơn phải có ít nhất một chi tiết"
  public static String INVOICE_DETAILS_EMPTY;
  // public static String INVOICE_DETAILS_EMPTY = "Hóa đơn phải có ít nhất một sản phẩm";
    // Invoice Detail error messages
  //"Sản phẩm không được để trống"
  public static String INVOICE_DETAIL_PRODUCT_NULL;
  //"Số lượng không được âm hoặc bằng 0"
  public static String INVOICE_DETAIL_QUANTITY_NEGATIVE;
  //"Đơn giá không được âm hoặc bằng 0"
  public static String INVOICE_DETAIL_PRICE_NEGATIVE;    // Invoice Detail controller specific messages
  //"Lỗi khởi tạo controller: %s"
  public static String INVOICE_DETAIL_CONTROLLER_INIT_ERROR;
  //"Lỗi khi thêm chi tiết hóa đơn: %s"
  public static String INVOICE_DETAIL_ADD_ERROR;
  //"Lỗi khi cập nhật chi tiết hóa đơn: %s"
  public static String INVOICE_DETAIL_UPDATE_ERROR;
  //"Lỗi khi xóa chi tiết hóa đơn: %s"
  public static String INVOICE_DETAIL_DELETE_ERROR;
  //"Lỗi khi tìm chi tiết hóa đơn: %s"
  public static String INVOICE_DETAIL_FIND_ERROR;
  //"Không tìm thấy sản phẩm"
  public static String PRODUCT_NOT_FOUND;    // Invoice controller specific messages
  //"Lỗi khởi tạo controller: %s"
  public static String INVOICE_CONTROLLER_INIT_ERROR;
  //"Lỗi khi tạo hóa đơn: %s"
  public static String INVOICE_CREATE_ERROR;
  //"Lỗi khi tải danh sách hóa đơn: %s"
  public static String INVOICE_LOAD_ERROR;
  //"Lỗi khi tải chi tiết hóa đơn: %s"
  public static String INVOICE_DETAIL_LOAD_ERROR;
  //"Lỗi khi thêm sản phẩm vào hóa đơn: %s"
  public static String INVOICE_ADD_PRODUCT_ERROR;    //"Lỗi khi cập nhật số lượng sản phẩm: %s"
  public static String INVOICE_UPDATE_QUANTITY_ERROR;
  //"Lỗi khi xóa sản phẩm khỏi hóa đơn: %s"
  public static String INVOICE_REMOVE_PRODUCT_ERROR;
  //"Lỗi khi hoàn thành hóa đơn: %s"
  public static String INVOICE_COMPLETE_ERROR;
  //"Lỗi khi hủy hóa đơn: %s"
  public static String INVOICE_CANCEL_ERROR;
  //"Lỗi khi xóa hóa đơn: %s"
  public static String INVOICE_DELETE_ERROR;    //"Lỗi khi xử lý thanh toán: %s"
  public static String INVOICE_PAYMENT_ERROR;
  //"Lỗi khi cập nhật trạng thái hóa đơn: %s"
  public static String INVOICE_STATUS_UPDATE_ERROR;
  //"Lỗi khi in hóa đơn: %s"
  public static String INVOICE_PRINT_ERROR;
  //"Lỗi khi xuất Excel: %s"
  public static String INVOICE_EXPORT_EXCEL_ERROR;
  //"Vui lòng chọn hóa đơn cần in!"
  public static String INVOICE_SELECT_TO_PRINT;    //"Vui lòng chọn hóa đơn cần thanh toán!"
  public static String INVOICE_SELECT_TO_PAY;
  //"Hóa đơn đã được thanh toán!"
  public static String INVOICE_ALREADY_PAID;
  //"Không tìm thấy hóa đơn!"
  public static String INVOICE_NOT_FOUND;
  //"Vui lòng tích chọn ít nhất một hóa đơn để xóa!"
  public static String INVOICE_SELECT_TO_DELETE;
  //"Bạn có chắc chắn muốn xóa %d hóa đơn đã chọn?"
  public static String INVOICE_DELETE_CONFIRM;    //"Đã xóa thành công %d hóa đơn.\n"
  public static String INVOICE_DELETE_RESULT;
  //"Không thể xóa %d hóa đơn.\n\n"
  public static String INVOICE_DELETE_FAILED;
  //"Chi tiết lỗi:\n"
  public static String INVOICE_DELETE_DETAILS;
  //"Hóa đơn #%d: Không thể xóa hóa đơn đã thanh toán hoặc đã giao hàng!"
  public static String INVOICE_PAID_OR_DELIVERED;
  //"Hóa đơn #%d: Xóa thất bại!"
  public static String INVOICE_DELETE_FAIL_DETAIL;    //"Hóa đơn không tồn tại"
  public static String INVOICE_NOT_EXISTS;
  //"Hóa đơn không có sản phẩm nào"
  public static String INVOICE_NO_PRODUCTS;
  //"Không thể hủy hóa đơn ở trạng thái %s"
  public static String INVOICE_CANNOT_CANCEL;
  //"Hóa đơn chưa hoàn thành thanh toán!"
  public static String INVOICE_PRINT_INCOMPLETE;
  //"Bạn có muốn in hóa đơn này không?"
  public static String INVOICE_PRINT_CONFIRM;    //"In hóa đơn thành công!"
  public static String INVOICE_PRINT_SUCCESS;
  //"In hóa đơn thất bại!"
  public static String INVOICE_PRINT_FAILED;
  //"Thanh toán hóa đơn thành công!"
  public static String INVOICE_PAYMENT_SUCCESS;
  //"Không có dữ liệu hóa đơn để xuất"
  public static String INVOICE_EXPORT_NO_DATA;
  //"Xuất Excel thành công!"
  public static String INVOICE_EXPORT_SUCCESS;    //"Xuất Excel không thành công!"
  public static String INVOICE_EXPORT_FAILED;
  //"Số lượng sản phẩm không đủ. Hiện chỉ còn %d"
  public static String PRODUCT_INSUFFICIENT_QUANTITY;
  //"Không thể xóa hóa đơn đã thanh toán hoặc đã giao"
  public static String CANNOT_DELETE_INVOICE;    // Supplier error messages
  //"Tên nhà cung cấp không được để trống"
  public static String SUPPLIER_NAME_EMPTY;
  //"Số điện thoại nhà cung cấp không được để trống"
  public static String SUPPLIER_PHONE_EMPTY;
  //"Số điện thoại không hợp lệ"
  public static String SUPPLIER_PHONE_INVALID;
  //"Email không hợp lệ"
  public static String SUPPLIER_EMAIL_INVALID;
    // Purchase Order error messages
  //"Nhà cung cấp không được để trống"
  public static String PURCHASE_ORDER_SUPPLIER_NULL;
  //"Nhân viên không được để trống"
  public static String PURCHASE_ORDER_EMPLOYEE_NULL;
  //"Tổng tiền đơn hàng không được âm"
  public static String PURCHASE_ORDER_TOTAL_NEGATIVE;
  //"Đơn đặt hàng phải có ít nhất một chi tiết"
  public static String PURCHASE_ORDER_DETAILS_EMPTY;
  // public static String PURCHASE_ORDER_DETAILS_EMPTY = "Đơn đặt hàng phải có ít nhất một sản phẩm";
    // Return error messages
  //"Hóa đơn trả hàng không được để trống"
  public static String RETURN_INVOICE_NULL;
  //"Lý do trả hàng không được để trống"
  public static String RETURN_REASON_EMPTY;
  //"Ngày trả hàng không hợp lệ"
  public static String RETURN_DATE_INVALID;    //"Chỉ có thể chấp nhận đơn trả hàng đang chờ xử lý"
  public static String RETURN_APPROVE_PENDING_ONLY;
  //"Chỉ có thể từ chối đơn trả hàng đang chờ xử lý"
  public static String RETURN_REJECT_PENDING_ONLY;
  //"Chỉ có thể hoàn thành đơn trả hàng đã được chấp nhận"
  public static String RETURN_COMPLETE_APPROVED_ONLY;    //"Số lượng trả phải lớn hơn 0"
  public static String RETURN_QUANTITY_POSITIVE;
  //"Không thể chuyển sang trạng thái %s"
  public static String RETURN_CANNOT_TRANSITION;
    // RepairService error messages
  //"Khách hàng không được để trống"
  public static String REPAIR_CUSTOMER_NULL;
  //"Sản phẩm cần sửa chữa không được để trống"
  public static String REPAIR_PRODUCT_NULL;
  //"Mô tả lỗi không được để trống"
  public static String REPAIR_DESCRIPTION_EMPTY;
  //"Trạng thái sửa chữa không hợp lệ"
  public static String REPAIR_STATUS_INVALID;
  //"Chi phí sửa chữa không được âm"
  public static String REPAIR_COST_NEGATIVE;    //Validation messages
  //"Số điện thoại không hợp lệ"
  public static String INVALID_PHONE_NUMBER;
  //"Email không hợp lệ"
  public static String INVALID_EMAIL;
    // Login related errors
  //"Lỗi đăng nhập"
  public static String LOGIN_ERROR;
  //"Tên đăng nhập hoặc mật khẩu không đúng"
  public static String AUTHENTICATION_FAILED;
  //"Lỗi khởi tạo dịch vụ đăng nhập: %s"
  public static String LOGIN_SERVICE_ERROR;
  //"Lỗi dịch vụ người dùng: %s"
  public static String USER_SERVICE_ERROR;
    // Forgot password related messages
  //"Vui lòng nhập địa chỉ email!"
  public static String EMAIL_EMPTY;
  //"Địa chỉ email không hợp lệ!"
  public static String EMAIL_INVALID;
  //"Email không tồn tại trong hệ thống!"
  public static String EMAIL_NOT_FOUND;
  //"Mã OTP đã được gửi đến email của bạn!"
  public static String OTP_SENT_SUCCESS;
  //"Không thể gửi mã OTP. Vui lòng thử lại sau!"
  public static String OTP_SENT_FAILED;    //"Vui lòng nhập đầy đủ thông tin!"
  public static String FIELDS_REQUIRED;
  //"Mã OTP không chính xác!"
  public static String OTP_INVALID;
  //"Mật khẩu xác nhận không khớp với mật khẩu mới!"
  public static String PASSWORD_MISMATCH;
  //"Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!"
  public static String PASSWORD_REQUIREMENT;
  //"Đặt lại mật khẩu thành công!"
  public static String PASSWORD_RESET_SUCCESS;    //"Không thể cập nhật mật khẩu. Vui lòng thử lại sau!"
  public static String PASSWORD_RESET_FAILED;
  //"Lỗi kết nối đến cơ sở dữ liệu: %s"
  public static String DATABASE_CONNECTION_ERROR;
  //"Lỗi hệ thống: %s"
  public static String SYSTEM_ERROR;    // Database error messages
  //"Không thể kết nối đến database"
  public static String DB_CONNECTION_ERROR;
  //"Không tìm thấy JDBC driver"
  public static String DB_DRIVER_ERROR;
    // Database specific errors
  //"Lỗi khởi tạo DatabaseConnection: %s"
  public static String DB_CONNECTION_INIT_ERROR;
  //"Lỗi khi kiểm tra kết nối: %s"
  public static String DB_CONNECTION_CHECK_ERROR;
  //"Không thể tạo lại kết nối: %s"
  public static String DB_CONNECTION_RECREATE_ERROR;
  //"Lỗi khi đóng kết nối: %s"
  public static String DB_CONNECTION_CLOSE_ERROR;    //"Kết nối không hợp lệ, tạo mới..."
  public static String DB_CONNECTION_INVALID;
  //"Kết nối null hoặc đã đóng, tạo mới..."
  public static String DB_CONNECTION_NULL_OR_CLOSED;
  //"Tạo kết nối database thành công"
  public static String DB_CONNECTION_SUCCESSFUL;
  //"Đã đóng kết nối database"
  public static String DB_CONNECTION_CLOSED;
    // Input validation errors
  
    // Encryption errors
  //"Lỗi khi mã hóa mật khẩu: %s"
  public static String PASSWORD_HASH_ERROR;
  //"Lỗi khi xác thực mật khẩu: %s"
  public static String PASSWORD_VERIFY_ERROR;
  //"Đã xảy ra lỗi khi xác thực người dùng: %s"
  public static String AUTHENTICATION_ERROR;    // Dashboard related messages
  //"Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?"
  public static String LOGOUT_CONFIRM;
  //"Xác nhận đăng xuất"
  public static String LOGOUT_TITLE;
  //"Bạn cần đăng nhập để sử dụng hệ thống"
  public static String LOGIN_REQUIRED;    //"Yêu cầu đăng nhập"
  public static String LOGIN_REQUIRED_TITLE;
  //"Đã xảy ra lỗi khi mở trang đăng nhập: %s"
  public static String LOGIN_REDIRECT_ERROR;
  //"Lỗi"
  
    // Language change messages
  //"Cần khởi động lại ứng dụng để áp dụng thay đổi ngôn ngữ. Bạn có muốn tiếp tục không?"
  public static String LANGUAGE_CHANGE_CONFIRM_VI;
  //"The application needs to restart to apply language changes. Do you want to continue?"
  public static String LANGUAGE_CHANGE_CONFIRM_EN;
  //"Thay đổi ngôn ngữ"
  public static String LANGUAGE_CHANGE_TITLE_VI;
  //"Language Change"
  public static String LANGUAGE_CHANGE_TITLE_EN;
  //"Lỗi khởi động lại ứng dụng: "
  public static String RESTART_ERROR_VI;
  //"Error restarting application:"
  public static String RESTART_ERROR_EN;    // Customer controller specific messages
  //"Lỗi khởi tạo controller: %s"
  public static String CUSTOMER_CONTROLLER_INIT_ERROR;
  //"Lỗi khi tải danh sách khách hàng: %s"
  public static String CUSTOMER_LOAD_ERROR;
  //"Lỗi khi tải thông tin khách hàng: %s"
  public static String CUSTOMER_DETAILS_LOAD_ERROR;
  //"Mã khách hàng, họ tên và số điện thoại không được để trống"
  public static String CUSTOMER_REQUIRED_FIELDS;    //"Điểm tích lũy không được âm"
  public static String CUSTOMER_POINTS_NEGATIVE;
  //"Điểm tích lũy phải là số nguyên"
  public static String CUSTOMER_POINTS_INTEGER;
  //"Mã khách hàng đã tồn tại"
  public static String CUSTOMER_ID_EXISTS;
  //"Xác nhận thêm mới khách hàng?"
  public static String CUSTOMER_ADD_CONFIRM;
  //"Thêm khách hàng mới thành công"
  public static String CUSTOMER_ADD_SUCCESS;    //"Cập nhật thông tin khách hàng thành công"
  public static String CUSTOMER_UPDATE_SUCCESS;
  //"Lỗi khi thêm khách hàng: %s"
  public static String CUSTOMER_ADD_ERROR;
  //"Lỗi khi cập nhật khách hàng: %s"
  public static String CUSTOMER_UPDATE_ERROR;    //"Vui lòng chọn khách hàng cần cập nhật"
  public static String CUSTOMER_SELECT_UPDATE;
  //"Vui lòng chọn khách hàng cần xóa"
  public static String CUSTOMER_SELECT_DELETE;
  //"Bạn có chắc chắn muốn xóa khách hàng %s?"
  public static String CUSTOMER_DELETE_CONFIRM;    //"Xác nhận xóa"
  public static String CUSTOMER_DELETE_TITLE;
  //"Xóa khách hàng thành công"
  public static String CUSTOMER_DELETE_SUCCESS;
  //"Lỗi khi xóa khách hàng: %s"
  public static String CUSTOMER_DELETE_ERROR;
  //"Không thể xóa khách hàng. Khách hàng có thể đã có giao dịch trong hệ thống."
  public static String CUSTOMER_DELETE_CONSTRAINT;    
  //"Lỗi khi tìm kiếm khách hàng: %s"
  public static String CUSTOMER_SEARCH_ERROR;
  //"Không có dữ liệu để xuất"
  public static String CUSTOMER_EXPORT_NO_DATA;
  //"Xuất Excel thành công!"
  public static String CUSTOMER_EXPORT_SUCCESS;
  //"Xuất Excel không thành công!"
  public static String CUSTOMER_EXPORT_FAILED;    //"Lỗi khi xuất Excel: %s"
  public static String CUSTOMER_EXPORT_ERROR;
  //"Bạn có muốn tiếp tục thêm khách hàng không?"
  public static String CUSTOMER_ADD_CONTINUE;
  //"Bạn có muốn hủy thao tác thêm khách hàng mới không?"
  public static String CUSTOMER_ADD_CANCEL;    //"Thông báo"
  public static String INFO_TITLE;
  //"Số điểm tích lũy không đủ để trừ"
  public static String INSUFFICIENT_POINTS;
  //"Lỗi khi tìm khách hàng theo ID: %s"
  public static String CUSTOMER_FIND_BY_ID_ERROR;
  //"Lỗi khi tìm khách hàng theo số điện thoại: %s"
  public static String CUSTOMER_FIND_BY_PHONE_ERROR;    //"Lỗi khi cập nhật điểm tích lũy: %s"
  public static String CUSTOMER_POINTS_UPDATE_ERROR;
  //"Lỗi khi cộng điểm tích lũy: %s"
  public static String CUSTOMER_POINTS_ADD_ERROR;
  //"Lỗi khi trừ điểm tích lũy: %s"
  public static String CUSTOMER_POINTS_DEDUCT_ERROR;    // Home controller messages
  //"Lỗi khi khởi tạo controller: %s"
  public static String HOME_CONTROLLER_INIT_ERROR;
  //"Lỗi khi tải thông tin người dùng: %s"
  public static String USER_INFO_LOAD_ERROR;
  //"Lỗi khi tải thống kê hôm nay: %s"
  public static String TODAY_STATS_LOAD_ERROR;
  //"Lỗi khi tải thống kê tháng: %s"
  public static String MONTH_STATS_LOAD_ERROR;    //"Lỗi khi tải biểu đồ doanh thu theo tuần: %s"
  public static String WEEKLY_CHART_LOAD_ERROR;
  //"Lỗi khi lấy dữ liệu doanh thu theo ngày: %s"
  public static String DAILY_REVENUE_DATA_ERROR;
  //"Lỗi khi lấy tổng doanh thu: %s"
  public static String TOTAL_REVENUE_ERROR;    // User controller specific messages
  //"Lỗi khởi tạo controller: %s"
  public static String USER_CONTROLLER_INIT_ERROR;
  //"Lỗi khi tải danh sách người dùng: %s"
  public static String USER_LOAD_ERROR;
  //"Lỗi khi lọc danh sách người dùng: %s"
  public static String USER_FILTER_ERROR;
  //"Lỗi khi tải thông tin người dùng: %s"
  public static String USER_DETAILS_LOAD_ERROR;    //"Vui lòng chọn nhân viên"
  public static String USER_SELECT_EMPLOYEE;
  //"Vui lòng điền đầy đủ thông tin tài khoản"
  public static String USER_COMPLETE_INFORMATION;
  //"Tên đăng nhập đã tồn tại"
  public static String USERNAME_ALREADY_EXISTS;
  //"Xác nhận thêm mới người dùng?"
  public static String USER_ADD_CONFIRM;    //"Xác nhận thêm"
  public static String USER_ADD_CONFIRM_TITLE;
  //"Thêm người dùng thành công"
  public static String USER_ADD_SUCCESS;
  //"Lỗi khi thêm người dùng: %s"
  public static String USER_ADD_ERROR;
  //"Vui lòng chọn người dùng cần cập nhật"
  public static String USER_SELECT_TO_UPDATE;    //"Bạn chỉ có thể cập nhật thông tin của mình"
  public static String USER_UPDATE_OWN_INFO_ONLY;
  //"Vui lòng điền đầy đủ thông tin"
  public static String USER_COMPLETE_INFO;
  //"Xác nhận cập nhật thông tin?"
  public static String USER_UPDATE_CONFIRM;
  //"Xác nhận cập nhật"
  public static String USER_UPDATE_CONFIRM_TITLE;    //"Cập nhật người dùng thành công"
  public static String USER_UPDATE_SUCCESS;
  //"Lỗi khi cập nhật người dùng: %s"
  public static String USER_UPDATE_ERROR;
  //"Vui lòng chọn người dùng cần xóa"
  public static String USER_SELECT_TO_DELETE;
  //"Bạn có chắc chắn muốn xóa người dùng này?"
  public static String USER_DELETE_CONFIRM;    //"Xác nhận xóa"
  public static String USER_DELETE_CONFIRM_TITLE;
  //"Xóa người dùng thành công"
  public static String USER_DELETE_SUCCESS;
  //"Lỗi khi xóa người dùng: %s"
  public static String USER_DELETE_ERROR;
  //"Vui lòng chọn người dùng cần đặt lại mật khẩu"
  public static String USER_SELECT_TO_RESET_PASSWORD;    //"Bạn có chắc chắn muốn đặt lại mật khẩu cho User: %s?"
  public static String USER_RESET_PASSWORD_CONFIRM;
  //"Xác nhận đặt lại mật khẩu"
  public static String USER_RESET_PASSWORD_CONFIRM_TITLE;
  //"Đặt lại mật khẩu thành công"
  public static String USER_RESET_PASSWORD_SUCCESS;
  //"Mật khẩu mới: %s"
  public static String USER_RESET_PASSWORD_NEW;    //"Lỗi khi đặt lại mật khẩu: %s"
  public static String USER_RESET_PASSWORD_ERROR;
  //"Lỗi khi mở hộp thoại đổi mật khẩu: %s"
  public static String USER_CHANGE_PASSWORD_ERROR;
  //"Lỗi khi tải thông tin người dùng"
  public static String USER_LOAD_CURRENT_ERROR;
  //"Vui lòng nhập đầy đủ thông tin"
  public static String PASSWORD_CHANGE_REQUIRED_FIELDS;    //"Mật khẩu mới không khớp với xác nhận mật khẩu"
  public static String PASSWORD_NEW_MISMATCH;
  //"Mật khẩu mới phải có ít nhất 6 ký tự"
  public static String PASSWORD_MIN_LENGTH;
  //"Mật khẩu hiện tại không đúng"
  public static String PASSWORD_CURRENT_INCORRECT;
  //"Đổi mật khẩu thành công"
  public static String PASSWORD_CHANGE_SUCCESS;
  //"Đổi mật khẩu thất bại"
  public static String PASSWORD_CHANGE_FAILED;
  //"Lỗi khi đổi mật khẩu: %s"
  public static String PASSWORD_CHANGE_ERROR;// Payment controller specific messages
  //"Lỗi khi xử lý thanh toán: %s"
  public static String PAYMENT_PROCESSING_ERROR;
  //"Thanh toán thành công! Số tiền thối lại: %s đ"
  public static String PAYMENT_SUCCESS_CHANGE;
  //"Chức năng này đang bảo trì! Vui lòng chọn phương thức thanh toán khác"
  public static String PAYMENT_METHOD_MAINTENANCE;
  //"Xác nhận đã thanh toán thành công?"
  public static String PAYMENT_CONFIRM;
  //"Xác nhận thanh toán"
  public static String PAYMENT_CONFIRM_TITLE;

  // File related messages
  //"File đã tồn tại. Bạn có muốn ghi đè không?"
  public static String FILE_EXISTS_OVERWRITE;
  //"Đường dẫn: "
  public static String FILE_PATH;
  //"Không thể tự động mở file: %s"
  public static String CANNOT_OPEN_FILE;

  // Sell controller messages
  //"Không tìm thấy khách hàng mặc định. Vui lòng kiểm tra lại."
  public static String DEFAULT_CUSTOMER_NOT_FOUND;
  //"Lỗi khi cập nhật bảng giỏ hàng: %s"
  public static String CART_UPDATE_ERROR;
  //"Không đủ số lượng tồn kho. Chỉ còn %d sản phẩm."
  public static String INSUFFICIENT_STOCK;
  //"Không thể lưu hóa đơn. Giỏ hàng trống hoặc thông tin hóa đơn không hợp lệ."
  public static String INVALID_INVOICE_SAVE;
  //"Lỗi khi lưu hóa đơn. Vui lòng thử lại."
  public static String INVOICE_SAVE_ERROR;
  //"Lỗi khi lưu chi tiết hóa đơn. Vui lòng thử lại."
  public static String INVOICE_DETAIL_SAVE_ERROR;
  //"Lỗi không xác định: %s"
  public static String UNKNOWN_ERROR;
  //"Không thể hoàn thành giao dịch. Hóa đơn không hợp lệ."
  public static String INVALID_INVOICE_COMPLETE;
  //"Lỗi khi xuất hóa đơn: %s"
  public static String EXPORT_INVOICE_ERROR;
  //"Xuất hóa đơn thất bại. Vui lòng thử lại."
  public static String EXPORT_INVOICE_FAILED;
  //"Hóa đơn đã sử dụng điểm."
  public static String POINTS_ALREADY_USED;

  // SellForm specific messages
  //"Lỗi khởi tạo form bán hàng: %s"
  public static String FORM_INIT_ERROR;
  //"Vui lòng chọn sản phẩm để xóa khỏi giỏ hàng."
  public static String SELECT_PRODUCT_TO_DELETE;
  //"Bạn có chắc chắn muốn xóa sản phẩm đã chọn khỏi giỏ hàng không?"
  public static String CONFIRM_DELETE_PRODUCT;
  //"Khách hàng chưa đủ điểm để áp dụng ưu đãi. Cần ít nhất 10,000 điểm."
  public static String CUSTOMER_POINTS_INSUFFICIENT;
  //"Khách hàng có %d điểm tích lũy.\nBạn có muốn sử dụng điểm để giảm giá không?"
  public static String CONFIRM_USE_POINTS;
  //"Vui lòng chọn khách hàng trước khi áp dụng ưu đãi điểm."
  public static String CUSTOMER_SELECT_REQUIRED;
  //"Giỏ hàng trống. Vui lòng thêm sản phẩm trước khi thanh toán."
  public static String EMPTY_CART;
  //"Số lượng không hợp lệ"
  public static String INVALID_QUANTITY;
  //"Số lượng phải lớn hơn 0"
  public static String QUANTITY_MUST_BE_POSITIVE;
  //"Vui lòng chọn sản phẩm để thêm vào giỏ hàng"
  public static String SELECT_PRODUCT_TO_ADD;
  //"Không thể cập nhật số lượng. Số lượng vượt quá tồn kho hoặc có lỗi khác."
  public static String QUANTITY_UPDATE_ERROR;
  //"Bạn có chắc chắn muốn thanh toán hóa đơn không?"
  public static String CONFIRM_SAVE_INVOICE;
  //"Lỗi khi lưu hóa đơn. Vui lòng thử lại."
  public static String SAVE_INVOICE_ERROR;
  //"Lưu hóa đơn thành công! ID: %s"
  public static String SAVE_INVOICE_SUCCESS;
  //"Thanh toán thành công!"
  public static String PAYMENT_SUCCESS;
  //"Thanh toán không thành công!"
  public static String PAYMENT_FAILED;
  //"Bạn có chắc chắn muốn làm mới giỏ hàng không?"
  public static String CONFIRM_RESET_CART;
  //"Vui lòng nhập tên khách hàng"
  public static String CUSTOMER_NAME_REQUIRED;

  // Bank payment related messages
  //"Không thể mở trình duyệt để thanh toán"
  public static String BROWSER_OPEN_ERROR;
  //"Vui lòng hoàn tất thanh toán trong trình duyệt.\nBấm OK khi đã thanh toán xong hoặc Cancel để hủy."
  public static String PAYMENT_WAITING_MESSAGE;
  //"Không thể xác nhận thanh toán. Vui lòng kiểm tra lại sau."
  public static String PAYMENT_VERIFICATION_FAILED;
  //"Cảnh báo"
  public static String WARNING_TITLE;
  //"Không thể tạo link thanh toán"
  public static String PAYMENT_LINK_CREATE_ERROR;

  static {
      refresh();
  }

  public static void refresh(){
    FIELD_EMPTY = LM.getMessageResourceBundle().getString("FIELD_EMPTY");
    FIELD_NEGATIVE = LM.getMessageResourceBundle().getString("FIELD_NEGATIVE");
    INVALID_VALUE = LM.getMessageResourceBundle().getString("INVALID_VALUE");
    VALUE_MUST_BE_POSITIVE = LM.getMessageResourceBundle().getString("VALUE_MUST_BE_POSITIVE");
    ERROR_TITLE = LM.getMessageResourceBundle().getString("ERROR_TITLE");
    
    // User messages
    
    USERNAME_EMPTY = LM.getMessageResourceBundle().getString("USERNAME_EMPTY");
    USERNAME_TOO_SHORT = LM.getMessageResourceBundle().getString("USERNAME_TOO_SHORT");
    PASSWORD_EMPTY = LM.getMessageResourceBundle().getString("PASSWORD_EMPTY");
    PASSWORD_TOO_SHORT = LM.getMessageResourceBundle().getString("PASSWORD_TOO_SHORT");
    ROLE_EMPTY = LM.getMessageResourceBundle().getString("ROLE_EMPTY");
    USERNAME_EXISTS = LM.getMessageResourceBundle().getString("USERNAME_EXISTS");
    USER_NOT_EXISTS = LM.getMessageResourceBundle().getString("USER_NOT_EXISTS");
    USERNAME_EXISTS_EN = LM.getMessageResourceBundle().getString("USERNAME_EXISTS_EN");  
    
    //Employee messages
    
    EMPLOYEE_ID_FORMAT = LM.getMessageResourceBundle().getString("EMPLOYEE_ID_FORMAT");
    EMPLOYEE_NAME_TOO_SHORT = LM.getMessageResourceBundle().getString("EMPLOYEE_NAME_TOO_SHORT");
    INVALID_EMPLOYEE_POSITION = LM.getMessageResourceBundle().getString("INVALID_EMPLOYEE_POSITION");
    EMPLOYEE_AGE_18 = LM.getMessageResourceBundle().getString("EMPLOYEE_AGE_18");  
    
    // Employee controller specific messages
    
    EMPLOYEE_LOAD_ERROR = LM.getMessageResourceBundle().getString("EMPLOYEE_LOAD_ERROR");
    EMPLOYEE_FILTER_ERROR = LM.getMessageResourceBundle().getString("EMPLOYEE_FILTER_ERROR");
    EMPLOYEE_GET_INFO_ERROR = LM.getMessageResourceBundle().getString("EMPLOYEE_GET_INFO_ERROR");  
    EMPLOYEE_SELECTION_REQUIRED = LM.getMessageResourceBundle().getString("EMPLOYEE_SELECTION_REQUIRED");
    INIT_CONTROLLER_ERROR = LM.getMessageResourceBundle().getString("INIT_CONTROLLER_ERROR");
    LOAD_EMPLOYEES_ERROR = LM.getMessageResourceBundle().getString("LOAD_EMPLOYEES_ERROR");
    LOAD_EMPLOYEE_DETAILS_ERROR = LM.getMessageResourceBundle().getString("LOAD_EMPLOYEE_DETAILS_ERROR");  
    INVALID_DATE_FORMAT = LM.getMessageResourceBundle().getString("INVALID_DATE_FORMAT");
    EMPTY_FIELDS_ERROR = LM.getMessageResourceBundle().getString("EMPTY_FIELDS_ERROR");
    INVALID_POSITION_ERROR = LM.getMessageResourceBundle().getString("INVALID_POSITION_ERROR");
    DUPLICATE_EMPLOYEE_ID_ERROR = LM.getMessageResourceBundle().getString("DUPLICATE_EMPLOYEE_ID_ERROR");  
    CONFIRM_ADD_EMPLOYEE = LM.getMessageResourceBundle().getString("CONFIRM_ADD_EMPLOYEE");
    ADD_EMPLOYEE_SUCCESS = LM.getMessageResourceBundle().getString("ADD_EMPLOYEE_SUCCESS");
    SELECT_EMPLOYEE_TO_UPDATE = LM.getMessageResourceBundle().getString("SELECT_EMPLOYEE_TO_UPDATE");
    CONFIRM_UPDATE_EMPLOYEE = LM.getMessageResourceBundle().getString("CONFIRM_UPDATE_EMPLOYEE"); 
      
    UPDATE_EMPLOYEE_SUCCESS = LM.getMessageResourceBundle().getString("UPDATE_EMPLOYEE_SUCCESS");
    UPDATE_EMPLOYEE_ERROR = LM.getMessageResourceBundle().getString("UPDATE_EMPLOYEE_ERROR");
    SELECT_EMPLOYEE_TO_DELETE = LM.getMessageResourceBundle().getString("SELECT_EMPLOYEE_TO_DELETE");
    CONFIRM_DELETE_EMPLOYEE = LM.getMessageResourceBundle().getString("CONFIRM_DELETE_EMPLOYEE");
    DELETE_EMPLOYEE_SUCCESS = LM.getMessageResourceBundle().getString("DELETE_EMPLOYEE_SUCCESS");
    DELETE_EMPLOYEE_ERROR = LM.getMessageResourceBundle().getString("DELETE_EMPLOYEE_ERROR");
    SEARCH_EMPLOYEE_ERROR = LM.getMessageResourceBundle().getString("SEARCH_EMPLOYEE_ERROR");  
    NO_DATA_TO_EXPORT = LM.getMessageResourceBundle().getString("NO_DATA_TO_EXPORT");
    EXPORT_EXCEL_SUCCESS = LM.getMessageResourceBundle().getString("EXPORT_EXCEL_SUCCESS");
    EXPORT_EXCEL_FAILURE = LM.getMessageResourceBundle().getString("EXPORT_EXCEL_FAILURE");
    EXPORT_EXCEL_ERROR = LM.getMessageResourceBundle().getString("EXPORT_EXCEL_ERROR");
    CONFIRM_CANCEL_ADD_EMPLOYEE = LM.getMessageResourceBundle().getString("CONFIRM_CANCEL_ADD_EMPLOYEE");
    INVALID_IMAGE_FILE = LM.getMessageResourceBundle().getString("INVALID_IMAGE_FILE");
    PROCESS_IMAGE_ERROR = LM.getMessageResourceBundle().getString("PROCESS_IMAGE_ERROR");
    DISPLAY_AVATAR_ERROR = LM.getMessageResourceBundle().getString("DISPLAY_AVATAR_ERROR");
    DISPLAY_DEFAULT_AVATAR_ERROR = LM.getMessageResourceBundle().getString("DISPLAY_DEFAULT_AVATAR_ERROR");
    CURRENT_USER_EMPLOYEE_NOT_FOUND = LM.getMessageResourceBundle().getString("CURRENT_USER_EMPLOYEE_NOT_FOUND");
    LOAD_CURRENT_USER_EMPLOYEE_ERROR = LM.getMessageResourceBundle().getString("LOAD_CURRENT_USER_EMPLOYEE_ERROR");
    CONFIRM_CONTINUE_ADD_EMPLOYEE = LM.getMessageResourceBundle().getString("CONFIRM_CONTINUE_ADD_EMPLOYEE");
    CONFIRM_TITLE = LM.getMessageResourceBundle().getString("CONFIRM_TITLE");
    
    //customer messages
    
    CUSTOMER_NAME_TOO_SHORT = LM.getMessageResourceBundle().getString("CUSTOMER_NAME_TOO_SHORT");
    CUSTOMER_ID_FORMAT = LM.getMessageResourceBundle().getString("CUSTOMER_ID_FORMAT");    
    
    // Category error messages
    
    CATEGORY_NAME_EMPTY = LM.getMessageResourceBundle().getString("CATEGORY_NAME_EMPTY");
    CATEGORY_SELF_REFERENCE = LM.getMessageResourceBundle().getString("CATEGORY_SELF_REFERENCE");
    CATEGORY_CIRCULAR_REFERENCE = LM.getMessageResourceBundle().getString("CATEGORY_CIRCULAR_REFERENCE");
    SUBCATEGORY_NULL = LM.getMessageResourceBundle().getString("SUBCATEGORY_NULL");
    PRODUCT_NULL = LM.getMessageResourceBundle().getString("PRODUCT_NULL");
    PARENT_CATEGORY_NULL = LM.getMessageResourceBundle().getString("PARENT_CATEGORY_NULL");  
    
    // Discount error messages
    
    DISCOUNT_CODE_EMPTY = LM.getMessageResourceBundle().getString("DISCOUNT_CODE_EMPTY");
    DISCOUNT_AMOUNT_NULL = LM.getMessageResourceBundle().getString("DISCOUNT_AMOUNT_NULL");
    DISCOUNT_AMOUNT_NEGATIVE = LM.getMessageResourceBundle().getString("DISCOUNT_AMOUNT_NEGATIVE");
    MIN_PURCHASE_NEGATIVE = LM.getMessageResourceBundle().getString("MIN_PURCHASE_NEGATIVE");
    DISCOUNT_PERCENTAGE_INVALID = LM.getMessageResourceBundle().getString("DISCOUNT_PERCENTAGE_INVALID");  
    START_DATE_NULL = LM.getMessageResourceBundle().getString("START_DATE_NULL");
    END_DATE_NULL = LM.getMessageResourceBundle().getString("END_DATE_NULL");
    START_DATE_AFTER_END_DATE = LM.getMessageResourceBundle().getString("START_DATE_AFTER_END_DATE");
    END_DATE_BEFORE_START_DATE = LM.getMessageResourceBundle().getString("END_DATE_BEFORE_START_DATE");
    USAGE_LIMIT_NEGATIVE = LM.getMessageResourceBundle().getString("USAGE_LIMIT_NEGATIVE");  
    USAGE_COUNT_NEGATIVE = LM.getMessageResourceBundle().getString("USAGE_COUNT_NEGATIVE");CATEGORY_NULL = LM.getMessageResourceBundle().getString("CATEGORY_NULL");DISCOUNT_INVALID = LM.getMessageResourceBundle().getString("DISCOUNT_INVALID");USAGE_LIMIT_EXCEEDED = LM.getMessageResourceBundle().getString("USAGE_LIMIT_EXCEEDED");  
    
    // Discount specific messages
    
    DISCOUNT_PERCENTAGE_RANGE = LM.getMessageResourceBundle().getString("DISCOUNT_PERCENTAGE_RANGE");
    DISCOUNT_START_DATE_AFTER_END_DATE = LM.getMessageResourceBundle().getString("DISCOUNT_START_DATE_AFTER_END_DATE");
    DISCOUNT_END_DATE_BEFORE_START_DATE = LM.getMessageResourceBundle().getString("DISCOUNT_END_DATE_BEFORE_START_DATE");
    DISCOUNT_USAGE_LIMIT_EXCEEDED = LM.getMessageResourceBundle().getString("DISCOUNT_USAGE_LIMIT_EXCEEDED");  
    
    // Product error messages
    
    PRODUCT_NAME_EMPTY = LM.getMessageResourceBundle().getString("PRODUCT_NAME_EMPTY");
    PRODUCT_CODE_EMPTY = LM.getMessageResourceBundle().getString("PRODUCT_CODE_EMPTY");
    PRODUCT_PRICE_NEGATIVE = LM.getMessageResourceBundle().getString("PRODUCT_PRICE_NEGATIVE");
    PRODUCT_CATEGORY_NULL = LM.getMessageResourceBundle().getString("PRODUCT_CATEGORY_NULL");
    PRODUCT_QUANTITY_NEGATIVE = LM.getMessageResourceBundle().getString("PRODUCT_QUANTITY_NEGATIVE");  
    PRODUCT_DESCRIPTION_EMPTY = LM.getMessageResourceBundle().getString("PRODUCT_DESCRIPTION_EMPTY");
    PRODUCT_SUPPLIER_NULL = LM.getMessageResourceBundle().getString("PRODUCT_SUPPLIER_NULL");
    PRODUCT_WARRANTY_NULL = LM.getMessageResourceBundle().getString("PRODUCT_WARRANTY_NULL");
    PRODUCT_INSUFFICIENT_STOCK = LM.getMessageResourceBundle().getString("PRODUCT_INSUFFICIENT_STOCK");
    PRODUCT_QUANTITY_NOT_POSITIVE = LM.getMessageResourceBundle().getString("PRODUCT_QUANTITY_NOT_POSITIVE");
    ENTER_PRODUCT_QUANTITY = LM.getMessageResourceBundle().getString("ENTER_PRODUCT_QUANTITY");

      // Customer error messages
    
    CUSTOMER_NAME_EMPTY = LM.getMessageResourceBundle().getString("CUSTOMER_NAME_EMPTY");
    CUSTOMER_PHONE_EMPTY = LM.getMessageResourceBundle().getString("CUSTOMER_PHONE_EMPTY");
    CUSTOMER_PHONE_INVALID = LM.getMessageResourceBundle().getString("CUSTOMER_PHONE_INVALID");
    CUSTOMER_EMAIL_INVALID = LM.getMessageResourceBundle().getString("CUSTOMER_EMAIL_INVALID");  
    
    // Employee error messages
    
    EMPLOYEE_NAME_EMPTY = LM.getMessageResourceBundle().getString("EMPLOYEE_NAME_EMPTY");
    EMPLOYEE_PHONE_EMPTY = LM.getMessageResourceBundle().getString("EMPLOYEE_PHONE_EMPTY");
    EMPLOYEE_PHONE_INVALID = LM.getMessageResourceBundle().getString("EMPLOYEE_PHONE_INVALID");
    EMPLOYEE_EMAIL_INVALID = LM.getMessageResourceBundle().getString("EMPLOYEE_EMAIL_INVALID");
    EMPLOYEE_POSITION_EMPTY = LM.getMessageResourceBundle().getString("EMPLOYEE_POSITION_EMPTY");
    
    
    // Invoice error messages  
    
    INVOICE_CUSTOMER_NULL = LM.getMessageResourceBundle().getString("INVOICE_CUSTOMER_NULL");
    INVOICE_EMPLOYEE_NULL = LM.getMessageResourceBundle().getString("INVOICE_EMPLOYEE_NULL");
    INVOICE_TOTAL_NEGATIVE = LM.getMessageResourceBundle().getString("INVOICE_TOTAL_NEGATIVE");
    INVOICE_DETAILS_EMPTY = LM.getMessageResourceBundle().getString("INVOICE_DETAILS_EMPTY");

    // Invoice Detail error messages  
         
    INVOICE_DETAIL_PRODUCT_NULL = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_PRODUCT_NULL");
    INVOICE_DETAIL_QUANTITY_NEGATIVE = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_QUANTITY_NEGATIVE");
    INVOICE_DETAIL_PRICE_NEGATIVE = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_PRICE_NEGATIVE");  
    
    // Invoice Detail controller specific messages    
    INVOICE_DETAIL_CONTROLLER_INIT_ERROR = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_CONTROLLER_INIT_ERROR");
    INVOICE_DETAIL_ADD_ERROR = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_ADD_ERROR");
    INVOICE_DETAIL_UPDATE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_UPDATE_ERROR");
    INVOICE_DETAIL_DELETE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_DELETE_ERROR");
    INVOICE_DETAIL_FIND_ERROR = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_FIND_ERROR");
    PRODUCT_NOT_FOUND = LM.getMessageResourceBundle().getString("PRODUCT_NOT_FOUND");  
    
    // Invoice controller specific messages    
    INVOICE_CONTROLLER_INIT_ERROR = LM.getMessageResourceBundle().getString("INVOICE_CONTROLLER_INIT_ERROR");
    INVOICE_CREATE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_CREATE_ERROR");
    INVOICE_LOAD_ERROR = LM.getMessageResourceBundle().getString("INVOICE_LOAD_ERROR");
    INVOICE_DETAIL_LOAD_ERROR = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_LOAD_ERROR");
    INVOICE_ADD_PRODUCT_ERROR = LM.getMessageResourceBundle().getString("INVOICE_ADD_PRODUCT_ERROR");  
    INVOICE_UPDATE_QUANTITY_ERROR = LM.getMessageResourceBundle().getString("INVOICE_UPDATE_QUANTITY_ERROR");
    INVOICE_REMOVE_PRODUCT_ERROR = LM.getMessageResourceBundle().getString("INVOICE_REMOVE_PRODUCT_ERROR");
    INVOICE_COMPLETE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_COMPLETE_ERROR");
    INVOICE_CANCEL_ERROR = LM.getMessageResourceBundle().getString("INVOICE_CANCEL_ERROR");
    INVOICE_DELETE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_DELETE_ERROR");  
    INVOICE_PAYMENT_ERROR = LM.getMessageResourceBundle().getString("INVOICE_PAYMENT_ERROR");
    INVOICE_STATUS_UPDATE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_STATUS_UPDATE_ERROR");
    INVOICE_PRINT_ERROR = LM.getMessageResourceBundle().getString("INVOICE_PRINT_ERROR");
    INVOICE_EXPORT_EXCEL_ERROR = LM.getMessageResourceBundle().getString("INVOICE_EXPORT_EXCEL_ERROR");
    INVOICE_SELECT_TO_PRINT = LM.getMessageResourceBundle().getString("INVOICE_SELECT_TO_PRINT");  
    INVOICE_SELECT_TO_PAY = LM.getMessageResourceBundle().getString("INVOICE_SELECT_TO_PAY");
    INVOICE_ALREADY_PAID = LM.getMessageResourceBundle().getString("INVOICE_ALREADY_PAID");
    INVOICE_NOT_FOUND = LM.getMessageResourceBundle().getString("INVOICE_NOT_FOUND");
    INVOICE_SELECT_TO_DELETE = LM.getMessageResourceBundle().getString("INVOICE_SELECT_TO_DELETE");
    INVOICE_DELETE_CONFIRM = LM.getMessageResourceBundle().getString("INVOICE_DELETE_CONFIRM");  
    INVOICE_DELETE_RESULT = LM.getMessageResourceBundle().getString("INVOICE_DELETE_RESULT");
    INVOICE_DELETE_FAILED = LM.getMessageResourceBundle().getString("INVOICE_DELETE_FAILED");
    INVOICE_DELETE_DETAILS = LM.getMessageResourceBundle().getString("INVOICE_DELETE_DETAILS");
    INVOICE_PAID_OR_DELIVERED = LM.getMessageResourceBundle().getString("INVOICE_PAID_OR_DELIVERED");
    INVOICE_DELETE_FAIL_DETAIL = LM.getMessageResourceBundle().getString("INVOICE_DELETE_FAIL_DETAIL");  
    INVOICE_NOT_EXISTS = LM.getMessageResourceBundle().getString("INVOICE_NOT_EXISTS");
    INVOICE_NO_PRODUCTS = LM.getMessageResourceBundle().getString("INVOICE_NO_PRODUCTS");
    INVOICE_CANNOT_CANCEL = LM.getMessageResourceBundle().getString("INVOICE_CANNOT_CANCEL");
    INVOICE_PRINT_INCOMPLETE = LM.getMessageResourceBundle().getString("INVOICE_PRINT_INCOMPLETE");
    INVOICE_PRINT_CONFIRM = LM.getMessageResourceBundle().getString("INVOICE_PRINT_CONFIRM");  
    INVOICE_PRINT_SUCCESS = LM.getMessageResourceBundle().getString("INVOICE_PRINT_SUCCESS");
    INVOICE_PRINT_FAILED = LM.getMessageResourceBundle().getString("INVOICE_PRINT_FAILED");
    INVOICE_PAYMENT_SUCCESS = LM.getMessageResourceBundle().getString("INVOICE_PAYMENT_SUCCESS");
    INVOICE_EXPORT_NO_DATA = LM.getMessageResourceBundle().getString("INVOICE_EXPORT_NO_DATA");
    INVOICE_EXPORT_SUCCESS = LM.getMessageResourceBundle().getString("INVOICE_EXPORT_SUCCESS");  
    INVOICE_EXPORT_FAILED = LM.getMessageResourceBundle().getString("INVOICE_EXPORT_FAILED");
    PRODUCT_INSUFFICIENT_QUANTITY = LM.getMessageResourceBundle().getString("PRODUCT_INSUFFICIENT_QUANTITY");
    CANNOT_DELETE_INVOICE = LM.getMessageResourceBundle().getString("CANNOT_DELETE_INVOICE");  
    
    // Supplier error messages    
    SUPPLIER_NAME_EMPTY = LM.getMessageResourceBundle().getString("SUPPLIER_NAME_EMPTY");
    SUPPLIER_PHONE_EMPTY = LM.getMessageResourceBundle().getString("SUPPLIER_PHONE_EMPTY");
    SUPPLIER_PHONE_INVALID = LM.getMessageResourceBundle().getString("SUPPLIER_PHONE_INVALID");
    SUPPLIER_EMAIL_INVALID = LM.getMessageResourceBundle().getString("SUPPLIER_EMAIL_INVALID");  
    
    // Purchase Order error messages    
    PURCHASE_ORDER_SUPPLIER_NULL = LM.getMessageResourceBundle().getString("PURCHASE_ORDER_SUPPLIER_NULL");
    PURCHASE_ORDER_EMPLOYEE_NULL = LM.getMessageResourceBundle().getString("PURCHASE_ORDER_EMPLOYEE_NULL");
    PURCHASE_ORDER_TOTAL_NEGATIVE = LM.getMessageResourceBundle().getString("PURCHASE_ORDER_TOTAL_NEGATIVE");
    PURCHASE_ORDER_DETAILS_EMPTY = LM.getMessageResourceBundle().getString("PURCHASE_ORDER_DETAILS_EMPTY");
    
    // Return error messages    
    RETURN_INVOICE_NULL = LM.getMessageResourceBundle().getString("RETURN_INVOICE_NULL");
    RETURN_REASON_EMPTY = LM.getMessageResourceBundle().getString("RETURN_REASON_EMPTY");
    RETURN_DATE_INVALID = LM.getMessageResourceBundle().getString("RETURN_DATE_INVALID");  
    RETURN_APPROVE_PENDING_ONLY = LM.getMessageResourceBundle().getString("RETURN_APPROVE_PENDING_ONLY");
    RETURN_REJECT_PENDING_ONLY = LM.getMessageResourceBundle().getString("RETURN_REJECT_PENDING_ONLY");
    RETURN_COMPLETE_APPROVED_ONLY = LM.getMessageResourceBundle().getString("RETURN_COMPLETE_APPROVED_ONLY");  
    RETURN_QUANTITY_POSITIVE = LM.getMessageResourceBundle().getString("RETURN_QUANTITY_POSITIVE");
    RETURN_CANNOT_TRANSITION = LM.getMessageResourceBundle().getString("RETURN_CANNOT_TRANSITION");  
    
    // RepairService error messages    
    REPAIR_CUSTOMER_NULL = LM.getMessageResourceBundle().getString("REPAIR_CUSTOMER_NULL");
    REPAIR_PRODUCT_NULL = LM.getMessageResourceBundle().getString("REPAIR_PRODUCT_NULL");
    REPAIR_DESCRIPTION_EMPTY = LM.getMessageResourceBundle().getString("REPAIR_DESCRIPTION_EMPTY");
    REPAIR_STATUS_INVALID = LM.getMessageResourceBundle().getString("REPAIR_STATUS_INVALID");
    REPAIR_COST_NEGATIVE = LM.getMessageResourceBundle().getString("REPAIR_COST_NEGATIVE");  
    
    //Validation messages    
    INVALID_PHONE_NUMBER = LM.getMessageResourceBundle().getString("INVALID_PHONE_NUMBER");
    INVALID_EMAIL = LM.getMessageResourceBundle().getString("INVALID_EMAIL");  
    
    // Login related errors    
    LOGIN_ERROR = LM.getMessageResourceBundle().getString("LOGIN_ERROR");
    AUTHENTICATION_FAILED = LM.getMessageResourceBundle().getString("AUTHENTICATION_FAILED");
    LOGIN_SERVICE_ERROR = LM.getMessageResourceBundle().getString("LOGIN_SERVICE_ERROR");
    USER_SERVICE_ERROR = LM.getMessageResourceBundle().getString("USER_SERVICE_ERROR");  

    // Forgot password related messages
    EMAIL_EMPTY = LM.getMessageResourceBundle().getString("EMAIL_EMPTY");
    EMAIL_INVALID = LM.getMessageResourceBundle().getString("EMAIL_INVALID");
    EMAIL_NOT_FOUND = LM.getMessageResourceBundle().getString("EMAIL_NOT_FOUND");
    OTP_SENT_SUCCESS = LM.getMessageResourceBundle().getString("OTP_SENT_SUCCESS");
    OTP_SENT_FAILED = LM.getMessageResourceBundle().getString("OTP_SENT_FAILED");  
    FIELDS_REQUIRED = LM.getMessageResourceBundle().getString("FIELDS_REQUIRED");
    OTP_INVALID = LM.getMessageResourceBundle().getString("OTP_INVALID");
    PASSWORD_MISMATCH = LM.getMessageResourceBundle().getString("PASSWORD_MISMATCH");
    PASSWORD_REQUIREMENT = LM.getMessageResourceBundle().getString("PASSWORD_REQUIREMENT");
    PASSWORD_RESET_SUCCESS = LM.getMessageResourceBundle().getString("PASSWORD_RESET_SUCCESS");  
    PASSWORD_RESET_FAILED = LM.getMessageResourceBundle().getString("PASSWORD_RESET_FAILED");
    DATABASE_CONNECTION_ERROR = LM.getMessageResourceBundle().getString("DATABASE_CONNECTION_ERROR");
    SYSTEM_ERROR = LM.getMessageResourceBundle().getString("SYSTEM_ERROR"); 
    
    // Database error messages    
    DB_CONNECTION_ERROR = LM.getMessageResourceBundle().getString("DB_CONNECTION_ERROR");
    DB_DRIVER_ERROR = LM.getMessageResourceBundle().getString("DB_DRIVER_ERROR");  
    // Database specific errors
    DB_CONNECTION_INIT_ERROR = LM.getMessageResourceBundle().getString("DB_CONNECTION_INIT_ERROR");
    DB_CONNECTION_CHECK_ERROR = LM.getMessageResourceBundle().getString("DB_CONNECTION_CHECK_ERROR");
    DB_CONNECTION_RECREATE_ERROR = LM.getMessageResourceBundle().getString("DB_CONNECTION_RECREATE_ERROR");
    DB_CONNECTION_CLOSE_ERROR = LM.getMessageResourceBundle().getString("DB_CONNECTION_CLOSE_ERROR");  
    DB_CONNECTION_INVALID = LM.getMessageResourceBundle().getString("DB_CONNECTION_INVALID");
    DB_CONNECTION_NULL_OR_CLOSED = LM.getMessageResourceBundle().getString("DB_CONNECTION_NULL_OR_CLOSED");
    DB_CONNECTION_SUCCESSFUL = LM.getMessageResourceBundle().getString("DB_CONNECTION_SUCCESSFUL");
    DB_CONNECTION_CLOSED = LM.getMessageResourceBundle().getString("DB_CONNECTION_CLOSED");  
    
    // Input validation errors
  
    // Encryption errors    
    PASSWORD_HASH_ERROR = LM.getMessageResourceBundle().getString("PASSWORD_HASH_ERROR");
    PASSWORD_VERIFY_ERROR = LM.getMessageResourceBundle().getString("PASSWORD_VERIFY_ERROR");
    AUTHENTICATION_ERROR = LM.getMessageResourceBundle().getString("AUTHENTICATION_ERROR");  
    
    // Dashboard related messages    
    LOGOUT_CONFIRM = LM.getMessageResourceBundle().getString("LOGOUT_CONFIRM");
    LOGOUT_TITLE = LM.getMessageResourceBundle().getString("LOGOUT_TITLE");
    LOGIN_REQUIRED = LM.getMessageResourceBundle().getString("LOGIN_REQUIRED");  
    LOGIN_REQUIRED_TITLE = LM.getMessageResourceBundle().getString("LOGIN_REQUIRED_TITLE");
    LOGIN_REDIRECT_ERROR = LM.getMessageResourceBundle().getString("LOGIN_REDIRECT_ERROR");
    
    // Language change messages    
    LANGUAGE_CHANGE_CONFIRM_VI = LM.getMessageResourceBundle().getString("LANGUAGE_CHANGE_CONFIRM_VI");
    LANGUAGE_CHANGE_CONFIRM_EN = LM.getMessageResourceBundle().getString("LANGUAGE_CHANGE_CONFIRM_EN");
    LANGUAGE_CHANGE_TITLE_VI = LM.getMessageResourceBundle().getString("LANGUAGE_CHANGE_TITLE_VI");
    LANGUAGE_CHANGE_TITLE_EN = LM.getMessageResourceBundle().getString("LANGUAGE_CHANGE_TITLE_EN");
    RESTART_ERROR_VI = LM.getMessageResourceBundle().getString("RESTART_ERROR_VI");
    RESTART_ERROR_EN = LM.getMessageResourceBundle().getString("RESTART_ERROR_EN");  
    
    // Customer controller specific messages    
    CUSTOMER_CONTROLLER_INIT_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_CONTROLLER_INIT_ERROR");
    CUSTOMER_LOAD_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_LOAD_ERROR");
    CUSTOMER_DETAILS_LOAD_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_DETAILS_LOAD_ERROR");
    CUSTOMER_REQUIRED_FIELDS = LM.getMessageResourceBundle().getString("CUSTOMER_REQUIRED_FIELDS");  
    CUSTOMER_POINTS_NEGATIVE = LM.getMessageResourceBundle().getString("CUSTOMER_POINTS_NEGATIVE");
    CUSTOMER_POINTS_INTEGER = LM.getMessageResourceBundle().getString("CUSTOMER_POINTS_INTEGER");
    CUSTOMER_ID_EXISTS = LM.getMessageResourceBundle().getString("CUSTOMER_ID_EXISTS");
    CUSTOMER_ADD_SUCCESS = LM.getMessageResourceBundle().getString("CUSTOMER_ADD_SUCCESS");  
    CUSTOMER_UPDATE_SUCCESS = LM.getMessageResourceBundle().getString("CUSTOMER_UPDATE_SUCCESS");
    CUSTOMER_ADD_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_ADD_ERROR");
    CUSTOMER_UPDATE_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_UPDATE_ERROR");  
    CUSTOMER_SELECT_UPDATE = LM.getMessageResourceBundle().getString("CUSTOMER_SELECT_UPDATE");
    CUSTOMER_SELECT_DELETE = LM.getMessageResourceBundle().getString("CUSTOMER_SELECT_DELETE");
    CUSTOMER_DELETE_CONFIRM = LM.getMessageResourceBundle().getString("CUSTOMER_DELETE_CONFIRM");  
    CUSTOMER_DELETE_TITLE = LM.getMessageResourceBundle().getString("CUSTOMER_DELETE_TITLE");
    CUSTOMER_DELETE_SUCCESS = LM.getMessageResourceBundle().getString("CUSTOMER_DELETE_SUCCESS");
    CUSTOMER_DELETE_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_DELETE_ERROR");
    CUSTOMER_DELETE_CONSTRAINT = LM.getMessageResourceBundle().getString("CUSTOMER_DELETE_CONSTRAINT");  
    CUSTOMER_SEARCH_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_SEARCH_ERROR");
    CUSTOMER_EXPORT_NO_DATA = LM.getMessageResourceBundle().getString("CUSTOMER_EXPORT_NO_DATA");
    CUSTOMER_EXPORT_SUCCESS = LM.getMessageResourceBundle().getString("CUSTOMER_EXPORT_SUCCESS");
    CUSTOMER_EXPORT_FAILED = LM.getMessageResourceBundle().getString("CUSTOMER_EXPORT_FAILED");  
    CUSTOMER_EXPORT_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_EXPORT_ERROR");
    CUSTOMER_ADD_CONTINUE = LM.getMessageResourceBundle().getString("CUSTOMER_ADD_CONTINUE");
    CUSTOMER_ADD_CANCEL = LM.getMessageResourceBundle().getString("CUSTOMER_ADD_CANCEL");  
    INFO_TITLE = LM.getMessageResourceBundle().getString("INFO_TITLE");
    INSUFFICIENT_POINTS = LM.getMessageResourceBundle().getString("INSUFFICIENT_POINTS");
    CUSTOMER_FIND_BY_ID_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_FIND_BY_ID_ERROR");
    CUSTOMER_FIND_BY_PHONE_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_FIND_BY_PHONE_ERROR");  
    CUSTOMER_POINTS_UPDATE_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_POINTS_UPDATE_ERROR");
    CUSTOMER_POINTS_ADD_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_POINTS_ADD_ERROR");
    CUSTOMER_POINTS_DEDUCT_ERROR = LM.getMessageResourceBundle().getString("CUSTOMER_POINTS_DEDUCT_ERROR");  
    CUSTOMER_ADD_CONFIRM = LM.getMessageResourceBundle().getString("CUSTOMER_ADD_CONFIRM");
    // Home controller messages    
    HOME_CONTROLLER_INIT_ERROR = LM.getMessageResourceBundle().getString("HOME_CONTROLLER_INIT_ERROR");
    USER_INFO_LOAD_ERROR = LM.getMessageResourceBundle().getString("USER_INFO_LOAD_ERROR");
    TODAY_STATS_LOAD_ERROR = LM.getMessageResourceBundle().getString("TODAY_STATS_LOAD_ERROR");
    MONTH_STATS_LOAD_ERROR = LM.getMessageResourceBundle().getString("MONTH_STATS_LOAD_ERROR");  
    WEEKLY_CHART_LOAD_ERROR = LM.getMessageResourceBundle().getString("WEEKLY_CHART_LOAD_ERROR");
    DAILY_REVENUE_DATA_ERROR = LM.getMessageResourceBundle().getString("DAILY_REVENUE_DATA_ERROR");
    TOTAL_REVENUE_ERROR = LM.getMessageResourceBundle().getString("TOTAL_REVENUE_ERROR");  
    
    // User controller specific messages    
    USER_CONTROLLER_INIT_ERROR = LM.getMessageResourceBundle().getString("USER_CONTROLLER_INIT_ERROR");
    USER_LOAD_ERROR = LM.getMessageResourceBundle().getString("USER_LOAD_ERROR");
    USER_FILTER_ERROR = LM.getMessageResourceBundle().getString("USER_FILTER_ERROR");
    USER_DETAILS_LOAD_ERROR = LM.getMessageResourceBundle().getString("USER_DETAILS_LOAD_ERROR");  
    USER_SELECT_EMPLOYEE = LM.getMessageResourceBundle().getString("USER_SELECT_EMPLOYEE");
    USER_COMPLETE_INFORMATION = LM.getMessageResourceBundle().getString("USER_COMPLETE_INFORMATION");
    USERNAME_ALREADY_EXISTS = LM.getMessageResourceBundle().getString("USERNAME_ALREADY_EXISTS");
    USER_ADD_CONFIRM = LM.getMessageResourceBundle().getString("USER_ADD_CONFIRM");  
    USER_ADD_CONFIRM_TITLE = LM.getMessageResourceBundle().getString("USER_ADD_CONFIRM_TITLE");
    USER_ADD_SUCCESS = LM.getMessageResourceBundle().getString("USER_ADD_SUCCESS");
    USER_ADD_ERROR = LM.getMessageResourceBundle().getString("USER_ADD_ERROR");
    USER_SELECT_TO_UPDATE = LM.getMessageResourceBundle().getString("USER_SELECT_TO_UPDATE");  
    USER_UPDATE_OWN_INFO_ONLY = LM.getMessageResourceBundle().getString("USER_UPDATE_OWN_INFO_ONLY");
    USER_COMPLETE_INFO = LM.getMessageResourceBundle().getString("USER_COMPLETE_INFO");
    USER_UPDATE_CONFIRM = LM.getMessageResourceBundle().getString("USER_UPDATE_CONFIRM");
    USER_UPDATE_CONFIRM_TITLE = LM.getMessageResourceBundle().getString("USER_UPDATE_CONFIRM_TITLE");  
    USER_UPDATE_SUCCESS = LM.getMessageResourceBundle().getString("USER_UPDATE_SUCCESS");
    USER_UPDATE_ERROR = LM.getMessageResourceBundle().getString("USER_UPDATE_ERROR");
    USER_SELECT_TO_DELETE = LM.getMessageResourceBundle().getString("USER_SELECT_TO_DELETE");
    USER_DELETE_CONFIRM = LM.getMessageResourceBundle().getString("USER_DELETE_CONFIRM");  
    USER_DELETE_CONFIRM_TITLE = LM.getMessageResourceBundle().getString("USER_DELETE_CONFIRM_TITLE");
    USER_DELETE_SUCCESS = LM.getMessageResourceBundle().getString("USER_DELETE_SUCCESS");
    USER_DELETE_ERROR = LM.getMessageResourceBundle().getString("USER_DELETE_ERROR");
    USER_SELECT_TO_RESET_PASSWORD = LM.getMessageResourceBundle().getString("USER_SELECT_TO_RESET_PASSWORD");  
    USER_RESET_PASSWORD_CONFIRM = LM.getMessageResourceBundle().getString("USER_RESET_PASSWORD_CONFIRM");
    USER_RESET_PASSWORD_CONFIRM_TITLE = LM.getMessageResourceBundle().getString("USER_RESET_PASSWORD_CONFIRM_TITLE");
    USER_RESET_PASSWORD_SUCCESS = LM.getMessageResourceBundle().getString("USER_RESET_PASSWORD_SUCCESS");
    USER_RESET_PASSWORD_NEW = LM.getMessageResourceBundle().getString("USER_RESET_PASSWORD_NEW");  
    USER_RESET_PASSWORD_ERROR = LM.getMessageResourceBundle().getString("USER_RESET_PASSWORD_ERROR");
    USER_CHANGE_PASSWORD_ERROR = LM.getMessageResourceBundle().getString("USER_CHANGE_PASSWORD_ERROR");
    USER_LOAD_CURRENT_ERROR = LM.getMessageResourceBundle().getString("USER_LOAD_CURRENT_ERROR");
    PASSWORD_CHANGE_REQUIRED_FIELDS = LM.getMessageResourceBundle().getString("PASSWORD_CHANGE_REQUIRED_FIELDS");  
    PASSWORD_NEW_MISMATCH = LM.getMessageResourceBundle().getString("PASSWORD_NEW_MISMATCH");
    PASSWORD_MIN_LENGTH = LM.getMessageResourceBundle().getString("PASSWORD_MIN_LENGTH");
    PASSWORD_CURRENT_INCORRECT = LM.getMessageResourceBundle().getString("PASSWORD_CURRENT_INCORRECT");
    PASSWORD_CHANGE_SUCCESS = LM.getMessageResourceBundle().getString("PASSWORD_CHANGE_SUCCESS");
    PASSWORD_CHANGE_FAILED = LM.getMessageResourceBundle().getString("PASSWORD_CHANGE_FAILED");
    PASSWORD_CHANGE_ERROR = LM.getMessageResourceBundle().getString("PASSWORD_CHANGE_ERROR");;
    
    // Payment controller specific messages    
    PAYMENT_PROCESSING_ERROR = LM.getMessageResourceBundle().getString("PAYMENT_PROCESSING_ERROR");
    PAYMENT_SUCCESS_CHANGE = LM.getMessageResourceBundle().getString("PAYMENT_SUCCESS_CHANGE");
    PAYMENT_METHOD_MAINTENANCE = LM.getMessageResourceBundle().getString("PAYMENT_METHOD_MAINTENANCE");
    PAYMENT_CONFIRM = LM.getMessageResourceBundle().getString("PAYMENT_CONFIRM");
    PAYMENT_CONFIRM_TITLE = LM.getMessageResourceBundle().getString("PAYMENT_CONFIRM_TITLE");;

    // File related messages
    FILE_EXISTS_OVERWRITE = LM.getMessageResourceBundle().getString("FILE_EXISTS_OVERWRITE");
    FILE_PATH = LM.getMessageResourceBundle().getString("FILE_PATH");
    CANNOT_OPEN_FILE = LM.getMessageResourceBundle().getString("CANNOT_OPEN_FILE");

    // Sell controller messages
    DEFAULT_CUSTOMER_NOT_FOUND = LM.getMessageResourceBundle().getString("DEFAULT_CUSTOMER_NOT_FOUND");
    CART_UPDATE_ERROR = LM.getMessageResourceBundle().getString("CART_UPDATE_ERROR");
    INSUFFICIENT_STOCK = LM.getMessageResourceBundle().getString("INSUFFICIENT_STOCK");
    INVALID_INVOICE_SAVE = LM.getMessageResourceBundle().getString("INVALID_INVOICE_SAVE");
    INVOICE_SAVE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_SAVE_ERROR");
    INVOICE_DETAIL_SAVE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_DETAIL_SAVE_ERROR");
    UNKNOWN_ERROR = LM.getMessageResourceBundle().getString("UNKNOWN_ERROR");
    INVALID_INVOICE_COMPLETE = LM.getMessageResourceBundle().getString("INVALID_INVOICE_COMPLETE");
    INVOICE_COMPLETE_ERROR = LM.getMessageResourceBundle().getString("INVOICE_COMPLETE_ERROR");
    EXPORT_INVOICE_ERROR = LM.getMessageResourceBundle().getString("EXPORT_INVOICE_ERROR");
    EXPORT_INVOICE_FAILED = LM.getMessageResourceBundle().getString("EXPORT_INVOICE_FAILED");
    POINTS_ALREADY_USED = LM.getMessageResourceBundle().getString("POINTS_ALREADY_USED");

    // SellForm specific messages
    FORM_INIT_ERROR = LM.getMessageResourceBundle().getString("FORM_INIT_ERROR");
    SELECT_PRODUCT_TO_DELETE = LM.getMessageResourceBundle().getString("SELECT_PRODUCT_TO_DELETE");
    CONFIRM_DELETE_PRODUCT = LM.getMessageResourceBundle().getString("CONFIRM_DELETE_PRODUCT");
    CUSTOMER_POINTS_INSUFFICIENT = LM.getMessageResourceBundle().getString("CUSTOMER_POINTS_INSUFFICIENT");
    CONFIRM_USE_POINTS = LM.getMessageResourceBundle().getString("CONFIRM_USE_POINTS");
    CUSTOMER_SELECT_REQUIRED = LM.getMessageResourceBundle().getString("CUSTOMER_SELECT_REQUIRED");
    EMPTY_CART = LM.getMessageResourceBundle().getString("EMPTY_CART");
    INVALID_QUANTITY = LM.getMessageResourceBundle().getString("INVALID_QUANTITY");
    QUANTITY_MUST_BE_POSITIVE = LM.getMessageResourceBundle().getString("QUANTITY_MUST_BE_POSITIVE");
    SELECT_PRODUCT_TO_ADD = LM.getMessageResourceBundle().getString("SELECT_PRODUCT_TO_ADD");
    QUANTITY_UPDATE_ERROR = LM.getMessageResourceBundle().getString("QUANTITY_UPDATE_ERROR");
    CONFIRM_SAVE_INVOICE = LM.getMessageResourceBundle().getString("CONFIRM_SAVE_INVOICE");
    SAVE_INVOICE_ERROR = LM.getMessageResourceBundle().getString("SAVE_INVOICE_ERROR");
    SAVE_INVOICE_SUCCESS = LM.getMessageResourceBundle().getString("SAVE_INVOICE_SUCCESS");
    PAYMENT_SUCCESS = LM.getMessageResourceBundle().getString("PAYMENT_SUCCESS");
    PAYMENT_FAILED = LM.getMessageResourceBundle().getString("PAYMENT_FAILED");
    CONFIRM_RESET_CART = LM.getMessageResourceBundle().getString("CONFIRM_RESET_CART");
    CUSTOMER_NAME_REQUIRED = LM.getMessageResourceBundle().getString("CUSTOMER_NAME_REQUIRED");

    // Bank payment related messages
    BROWSER_OPEN_ERROR = LM.getMessageResourceBundle().getString("BROWSER_OPEN_ERROR");
    PAYMENT_WAITING_MESSAGE = LM.getMessageResourceBundle().getString("PAYMENT_WAITING_MESSAGE");
    PAYMENT_VERIFICATION_FAILED = LM.getMessageResourceBundle().getString("PAYMENT_VERIFICATION_FAILED");
    WARNING_TITLE = LM.getMessageResourceBundle().getString("WARNING_TITLE");
    PAYMENT_LINK_CREATE_ERROR = LM.getMessageResourceBundle().getString("PAYMENT_LINK_CREATE_ERROR");
  }
}