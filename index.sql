CREATE INDEX IX_Products_CategoryID ON Products(CategoryID);

-- Mục đích: Tăng tốc tìm kiếm sản phẩm theo danh mục
-- Tình huống: Khi hiển thị tất cả sản phẩm thuộc danh mục "Laptop"

CREATE INDEX IX_Invoices_CustomerID ON Invoices(CustomerID);
-- Mục đích: Tăng tốc tìm kiếm hóa đơn theo khách hàng
-- Tình huống: Khi xem lịch sử mua hàng của một khách hàng


CREATE INDEX IX_InvoiceDetails_ProductID ON InvoiceDetails(ProductID);
-- Mục đích: Tăng tốc tìm kiếm chi tiết hóa đơn theo sản phẩm
-- Tình huống: Khi cần biết sản phẩm nào bán chạy nhất

CREATE INDEX IX_Invoices_InvoiceDate ON Invoices(InvoiceDate);
-- Mục đích: Tăng tốc báo cáo doanh thu theo thời gian
-- Tình huống: Khi làm báo cáo doanh thu theo ngày/tháng/năm

CREATE INDEX IX_Users_EmployeeID ON Users(EmployeeID);
-- Mục đích: Tăng tốc tìm kiếm thông tin nhân viên
-- Tình huống: Khi cần biết thông tin chi tiết của một nhân viên

CREATE INDEX IX_RepairServices_CustomerID ON RepairServices(CustomerID);
CREATE INDEX IX_Returns_InvoiceDetailID ON Returns(InvoiceDetailID);