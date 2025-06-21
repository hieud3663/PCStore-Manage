import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class html2pdf {
    public static void main(String[] args) throws Exception {
        // Cấu hình Thymeleaf
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("src/test/java/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        // Tạo danh sách sản phẩm (nhiều dòng)
        List<Item> items = new ArrayList<>();
        items.add(new Item("Laptop Dell XPS 13", 1, 25000000));
        items.add(new Item("Chuột Logitech MX Master", 2, 1500000));
        items.add(new Item("Bàn phím Keychron K8", 1, 2200000));
        items.add(new Item("Tai nghe Sony WH-1000XM5", 3, 8500000));
        items.add(new Item("Màn hình Samsung 27 inch", 2, 4500000));
        items.add(new Item("Ổ cứng SSD 1TB", 4, 2500000));
        items.add(new Item("Cáp USB-C Anker", 5, 200000));
        items.add(new Item("Bộ sạc nhanh 65W", 2, 600000));
        items.add(new Item("Bàn di chuột Razer", 3, 300000));
        items.add(new Item("Webcam Logitech C920", 1, 1800000));

        // Định dạng tiền tệ
        DecimalFormat formatter = new DecimalFormat("#,###");
        for (Item item : items) {
            item.setFormattedPrice(formatter.format(item.getPrice()));
            item.setFormattedTotal(formatter.format(item.getQuantity() * item.getPrice()));
        }

        // Dữ liệu hóa đơn
        Context context = new Context();
        context.setVariable("customerName", "Nguyễn Văn A");
        context.setVariable("customerAddress", "123 Đường Láng, Hà Nội");
        context.setVariable("date", "28/05/2025");
        context.setVariable("invoiceId", "HD20250528");
        context.setVariable("items", items);
        context.setVariable("formattedTotal", formatter.format(
                items.stream().mapToDouble(item -> item.getQuantity() * item.getPrice()).sum()
        ));

        // Thêm logo
        String logoBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAEAwD6z6QAAAAASUVORK5CYII=";
        context.setVariable("logo", logoBase64);
        
        // === THÊM CSS SCALING VÀO CONTEXT ===
        // double scale = 0.3; // Thay đổi giá trị này để scale (0.5 = 50%, 0.8 = 80%)
        // context.setVariable("cssScale", String.format(
        //     "body{transform:scale(%.2f);transform-origin:top left;width:%.0f%%;font-size:%.0fpx;}", 
        //     scale, 100/scale, 14*scale
        // ));

        // Render template thành HTML
        String htmlContent = templateEngine.process("bill", context);
        
        // Tạo PDF với các kích thước trang khác nhau
        createScaledPagePDF(htmlContent, "invoice_full.pdf", 1.0);    // 100% - A4 đầy đủ
        createScaledPagePDF(htmlContent, "invoice_80.pdf", 0.8);      // 80% A4
        createScaledPagePDF(htmlContent, "invoice_70.pdf", 0.7);      // 70% A4
        createScaledPagePDF(htmlContent, "invoice_60.pdf", 0.6);      // 60% A4
        createScaledPagePDF(htmlContent, "invoice_50.pdf", 0.5);      // 50% A4 (receipt size)

        System.out.println("Hóa đơn PDF màu mè, hiện đại đã được tạo thành công!");
    }

    // Thêm method này vào class
    private static void createScaledPagePDF(String htmlContent, String fileName, double scale) throws Exception {
        ConverterProperties properties = new ConverterProperties();
        properties.setCharset("UTF-8");

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdf = new PdfDocument(writer);
            
            // Scale kích thước trang
            float newWidth = (float) (PageSize.A4.getWidth() * scale);
            float newHeight = (float) (PageSize.A4.getHeight() * scale);
            pdf.setDefaultPageSize(new PageSize(newWidth, newHeight));
            
            HtmlConverter.convertToPdf(htmlContent, pdf, properties);
            
            System.out.printf("✅ Created: %s - Scale: %.0f%% - Size: %.0fx%.0f points%n", 
                             fileName, scale * 100, newWidth, newHeight);
        }
    }

    // Class đại diện cho một mục trong hóa đơn - ĐÃ SỬA
    static class Item {
        private String name;
        private int quantity;
        private double price;
        private String formattedPrice;
        private String formattedTotal;

        public Item(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
        
        // Getter methods - BẮT BUỘC cho Thymeleaf
        public String getName() {
            return name;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public double getPrice() {
            return price;
        }
        
        public String getFormattedPrice() {
            return formattedPrice;
        }
        
        public String getFormattedTotal() {
            return formattedTotal;
        }
        
        // Setter methods
        public void setFormattedPrice(String formattedPrice) {
            this.formattedPrice = formattedPrice;
        }
        
        public void setFormattedTotal(String formattedTotal) {
            this.formattedTotal = formattedTotal;
        }
    }
}