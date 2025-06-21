package com.pcstore.utils;

import java.awt.Component;
import javax.swing.JOptionPane;

import org.thymeleaf.context.Context;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Utility class chung cho việc in các loại phiếu/bill
 */
public class BillPrintUtils {

    /**
     * Xử lý in phiếu chung cho tất cả loại bill
     */
    public static void handlePrintBill(Component parent, String templateName, Context context, String defaultFileName, String successMessage) {
        try {
            File fileToSave = PDFUtils.chooseSaveFile(parent, defaultFileName);

            if (fileToSave != null) {
                String htmlContent = PDFUtils.renderTemplate(templateName, context);
                PDFUtils.generatePDF(htmlContent, fileToSave.getAbsolutePath());

                PDFUtils.openPDF(parent, fileToSave);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Lỗi tạo file PDF: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Validate dữ liệu trước khi in
     */
    public static boolean validatePrintData(Component parent, Object mainData, List<?> detailData) {
        if (mainData == null) {
            JOptionPane.showMessageDialog(parent,
                    "Không có dữ liệu để in!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        if (detailData == null || detailData.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Không có chi tiết để in!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Tạo context từ Map dữ liệu - GENERIC cho tất cả loại phiếu
     *
     * @param dataMap Map chứa tất cả dữ liệu cần thiết
     * @return Context đã được setup
     */
    public static Context createContextFromMap(Map<String, Object> dataMap) {
        Context context = BillDataUtils.createBaseContext();

        if (dataMap != null && !dataMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (key.toLowerCase().contains("date") && value instanceof java.time.LocalDateTime) {
                    context.setVariable(key, BillDataUtils.formatDate((java.time.LocalDateTime) value));
                } else if (key.toLowerCase().contains("amount") || key.toLowerCase().contains("price") ||
                        key.toLowerCase().contains("totalamount")) {
                    context.setVariable(key, BillDataUtils.formatCurrency(value));
                } else if (key.toLowerCase().contains("quantity") || key.toLowerCase().contains("count")) {
                    context.setVariable(key, BillDataUtils.formatNumber(value));
                } else if (key.toLowerCase().contains("status")) {
                    context.setVariable(key, BillDataUtils.getStatusDisplayText(value != null ? value.toString() : ""));
                } else {
                    context.setVariable(key, value);
                }
            }
        }

        return context;
    }

    /**
     * Tạo context với custom format rules
     *
     * @param dataMap     Map chứa dữ liệu
     * @param formatRules Map chứa rules format (key -> format_type)
     * @return Context đã được setup
     */
    public static Context createContextWithCustomFormat(Map<String, Object> dataMap, Map<String, String> formatRules) {
        Context context = BillDataUtils.createBaseContext();

        if (dataMap != null && !dataMap.isEmpty()) {
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                String formatType = formatRules != null ? formatRules.get(key) : null;

                if (formatType != null) {
                    switch (formatType.toLowerCase()) {
                        case "date":
                            if (value instanceof java.time.LocalDateTime) {
                                context.setVariable(key, BillDataUtils.formatDate((java.time.LocalDateTime) value));
                            } else {
                                context.setVariable(key, value);
                            }
                            break;
                        case "datetime":
                            if (value instanceof java.time.LocalDateTime) {
                                context.setVariable(key, BillDataUtils.formatDateTime((java.time.LocalDateTime) value));
                            } else {
                                context.setVariable(key, value);
                            }
                            break;
                        case "currency":
                            context.setVariable(key, BillDataUtils.formatCurrency(value));
                            break;
                        case "number":
                            context.setVariable(key, BillDataUtils.formatNumber(value));
                            break;
                        case "status":
                            context.setVariable(key, BillDataUtils.getStatusDisplayText(value != null ? value.toString() : ""));
                            break;
                        default:
                            context.setVariable(key, value);
                            break;
                    }
                } else {
                    if (key.toLowerCase().contains("date") && value instanceof java.time.LocalDateTime) {
                        context.setVariable(key, BillDataUtils.formatDate((java.time.LocalDateTime) value));
                    } else if (key.toLowerCase().contains("amount") || key.toLowerCase().contains("price") ||
                            key.toLowerCase().contains("total")) {
                        context.setVariable(key, BillDataUtils.formatCurrency(value));
                    } else if (key.toLowerCase().contains("quantity") || key.toLowerCase().contains("count")) {
                        context.setVariable(key, BillDataUtils.formatNumber(value));
                    } else if (key.toLowerCase().contains("status")) {
                        context.setVariable(key, BillDataUtils.getStatusDisplayText(value != null ? value.toString() : ""));
                    } else {
                        context.setVariable(key, value);
                    }
                }
            }
        }

        return context;
    }

    /**
     * Method để in phiếu với Map data - Đơn giản nhất
     */
    public static void printBill(Component parent, String templateName, Map<String, Object> dataMap, String defaultFileName) {
        try {
            Context context = createContextFromMap(dataMap);
            handlePrintBill(parent, templateName, context, defaultFileName, "In thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Lỗi in phiếu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /*
     * in trực tiếp không tạo file pdf
     */
    public static void printBillDirectly(Component parent, String templateName, Map<String, Object> dataMap) {
        try {
            Context context = createContextFromMap(dataMap);
            String htmlContent = PDFUtils.renderTemplate(templateName, context);
            PDFUtils.printPDFDirectly(parent, htmlContent, "bill_inventory_");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Lỗi in phiếu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Method để in phiếu với Map data và custom format
     */
    public static void printBillFromMapWithFormat(Component parent, String templateName,
                                                  Map<String, Object> dataMap,
                                                  Map<String, String> formatRules,
                                                  String defaultFileName) {
        try {
            Context context = createContextWithCustomFormat(dataMap, formatRules);
            handlePrintBill(parent, templateName, context, defaultFileName, "In thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Lỗi in phiếu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    //in trực tiếp không tạo file pdf
    
}