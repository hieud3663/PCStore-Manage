package com.pcstore.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Return;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.ReturnRepository;
import com.pcstore.service.InvoiceDetailService;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ProductService;
import com.pcstore.service.ReturnService;
import com.pcstore.utils.ErrorMessage;

/**
 * Controller để quản lý các thao tác trả hàng
 */
public class ReturnController {
    private final ReturnService returnService;
    private final InvoiceService invoiceService;
    private final ProductService productService;
    private final InvoiceDetailService invoiceDetailService;
    private final Connection connection;

    /**
     * Khởi tạo controller với các service cần thiết
     * 
     * @param connection Kết nối database
     * @param invoiceService Service xử lý hóa đơn
     * @param productService Service xử lý sản phẩm
     */
    public ReturnController(Connection connection, 
                            InvoiceService invoiceService,
                            ProductService productService) {
        // Khởi tạo RepositoryFactory
        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance(connection);
        
        // Khởi tạo các repositories với connection và repositoryFactory
        ProductRepository productRepository = repositoryFactory.getProductRepository();
        InvoiceDetailRepository invoiceDetailRepository = repositoryFactory.getInvoiceDetailRepository();
        
        // Khởi tạo các services
        this.returnService = new ReturnService(new ReturnRepository(connection), productRepository);
        this.invoiceService = invoiceService;
        this.productService = productService;
        this.invoiceDetailService = new InvoiceDetailService(invoiceDetailRepository, productRepository);
        this.connection = connection;
    }

