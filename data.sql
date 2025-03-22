USE ComputerStoreManagement;

-- Đảm bảo không vi phạm ràng buộc khóa ngoại khi thêm dữ liệu

-- Thêm dữ liệu cho bảng InvoiceStatus
INSERT INTO InvoiceStatus (StatusID, StatusName, Description) VALUES
(0, N'Chờ xử lý', N'Hóa đơn vừa được tạo, đang chờ xử lý'),
(1, N'Đã xác nhận', N'Hóa đơn đã được xác nhận'),
(2, N'Đang giao hàng', N'Sản phẩm đang được giao cho khách hàng'),
(3, N'Hoàn thành', N'Hóa đơn đã hoàn thành, khách hàng đã nhận hàng'),
(4, N'Đã hủy', N'Hóa đơn đã bị hủy');



-- Thêm dữ liệu cho bảng PaymentMethods
INSERT INTO PaymentMethods (PaymentMethodID, MethodName, Description) VALUES
('1', N'Tiền mặt', N'Thanh toán bằng tiền mặt tại cửa hàng'),
('2', N'Momo', N'Thanh toán qua MoMo'),
('3', N'Zalopay', N'Thanh toán qua ZaloPay'),
('4', N'Chuyển khoản', N'Thanh toán qua chuyển khoản ngân hàng');

-- Thêm dữ liệu cho bảng Categories
INSERT INTO Categories (CategoryID, CategoryName) VALUES
('LAP', N'Laptop'),
('PC', N'Máy tính để bàn'),
('MH', N'Màn hình'),
('LK', N'Linh kiện'),
('PK', N'Phụ kiện'),
('TB', N'Thiết bị mạng');

-- Thêm dữ liệu cho bảng Suppliers
INSERT INTO Suppliers (SupplierID, Name, PhoneNumber, Email, Address) VALUES
('NCC01', N'Công ty TNHH ABC', '0912345678', 'abc@gmail.com', N'123 Nguyễn Trãi, Quận 1, TP.HCM'),
('NCC02', N'Công ty TNHH XYZ', '0923456789', 'xyz@gmail.com', N'456 Lê Lợi, Quận 5, TP.HCM'),
('NCC03', N'Công ty TNHH DEF', '0934567890', 'def@gmail.com', N'789 Lý Thường Kiệt, Quận 10, TP.HCM'),
('NCC04', N'Công ty TNHH GHI', '0945678901', 'ghi@gmail.com', N'101 Điện Biên Phủ, Quận 3, TP.HCM'),
('NCC05', N'Công ty TNHH JKL', '0956789012', 'jkl@gmail.com', N'202 Cách Mạng Tháng 8, Quận Tân Bình, TP.HCM');

-- Thêm dữ liệu cho bảng Employees
INSERT INTO Employees (EmployeeID, FullName, PhoneNumber, Email, Position) VALUES
('NV01', N'Nguyễn Văn An', '0987654321', 'an@pcstore.com', 'Manager'),
('NV02', N'Trần Thị Bình', '0976543210', 'binh@pcstore.com', 'Sales'),
('NV03', N'Lê Văn Công', '0965432109', 'cong@pcstore.com', 'Sales'),
('NV04', N'Phạm Thị Dung', '0954321098', 'dung@pcstore.com', 'Stock Keeper'),
('NV05', N'Hoàng Văn Em', '0943210987', 'em@pcstore.com', 'Sales');

-- Thêm dữ liệu cho bảng Customers
INSERT INTO Customers (CustomerID, FullName, PhoneNumber, Email, Address, CreatedAt) VALUES
('KH01', N'Trần Văn Khách', '0812345678', 'khach@gmail.com', N'10 Võ Văn Tần, Quận 3, TP.HCM', '2023-01-01'),
('KH02', N'Lê Thị Hàng', '0823456789', 'hang@gmail.com', N'20 Võ Thị Sáu, Quận 1, TP.HCM', '2023-01-15'),
('KH03', N'Nguyễn Văn Mua', '0834567890', 'mua@gmail.com', N'30 Nguyễn Thái Học, Quận 1, TP.HCM', '2023-01-20'),
('KH04', N'Phan Thị Bán', '0845678901', 'ban@gmail.com', N'40 Lê Thánh Tôn, Quận 1, TP.HCM', '2023-02-10'),
('KH05', N'Hoàng Văn Linh', '0856789012', 'linh@gmail.com', N'50 Trần Hưng Đạo, Quận 5, TP.HCM', '2023-02-15'),
('KH06', N'Đặng Thị Kiện', '0867890123', 'kien@gmail.com', N'60 Nguyễn Đình Chiểu, Quận 3, TP.HCM', '2023-03-01'),
('KH07', N'Bùi Văn Gia', '0878901234', 'gia@gmail.com', N'70 Phạm Ngọc Thạch, Quận 3, TP.HCM', '2023-03-15'),
('KH08', N'Võ Thị Tín', '0889012345', 'tin@gmail.com', N'80 Lý Chính Thắng, Quận 3, TP.HCM', '2023-04-01');

