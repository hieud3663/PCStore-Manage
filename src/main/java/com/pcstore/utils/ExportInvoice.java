package com.pcstore.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.payment.CashPayment;
import com.pcstore.payment.ZalopayPayment;
import com.pcstore.utils.LocaleManager;
import raven.toast.Notifications;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.swing.JOptionPane;

public class ExportInvoice {

    public static boolean exportPDF(Invoice invoice, BasePayment payment) throws Exception {
        boolean isCashPayment = payment instanceof CashPayment;
        try {
            NumberFormat formatter = LocaleManager.getInstance().getCurrencyFormatter();

            // Kiểm tra đường dẫn file mẫu
            File f = new File("src\\main\\java\\com\\pcstore\\resources\\bill_sell_template.xlsx");
            if (!f.exists()) {
                JOptionPane.showMessageDialog(null,
                        "Không tìm thấy file mẫu hóa đơn!\nĐường dẫn: " + f.getAbsolutePath(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            String filePath = f.getAbsolutePath();
            FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            // Dữ liệu tĩnh
            Map<String, String> fields = new HashMap<>();
            fields.put("nowdate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            fields.put("invoiceID", String.valueOf(invoice.getInvoiceId()));
            fields.put("employeeName", invoice.getEmployee().getFullName());
            fields.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            fields.put("customerName", invoice.getCustomer().getFullName());
            fields.put("customerPhone", invoice.getCustomer().getPhoneNumber());
            fields.put("paymentMethod", payment.getPaymentMethod().getDisplayName());
            fields.put("usePoint", formatter.format(invoice.getDiscountAmount()));


            if (isCashPayment) {
                CashPayment cashPayment = (CashPayment) payment;
                // Sử dụng phương thức formatCurrency của LocaleManager thay vì formatter.format
                fields.put("amountReceived", formatter.format(cashPayment.getAmountReceived()));
                fields.put("amountChange", formatter.format(cashPayment.getChange()));
            } else {
                fields.put("amountReceived", formatter.format(payment.getAmount()));
                fields.put("amountChange", formatter.format(0));
            }

            // Dữ liệu sản phẩm 
            List<Map<String, String>> products = new ArrayList<>();
            for (InvoiceDetail detail : invoice.getInvoiceDetails()) {
                Map<String, String> product = new HashMap<>();
                product.put("productName", detail.getProduct().getProductName());
                product.put("quantity", String.valueOf(detail.getQuantity()));
                product.put("price", String.valueOf(detail.getUnitPrice()));
                product.put("totalPrice", String.valueOf(detail.getSubTotal()));
                products.add(product);
            }

            // Tính tổng số lượng sản phẩm
            int sumQuantity = products.stream()
                    .mapToInt(p -> Integer.parseInt(p.get("quantity")))
                    .sum();
            BigDecimal totalAmount = products.stream()
                    .map(p -> new BigDecimal(p.get("totalPrice")))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Thêm vào fields với định dạng thích hợp
            fields.put("sumQuantity", String.valueOf(sumQuantity));
            fields.put("totalAmount", formatter.format(totalAmount));
            fields.put("paymentAmount", formatter.format(invoice.getTotalAmount()));


            // Dòng chứa sản phẩm mẫu
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

            if (productRowIndex == -1) {
                JOptionPane.showMessageDialog(null,
                        "File mẫu không hợp lệ! Không tìm thấy placeholder {{productName}}",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                workbook.close();
                fis.close();
                return false;
            }

            // 1. Tạo một Style Cache để lưu lại style của mỗi cell trong dòng mẫu
            Row templateRow = sheet.getRow(productRowIndex);
            Map<Integer, CellStyle> styleMap = new HashMap<>();
            for (Cell cell : templateRow) {
                styleMap.put(cell.getColumnIndex(), cell.getCellStyle());
            }

            // 2. Sao chép dòng mẫu cho mỗi sản phẩm
            if (products.size() > 1) {
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

                    try {
                        switch (col) {
                            case 0 -> cell.setCellValue(product.get("productName"));
                            case 1 -> cell.setCellValue(product.get("quantity"));
                            case 2 -> {
                                // An toàn hơn với try-catch
                                try {
                                    int price = Integer.parseInt(product.get("price"));
                                    cell.setCellValue(formatter.format(price));
                                } catch (NumberFormatException e) {
                                    cell.setCellValue(product.get("price"));
                                }
                            }
                            case 3 -> {
                                try {
                                    int totalPrice = Integer.parseInt(product.get("totalPrice"));
                                    cell.setCellValue(formatter.format(totalPrice));
                                } catch (NumberFormatException e) {
                                    cell.setCellValue(product.get("totalPrice"));
                                }
                            }
                            default -> cell.setCellValue("");
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý dữ liệu sản phẩm: " + e.getMessage());
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
                                // Add null check here to prevent NullPointerException
                                String replacement = fields.get(key);
                                if (replacement != null) {
                                    text = text.replace(placeholder, replacement);
                                } else {
                                    // Replace null values with empty string or some default text
                                    text = text.replace(placeholder, "");
                                }
                                changed = true;
                            }
                        }

                        if (changed) {
                            cell.setCellValue(text);
                        }
                    }
                }
            }

            // Tạo tên file với timestamp để tránh trùng lặp
            String fileName = "output\\" + "INVOICE_" + invoice.getInvoiceId() +
                    "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

            // Ghi file mới
            FileOutputStream fos = new FileOutputStream(fileName);
            workbook.write(fos);
            workbook.close();
            fis.close();
            fos.close();

//            JOptionPane.showMessageDialog(null,
//                    "Xuất hóa đơn thành công!\n" +
//                            "Tên file: " + fileName,
//                    "Thông báo",
//                    JOptionPane.INFORMATION_MESSAGE);

//            Notifications.getInstance().show(Notifications.Type.SUCCESS, 3500, "Xuất hóa đơn thành công!\n" +
//                    "Tên file: " + fileName);

            //mở file vừa tạo
            try {
                // Chạy lệnh mở file trên hệ điều hành
                String command = "cmd /c start " + fileName;
                Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Không thể mở file hóa đơn: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }

            return true;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi xuất hóa đơn: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi không xác định: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportPDF(Invoice invoice, PaymentMethodEnum paymentMethod) throws Exception {

        BasePayment dummyPayment;

        if (paymentMethod == PaymentMethodEnum.ZALOPAY) {
            dummyPayment = new ZalopayPayment(invoice);
        } else {
            dummyPayment = new CashPayment(invoice);
            ((CashPayment) dummyPayment).setAmountReceived(invoice.getTotalAmount());
            ((CashPayment) dummyPayment).setChange(BigDecimal.ZERO);
        }

        return exportPDF(invoice, dummyPayment);
    }
}