-- ### Mô tả quan hệ giữa các bảng:

-- 1. **Customers (Khách hàng) ↔ Invoices (Hóa đơn)**
--    - Mỗi khách hàng **có thể có nhiều** hóa đơn.
--    - Nếu khách hàng bị xóa, hóa đơn sẽ giữ nguyên với `CustomerID` NULL.

-- 2. **Employees (Nhân viên) ↔ Invoices (Hóa đơn)**
--    - Mỗi hóa đơn được lập bởi **một nhân viên**.
--    - Nếu nhân viên bị xóa, hóa đơn giữ nguyên với `EmployeeID` NULL.

-- 3. **Invoices (Hóa đơn) ↔ InvoiceDetails (Chi tiết hóa đơn)**
--    - Một hóa đơn **có nhiều** chi tiết hóa đơn.
--    - Nếu hóa đơn bị xóa, tất cả chi tiết hóa đơn cũng bị xóa (`CASCADE`).

-- 4. **InvoiceDetails (Chi tiết hóa đơn) ↔ Products (Sản phẩm)**
--    - Mỗi chi tiết hóa đơn chỉ thuộc về **một sản phẩm**.
--    - Không thể xóa sản phẩm nếu đã có trong hóa đơn (`NO ACTION`).

-- 5. **Suppliers (Nhà cung cấp) ↔ Products (Sản phẩm)**
--    - Một sản phẩm có thể có **một nhà cung cấp**.
--    - Nếu nhà cung cấp bị xóa, sản phẩm vẫn tồn tại với `SupplierID` NULL.

-- 6. **Categories (Danh mục) ↔ Products (Sản phẩm)**
--    - Một danh mục chứa **nhiều sản phẩm**.
--    - Nếu danh mục bị xóa, sản phẩm cũng bị xóa (`CASCADE`).

-- 7. **Employees (Nhân viên) ↔ PurchaseOrders (Nhập hàng)**
--    - Một nhân viên có thể nhập **nhiều phiếu nhập hàng**.
--    - Nếu nhân viên bị xóa, phiếu nhập vẫn giữ nguyên (`SET NULL`).

-- 8. **Suppliers (Nhà cung cấp) ↔ PurchaseOrders (Nhập hàng)**
--    - Một nhà cung cấp có thể có **nhiều phiếu nhập hàng**.
--    - Nếu nhà cung cấp bị xóa, phiếu nhập vẫn giữ nguyên (`SET NULL`).

-- 9. **PurchaseOrders (Nhập hàng) ↔ PurchaseOrderDetails (Chi tiết nhập hàng)**
--    - Một phiếu nhập hàng có thể có **nhiều sản phẩm nhập**.
--    - Nếu phiếu nhập bị xóa, chi tiết nhập hàng cũng bị xóa (`CASCADE`).

-- 10. **Products (Sản phẩm) ↔ PurchaseOrderDetails (Chi tiết nhập hàng)**
--     - Một sản phẩm có thể xuất hiện trong **nhiều phiếu nhập**.
--     - Nếu sản phẩm bị xóa, chi tiết nhập hàng vẫn giữ nguyên (`NO ACTION`).

-- 11. **InvoiceDetails (Chi tiết hóa đơn) ↔ Warranties (Bảo hành)**
--     - Mỗi sản phẩm trong hóa đơn **có thể có** bảo hành.
--     - Nếu chi tiết hóa đơn bị xóa, bảo hành cũng bị xóa (`CASCADE`).

-- 12. **Invoices (Hóa đơn) ↔ InvoiceStatus (Trạng thái hóa đơn)**
--     - Một hóa đơn **có một** trạng thái.
  
-- 13. **Invoices (Hóa đơn) ↔ PaymentMethods (Phương thức thanh toán)**
--     - Mỗi hóa đơn được thanh toán bằng **một phương thức**.

-- 14. **Promotions (Khuyến mãi)**
--     - Lưu thông tin giảm giá, không liên kết trực tiếp với hóa đơn.

