CREATE OR ALTER TRIGGER trg_GenerateUserID
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
        RoleID INT,
        IsActive BIT,
        LastLogin DATETIME,
        CreatedAt DATETIME
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedUsers (Username, PasswordHash, EmployeeID, RoleID, IsActive, LastLogin, CreatedAt)
    SELECT Username, PasswordHash, EmployeeID, RoleID, IsActive, LastLogin, CreatedAt
    FROM inserted;
    
    -- Lấy ID tiếp theo
    DECLARE @NextUserID INT;
    SELECT @NextUserID = ISNULL(MAX(CAST(SUBSTRING(UserID, 2, LEN(UserID)-1) AS INT)), 0) + 1
    FROM Users;
    
    -- Thêm dữ liệu với ID đã tạo
    INSERT INTO Users (UserID, Username, PasswordHash, EmployeeID, RoleID, IsActive, LastLogin, CreatedAt)
    SELECT 'U' + RIGHT('000' + CAST((@NextUserID + RowNum - 1) AS VARCHAR(3)), 3),
           Username, PasswordHash, EmployeeID, RoleID, 
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

-- Cập nhật trigger tự động tạo mã sản phẩm chỉ dùng số tăng dần
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
        Price DECIMAL(18,2),
        StockQuantity INT,
        Specifications NVARCHAR(MAX),
        Description NVARCHAR(MAX),
        Manufacturer NVARCHAR(100),
        CostPrice DECIMAL(18,2),
        AverageCostPrice DECIMAL(18,2),
        ProfitMargin DECIMAL(5,2),
        CreatedAt DATETIME,
        UpdatedAt DATETIME
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedProducts 
    (ProductName, CategoryID, SupplierID, Price, StockQuantity, Specifications, Description, 
     Manufacturer, CostPrice, AverageCostPrice, ProfitMargin, CreatedAt, UpdatedAt)
    SELECT 
        ProductName, CategoryID, SupplierID, Price, StockQuantity, Specifications, Description,
        Manufacturer, CostPrice, AverageCostPrice, ProfitMargin, 
        ISNULL(CreatedAt, GETDATE()), ISNULL(UpdatedAt, GETDATE())
    FROM inserted;
    
    -- Lấy mã sản phẩm tiếp theo
    DECLARE @NextProductNumber INT;
    SELECT @NextProductNumber = ISNULL(MAX(CAST(ProductID AS INT)), 0) + 1
    FROM Products
    WHERE ISNUMERIC(ProductID) = 1;
    
    -- Xử lý từng dòng dữ liệu được thêm vào
    DECLARE @RowCount INT = (SELECT COUNT(*) FROM @InsertedProducts)
    DECLARE @CurrentRow INT = 1
    
    WHILE @CurrentRow <= @RowCount
    BEGIN
        -- Thêm sản phẩm với mã mới (dạng 00001, 00002,...)
        INSERT INTO Products 
        (ProductID, ProductName, CategoryID, SupplierID, Price, StockQuantity, 
         Specifications, Description, Manufacturer, CostPrice, AverageCostPrice, 
         ProfitMargin, CreatedAt, UpdatedAt)
        SELECT 
            RIGHT('00000' + CAST((@NextProductNumber + @CurrentRow - 1) AS VARCHAR(5)), 5),
            ProductName, 
            CategoryID, 
            SupplierID, 
            Price, 
            ISNULL(StockQuantity, 0), 
            Specifications, 
            Description,
            Manufacturer,
            CostPrice,
            AverageCostPrice,
            ProfitMargin,
            CreatedAt,
            UpdatedAt
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
-- Sửa lại trigger tự động tạo mã khách hàng để tránh lỗi chuyển đổi
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
    
    -- Lấy mã tiếp theo - sửa phần này để xử lý giá trị không phải số
    DECLARE @NextCustomerID INT;
    SELECT @NextCustomerID = ISNULL(MAX(TRY_CAST(SUBSTRING(CustomerID, 3, LEN(CustomerID)-2) AS INT)), 0) + 1
    FROM Customers
    WHERE CustomerID LIKE 'KH%'
    AND ISNUMERIC(SUBSTRING(CustomerID, 3, LEN(CustomerID)-2)) = 1;
    
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
        Gender NVARCHAR(10),
        DateOfBirth DATE,
        Email NVARCHAR(255),
        Position NVARCHAR(50),
        CreatedAt DATETIME
    );
    
    -- Đưa dữ liệu vào bảng tạm
    INSERT INTO @InsertedEmployees (FullName, PhoneNumber, Gender, DateOfBirth, Email, Position, CreatedAt)
    SELECT FullName, PhoneNumber, Gender, DateOfBirth, Email, Position, ISNULL(CreatedAt, GETDATE())
    FROM inserted;
    
    -- Lấy mã tiếp theo
    DECLARE @NextEmployeeID INT;
    SELECT @NextEmployeeID = ISNULL(MAX(CAST(SUBSTRING(EmployeeID, 3, LEN(EmployeeID)-2) AS INT)), 0) + 1
    FROM Employees;
    
    -- Thêm dữ liệu với mã đã tạo và UpdatedAt = CreatedAt
    INSERT INTO Employees (EmployeeID, FullName, PhoneNumber, Gender, DateOfBirth, Email, Position, CreatedAt, UpdatedAt)
    SELECT 
        'NV' + RIGHT('0' + CAST((@NextEmployeeID + RowNum - 1) AS VARCHAR(2)), 2),
        FullName, 
        PhoneNumber, 
        Gender,
        DateOfBirth,
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
       OR UPDATE(Position) OR UPDATE(Gender) OR UPDATE(DateOfBirth)
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
GO

