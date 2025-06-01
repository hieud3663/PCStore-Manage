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
    public static final String INVALID_EMPLOYEE_POSITION = "Chức vụ không hợp lệ";
    public static final String EMPLOYEE_AGE_18 = "Nhân viên phải từ 18 tuổi trở lên";

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