-- 15. **RepairServices (Dịch vụ sửa chữa)**
--     - Một khách hàng **có thể có nhiều** yêu cầu sửa chữa.
--     - Một nhân viên có thể phụ trách **nhiều dịch vụ sửa chữa**.

-- 16. **Users (Tài khoản) ↔ Employees (Nhân viên)**
--     - Mỗi tài khoản có thể liên kết với **một nhân viên**.
--     - Nếu nhân viên bị xóa, tài khoản giữ nguyên (`SET NULL`).

-- 18. **Returns (Trả hàng)**
--     - Một chi tiết hóa đơn có thể **bị trả lại một phần hoặc toàn bộ**.
--     - Nếu sản phẩm bị đổi, liên kết với sản phẩm mới (`NewProductID`).

-- CREATE DATABASE ComputerStoreManagement;
USE ComputerStoreManagement;

-- Bảng khách hàng
CREATE TABLE Customers (
    CustomerID VARCHAR(10) PRIMARY KEY, -- Mã KH01, KH02...
    FullName NVARCHAR(255) NOT NULL, -- Họ và tên
    PhoneNumber NVARCHAR(15) UNIQUE NOT NULL, -- Số điện thoại
    Email NVARCHAR(255) NULL, -- Email
    Address NVARCHAR(MAX), -- Địa chỉ
    CreatedAt DATETIME DEFAULT GETDATE() -- Ngày đăng ký tài khoản
);

-- Bảng nhà cung cấp
CREATE TABLE Suppliers (
    SupplierID VARCHAR(10) PRIMARY KEY, -- Mã nhà cung cấp
    Name NVARCHAR(255) NOT NULL, -- Tên nhà cung cấp
    PhoneNumber NVARCHAR(15) UNIQUE NOT NULL, -- Số điện thoại
    Email NVARCHAR(255) UNIQUE NOT NULL, -- Email
    Address NVARCHAR(MAX) NOT NULL -- Địa chỉ
);

-- Bảng danh mục sản phẩm
CREATE TABLE Categories (
    CategoryID VARCHAR(10) PRIMARY KEY, -- Mã danh mục sản phẩm, LAP, PC, PK, LK,...
    CategoryName NVARCHAR(255) NOT NULL UNIQUE -- Tên danh mục
);

-- Bảng sản phẩm
CREATE TABLE Products (
    ProductID VARCHAR(10) PRIMARY KEY, -- Mã sản phẩm
    ProductName NVARCHAR(255) NOT NULL, -- Tên sản phẩm
    CategoryID VARCHAR(10), -- Mã danh mục sản phẩm (FK)
    SupplierID VARCHAR(10) NULL, -- Mã nhà cung cấp (FK)
    Price DECIMAL(10,2) NOT NULL CHECK (Price > 0) , -- Giá bán
    StockQuantity INT DEFAULT 0 CHECK (StockQuantity >= 0), -- Số lượng tồn kho
    Specifications NVARCHAR(MAX), -- Thông số kỹ thuật
    Description NVARCHAR(MAX), -- Mô tả sản phẩm
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) 
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID) 
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng nhân viên
CREATE TABLE Employees (
    EmployeeID VARCHAR(10) PRIMARY KEY, -- Mã nhân viên, NV01, NV02,...
    FullName NVARCHAR(255) NOT NULL, -- Họ và tên
    PhoneNumber NVARCHAR(15) UNIQUE NOT NULL, -- Số điện thoại
    Email NVARCHAR(255) UNIQUE NOT NULL, -- Email
    Position NVARCHAR(50) CHECK (Position IN ('Manager', 'Sales', 'Stock Keeper')) NOT NULL -- Chức vụ
);
ALTER TABLE Employees
ADD CreatedAt DATETIME DEFAULT GETDATE(), -- Ngày tạo nhân viên
    UpdatedAt DATETIME DEFAULT GETDATE();