-- Tạo trigger duy nhất để cập nhật giá bán, giá vốn và tỉ suất lợi nhuận
CREATE OR ALTER TRIGGER trg_UpdateProductPricing
ON Products
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Lấy mức độ lồng nhau của trigger để tránh vòng lặp vô hạn
    DECLARE @TriggerNestLevel INT = TRIGGER_NESTLEVEL();
    
    -- Chỉ thực hiện khi mức độ lồng nhau của trigger < 2
    IF @TriggerNestLevel < 2
    BEGIN
        -- TRƯỜNG HỢP 1: Cập nhật giá bán khi giá vốn hoặc tỉ suất lợi nhuận thay đổi
        IF (UPDATE(CostPrice) OR UPDATE(ProfitMargin)) AND NOT UPDATE(Price)
        BEGIN
            -- Cập nhật giá bán dựa trên giá vốn và tỉ suất lợi nhuận
            UPDATE p
            SET 
                Price = CASE 
                          WHEN i.CostPrice IS NOT NULL AND i.ProfitMargin IS NOT NULL 
                          THEN ROUND(i.CostPrice * (1 + i.ProfitMargin/100), 0)
                          ELSE p.Price
                       END,
                UpdatedAt = GETDATE()
            FROM Products p
            INNER JOIN inserted i ON p.ProductID = i.ProductID
            WHERE i.CostPrice IS NOT NULL 
              AND i.ProfitMargin IS NOT NULL;
            
            -- Ghi lại lịch sử thay đổi giá
            INSERT INTO PriceHistory (ProductID, OldPrice, NewPrice, OldCostPrice, NewCostPrice, ChangedDate, Note)
            SELECT 
                i.ProductID,
                d.Price,
                i.Price,
                d.CostPrice,
                i.CostPrice,
                GETDATE(),
                'Tự động cập nhật giá bán từ giá vốn và tỉ suất lợi nhuận'
            FROM inserted i
            INNER JOIN deleted d ON i.ProductID = d.ProductID
            WHERE i.Price <> d.Price OR i.CostPrice <> d.CostPrice;
        END;
        
        -- TRƯỜNG HỢP 2: Cập nhật tỉ suất lợi nhuận khi giá bán và giá vốn thay đổi
        ELSE IF UPDATE(Price) AND UPDATE(CostPrice) AND NOT UPDATE(ProfitMargin)
        BEGIN
            -- Cập nhật tỉ suất lợi nhuận dựa trên giá bán và giá vốn mới
            UPDATE p
            SET 
                ProfitMargin = CASE 
                                 WHEN i.CostPrice > 0 
                                 THEN ROUND(((i.Price / i.CostPrice) - 1) * 100, 2)
                                 ELSE p.ProfitMargin
                               END,
                UpdatedAt = GETDATE()
            FROM Products p
            INNER JOIN inserted i ON p.ProductID = i.ProductID
            WHERE i.CostPrice > 0;
            
            -- Ghi lại lịch sử thay đổi giá
            INSERT INTO PriceHistory (ProductID, OldPrice, NewPrice, OldCostPrice, NewCostPrice, ChangedDate, Note)
            SELECT 
                i.ProductID,
                d.Price,
                i.Price,
                d.CostPrice,
                i.CostPrice,
                GETDATE(),
                'Tự động cập nhật tỉ suất lợi nhuận từ giá bán và giá vốn mới'
            FROM inserted i
            INNER JOIN deleted d ON i.ProductID = d.ProductID
            WHERE i.Price <> d.Price OR i.CostPrice <> d.CostPrice;
        END;
        
        -- TRƯỜNG HỢP 3: Cập nhật tỉ suất lợi nhuận khi chỉ giá bán thay đổi
        ELSE IF UPDATE(Price) AND NOT UPDATE(ProfitMargin) AND NOT UPDATE(CostPrice)
        BEGIN
            -- Cập nhật tỉ suất lợi nhuận dựa trên giá bán mới và giá vốn hiện tại
            UPDATE p
            SET 
                ProfitMargin = CASE 
                                 WHEN i.CostPrice > 0 
                                 THEN ROUND(((i.Price / i.CostPrice) - 1) * 100, 2)
                                 ELSE p.ProfitMargin
                               END,
                UpdatedAt = GETDATE()
            FROM Products p
            INNER JOIN inserted i ON p.ProductID = i.ProductID
            WHERE i.CostPrice > 0;
            
            -- Ghi lại lịch sử thay đổi giá
            INSERT INTO PriceHistory (ProductID, OldPrice, NewPrice, OldCostPrice, NewCostPrice, ChangedDate, Note)
            SELECT 
                i.ProductID,
                d.Price,
                i.Price,
                d.CostPrice,
                i.CostPrice,
                GETDATE(),
                'Tự động cập nhật tỉ suất lợi nhuận từ giá bán'
            FROM inserted i
            INNER JOIN deleted d ON i.ProductID = d.ProductID
            WHERE i.Price <> d.Price;
        END;
        
        -- TRƯỜNG HỢP 4: Cập nhật giá bán và tỉ suất lợi nhuận khi chỉ giá vốn thay đổi
        ELSE IF UPDATE(CostPrice) AND NOT UPDATE(Price) AND NOT UPDATE(ProfitMargin)
        BEGIN
            -- Cập nhật giá bán dựa trên giá vốn mới và tỉ suất lợi nhuận hiện tại
            UPDATE p
            SET 
                Price = CASE 
                          WHEN i.CostPrice IS NOT NULL AND p.ProfitMargin IS NOT NULL 
                          THEN ROUND(i.CostPrice * (1 + p.ProfitMargin/100), 0)
                          ELSE p.Price
                       END,
                UpdatedAt = GETDATE()
            FROM Products p
            INNER JOIN inserted i ON p.ProductID = i.ProductID
            WHERE i.CostPrice IS NOT NULL AND i.CostPrice > 0;
            
            -- Ghi lại lịch sử thay đổi giá
            INSERT INTO PriceHistory (ProductID, OldPrice, NewPrice, OldCostPrice, NewCostPrice, ChangedDate, Note)
            SELECT 
                i.ProductID,
                d.Price,
                p.Price, -- Lấy giá mới sau khi cập nhật
                d.CostPrice,
                i.CostPrice,
                GETDATE(),
                'Tự động cập nhật giá bán từ giá vốn mới'
            FROM inserted i
            INNER JOIN deleted d ON i.ProductID = d.ProductID
            INNER JOIN Products p ON i.ProductID = p.ProductID
            WHERE p.Price <> d.Price OR i.CostPrice <> d.CostPrice;
        END;

        -- TRƯỜNG HỢP 5: CẢ 3 TRƯỜNG ĐỀU CÙNG THAY ĐỔI THÌ LẤY GIÁ VỐN VÀ TỈ SUẤT LỢI NHUẬN LÀM GỐC
        ELSE IF UPDATE(Price) AND UPDATE(CostPrice) AND UPDATE(ProfitMargin)
        BEGIN
            -- Cập nhật giá bán dựa trên giá vốn mới và tỉ suất lợi nhuận mới
            UPDATE p
            SET 
                Price = CASE 
                          WHEN i.CostPrice IS NOT NULL AND i.ProfitMargin IS NOT NULL 
                          THEN ROUND(i.CostPrice * (1 + i.ProfitMargin/100), 0)
                          ELSE p.Price
                       END,
                UpdatedAt = GETDATE()
            FROM Products p
            INNER JOIN inserted i ON p.ProductID = i.ProductID
            WHERE i.CostPrice IS NOT NULL AND i.CostPrice > 0;

            -- Ghi lại lịch sử thay đổi giá
            INSERT INTO PriceHistory (ProductID, OldPrice, NewPrice, OldCostPrice, NewCostPrice, ChangedDate, Note)
            SELECT 
                i.ProductID,
                d.Price,
                p.Price, -- Lấy giá mới sau khi cập nhật
                d.CostPrice,
                i.CostPrice,
                GETDATE(),
                'Tự động cập nhật giá bán từ giá vốn và tỉ suất lợi nhuận mới'
            FROM inserted i
            INNER JOIN deleted d ON i.ProductID = d.ProductID
            INNER JOIN Products p ON i.ProductID = p.ProductID
            WHERE p.Price <> d.Price OR i.CostPrice <> d.CostPrice;
        END;
    END;
END;
GO

-- Xóa các trigger cũ nếu có
IF OBJECT_ID('trg_UpdatePriceFromCostAndMargin', 'TR') IS NOT NULL
    DROP TRIGGER trg_UpdatePriceFromCostAndMargin;
GO

IF OBJECT_ID('trg_UpdateMarginFromPrice', 'TR') IS NOT NULL
    DROP TRIGGER trg_UpdateMarginFromPrice;
GO