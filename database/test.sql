SELECT icd.*, p.ProductName, p.Price, ic.CheckCode 
                     FROM InventoryCheckDetails icd 
                     LEFT JOIN Products p ON icd.ProductID = p.ProductID 
                     LEFT JOIN InventoryChecks ic ON icd.InventoryCheckID = ic.InventoryCheckID 
                     WHERE icd.InventoryCheckID = 15
                     ORDER BY p.ProductName