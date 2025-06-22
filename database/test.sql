SELECT
    p.ProductID AS productId,
    p.ProductName AS productName,
    COALESCE(SUM(id.Quantity), 0) AS quantity,
    COALESCE(id.CostPrice, 0) AS costPrice,
    COALESCE(id.UnitPrice, 0) AS unitPrice,
    COALESCE(SUM(id.DiscountAmount), 0) AS discountAmount,
    COALESCE(SUM(id.Quantity * id.UnitPrice), 0) AS totalAmount,
    COALESCE(SUM(id.Quantity * id.UnitPrice), 0) - COALESCE(SUM(id.DiscountAmount), 0) AS revenue
FROM InvoiceDetails id
    JOIN Invoices i ON id.InvoiceID = i.InvoiceID
    JOIN Products p ON id.ProductID = p.ProductID
WHERE i.StatusID = 3
    AND i.InvoiceDate BETWEEN '2025-06-01' AND '2025-06-30'
GROUP BY p.ProductID, p.ProductName, id.CostPrice, id.UnitPrice
ORDER BY revenue DESC

--Lấy giá vốn (CostPrice) từ Products sang InvoiceDetails

UPDATE InvoiceDetails
SET CostPrice = p.CostPrice, ProfitMargin = p.ProfitMargin
FROM InvoiceDetails id
    JOIN Products p ON p.ProductID = id.ProductID;


SELECT
    SUM(COALESCE(id.Quantity * id.UnitPrice, 0) - COALESCE(id.DiscountAmount, 0) - COALESCE(id.Quantity * id.CostPrice, 0)) AS totalProfit,
    CASE 
        WHEN SUM(COALESCE(id.Quantity * id.UnitPrice, 0) - COALESCE(id.DiscountAmount, 0)) = 0 THEN 0
        ELSE (SUM(COALESCE(id.Quantity * id.UnitPrice, 0) - COALESCE(id.DiscountAmount, 0) - COALESCE(id.Quantity * id.CostPrice, 0)) * 100.0) / 
             SUM(COALESCE(id.Quantity * id.UnitPrice, 0) - COALESCE(id.DiscountAmount, 0))  
    END AS profitMarginPercent
FROM InvoiceDetails id
    JOIN Invoices i ON id.InvoiceID = i.InvoiceID
WHERE i.StatusID = 3
    AND i.InvoiceDate BETWEEN '2025-06-22' AND '2025-06-23'




SELECT i.*, c.FullName as CustomerName, c.PhoneNumber as CustomerPhone, e.FullName as EmployeeName, e.PhoneNumber as EmployeePhone, SUM(id.Quantity) as TotalQuantity
FROM Invoices i
    LEFT JOIN Customers c ON i.CustomerID = c.CustomerID
    LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID
    JOIN InvoiceDetails id ON i.InvoiceID = id.InvoiceID
WHERE i.InvoiceDate BETWEEN '2025-06-01' AND '2025-06-30'
GROUP BY i.InvoiceID, i.InvoiceDate, i.TotalAmount, i.StatusID, i.CustomerID, i.EmployeeID, i.Notes, i.PaymentMethodID, i.DiscountAmount, c.FullName, c.PhoneNumber, e.FullName, e.PhoneNumber
ORDER BY i.InvoiceDate DESC










SELECT
    c.CustomerID,
    c.FullName,
    COUNT(DISTINCT i.InvoiceID) AS OrderCount,
    COALESCE(SUM(i.TotalAmount), 0) AS TotalAmount,
    COALESCE(SUM(i.DiscountAmount), 0) AS DiscountAmount,
    COALESCE(SUM(i.TotalAmount - ISNULL(i.DiscountAmount, 0)), 0) AS Revenue,
    COALESCE(return_data.ReturnCount, 0) AS ReturnCount,
    COALESCE(return_data.ReturnValue, 0) AS ReturnValue,
    COALESCE(SUM(i.TotalAmount - ISNULL(i.DiscountAmount, 0)), 0) - COALESCE(return_data.ReturnValue, 0) AS NetRevenue
FROM Customers c
    LEFT JOIN Invoices i ON c.CustomerID = i.CustomerID
        AND i.InvoiceDate BETWEEN '2025-03-01' AND '2025-06-30'
        AND i.StatusID = 3
    LEFT JOIN ( 
        SELECT
            i2.CustomerID,
            COUNT(DISTINCT r.ReturnID) AS ReturnCount,
            COALESCE(SUM(r.ReturnAmount), 0) AS ReturnValue
        FROM Returns r
            JOIN InvoiceDetails id ON r.InvoiceDetailID = id.InvoiceDetailID
            JOIN Invoices i2 ON id.InvoiceID = i2.InvoiceID
        WHERE r.ReturnDate BETWEEN '2025-03-01' AND '2025-06-30'
            AND r.Status = 'Completed'
        GROUP BY i2.CustomerID 
    ) return_data ON c.CustomerID = return_data.CustomerID
WHERE i.InvoiceID IS NOT NULL
GROUP BY c.CustomerID, c.FullName, return_data.ReturnCount, return_data.ReturnValue
ORDER BY NetRevenue DESC;