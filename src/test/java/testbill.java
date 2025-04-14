import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pcstore.utils.LocaleManager;

import java.io.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class testbill {
    public static void main(String[] args) throws Exception {

        NumberFormat formatter = LocaleManager.getInstance().getCurrencyFormatter();
        LocaleManager localeManager = LocaleManager.getInstance();

        // System.out.println("Working directory: " + System.getProperty("user.dir"));
        File f = new File("bill_test.xlsx");
        // System.out.println("File exists: " + f.exists());
        // System.out.println("Absolute path: " + f.getAbsolutePath());

        // Sử dụng đường dẫn tuyệt đối
        String filePath = f.getAbsolutePath(); 
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        // ✅ Dữ liệu tĩnh
        Map<String, String> fields = new HashMap<>();
        fields.put("nowdate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        fields.put("invoiceID", "INV-000123");
        fields.put("employeeName", "Nguyễn Văn A");
        fields.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        fields.put("customerName", "Trần Thị B");
        fields.put("customerPhone", "0123456789");
        fields.put("paymentMethod", "Tiền mặt");
        fields.put("amountReceived", formatter.format(500000));
        fields.put("amountChange", formatter.format(50000));

        // ✅ Dữ liệu sản phẩm - Không sử dụng giá trị có dấu phẩy trong key totalPrice
        List<Map<String, String>> products = Arrays.asList(
            Map.of("productName", "Bút bi", "quantity", "10", "price", "50000", "totalPrice", "50000"),
            Map.of("productName", "Vở 96 trang", "quantity", "5", "price", "12000", "totalPrice", "60000"),
            Map.of("productName", "Gôm tẩy", "quantity", "3", "price", "4000", "totalPrice", "12000")
        );

        // ✅ Tính tổng số lượng sản phẩm
        int sumQuantity = products.stream()
            .mapToInt(p -> Integer.parseInt(p.get("quantity")))
            .sum();
        
        // ✅ Tính tổng tiền - Giá trị số nguyên
        int totalAmount = products.stream()
            .mapToInt(p -> Integer.parseInt(p.get("totalPrice")))
            .sum();
        
        // ✅ Thêm vào fields với định dạng thích hợp
        fields.put("sumQuantity", String.valueOf(sumQuantity));
        fields.put("totalAmount", formatter.format(totalAmount));
        fields.put("textTotalAmount", "Một trăm hai mươi hai nghìn đồng");

        // ✅ Dòng chứa sản phẩm mẫu
        int productRowIndex = -1;

        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == CellType.STRING &&
                        cell.getStringCellValue().contains("{{productName}}")) {
                    productRowIndex = row.getRowNum();
                    break;
                }
            }
            if (productRowIndex != -1) break;
        }

        // 1. Tạo một Style Cache để lưu lại style của mỗi cell trong dòng mẫu
        Row templateRow = sheet.getRow(productRowIndex);
        Map<Integer, CellStyle> styleMap = new HashMap<>();
        for (Cell cell : templateRow) {
            styleMap.put(cell.getColumnIndex(), cell.getCellStyle());
        }
        
        // 2. Sao chép dòng mẫu cho mỗi sản phẩm
        for (int i = 1; i < products.size(); i++) {
            // Tạo dòng mới dưới dòng mẫu
            sheet.shiftRows(productRowIndex + i, sheet.getLastRowNum(), 1, true, false);
            Row newRow = sheet.createRow(productRowIndex + i);
            
            // Sao chép style từ dòng mẫu
            for (int j = 0; j < templateRow.getLastCellNum(); j++) {
                Cell newCell = newRow.createCell(j);
                if (styleMap.containsKey(j)) {
                    newCell.setCellStyle(styleMap.get(j));
                }
            }
        }
        
        // 3. Điền dữ liệu sản phẩm vào các dòng
        for (int i = 0; i < products.size(); i++) {
            Map<String, String> product = products.get(i);
            Row row = sheet.getRow(productRowIndex + i);
            
            for (int col = 0; col < 5; col++) {
                Cell cell = row.getCell(col);
                if (cell == null) {
                    cell = row.createCell(col);
                    if (styleMap.containsKey(col)) {
                        cell.setCellStyle(styleMap.get(col));
                    }
                }
                
                switch (col) {
                    case 0 -> cell.setCellValue(product.get("productName"));
                    case 1 -> cell.setCellValue(product.get("quantity"));
                    case 2 -> cell.setCellValue(formatter.format(Integer.parseInt(product.get("price"))));
                    // Định dạng lại totalPrice khi hiển thị
                    case 3 -> {
                        int price = Integer.parseInt(product.get("totalPrice"));
                        cell.setCellValue(formatter.format(price));
                    } 
                    default -> cell.setCellValue("");
                }
            }
        }

        // 4. Thay thế các placeholder
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String text = cell.getStringCellValue();
                    boolean changed = false;
                    
                    for (String key : fields.keySet()) {
                        String placeholder = "{{" + key + "}}";
                        if (text.contains(placeholder)) {
                            text = text.replace(placeholder, fields.get(key));
                            changed = true;
                        }
                    }
                    
                    if (changed) {
                        cell.setCellValue(text);
                    }
                }
            }
        }

        // Ghi file mới
        FileOutputStream fos = new FileOutputStream("bill_filled.xlsx");
        workbook.write(fos);
        workbook.close();
        fis.close();
        fos.close();

        System.out.println("✅ Đã tạo file hóa  ơn hoàn chỉnh!");
    }
}