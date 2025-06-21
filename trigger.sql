CREATE TRIGGER trg_GenerateUserID
ON Users
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tạo bảng tạm để lưu dữ liệu
    DECLARE @InsertedUsers TABLE (
        RowNum INT IDENTITY(1,1),
        Username NVARCHAR(50),
        PasswordHash NVARCHAR(255),
        EmployeeID VARCHAR(10),
        IsActive BIT,
        LastLogin DATETIME,
        CreatedAt DATETIME
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedUsers (Username, PasswordHash, EmployeeID, IsActive, LastLogin, CreatedAt)
    SELECT Username, PasswordHash, EmployeeID, IsActive, LastLogin, CreatedAt
    FROM inserted;
    
    -- Lấy ID tiếp theo
    DECLARE @NextUserID INT;
    SELECT @NextUserID = ISNULL(MAX(CAST(SUBSTRING(UserID, 2, LEN(UserID)-1) AS INT)), 0) + 1
    FROM Users;
    
    -- Thêm dữ liệu với ID đã tạo
    INSERT INTO Users (UserID, Username, PasswordHash, EmployeeID, IsActive, LastLogin, CreatedAt)
    SELECT 'U' + RIGHT('000' + CAST((@NextUserID + RowNum - 1) AS VARCHAR(3)), 3),
           Username, PasswordHash, EmployeeID, 
           ISNULL(IsActive, 1), 
           LastLogin, 
           ISNULL(CreatedAt, GETDATE())
    FROM @InsertedUsers;
END;
GO

--Trigger cập nhật tồn kho sau khi bán hàng
CREATE TRIGGER trg_UpdateWareHouseAfterSale
ON InvoiceDetails
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Cập nhật số lượng tồn kho
    UPDATE p
    SET StockQuantity = p.StockQuantity - i.Quantity
    FROM Products p
    INNER JOIN inserted i ON p.ProductID = i.ProductID;
    
    -- Kiểm tra nếu có sản phẩm tồn kho âm
    IF EXISTS (SELECT 1 FROM Products WHERE StockQuantity < 0)
    BEGIN
        RAISERROR('Không đủ hàng tồn kho cho một hoặc nhiều sản phẩm.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;
END;
GO


--Trigger cập nhật tồn kho sau khi nhập hàng
CREATE TRIGGER trg_UpdateWareHouseAfterPurchase
ON PurchaseOrderDetails
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Cập nhật số lượng tồn kho
    UPDATE p
    SET StockQuantity = p.StockQuantity + i.Quantity
    FROM Products p
    INNER JOIN inserted i ON p.ProductID = i.ProductID;
END;
GO

--Trigger tự động tính tổng tiền hóa đơn
CREATE TRIGGER trg_CalculateInvoiceTotal
ON InvoiceDetails
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Lấy các InvoiceID bị ảnh hưởng
    DECLARE @AffectedInvoiceIDs TABLE (InvoiceID INT);
    
    INSERT INTO @AffectedInvoiceIDs (InvoiceID)
    SELECT InvoiceID FROM inserted
    UNION
    SELECT InvoiceID FROM deleted;
    
    -- Cập nhật tổng tiền cho từng hóa đơn
    UPDATE inv
    SET TotalAmount = ISNULL((
        SELECT SUM(Quantity * UnitPrice)
        FROM InvoiceDetails
        WHERE InvoiceID = inv.InvoiceID
    ), 0)
    FROM Invoices inv
    INNER JOIN @AffectedInvoiceIDs a ON inv.InvoiceID = a.InvoiceID;
END;
GO

--Trigger tự động tạo mã khách hàng (KH01, KH02...)
CREATE TRIGGER trg_GenerateCustomerID
ON Customers
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tạo bảng tạm
    DECLARE @InsertedCustomers TABLE (
        RowNum INT IDENTITY(1,1),
        FullName NVARCHAR(255),
        PhoneNumber NVARCHAR(15),
        Email NVARCHAR(255),
        Address NVARCHAR(MAX),
        CreatedAt DATETIME
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedCustomers (FullName, PhoneNumber, Email, Address, CreatedAt)
    SELECT FullName, PhoneNumber, Email, Address, CreatedAt
    FROM inserted;
    
    -- Lấy mã tiếp theo
    DECLARE @NextCustomerID INT;
    SELECT @NextCustomerID = ISNULL(MAX(CAST(SUBSTRING(CustomerID, 3, LEN(CustomerID)-2) AS INT)), 0) + 1
    FROM Customers;
    
    -- Thêm dữ liệu với mã đã tạo
    INSERT INTO Customers (CustomerID, FullName, PhoneNumber, Email, Address, CreatedAt)
    SELECT 'KH' + RIGHT('0' + CAST((@NextCustomerID + RowNum - 1) AS VARCHAR(2)), 2),
           FullName, PhoneNumber, Email, Address, 
           ISNULL(CreatedAt, GETDATE())
    FROM @InsertedCustomers;
END;
GO

--Trigger tự động tạo mã nhân viên (NV01, NV02...)
CREATE TRIGGER trg_GenerateEmployeeID
ON Employees
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tạo bảng tạm
    DECLARE @InsertedEmployees TABLE (
        RowNum INT IDENTITY(1,1),
        FullName NVARCHAR(255),
        PhoneNumber NVARCHAR(15),
        Email NVARCHAR(255),
        Position NVARCHAR(50)
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedEmployees (FullName, PhoneNumber, Email, Position)
    SELECT FullName, PhoneNumber, Email, Position
    FROM inserted;
    
    -- Lấy mã tiếp theo
    DECLARE @NextEmployeeID INT;
    SELECT @NextEmployeeID = ISNULL(MAX(CAST(SUBSTRING(EmployeeID, 3, LEN(EmployeeID)-2) AS INT)), 0) + 1
    FROM Employees;
    
    -- Thêm dữ liệu với mã đã tạo
    INSERT INTO Employees (EmployeeID, FullName, PhoneNumber, Email, Position)
    SELECT 'NV' + RIGHT('0' + CAST((@NextEmployeeID + RowNum - 1) AS VARCHAR(2)), 2),
           FullName, PhoneNumber, Email, Position
    FROM @InsertedEmployees;
END;
GO

USE [ComputerStoreManagement]
GO
/****** Object:  Trigger [dbo].[trg_GenerateProductID]    Script Date: 05/04/2025 1:57:31 CH ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

USE [ComputerStoreManagement]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- Trigger tự động tạo mã sản phẩm theo danh mục (LAP001, LK002, PC001...)
ALTER TRIGGER [dbo].[trg_GenerateProductID]
ON [dbo].[Products]
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tạo bảng tạm
    DECLARE @InsertedProducts TABLE (
        RowNum INT IDENTITY(1,1),
        ProductName NVARCHAR(255),
        CategoryID VARCHAR(10),
        SupplierID VARCHAR(10),
        Price DECIMAL(10,2),
        StockQuantity INT,
        Specifications NVARCHAR(MAX),
        Description NVARCHAR(MAX)
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedProducts 
    (ProductName, CategoryID, SupplierID, Price, StockQuantity, Specifications, Description)
    SELECT ProductName, CategoryID, SupplierID, Price, StockQuantity, Specifications, Description
    FROM inserted;
    
    -- Thêm sản phẩm với mã tự động cho từng danh mục
    DECLARE @RowCount INT = (SELECT COUNT(*) FROM @InsertedProducts)
    DECLARE @CurrentRow INT = 1
    
    WHILE @CurrentRow <= @RowCount
    BEGIN
        DECLARE @CategoryID VARCHAR(10)
        DECLARE @NextProductNumber INT
        
        -- Lấy CategoryID của dòng hiện tại
        SELECT @CategoryID = CategoryID
        FROM @InsertedProducts
        WHERE RowNum = @CurrentRow
        
        -- Tìm số tiếp theo cho CategoryID cụ thể
        SELECT @NextProductNumber = ISNULL(MAX(CAST(SUBSTRING(ProductID, LEN(@CategoryID) + 1, 
                    LEN(ProductID) - LEN(@CategoryID)) AS INT)), 0) + 1
        FROM Products 
        WHERE ProductID LIKE @CategoryID + '%'
        
        -- Thêm sản phẩm với mã mới
        INSERT INTO Products 
        (ProductID, ProductName, CategoryID, SupplierID, Price, StockQuantity, Specifications, Description)
        SELECT 
            @CategoryID + RIGHT('000' + CAST(@NextProductNumber AS VARCHAR(3)), 3),
            ProductName, 
            CategoryID, 
            SupplierID, 
            Price, 
            ISNULL(StockQuantity, 0), 
            Specifications, 
            Description
        FROM @InsertedProducts
        WHERE RowNum = @CurrentRow
        
        SET @CurrentRow = @CurrentRow + 1
    END
END;
GO

--Trigger xử lý đổi/trả hàng
CREATE TRIGGER trg_HandleProductReturn
ON Returns
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Xử lý khi trạng thái chuyển sang Completed
    IF EXISTS (
        SELECT 1 FROM inserted i 
        INNER JOIN deleted d ON i.ReturnID = d.ReturnID
        WHERE i.Status = 'Completed' AND d.Status <> 'Completed'
    )
    BEGIN
        -- Cập nhật tồn kho cho trường hợp trả hàng (không phải đổi)
        UPDATE p
        SET StockQuantity = p.StockQuantity + r.Quantity
        FROM Products p
        INNER JOIN inserted r ON p.ProductID = (
            SELECT ProductID 
            FROM InvoiceDetails 
            WHERE InvoiceDetailID = r.InvoiceDetailID
        )
        WHERE r.Status = 'Completed' AND r.IsExchange = 0;
        
        -- Cập nhật tồn kho cho trường hợp đổi hàng
        UPDATE p
        SET StockQuantity = p.StockQuantity - 1
        FROM Products p
        INNER JOIN inserted r ON p.ProductID = r.NewProductID
        WHERE r.Status = 'Completed' AND r.IsExchange = 1;
    END;
END;
GO
--Trigger tự động tính ngày hết hạn bảo hành
CREATE TRIGGER trg_SetWarrantyDates
ON Warranties
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Lấy thông tin danh mục sản phẩm để xác định thời hạn bảo hành
    WITH ProductCategories AS (
        SELECT 
            i.InvoiceDetailID,
            p.CategoryID,
            i.WarrantyID,
            i.StartDate
        FROM inserted i
        INNER JOIN InvoiceDetails id ON i.InvoiceDetailID = id.InvoiceDetailID
        INNER JOIN Products p ON id.ProductID = p.ProductID
    )
    
    INSERT INTO Warranties (WarrantyID, InvoiceDetailID, StartDate, EndDate)
    SELECT 
        pc.WarrantyID,
        pc.InvoiceDetailID,
        pc.StartDate,
        CASE 
            -- Thời hạn bảo hành khác nhau theo danh mục
            WHEN pc.CategoryID = 'LAP' THEN DATEADD(YEAR, 1, pc.StartDate) -- Laptop: 2 năm
            WHEN pc.CategoryID = 'PC' THEN DATEADD(YEAR, 1, pc.StartDate)  -- Desktop: 3 năm
            WHEN pc.CategoryID = 'LK' THEN DATEADD(YEAR, 1, pc.StartDate)  -- Linh kiện: 1 năm
            WHEN pc.CategoryID = 'MH' THEN DATEADD(YEAR, 1, pc.StartDate)  -- Màn hình: 2 năm
            ELSE DATEADD(MONTH, 6, pc.StartDate)                          -- Mặc định: 6 tháng
        END
    FROM ProductCategories pc;
END;
GO

--


---------------
-- SỬA TRIGGER
----------
-- Cập nhật trigger tự động tạo mã khách hàng và thiết lập UpdatedAt = CreatedAt
ALTER TRIGGER trg_GenerateCustomerID
ON Customers
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tạo bảng tạm
    DECLARE @InsertedCustomers TABLE (
        RowNum INT IDENTITY(1,1),
        FullName NVARCHAR(255),
        PhoneNumber NVARCHAR(15),
        Email NVARCHAR(255),
        Address NVARCHAR(MAX),
        Point INT,
        CreatedAt DATETIME
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedCustomers (FullName, PhoneNumber, Email, Address, Point, CreatedAt)
    SELECT FullName, PhoneNumber, Email, Address, Point, ISNULL(CreatedAt, GETDATE())
    FROM inserted;
    
    -- Lấy mã tiếp theo
    DECLARE @NextCustomerID INT;
    SELECT @NextCustomerID = ISNULL(MAX(CAST(SUBSTRING(CustomerID, 3, LEN(CustomerID)-2) AS INT)), 0) + 1
    FROM Customers;
    
    -- Thêm dữ liệu với mã đã tạo và UpdatedAt = CreatedAt
    INSERT INTO Customers (CustomerID, FullName, PhoneNumber, Email, Address, Point, CreatedAt, UpdatedAt)
    SELECT 
        'KH' + RIGHT('0' + CAST((@NextCustomerID + RowNum - 1) AS VARCHAR(2)), 2),
        FullName, 
        PhoneNumber, 
        Email, 
        Address, 
        ISNULL(Point, 0), 
        CreatedAt,
        CreatedAt  -- Đặt UpdatedAt = CreatedAt khi thêm mới
    FROM @InsertedCustomers;
END;
GO

-- Tạo trigger để cập nhật UpdatedAt khi cập nhật thông tin khách hàng
CREATE TRIGGER trg_UpdateCustomerTimestamp
ON Customers
AFTER UPDATE
AS
BEGIN
    -- Chỉ cập nhật UpdatedAt nếu có trường nào đó thay đổi (trừ UpdatedAt)
    IF UPDATE(FullName) OR UPDATE(PhoneNumber) OR UPDATE(Email) 
       OR UPDATE(Address) OR UPDATE(Point)
    BEGIN
        UPDATE Customers
        SET UpdatedAt = GETDATE()
        FROM Customers c
        INNER JOIN inserted i ON c.CustomerID = i.CustomerID;
    END
END;
GO

-- Tương tự cho các bảng liên quan
-- Cập nhật trigger tự động tạo mã nhân viên và thiết lập UpdatedAt = CreatedAt
ALTER TRIGGER trg_GenerateEmployeeID
ON Employees
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tạo bảng tạm
    DECLARE @InsertedEmployees TABLE (
        RowNum INT IDENTITY(1,1),
        FullName NVARCHAR(255),
        PhoneNumber NVARCHAR(15),
        Email NVARCHAR(255),
        Position NVARCHAR(50),
        CreatedAt DATETIME
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedEmployees (FullName, PhoneNumber, Email, Position, CreatedAt)
    SELECT FullName, PhoneNumber, Email, Position, ISNULL(CreatedAt, GETDATE())
    FROM inserted;
    
    -- Lấy mã tiếp theo
    DECLARE @NextEmployeeID INT;
    SELECT @NextEmployeeID = ISNULL(MAX(CAST(SUBSTRING(EmployeeID, 3, LEN(EmployeeID)-2) AS INT)), 0) + 1
    FROM Employees;
    
    -- Thêm dữ liệu với mã đã tạo và UpdatedAt = CreatedAt
    INSERT INTO Employees (EmployeeID, FullName, PhoneNumber, Email, Position, CreatedAt, UpdatedAt)
    SELECT 
        'NV' + RIGHT('0' + CAST((@NextEmployeeID + RowNum - 1) AS VARCHAR(2)), 2),
        FullName, 
        PhoneNumber, 
        Email, 
        Position,
        CreatedAt,
        CreatedAt  -- Đặt UpdatedAt = CreatedAt khi thêm mới
    FROM @InsertedEmployees;
END;
GO

-- Tạo trigger để cập nhật UpdatedAt khi cập nhật thông tin nhân viên
CREATE OR ALTER TRIGGER trg_UpdateEmployeeTimestamp
ON Employees
AFTER UPDATE
AS
BEGIN
    -- Chỉ cập nhật UpdatedAt nếu có trường nào đó thay đổi (trừ UpdatedAt)
    IF UPDATE(FullName) OR UPDATE(PhoneNumber) OR UPDATE(Email) 
       OR UPDATE(Position)
    BEGIN
        UPDATE Employees
        SET UpdatedAt = GETDATE()
        FROM Employees e
        INNER JOIN inserted i ON e.EmployeeID = i.EmployeeID;
    END
END;
GO

-- Cập nhật lại dữ liệu hiện có để UpdatedAt = CreatedAt nếu chưa có giá trị
UPDATE Customers
SET UpdatedAt = CreatedAt
WHERE UpdatedAt IS NULL OR UpdatedAt <> CreatedAt;
GO

UPDATE Employees
SET UpdatedAt = CreatedAt
WHERE UpdatedAt IS NULL OR UpdatedAt <> CreatedAt;
GO
-- Tương tự cho bảng Users
ALTER TRIGGER trg_GenerateUserID
ON Users
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tạo bảng tạm để lưu dữ liệu
    DECLARE @InsertedUsers TABLE (
        RowNum INT IDENTITY(1,1),
        Username NVARCHAR(50),
        PasswordHash NVARCHAR(255),
        EmployeeID VARCHAR(10),
        IsActive BIT,
        LastLogin DATETIME,
        CreatedAt DATETIME
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedUsers (Username, PasswordHash, EmployeeID, IsActive, LastLogin, CreatedAt)
    SELECT Username, PasswordHash, EmployeeID, IsActive, LastLogin, ISNULL(CreatedAt, GETDATE())
    FROM inserted;
    
    -- Lấy ID tiếp theo
    DECLARE @NextUserID INT;
    SELECT @NextUserID = ISNULL(MAX(CAST(SUBSTRING(UserID, 2, LEN(UserID)-1) AS INT)), 0) + 1
    FROM Users;
    
    -- Thêm dữ liệu với ID đã tạo và UpdatedAt = CreatedAt
    INSERT INTO Users (UserID, Username, PasswordHash, EmployeeID, IsActive, LastLogin, CreatedAt, UpdatedAt)
    SELECT 
        'U' + RIGHT('000' + CAST((@NextUserID + RowNum - 1) AS VARCHAR(3)), 3),
        Username, 
        PasswordHash, 
        EmployeeID, 
        ISNULL(IsActive, 1), 
        LastLogin, 
        CreatedAt,
        CreatedAt  -- Đặt UpdatedAt = CreatedAt khi thêm mới
    FROM @InsertedUsers;
END;
GO

-- Tạo trigger để cập nhật UpdatedAt khi cập nhật thông tin người dùng
CREATE OR ALTER TRIGGER trg_UpdateUserTimestamp
ON Users
AFTER UPDATE
AS
BEGIN
    -- Chỉ cập nhật UpdatedAt nếu có trường nào đó thay đổi (trừ UpdatedAt)
    IF UPDATE(Username) OR UPDATE(PasswordHash) OR UPDATE(EmployeeID) 
       OR UPDATE(IsActive) OR UPDATE(LastLogin)
    BEGIN
        UPDATE Users
        SET UpdatedAt = GETDATE()
        FROM Users u
        INNER JOIN inserted i ON u.UserID = i.UserID;
    END
END;
GO

-- Cập nhật lại dữ liệu hiện có
UPDATE Users
SET UpdatedAt = CreatedAt
WHERE UpdatedAt IS NULL OR UpdatedAt <> CreatedAt;