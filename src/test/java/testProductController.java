import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.pcstore.model.Category;
import com.pcstore.model.Product;
import com.pcstore.model.Supplier;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.utils.DatabaseConnection;

public class testProductController {
    private Product product;
    private Connection connection;
    private ProductRepository productRepository;

    public testProductController() {
        this.product = product;
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.productRepository = new ProductRepository(connection);
    }

    public Product getProduct() {
        return product;
    }

    public void close(){
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     //test thêm sản phẩm
    public void testAddProduct(ProductRepository productRepository, Product product) {
        try {
            productRepository.add(product);
            System.out.println("Product added successfully!");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // this.close();
        }
    }

    //test sửa sản phẩm
    public void testUpdateProduct(ProductRepository productRepository, Product product) {
        try {
            product.setProductName("Sản phẩm mới");
            product.setPrice(new BigDecimal(1000));
            product.setStockQuantity(10);
            product.setSpecifications("Thông số kỹ thuật mới");
            product.setDescription("Mô tả sản phẩm mới");

            productRepository.update(product);
            System.out.println("Product updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        /**
         * Tạo mã sản phẩm dựa vào mã danh mục (CategoryID) bằng truy vấn SQL trực tiếp
         * 
         * @param categoryId Mã danh mục (ví dụ: "LK", "LAP", "PC")
         * @return Mã sản phẩm mới theo định dạng CategoryID + số thứ tự (ví dụ: LK001, LAP002)
         */
        public String generateProductId(String categoryId) {
            if (categoryId == null || categoryId.isEmpty()) {
                throw new IllegalArgumentException("Mã danh mục không được để trống");
            }
            
            int nextIdNumber = 1; // Mặc định bắt đầu từ 1
            
            String sql = "SELECT MAX(CAST(SUBSTRING(ProductID, ?, LEN(ProductID) - ? + 1) AS INT)) " +
                         "FROM Products WHERE ProductID LIKE ?";
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, categoryId.length() + 1);
                statement.setInt(2, categoryId.length());
                statement.setString(3, categoryId + "%");
                
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next() && resultSet.getObject(1) != null) {
                        nextIdNumber = resultSet.getInt(1) + 1;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi khi tạo mã sản phẩm", e);
            }
            
            return categoryId + String.format("%03d", nextIdNumber);
        }


    public static void main(String[] args) {
        
        testProductController test = new testProductController();


// Tạo một sản phẩm mới===============================
        Product product = new Product();
       // product.setProductId("SP001"); //cái này tự tạo phải giống như trigger trong csdl
        product.setProductName("Sản phẩm 1");
        product.setPrice(new BigDecimal(1000));
        product.setStockQuantity(10);
        product.setSpecifications("Thông số kỹ thuật 1");
        product.setDescription("Mô tả sản phẩm 1");
        product.setCategory(new Category("LAP", "Danh mục 1")); //Danh mục phải có trong csdl
        product.setSupplier(new Supplier("NCC01", "Nhà cung cấp 1", "Địa chỉ 1", "Số điện thoại 1", "Email 1")); //Nhà cung cấp phải có trong csdl
        

        // Tạo mã sản phẩm dựa vào mã danh mục
        String categoryId = product.getCategory().getCategoryId(); // Lấy mã danh mục từ sản phẩm
        product.setProductId(test.generateProductId(categoryId));
//======================================================Kết thúc tạo sản phẩm mới

//======================================================
        // Thêm sản phẩm vào cơ sở dữ liệu
        test.testAddProduct(test.productRepository, product);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine(); // Đợi người dùng nhấn Enter để tiếp tục
        test.testUpdateProduct(test.productRepository, product);     
//======================================================Kết thúc sửa sản phẩm
       
        test.close();
    }



}