-- Bảng phương thức thanh toán
CREATE TABLE PaymentMethods (
    PaymentMethodID VARCHAR(10) PRIMARY KEY,
    MethodName NVARCHAR(50) NOT NULL UNIQUE, -- VD: Tiền mặt, Thẻ tín dụng, Chuyển khoản...
    Description NVARCHAR(255)
);

-- Bổ sung trạng thái đơn hàng
CREATE TABLE InvoiceStatus (
    StatusID INT PRIMARY KEY,
    StatusName NVARCHAR(50) NOT NULL UNIQUE, -- VD: Chờ xử lý, Đã thanh toán, Đã hủy...
    Description NVARCHAR(255)
);

-- Bảng hóa đơn
CREATE TABLE Invoices (
    InvoiceID INT IDENTITY(1,1) PRIMARY KEY, -- Mã hóa đơn
    CustomerID VARCHAR(10) NULL, -- Mã khách hàng (FK)
    EmployeeID VARCHAR(10) NULL, -- Mã nhân viên lập hóa đơn (FK)
    TotalAmount DECIMAL(10,2) CHECK (TotalAmount >= 0) NOT NULL, -- Tổng tiền hóa đơn
    InvoiceDate DATETIME DEFAULT GETDATE(), -- Ngày lập hóa đơn
    StatusID INT NOT NULL DEFAULT 0, -- Default status (e.g., 1 could be "Pending")
    PaymentMethodID VARCHAR(10) NOT NULL DEFAULT 1, -- Default payment method (e.g., 1 = Cash)
    Notes NVARCHAR(MAX), -- Ghi chú
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) 
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID) 
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (StatusID) REFERENCES InvoiceStatus(StatusID)
        ON DELETE NO ACTION
        ON UPDATE CASCADE,
    FOREIGN KEY (PaymentMethodID) REFERENCES PaymentMethods(PaymentMethodID)
        ON DELETE NO ACTION
        ON UPDATE CASCADE

);



-- ALTER TABLE Invoices
-- ADD StatusID INT NOT NULL DEFAULT 1, -- Default status (e.g., 1 could be "Pending")
-- FOREIGN KEY (StatusID) REFERENCES InvoiceStatus(StatusID)
--     ON UPDATE CASCADE;

-- ALTER TABLE Invoices
-- ADD PaymentMethodID INT NOT NULL DEFAULT 1, -- Default payment method (e.g., 1 = Cash)
-- FOREIGN KEY (PaymentMethodID) REFERENCES PaymentMethods(PaymentMethodID)
--     ON UPDATE CASCADE;
    
-- Bảng chi tiết hóa đơn
CREATE TABLE InvoiceDetails (
    InvoiceDetailID INT IDENTITY(1,1) PRIMARY KEY, -- Mã chi tiết hóa đơn
    InvoiceID INT NOT NULL, -- Mã hóa đơn (FK)
    ProductID VARCHAR(10) NOT NULL, -- Mã sản phẩm (FK)
    Quantity INT CHECK (Quantity > 0) NOT NULL, -- Số lượng sản phẩm trong hóa đơn
    UnitPrice DECIMAL(10,2) CHECK (UnitPrice > 0) NOT NULL, -- Giá bán từng sản phẩm
    FOREIGN KEY (InvoiceID) REFERENCES Invoices(InvoiceID) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) 
        ON DELETE NO ACTION
        ON UPDATE CASCADE,
);