    /**
     * Tạo đơn trả hàng mới
     * 
     * @param invoiceDetailId ID chi tiết hóa đơn
     * @param quantity Số lượng trả
     * @param reason Lý do trả
     * @return Đơn trả hàng mới tạo
     */
    public Return createReturn(Integer invoiceDetailId, int quantity, String reason) {
        try {
            System.out.println("ReturnController: Tạo đơn trả hàng mới với InvoiceDetailId=" + invoiceDetailId + 
                             ", Reason=" + reason);
            
            // Kiểm tra tham số
            if (invoiceDetailId == null || reason == null || reason.trim().isEmpty()) {
                System.err.println("ReturnController: Tham số không hợp lệ");
                return null;
            }
            
            // Lấy chi tiết hóa đơn
            Optional<InvoiceDetail> detailOpt = invoiceDetailService.findInvoiceDetailById(invoiceDetailId);
            if (!detailOpt.isPresent()) {
                System.err.println("ReturnController: Không tìm thấy chi tiết hóa đơn với ID: " + invoiceDetailId);
                return null;
            }
            
            InvoiceDetail detail = detailOpt.get();
            System.out.println("ReturnController: Tìm thấy chi tiết hóa đơn: ProductID=" + 
                              (detail.getProduct() != null ? detail.getProduct().getProductId() : "null") + 
                              ", Quantity=" + detail.getQuantity());
            
            // Lấy danh sách trả hàng hiện có để kiểm tra số lượng còn lại
            List<Return> existingReturns = getReturnsByInvoiceDetail(invoiceDetailId);
            int returnedQuantity = 0;
            
            if (existingReturns != null && !existingReturns.isEmpty()) {
                for (Return ret : existingReturns) {
                    if (ret != null && ("Approved".equals(ret.getStatus()) || "Completed".equals(ret.getStatus()))) {
                        returnedQuantity += ret.getQuantity();
                    }
                }
            }
            
            // Tính số lượng còn lại có thể trả
            int remainingQuantity = detail.getQuantity() - returnedQuantity;
            System.out.println("ReturnController: Số lượng đã trả: " + returnedQuantity + ", Số lượng còn lại: " + remainingQuantity);
            
            if (remainingQuantity <= 0) {
                System.err.println("ReturnController: Không còn sản phẩm nào để trả");
                return null;
            }
            
            // Kiểm tra số lượng yêu cầu trả
            if (quantity > remainingQuantity) {
                System.out.println("ReturnController: Số lượng trả vượt quá số còn lại, điều chỉnh về tối đa: " + remainingQuantity);
                quantity = remainingQuantity;
            }
            
            if (quantity <= 0) {
                System.err.println("ReturnController: Số lượng trả phải lớn hơn 0");
                return null;
            }
            
            // Tạo đối tượng Return mới
            Return returnObj = new Return();
            returnObj.setInvoiceDetail(detail);
            returnObj.setQuantity(quantity);
            returnObj.setReason(reason);
            returnObj.setStatus("Pending"); // Trạng thái mặc định khi tạo mới
            returnObj.setReturnDate(LocalDateTime.now());
            
            // Tính số tiền trả lại
            java.math.BigDecimal unitPrice = detail.getUnitPrice();
            java.math.BigDecimal returnAmount = unitPrice.multiply(new java.math.BigDecimal(quantity));
            returnObj.setReturnAmount(returnAmount);
            
            // Lưu vào cơ sở dữ liệu
            System.out.println("ReturnController: Đang lưu đơn trả hàng vào cơ sở dữ liệu...");
            Return createdReturn = returnService.createReturn(returnObj);
            
            System.out.println("ReturnController: Đã tạo đơn trả hàng thành công với ID: " + 
                              (createdReturn != null ? createdReturn.getReturnId() : "null"));
            
            return createdReturn;
        } catch (Exception e) {
            System.err.println("ReturnController: Lỗi khi tạo đơn trả hàng: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy tất cả đơn trả hàng
     * 
     * @return Danh sách các đơn trả hàng
     */
    public List<Return> getAllReturns() {
        try {
            return returnService.findAllReturns();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách đơn trả hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy đơn trả hàng theo ID
     * 
     * @param returnId ID đơn trả hàng
     * @return Đơn trả hàng nếu tìm thấy
     */
    public Optional<Return> getReturnById(Integer returnId) {
        try {
            return returnService.findReturnById(returnId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm đơn trả hàng theo hóa đơn
     * 
     * @param invoiceId ID hóa đơn
     * @return Danh sách đơn trả hàng thuộc hóa đơn
     */
    public List<Return> getReturnsByInvoice(Integer invoiceId) {
        try {
            return returnService.findReturnsByInvoice(invoiceId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo hóa đơn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm đơn trả hàng theo khách hàng
     * 
     * @param customerId ID khách hàng
     * @return Danh sách đơn trả hàng của khách hàng
     */
    public List<Return> getReturnsByCustomer(String customerId) {
        try {
            return returnService.findReturnsByCustomer(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo khách hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm đơn trả hàng theo sản phẩm
     * 
     * @param productId ID sản phẩm
     * @return Danh sách đơn trả hàng có sản phẩm này
     */
    public List<Return> getReturnsByProduct(String productId) {
        try {
            return returnService.findReturnsByProduct(productId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo sản phẩm: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm đơn trả hàng theo trạng thái
     * 
     * @param status Trạng thái đơn trả hàng
     * @return Danh sách đơn trả hàng có trạng thái tương ứng
     */
    public List<Return> getReturnsByStatus(String status) {
        try {
            return returnService.findReturnsByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo trạng thái: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm đơn trả hàng trong khoảng thời gian
     * 
     * @param startDate Thời gian bắt đầu
     * @param endDate Thời gian kết thúc
     * @return Danh sách đơn trả hàng trong khoảng thời gian
     */
    public List<Return> getReturnsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return returnService.findReturnsByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo khoảng thời gian: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy danh sách đơn trả hàng theo chi tiết hóa đơn
     * 
     * @param invoiceDetailId ID chi tiết hóa đơn
     * @return Danh sách đơn trả hàng
     */
    public List<Return> getReturnsByInvoiceDetail(Integer invoiceDetailId) {
        try {
            if (invoiceDetailId == null) {
                System.out.println("Warning: invoiceDetailId is null in getReturnsByInvoiceDetail");
                return new ArrayList<>();
            }
            
            return returnService.findReturnsByInvoiceDetail(invoiceDetailId);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm đơn trả hàng theo chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Cập nhật đơn trả hàng
     * 
     * @param returnId ID đơn trả hàng
     * @param quantity Số lượng mới (nếu không thay đổi, truyền -1)
     * @param reason Lý do mới (nếu không thay đổi, truyền null)
     * @param notes Ghi chú mới (nếu không thay đổi, truyền null)
     * @return Đơn trả hàng đã được cập nhật
     */
    public Return updateReturn(Integer returnId, int quantity, String reason, String notes) {
        try {
            Optional<Return> returnOpt = returnService.findReturnById(returnId);
            if (!returnOpt.isPresent()) {
                JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_NOT_FOUND, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            Return returnObj = returnOpt.get();
            
            // Chỉ cho phép cập nhật khi đơn hàng ở trạng thái Pending
            if (!returnObj.canUpdate()) {
                JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_CANNOT_UPDATE, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            // Cập nhật các trường nếu được chỉ định
            if (quantity > 0) {
                // Kiểm tra số lượng không vượt quá số lượng trong hóa đơn ban đầu
                InvoiceDetail invoiceDetail = returnObj.getInvoiceDetail();
                if (quantity > invoiceDetail.getQuantity()) {
                    JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_QUANTITY_EXCEED, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                returnObj.setQuantity(quantity);
            }
            
            if (reason != null && !reason.isEmpty()) {
                returnObj.setReason(reason);
            }
            
            if (notes != null) {
                returnObj.setNotes(notes);
            }
            
            return returnService.updateReturn(returnObj);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_UPDATE_ERROR + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Xóa đơn trả hàng
     * 
     * @param returnId ID của đơn trả hàng cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteReturn(Integer returnId) {
        try {
            System.out.println("ReturnController: Đang xóa đơn trả hàng có ID=" + returnId);
            
            if (returnId == null) {
                System.err.println("ReturnController: ID trả hàng không hợp lệ");
                return false;
            }
            
            // Lấy thông tin đơn trả hàng để kiểm tra
            Optional<Return> returnOpt = returnService.findById(returnId);
            if (!returnOpt.isPresent()) {
                System.err.println("ReturnController: Không tìm thấy đơn trả hàng có ID=" + returnId);
                return false;
            }
            
            Return returnObj = returnOpt.get();
            
            // Kiểm tra trạng thái đơn trả hàng - chỉ cho phép xóa đơn trả có trạng thái Pending
            if (!"Pending".equalsIgnoreCase(returnObj.getStatus())) {
                System.err.println("ReturnController: Không thể xóa đơn trả hàng có trạng thái " + returnObj.getStatus());
                return false;
            }
            
            // Thực hiện xóa đơn trả hàng
            boolean result = returnService.deleteReturn(returnId);
            
            System.out.println("ReturnController: Kết quả xóa đơn trả hàng: " + (result ? "Thành công" : "Thất bại"));
            return result;
        } catch (Exception e) {
            System.err.println("ReturnController: Lỗi khi xóa đơn trả hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Phê duyệt đơn trả hàng
     * 
     * @param returnId ID đơn trả hàng
     * @param processorId ID nhân viên xử lý
     * @param notes Ghi chú khi phê duyệt
     * @return true nếu phê duyệt thành công
     */
    public boolean approveReturn(Integer returnId, String processorId, String notes) {
        try {
            boolean success = returnService.approveReturn(returnId, processorId, notes);
            
            // Sau khi phê duyệt, cần cập nhật số lượng tồn kho
            if (success) {
                Optional<Return> returnOpt = returnService.findReturnById(returnId);
                if (returnOpt.isPresent()) {
                    Return returnObj = returnOpt.get();
                    InvoiceDetail invoiceDetail = returnObj.getInvoiceDetail();
                    
                    // Cập nhật số lượng tồn kho
                    String productId = invoiceDetail.getProduct().getProductId();
                    int returnQuantity = returnObj.getQuantity();
                    
                    // Tăng số lượng tồn kho sản phẩm
                    productService.updateProductStock(productId, returnQuantity);
                }
            }
            
            return success;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi phê duyệt đơn trả hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Từ chối đơn trả hàng
     * 
     * @param returnId ID đơn trả hàng
     * @param processorId ID nhân viên xử lý
     * @param notes Lý do từ chối
     * @return true nếu từ chối thành công
     */
    public boolean rejectReturn(Integer returnId, String processorId, String notes) {
        try {
            return returnService.rejectReturn(returnId, processorId, notes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi từ chối đơn trả hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Hoàn thành đơn trả hàng
     * 
     * @param returnId ID đơn trả hàng
     * @param processorId ID nhân viên xử lý
     * @param notes Ghi chú khi hoàn thành
     * @return true nếu hoàn thành thành công
     */
    public boolean completeReturn(Integer returnId, String processorId, String notes) {
        try {
            return returnService.completeReturn(returnId, processorId, notes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hoàn thành đơn trả hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Đổi sản phẩm (trả hàng và lấy sản phẩm khác)
     * 
     * @param returnId ID đơn trả hàng
     * @param newProductId ID sản phẩm mới
     * @param processorId ID nhân viên xử lý
     * @param notes Ghi chú
     * @return true nếu đổi sản phẩm thành công
     */
    public boolean exchangeProduct(Integer returnId, String newProductId, String processorId, String notes) {
        try {
            Optional<Return> returnOpt = returnService.findReturnById(returnId);
            if (!returnOpt.isPresent()) {
                JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_NOT_FOUND, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            Return returnObj = returnOpt.get();
            
            // Chỉ cho phép đổi sản phẩm khi đơn hàng ở trạng thái Pending hoặc Approved
            if (!"Pending".equals(returnObj.getStatus()) && !"Approved".equals(returnObj.getStatus())) {
                JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_CANNOT_EXCHANGE, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Kiểm tra sản phẩm mới tồn tại
            if (!productService.productExists(newProductId)) {
                JOptionPane.showMessageDialog(null, ErrorMessage.PRODUCT_NOT_FOUND, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Đánh dấu đơn trả hàng là hoàn thành
            returnService.completeReturn(returnId, processorId, "Đã đổi sang sản phẩm " + newProductId + ". " + notes);
            
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_EXCHANGE_ERROR + ": " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Kiểm tra xem đơn trả hàng có tồn tại không
     * 
     * @param returnId ID đơn trả hàng
     * @return true nếu tồn tại
     */
    public boolean returnExists(Integer returnId) {
        try {
            return returnService.returnExists(returnId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại đơn trả hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm kiếm đơn trả hàng theo từ khóa
     * 
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách đơn trả hàng phù hợp
     */
    public List<Return> searchReturns(String keyword) {
        try {
            // Triển khai tìm kiếm thông qua service
            // Đây là phương thức giả định, cần thêm vào ReturnService
            
            // Vì chức năng này chưa được triển khai, nên trả về tất cả đơn trả hàng
            return returnService.findAllReturns();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm đơn trả hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật trạng thái đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param newStatus Trạng thái mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateReturnStatus(int returnId, String newStatus) throws SQLException {
        // Lấy đối tượng Return hiện tại để kiểm tra
        Optional<Return> currentReturn = getReturnById(returnId);
        if (currentReturn.isEmpty()) {
            JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_NOT_FOUND, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        Return returnObj = currentReturn.get();
        String currentStatus = returnObj.getStatus();
        
        // Kiểm tra logic chuyển đổi trạng thái
        // Ví dụ: không cho phép chuyển từ Rejected về Pending
        if ("Rejected".equals(currentStatus) && "Pending".equals(newStatus)) {
            JOptionPane.showMessageDialog(null, ErrorMessage.RETURN_STATUS_INVALID, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Cập nhật trạng thái trong cơ sở dữ liệu
        String sql = "UPDATE Returns SET Status = ? WHERE ReturnID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, returnId);
            int rowsUpdated = stmt.executeUpdate();
            
            // Nếu là "Completed", cập nhật lại số lượng trong kho
            if (rowsUpdated > 0 && "Completed".equals(newStatus) && !"Completed".equals(currentStatus)) {
                // Lấy thông tin sản phẩm và số lượng
                String productId = returnObj.getInvoiceDetail().getProduct().getProductId();
                int quantity = returnObj.getQuantity();
                
                // Cập nhật số lượng trong kho
                updateProductStock(productId, quantity);
            }
            
            return rowsUpdated > 0;
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong kho
     */
    private void updateProductStock(String productId, int quantity) throws SQLException {
        // Lấy số lượng hiện tại
        String selectSql = "SELECT StockQuantity FROM Products WHERE ProductID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery(); 
            
            if (rs.next()) {
                int currentStock = rs.getInt("StockQuantity");
                int newStock = currentStock + quantity;
                
                // Cập nhật số lượng mới
                String updateSql = "UPDATE Products SET StockQuantity = ? WHERE ProductID = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, newStock);
                    updateStmt.setString(2, productId);
                    updateStmt.executeUpdate();
                }
            }
        }
    }
}