-- Thêm dữ liệu cho bảng Products
INSERT INTO Products (ProductID, ProductName, CategoryID, SupplierID, Price, StockQuantity, Specifications, Description) VALUES
('SP001', N'Laptop Dell XPS 13', 'LAP', 'NCC01', 25000000, 10, N'Core i7, 16GB RAM, 512GB SSD', N'Laptop cao cấp cho công việc văn phòng và đồ họa'),
('SP002', N'Laptop HP Pavilion', 'LAP', 'NCC01', 15000000, 15, N'Core i5, 8GB RAM, 256GB SSD', N'Laptop phổ thông cho sinh viên và văn phòng'),
('SP003', N'Màn hình Dell 27 inch', 'MH', 'NCC02', 5500000, 20, N'27 inch, 2K, IPS', N'Màn hình chất lượng cao cho thiết kế và chơi game'),
('SP004', N'PC Gaming Asus', 'PC', 'NCC03', 35000000, 5, N'Core i9, 32GB RAM, RTX 3080, 1TB SSD', N'Máy tính chơi game cao cấp'),
('SP005', N'RAM Kingston 8GB', 'LK', 'NCC04', 800000, 50, N'DDR4, 3200MHz', N'Bộ nhớ RAM cho máy tính'),
('SP006', N'SSD Samsung 1TB', 'LK', 'NCC04', 2500000, 30, N'NVMe, Read 7000MB/s', N'Ổ cứng thể rắn tốc độ cao'),
('SP007', N'Chuột Logitech G502', 'PK', 'NCC05', 1200000, 25, N'16000 DPI, RGB', N'Chuột gaming cao cấp'),
('SP008', N'Bàn phím cơ Corsair K70', 'PK', 'NCC05', 2800000, 15, N'Cherry MX Red, RGB', N'Bàn phím cơ cho gaming'),
('SP009', N'Router TP-Link Archer C6', 'TB', 'NCC02', 1100000, 20, N'Dual Band, AC1200', N'Router Wi-Fi cho gia đình và văn phòng nhỏ'),
('SP010', N'Switch TP-Link 8 Port', 'TB', 'NCC02', 500000, 15, N'Gigabit, 8 cổng', N'Switch mạng cho văn phòng nhỏ');

-- Thêm dữ liệu cho bảng Roles
INSERT INTO Roles (RoleID, RoleName, Description) VALUES
(1, 'Admin', N'Quyền quản trị cao nhất'),
(2, 'Manager', N'Quyền quản lý'),
(3, 'Sales', N'Quyền nhân viên bán hàng'),
(4, 'Stock', N'Quyền nhân viên kho');

-- Thêm dữ liệu cho bảng Users
INSERT INTO Users (UserID, Username, PasswordHash, EmployeeID, IsActive) VALUES
('U001', 'admin', '123456', NULL, 1), -- Password: 123456
('U002', 'an', '123456', 'NV01', 1),
('U003', 'binh', '123456', 'NV02', 1),
('U004', 'cong', '123456', 'NV03', 1),
('U005', 'dung', '123456', 'NV04', 1),
('U006', 'em', '123456', 'NV05', 1);

-- Thêm dữ liệu cho bảng UserRoles
INSERT INTO UserRoles (UserID, RoleID) VALUES
('U001', 1), -- admin có quyền Admin
('U002', 2), -- an có quyền Manager
('U003', 3), -- binh có quyền Sales
('U004', 3), -- cong có quyền Sales
('U005', 4), -- dung có quyền Stock
('U006', 3); -- em có quyền Sales


-- Thêm dữ liệu cho bảng PurchaseOrders
INSERT INTO PurchaseOrders (PurchaseOrderID, SupplierID, EmployeeID, OrderDate) VALUES
('PO001', 'NCC01', 'NV04', '2023-01-01'),
('PO002', 'NCC02', 'NV04', '2023-01-15'),
('PO003', 'NCC03', 'NV04', '2023-02-01'),
('PO004', 'NCC04', 'NV04', '2023-02-15'),
('PO005', 'NCC05', 'NV04', '2023-03-01');

-- Thêm dữ liệu cho bảng PurchaseOrderDetails
INSERT INTO PurchaseOrderDetails (PurchaseOrderID, ProductID, Quantity, UnitCost) VALUES
('PO001', 'SP001', 5, 20000000),
('PO001', 'SP002', 10, 12000000),
('PO002', 'SP003', 15, 4500000),
('PO003', 'SP004', 3, 30000000),
('PO004', 'SP005', 30, 600000),
('PO004', 'SP006', 20, 2000000),
('PO005', 'SP007', 15, 900000),
('PO005', 'SP008', 10, 2300000);