-- Bảng nhập hàng từ nhà cung cấp
CREATE TABLE PurchaseOrders (
    PurchaseOrderID VARCHAR(30) PRIMARY KEY, -- Mã phiếu nhập hàng
    SupplierID VARCHAR(10) NULL, -- Mã nhà cung cấp (FK)
    EmployeeID VARCHAR(10) NULL, -- Mã nhân viên nhập hàng (FK)
    OrderDate DATETIME DEFAULT GETDATE(), -- Ngày nhập hàng
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID) 
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID) 
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- DROP TABLE PurchaseOrderDetails;
-- Bảng chi tiết nhập hàng
CREATE TABLE PurchaseOrderDetails (
    PurchaseOrderDetailID INT IDENTITY(1,1) PRIMARY KEY, -- Mã chi tiết nhập hàng
    PurchaseOrderID VARCHAR(30) NOT NULL, -- Mã phiếu nhập hàng (FK)
    ProductID VARCHAR(10) NOT NULL, -- Mã sản phẩm (FK)
    Quantity INT CHECK (Quantity > 0) NOT NULL, -- Số lượng nhập
    UnitCost DECIMAL(10,2) CHECK (UnitCost > 0) NOT NULL, -- Giá nhập từng sản phẩm
    FOREIGN KEY (PurchaseOrderID) REFERENCES PurchaseOrders(PurchaseOrderID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) 
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);

-- Bảng bảo hành sản phẩm
CREATE TABLE Warranties (
    WarrantyID VARCHAR(10) PRIMARY KEY, -- Mã bảo hành
    InvoiceDetailID INT NOT NULL, -- Mã chi tiết hóa đơn (FK)
    StartDate DATE NOT NULL, -- Ngày bắt đầu bảo hành
    EndDate DATE NOT NULL, -- Ngày kết thúc bảo hành
    FOREIGN KEY (InvoiceDetailID) REFERENCES InvoiceDetails(InvoiceDetailID) 
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CHECK (EndDate > StartDate)
);



-- Bảng khuyến mãi và giảm giá
CREATE TABLE Promotions (
    PromotionID INT IDENTITY(1,1) PRIMARY KEY,
    PromotionName NVARCHAR(255) NOT NULL,
    DiscountType NVARCHAR(20) NOT NULL CHECK (DiscountType IN ('Percentage', 'Fixed Amount')),
    DiscountValue DECIMAL(10,2) NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    MinimumPurchase DECIMAL(10,2) DEFAULT 0,
    IsActive BIT DEFAULT 1,
    Description NVARCHAR(MAX),
    CHECK (EndDate >= StartDate),
    CHECK ((DiscountType = 'Percentage' AND DiscountValue BETWEEN 0 AND 100) OR 
           (DiscountType = 'Fixed Amount' AND DiscountValue >= 0))
);

-- Bảng dịch vụ sửa chữa
CREATE TABLE RepairServices (
    RepairID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID VARCHAR(10) NOT NULL,
    EmployeeID VARCHAR(10) NULL, -- Nhân viên phụ trách
    DeviceName NVARCHAR(255) NOT NULL, -- Mô tả thiết bị cần sửa
    Problem NVARCHAR(MAX) NOT NULL, -- Mô tả vấn đề
    DiagnosisResult NVARCHAR(MAX), -- Kết quả chẩn đoán
    RepairCost DECIMAL(10,2), -- Chi phí sửa chữa
    ReceiveDate DATETIME DEFAULT GETDATE(), -- Ngày nhận thiết bị
    EstimatedCompletionDate DATETIME, -- Dự kiến hoàn thành
    ActualCompletionDate DATETIME, -- Ngày hoàn thành thực tế
    Status NVARCHAR(50) DEFAULT 'Received' CHECK (Status IN ('Received', 'Diagnosing', 'Waiting for Parts', 'Repairing', 'Completed', 'Delivered', 'Cancelled')),
    Notes NVARCHAR(MAX),
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID) 
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID) 
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Bảng quản lý người dùng (cho đăng nhập hệ thống)
CREATE TABLE Users (
    UserID VARCHAR(10) PRIMARY KEY, -- vd: U001, U002,... (nên tạo triger để tự động tạo mã)
    Username NVARCHAR(50) NOT NULL UNIQUE,
    PasswordHash NVARCHAR(255) NOT NULL, -- Lưu hash của mật khẩu, không lưu trực tiếp
    EmployeeID VARCHAR(10) NULL, -- Liên kết với nhân viên nếu là tài khoản nhân viên
    IsActive BIT DEFAULT 1,
    LastLogin DATETIME,
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID) ON DELETE SET NULL
);


