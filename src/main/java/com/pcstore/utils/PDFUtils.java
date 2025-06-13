package com.pcstore.utils;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.css.apply.impl.DefaultCssApplierFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.awt.Component;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Utility class cho việc tạo và in PDF
 */
public class PDFUtils {

    /**
     * Tạo TemplateEngine với cấu hình mặc định
     */
    public static TemplateEngine createTemplateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("com/pcstore/resources/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        return templateEngine;
    }

    /**
     * Tạo PDF từ HTML content
     */
    public static void generatePDF(String htmlContent, String outputPath) throws Exception {
        generatePDF(htmlContent, outputPath, PageSize.A4);
    }    /**
     * Tạo PDF từ HTML content với kích thước trang tùy chỉnh
     */
    public static void generatePDF(String htmlContent, String outputPath, PageSize pageSize) throws Exception {
        ConverterProperties properties = new ConverterProperties();
        properties.setCharset("UTF-8");
        
        // Cấu hình để tràn viền
        properties.setImmediateFlush(false);

        FontProvider fontProvider = new FontProvider();
        
        try {
            fontProvider.addFont("com/pcstore/resources/fonts/SVN-Times New Roman.ttf");
            fontProvider.addFont("com/pcstore/resources/fonts/SVN-Times New Roman bold.ttf");
            fontProvider.addFont("com/pcstore/resources/fonts/SVN-Times New Roman italic.ttf");
            fontProvider.addFont("com/pcstore/resources/fonts/SVN-Times New Roman 2 bold italic.ttf");
        } catch (Exception e) {
            try {
                fontProvider.addFont("Times New Roman");
                fontProvider.addFont("Times New Roman Bold");
            } catch (Exception ex) {
                fontProvider.addFont(StandardFonts.TIMES_ROMAN);
                fontProvider.addFont(StandardFonts.TIMES_BOLD);
                fontProvider.addFont(StandardFonts.TIMES_ITALIC);
                fontProvider.addFont(StandardFonts.TIMES_BOLDITALIC);
            }
        }

        properties.setFontProvider(fontProvider);

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdf = new PdfDocument(writer);
            
            // Thiết lập page size KHÔNG có margin
            pdf.setDefaultPageSize(pageSize);
            
            // Tạo PDF từ HTML
            HtmlConverter.convertToPdf(htmlContent, pdf, properties);
        }
    }

    

    /**
     * Render template thành HTML
     */
    public static String renderTemplate(String templateName, Context context) throws Exception {
        TemplateEngine templateEngine = createTemplateEngine();
        return templateEngine.process(templateName, context);
    }

    /**
     * Cho người dùng chọn file để lưu PDF
     */
    public static File chooseSaveFile(Component parent, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file PDF");
        fileChooser.setSelectedFile(new File(defaultFileName + ".pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));

        int userSelection = fileChooser.showSaveDialog(parent);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Đảm bảo file có extension .pdf
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".pdf");
            }

            return fileToSave;
        }

        return null;
    }

    /**
     * Hỏi người dùng có muốn mở file PDF không và mở nếu đồng ý
     */
    public static void promptAndOpenPDF(Component parent, File pdfFile) {
        int openResult = JOptionPane.showConfirmDialog(parent,
                "Tạo file PDF thành công!\nBạn có muốn mở file PDF không?",
                "Thành công",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (openResult == JOptionPane.YES_OPTION) {
            openPDF(parent, pdfFile);
        }
    }

    /**
     * Mở file PDF bằng ứng dụng mặc định
     */
    public static void openPDF(Component parent, File pdfFile) {
        try {
            Desktop.getDesktop().open(pdfFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Không thể mở file PDF. Vui lòng mở thủ công tại: " + pdfFile.getAbsolutePath(),
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Tạo file PDF tạm thời để in trực tiếp
     */
    public static File createTempPDF(String htmlContent, String prefix) throws Exception {
        File tempFile = File.createTempFile(prefix, ".pdf");
        generatePDF(htmlContent, tempFile.getAbsolutePath());
        tempFile.deleteOnExit();
        return tempFile;
    }

    /**
     * In PDF trực tiếp mà không lưu file
     */
    public static void printPDFDirectly(Component parent, String htmlContent, String filePrefix) {
        try {
            File tempFile = createTempPDF(htmlContent, filePrefix);
            Desktop.getDesktop().print(tempFile);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Lỗi in file: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}