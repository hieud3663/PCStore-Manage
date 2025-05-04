package com.pcstore.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JExcel {
    

    /**
     * Hiển thị hộp thoại cho người dùng chọn vị trí lưu file
     * @param defaultFileName Tên file mặc định đề xuất
     * @return File được chọn hoặc null nếu hủy chọn
     */
    private File showSaveFileDialog(String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        
        // Thiết lập tên file mặc định
        fileChooser.setSelectedFile(new File(defaultFileName + ".xlsx"));
        
        // Thiết lập filter cho file Excel
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Excel Workbook (*.xlsx)", "xlsx");
        fileChooser.setFileFilter(filter);
        
        // Hiển thị hộp thoại
        int userSelection = fileChooser.showSaveDialog(null);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Đảm bảo file có đuôi .xlsx
            if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".xlsx");
            }
            
            // Kiểm tra nếu file đã tồn tại
            if (selectedFile.exists()) {
                int result = JOptionPane.showConfirmDialog(null,
                        "File đã tồn tại. Bạn có muốn ghi đè không?",
                        "Xác nhận ghi đè",
                        JOptionPane.YES_NO_OPTION);
                
                if (result != JOptionPane.YES_OPTION) {
                    return null;
                }
            }
            
            return selectedFile;
        }
        
        return null;
    }

    /**
     * Xuất mảng hai chiều ra file Excel và cho phép chọn nơi lưu file
     * @param data Dữ liệu dạng mảng hai chiều
     * @param suggestedFilename Tên file đề xuất (sẽ hiển thị trong hộp thoại lưu)
     * @return true nếu xuất thành công, false nếu thất bại hoặc hủy
     */
    public boolean toExcel(String[][] data, String suggestedFilename) {
        if (data == null || data.length == 0) {
            return false;
        }
        
        // Hiển thị hộp thoại chọn nơi lưu file
        File selectedFile = showSaveFileDialog(suggestedFilename);
        if (selectedFile == null) {
            return false; // Người dùng đã hủy
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // Tạo font in đậm cho header
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            // Style cho tiêu đề
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(boldFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Ghi dữ liệu vào sheet
            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                Row row = sheet.createRow(rowIndex);
                
                for (int colIndex = 0; colIndex < data[rowIndex].length; colIndex++) {
                    Cell cell = row.createCell(colIndex);
                    cell.setCellValue(data[rowIndex][colIndex]);
                    
                    if (rowIndex == 0) {
                        cell.setCellStyle(titleStyle); // Áp dụng style cho dòng tiêu đề
                    } else if (rowIndex == 1) {
                        cell.setCellStyle(headerStyle); // Áp dụng style cho dòng header
                    }
                }
            }

            // Gộp ô cho tiêu đề
            if (data.length > 0 && data[0].length > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, data[0].length - 1));
                
                // Đảm bảo tiêu đề được căn giữa sau khi gộp ô
                Row titleRow = sheet.getRow(0);
                Cell titleCell = titleRow.getCell(0);
                titleCell.setCellStyle(titleStyle);
                titleCell.setCellValue(data[0][0]);
            }

            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < data[0].length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Xuất ra file
            try (FileOutputStream fileOut = new FileOutputStream(selectedFile)) {
                workbook.write(fileOut);
                
                JOptionPane.showMessageDialog(null, 
                        "Xuất Excel thành công!\nĐường dẫn: " + selectedFile.getAbsolutePath(), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi xuất file Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Xuất List dữ liệu ra file Excel và cho phép chọn nơi lưu file
     * @param data Dữ liệu dạng List<List<String>>
     * @param suggestedFilename Tên file đề xuất (sẽ hiển thị trong hộp thoại lưu)
     * @return true nếu xuất thành công, false nếu thất bại hoặc hủy
     */
    public boolean toExcel(List<List<String>> data, String suggestedFilename) {
        if (data == null || data.isEmpty() || data.get(0).isEmpty()) {
            return false;
        }
        
        // Hiển thị hộp thoại chọn nơi lưu file
        File selectedFile = showSaveFileDialog(suggestedFilename);
        if (selectedFile == null) {
            return false; // Người dùng đã hủy
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            // Tạo font in đậm cho header
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            // Style cho tiêu đề
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(boldFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Ghi dữ liệu vào sheet
            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Row row = sheet.createRow(rowIndex);
                List<String> rowData = data.get(rowIndex);
                
                for (int colIndex = 0; colIndex < rowData.size(); colIndex++) {
                    Cell cell = row.createCell(colIndex);
                    cell.setCellValue(rowData.get(colIndex));
                    
                    if (rowIndex == 0) {
                        cell.setCellStyle(titleStyle); // Áp dụng style cho dòng tiêu đề
                    } else if (rowIndex == 1) {
                        cell.setCellStyle(headerStyle); // Áp dụng style cho dòng header
                    }
                }
            }

            // Gộp ô cho tiêu đề
            if (!data.isEmpty() && !data.get(0).isEmpty() && data.get(0).size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, data.get(0).size() - 1));
                
                // Đảm bảo tiêu đề được căn giữa sau khi gộp ô
                Row titleRow = sheet.getRow(0);
                Cell titleCell = titleRow.getCell(0);
                titleCell.setCellStyle(titleStyle);
            }

            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < data.get(0).size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Xuất ra file
            try (FileOutputStream fileOut = new FileOutputStream(selectedFile)) {
                workbook.write(fileOut);
                
                JOptionPane.showMessageDialog(null, 
                        "Xuất Excel thành công!\nĐường dẫn: " + selectedFile.getAbsolutePath(), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi xuất file Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Xuất dữ liệu từ một đối tượng và các header tương ứng, cho phép chọn nơi lưu file
     * @param headers Mảng các tiêu đề cột
     * @param data Mảng dữ liệu 2 chiều (hàng x cột)
     * @param title Tiêu đề của bảng
     * @param suggestedFilename Tên file đề xuất (sẽ hiển thị trong hộp thoại lưu)
     * @return true nếu xuất thành công, false nếu thất bại hoặc hủy
     */
    public boolean toExcel(String[] headers, Object[][] data, String title, String suggestedFilename) {
        if (headers == null || headers.length == 0 || data == null || data.length == 0) {
            return false;
        }
        
        // Hiển thị hộp thoại chọn nơi lưu file
        File selectedFile = showSaveFileDialog(suggestedFilename);
        if (selectedFile == null) {
            return false; // Người dùng đã hủy
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            
            // Tạo font in đậm cho header và tiêu đề
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            
            // Style cho tiêu đề
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(boldFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            // Tạo dòng tiêu đề
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
            
            // Tạo dòng header
            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Tạo style cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            
            // Ghi dữ liệu
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i + 2); // +2 vì đã có dòng tiêu đề và header
                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    if (data[i][j] != null) {
                        if (data[i][j] instanceof Number) {
                            cell.setCellValue(((Number) data[i][j]).doubleValue());
                        } else if (data[i][j] instanceof Boolean) {
                            cell.setCellValue((Boolean) data[i][j]);
                        } else {
                            cell.setCellValue(data[i][j].toString());
                        }
                    }
                    cell.setCellStyle(dataStyle);
                }
            }
            
            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Xuất ra file
            try (FileOutputStream fileOut = new FileOutputStream(selectedFile)) {
                workbook.write(fileOut);
                
                JOptionPane.showMessageDialog(null, 
                        "Xuất Excel thành công!\nĐường dẫn: " + selectedFile.getAbsolutePath(), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                //Mở file sau khi xuất
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.OPEN)) {
                            desktop.open(selectedFile);
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Không thể tự động mở file: " + ex.getMessage());
                }

                return true;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi xuất file Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Xuất dữ liệu từ một đối tượng và các header tương ứng, có thêm metadata, cho phép chọn nơi lưu file
     * @param headers Mảng các tiêu đề cột
     * @param data Mảng dữ liệu 2 chiều (hàng x cột)
     * @param title Tiêu đề của bảng
     * @param metaData Map chứa các cặp key-value để hiển thị dưới dòng tiêu đề
     * @param suggestedFilename Tên file đề xuất (sẽ hiển thị trong hộp thoại lưu)
     * @return Đường dẫn đến file nếu xuất thành công, null nếu thất bại hoặc hủy
     */
    public String toExcel(List<String> headers, List<List<Object>> data, String title, 
                          Map<String, Object> metaData, String suggestedFilename) {
        if (headers == null || headers.isEmpty() || data == null || data.isEmpty()) {
            return null;
        }
        
        // Hiển thị hộp thoại chọn nơi lưu file
        File selectedFile = showSaveFileDialog(suggestedFilename);
        if (selectedFile == null) {
            return null; // Người dùng đã hủy
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            
            // Tạo font in đậm cho header và tiêu đề
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            
            // Style cho tiêu đề
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(boldFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // Style cho metadata
            CellStyle metaStyle = workbook.createCellStyle();
            metaStyle.setFont(boldFont);
            metaStyle.setAlignment(HorizontalAlignment.LEFT);
            
            // Style cho giá trị metadata
            CellStyle metaValueStyle = workbook.createCellStyle();
            metaValueStyle.setAlignment(HorizontalAlignment.LEFT);
            
            // Style cho header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            // Tạo dòng tiêu đề
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.size() - 1));
            
            // Tính toán số dòng metadata cần thêm vào
            int metaDataRows = (metaData != null) ? metaData.size() : 0;
            int currentRow = 1; // Dòng tiếp theo sau tiêu đề
            
            // Thêm metadata nếu có
            if (metaData != null && !metaData.isEmpty()) {
                for (Map.Entry<String, Object> entry : metaData.entrySet()) {
                    Row metaRow = sheet.createRow(currentRow++);
                    
                    // Tạo ô cho tên metadata
                    Cell metaNameCell = metaRow.createCell(0);
                    metaNameCell.setCellValue(entry.getKey() + ":");
                    metaNameCell.setCellStyle(metaStyle);
                    
                    // Tạo ô cho giá trị metadata
                    Cell metaValueCell = metaRow.createCell(1);
                    if (entry.getValue() != null) {
                        if (entry.getValue() instanceof Number) {
                            metaValueCell.setCellValue(((Number) entry.getValue()).doubleValue());
                        } else if (entry.getValue() instanceof Boolean) {
                            metaValueCell.setCellValue((Boolean) entry.getValue());
                        } else {
                            metaValueCell.setCellValue(entry.getValue().toString());
                        }
                    }
                    metaValueCell.setCellStyle(metaValueStyle);
                    
                    // Gộp các ô còn lại nếu có nhiều hơn 2 cột
                    if (headers.size() > 2) {
                        sheet.addMergedRegion(new CellRangeAddress(
                                currentRow - 1, currentRow - 1, 1, headers.size() - 1));
                    }
                }
            }
            
            // Để trống một dòng sau metadata
            currentRow++;
            
            // Tạo dòng header
            Row headerRow = sheet.createRow(currentRow++);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            // Tạo style cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            
            // Ghi dữ liệu
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(currentRow++);
                List<Object> rowData = data.get(i);
                
                for (int j = 0; j < Math.min(rowData.size(), headers.size()); j++) {
                    Cell cell = row.createCell(j);
                    Object value = rowData.get(j);
                    
                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else if (value instanceof Boolean) {
                            cell.setCellValue((Boolean) value);
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                    cell.setCellStyle(dataStyle);
                }
            }
            
            // Tự động điều chỉnh kích thước cột
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Xuất ra file
            try (FileOutputStream fileOut = new FileOutputStream(selectedFile)) {
                workbook.write(fileOut);
                
                JOptionPane.showMessageDialog(null, 
                        "Xuất Excel thành công!\nĐường dẫn: " + selectedFile.getAbsolutePath(), 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Mở file sau khi xuất
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.OPEN)) {
                            desktop.open(selectedFile);
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Không thể tự động mở file: " + ex.getMessage());
                }

                return selectedFile.getAbsolutePath();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi xuất file Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}