SELECT i.*, c.FullName as CustomerName, c.PhoneNumber as CustomerPhone,  
                     s.StatusName as StatusName, p.MethodName as PaymentMethodName, e.FullName as EmployeeName
                     FROM Invoices i  
                     JOIN Customers c ON i.CustomerID = c.CustomerID 
                     JOIN Employees e ON i.EmployeeID = e.EmployeeID 
                     JOIN InvoiceStatus s ON i.StatusID = s.StatusID  
                     JOIN PaymentMethods p ON i.PaymentMethodID = p.PaymentMethodID  
                     WHERE i.CustomerID = 'KH01'  
                     ORDER BY i.InvoiceDate DESC;