-- Bảng phân quyền
CREATE TABLE Roles (
    RoleID INT PRIMARY KEY,
    RoleName NVARCHAR(50) NOT NULL UNIQUE,
    Description NVARCHAR(255)
);


-- Bảng quản lý trả hàng và đổi sản phẩm
CREATE TABLE Returns (
    ReturnID INT IDENTITY(1,1) PRIMARY KEY,
    InvoiceDetailID INT NOT NULL, -- Chi tiết hóa đơn gốc
    ReturnDate DATETIME DEFAULT GETDATE(),
    ReturnReason NVARCHAR(MAX) NOT NULL,
    Quantity INT NOT NULL CHECK (Quantity > 0),
    ReturnAmount DECIMAL(10,2) NOT NULL CHECK (ReturnAmount >= 0),
    ProcessedBy VARCHAR(10) NULL, -- Nhân viên xử lý
    Status NVARCHAR(20) DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Approved', 'Rejected', 'Completed')),
    IsExchange BIT DEFAULT 0, -- Đánh dấu là đổi hàng hay hoàn tiền
    NewProductID VARCHAR(10) NULL, -- Sản phẩm mới nếu là đổi hàng
    FOREIGN KEY (InvoiceDetailID) REFERENCES InvoiceDetails(InvoiceDetailID) ON DELETE CASCADE,
    FOREIGN KEY (ProcessedBy) REFERENCES Employees(EmployeeID) 
        ON DELETE NO ACTION
        ON UPDATE CASCADE,
    FOREIGN KEY (NewProductID) REFERENCES Products(ProductID) 
        ON DELETE NO ACTION
        ON UPDATE CASCADE
);

-- Thêm trường giới tính trong nhân viên
ALTER TABLE Employees
ADD Gender NVARCHAR(10) CHECK (Gender IN ('Male', 'Female', 'Other'));

-- thêm trường ngày sinh cho employees
ALTER TABLE Employees
ADD DateOfBirth DATE;
GO
-- Thêm trường avatar trong bảng Employees

ALTER TABLE Employees
ADD Avatar VARCHAR(MAX);

-- Thêm trường point cho khách hàng
ALTER TABLE Customers
ADD Point INT DEFAULT 0 CHECK (Point >= 0);

--Thêm trường CreatedAt, UpdatedAt cho bảng Products
ALTER TABLE Products
ADD CreatedAt DATETIME DEFAULT GETDATE(), -- Ngày tạo sản phẩm
    UpdatedAt DATETIME DEFAULT GETDATE(); -- Ngày cập nhật sản phẩm
-- Cập nhật trường UpdatedAt mỗi khi có thay đổi

--thêm dữ liệu thời gian tạo và cập nhật cho bảng Products
UPDATE Products
SET CreatedAt = GETDATE(), UpdatedAt = GETDATE()
WHERE CreatedAt IS NULL OR UpdatedAt IS NULL;



-- Thêm trường RoleID vào bẳng Users
ALTER TABLE Users
ADD RoleID INT NULL,
    FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
    ON DELETE SET NULL
    ON UPDATE CASCADE;


--Thểm cột notes cho bảng Returns
ALTER TABLE Returns
ADD Notes NVARCHAR(MAX) DEFAULT '';

-- Thêm cột Manufacturer (Hãng sản xuất) vào bảng Products
ALTER TABLE Products
ADD Manufacturer NVARCHAR(100);

-- Thêm cột trạng thái PurchaseOrders
ALTER TABLE PurchaseOrders
ADD Status NVARCHAR(50) DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Completed', 'Cancelled', 'Delivering'));

--Thêm cột TotalAmount cho bảng PurchaseOrders
ALTER TABLE PurchaseOrders
ADD TotalAmount DECIMAL(10,2) CHECK (TotalAmount >= 0) NOT NULL DEFAULT 0; -- Tổng tiền hóa đơn