-- Thêm dữ liệu cho bảng Invoices
INSERT INTO Invoices (CustomerID, EmployeeID, TotalAmount, InvoiceDate, StatusID, PaymentMethodID, Notes) VALUES
('KH01', 'NV02', 24500000, '2023-01-15 10:30:00', 3, '1', N'Khách đã thanh toán đầy đủ'),
('KH03', 'NV03', 15000000, '2023-01-20 14:45:00', 3, '2', N'Khách thanh toán qua MoMo'),
('KH02', 'NV02', 39700000, '2023-02-05 09:15:00', 3, '3', N'Khách thanh toán qua ZaloPay'),
('KH05', 'NV05', 5500000, '2023-02-10 16:20:00', 3, '1', N'Khách đã thanh toán đầy đủ'),
('KH04', 'NV03', 8500000, '2023-03-01 11:10:00', 3, '4', N'Khách thanh toán qua chuyển khoản'),
('KH06', 'NV05', 35000000, '2023-03-15 13:25:00', 2, '2', N'Đang giao hàng cho khách'),
('KH07', 'NV02', 1200000, '2023-04-02 15:40:00', 1, '4', N'Đã xác nhận đơn hàng'),
('KH08', 'NV03', 4500000, '2023-04-10 10:05:00', 0, '1', N'Đang chờ xử lý');

-- Thêm dữ liệu cho bảng InvoiceDetails
INSERT INTO InvoiceDetails (InvoiceID, ProductID, Quantity, UnitPrice) VALUES
(1, 'SP001', 1, 24500000),
(2, 'SP002', 1, 15000000),
(3, 'SP001', 1, 24500000),
(3, 'SP004', 1, 15200000),
(4, 'SP003', 1, 5500000),
(5, 'SP005', 2, 800000),
(5, 'SP006', 1, 2500000),
(5, 'SP007', 2, 1200000),
(6, 'SP004', 1, 35000000),
(7, 'SP007', 1, 1200000),
(8, 'SP006', 2, 2250000);

-- Thêm dữ liệu cho bảng Warranties
INSERT INTO Warranties (WarrantyID, InvoiceDetailID, StartDate, EndDate) VALUES
('BH001', 1, '2023-01-15', '2025-01-15'),
('BH002', 2, '2023-01-20', '2025-01-20'),
('BH003', 3, '2023-02-05', '2025-02-05'),
('BH004', 4, '2023-02-05', '2025-02-05'),
('BH005', 5, '2023-02-10', '2024-02-10'),
('BH006', 6, '2023-03-01', '2024-03-01'),
('BH007', 8, '2023-03-01', '2025-03-01'),
('BH008', 9, '2023-03-15', '2025-03-15');

-- Thêm dữ liệu cho bảng RepairServices
INSERT INTO RepairServices (CustomerID, EmployeeID, DeviceDescription, Problem, DiagnosisResult, RepairCost, ReceiveDate, EstimatedCompletionDate, ActualCompletionDate, Status) VALUES
('KH01', 'NV03', N'Laptop Dell XPS bị hỏng màn hình', N'Màn hình không hiển thị', N'Cần thay màn hình mới', 3500000, '2023-02-01', '2023-02-05', '2023-02-04', 'Completed'),
('KH03', 'NV03', N'PC không khởi động được', N'Máy không lên nguồn', N'Hỏng nguồn, cần thay PSU', 1200000, '2023-02-10', '2023-02-15', NULL, 'Repairing'),
('KH02', NULL, N'Màn hình Dell 27 inch flicker', N'Màn hình chớp liên tục', NULL, NULL, '2023-02-20', '2023-02-25', NULL, 'Received'),
('KH05', 'NV03', N'Laptop HP lỗi bàn phím', N'Một số phím không hoạt động', N'Bàn phím bị hỏng, cần thay mới', 800000, '2023-03-05', '2023-03-10', '2023-03-09', 'Delivered');

-- Thêm dữ liệu cho bảng Returns
INSERT INTO Returns (InvoiceDetailID, ReturnDate, ReturnReason, Quantity, ReturnAmount, ProcessedBy, Status, IsExchange, NewProductID) VALUES
(5, '2023-02-15', N'Sản phẩm bị lỗi màu sắc', 1, 5500000, 'NV02', 'Completed', 1, 'SP003'),
(7, '2023-03-10', N'Sản phẩm không hoạt động', 1, 2500000, 'NV03', 'Approved', 0, NULL),
(10, '2023-04-05', N'Khách hàng đổi ý', 1, 1200000, 'NV02', 'Rejected', 0, NULL);

-- Thêm dữ liệu cho bảng Promotions
INSERT INTO Promotions (PromotionName, DiscountType, DiscountValue, StartDate, EndDate, MinimumPurchase, IsActive, Description) VALUES
(N'Khuyến mãi mùa hè', 'Percentage', 10, '2023-05-01', '2023-06-30', 5000000, 1, N'Giảm 10% cho các đơn hàng từ 5 triệu trở lên'),
(N'Black Friday', 'Percentage', 20, '2023-11-24', '2023-11-26', 10000000, 1, N'Giảm 20% cho các đơn hàng từ 10 triệu trở lên'),
(N'Giảm giá đầu năm', 'Fixed Amount', 1000000, '2023-01-01', '2023-01-31', 20000000, 0, N'Giảm 1 triệu cho các đơn hàng từ 20 triệu trở lên');