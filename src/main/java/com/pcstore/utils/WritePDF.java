package com.pcstore.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WritePDF {
    
    /**
     * Chuyển đổi file Excel sang PDF
     * @param excelPath Đường dẫn file Excel nguồn
     * @param pdfPath Đường dẫn file PDF đích
     * @return true nếu chuyển đổi thành công
     */
    public static boolean convertExcelToPDF(String excelPath, String pdfPath) {
        PDDocument document = null;
        
        try {
            // Đọc file Excel
            FileInputStream fis = new FileInputStream(excelPath);
            Workbook workbook = new XSSFWorkbook(fis);
            
            // Tạo PDF document
            document = new PDDocument();
            
            // Load font - kiểm tra font tồn tại
            File fontFile = new File("src/main/java/com/pcstore/resources/fonts/DejaVuSans.ttf");
            if (!fontFile.exists()) {
                System.err.println("Font file không tồn tại: " + fontFile.getAbsolutePath());
                // Thử tìm font ở các vị trí khác
                fontFile = new File("fonts/DejaVuSans.ttf");
                if (!fontFile.exists()) {
                    System.err.println("Không thể tìm thấy font file ở vị trí thay thế");
                    return false;
                }
            }
            
            PDType0Font font = PDType0Font.load(document, fontFile);
            
            // Lấy sheet đầu tiên
            Sheet sheet = workbook.getSheetAt(0);
            
            // Vị trí và kích thước
            float margin = 50;
            float tableWidth = PDRectangle.A4.getWidth() - 2 * margin;
            
            // Tính toán chiều rộng cột
            List<Float> colWidths = calculateColumnWidths(sheet, tableWidth);
            
            // Tạo trang đầu tiên
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            // Lấy header row để dùng cho các tính toán sau này
            Row headerRow = sheet.getRow(0);
            
            // Quản lý trang và vị trí hiện tại
            float y = 750;
            int currentRow = 0;
            
            // Vẽ header nếu có
            if (headerRow != null) {
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(font, 10);
                
                float x = margin;
                for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                    Cell cell = headerRow.getCell(i);
                    String text = sanitizeText(getCellValueAsString(cell));
                    contentStream.beginText();
                    contentStream.newLineAtOffset(x, y);
                    contentStream.showText(text);
                    contentStream.endText();
                    x += colWidths.get(i);
                }
                contentStream.close();
                y -= 15; // Xuống dòng sau header
                currentRow = 1; // Bắt đầu từ row 1 (sau header)
            }
            
            // Vẽ nội dung từng trang
            while (currentRow <= sheet.getLastRowNum()) {
                // Tạo content stream mới cho mỗi trang
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(font, 10);
                
                // Vẽ các dòng cho đến khi hết trang hoặc hết dữ liệu
                int rowsProcessed = 0;
                while (currentRow <= sheet.getLastRowNum() && y >= 50) {
                    Row row = sheet.getRow(currentRow);
                    if (row != null) {
                        float x = margin;
                        for (int i = 0; i < (headerRow != null ? headerRow.getLastCellNum() : row.getLastCellNum()); i++) {
                            if (i < colWidths.size()) { // Đảm bảo không vượt quá số cột đã tính
                                Cell cell = row.getCell(i);
                                String text = sanitizeText(getCellValueAsString(cell));
                                contentStream.beginText();
                                contentStream.newLineAtOffset(x, y);
                                contentStream.showText(text);
                                contentStream.endText();
                                x += colWidths.get(i);
                            }
                        }
                        y -= 15; // Xuống dòng
                        rowsProcessed++;
                    }
                    currentRow++;
                }
                
                contentStream.close();
                
                // Nếu còn dữ liệu, tạo trang mới
                if (currentRow <= sheet.getLastRowNum()) {
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    y = 750; // Reset vị trí y cho trang mới
                }
            }
            
            // Đóng workbook và FileInputStream
            workbook.close();
            fis.close();
            
            // Lưu file PDF
            document.save(pdfPath);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Làm sạch text trước khi hiển thị trong PDF để tránh lỗi font
     */
    private static String sanitizeText(String text) {
        if (text == null) return "";
        
        // Loại bỏ các ký tự không hiển thị được
        text = text.replace("\n", " ")  // Thay thế xuống dòng bằng khoảng trắng
                  .replace("\r", "")    // Loại bỏ carriage return
                  .replace("\t", " ")   // Thay thế tab bằng khoảng trắng
                  .trim();              // Cắt khoảng trắng thừa
        
        // Thay thế các ký tự đặc biệt nếu cần
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Giữ lại các ký tự ASCII và tiếng Việt phổ biến
            if (c >= 32 && c <= 126 || isVietnameseChar(c)) {
                sb.append(c);
            } else {
                sb.append(" "); // Thay thế các ký tự không xác định bằng khoảng trắng
            }
        }
        return sb.toString();
    }
    
    /**
     * Kiểm tra xem một ký tự có phải là ký tự tiếng Việt hay không
     */
    private static boolean isVietnameseChar(char c) {
        // Danh sách các ký tự tiếng Việt phổ biến
        return "áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđĐÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ"
                .indexOf(c) >= 0;
    }
    
    /**
     * Tính toán chiều rộng các cột dựa trên nội dung
     */
    private static List<Float> calculateColumnWidths(Sheet sheet, float totalWidth) {
        int columnCount = 0;
        // Tìm số cột tối đa
        for (Row row : sheet) {
            columnCount = Math.max(columnCount, row.getLastCellNum());
        }
        
        // Phân bổ đều chiều rộng cho các cột
        List<Float> widths = new ArrayList<>();
        float colWidth = totalWidth / Math.max(1, columnCount);
        for (int i = 0; i < columnCount; i++) {
            widths.add(colWidth);
        }
        
        return widths;
    }
    
    /**
     * Lấy giá trị của cell dưới dạng chuỗi
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    }
                    // Định dạng số không có phần thập phân nếu là số nguyên
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return String.format("%.0f", value);
                    }
                    return String.valueOf(value);
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e) {
                        try {
                            return cell.getStringCellValue();
                        } catch (Exception ex) {
                            return "";
                        }
                    }
                default:
                    return "";
            }
        } catch (Exception e) {
            return ""; // Trả về chuỗi rỗng nếu có lỗi
        }
    }
}