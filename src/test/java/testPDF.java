import com.pcstore.utils.WritePDF;

public class testPDF {
    public static void main(String[] args) {
        String excelPath = "src\\output\\INVOICE_73_20250408_003341.xlsx";
        String pdfPath = "src\\output\\test.pdf";
        
        // Gọi phương thức chuyển đổi
        boolean result = WritePDF.convertExcelToPDF(excelPath, pdfPath);
        
        // Kiểm tra kết quả
        if (result) {
            System.out.println("Chuyển đổi thành công!");
        } else {
            System.out.println("Chuyển đổi thất bại!");
        }
    }
}
