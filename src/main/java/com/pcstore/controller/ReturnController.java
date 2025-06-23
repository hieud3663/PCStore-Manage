package com.pcstore.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;

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
import com.pcstore.utils.TableUtils;
import com.pcstore.view.AddReturnProductForm;
import com.pcstore.view.ReturnDetailForm;
import com.pcstore.view.ReturnServiceForm;

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

    public void setupTable(ReturnServiceForm form) {
        DefaultTableModel tableModel = (DefaultTableModel) form.getTbReturn().getModel();
        tableModel.setRowCount(0);
        String[] columnNames = {
            "Mã Trả Hàng", "Mã Sản Phẩm", "Tên Sản Phẩm",
            "Số Lượng", "Lý Do", "Ngày Trả", "Trạng Thái"
        };
        tableModel.setColumnIdentifiers(columnNames);
        TableUtils.applyDefaultStyle(form.getTbReturn());
    }

    public void loadAllReturns(ReturnServiceForm form) {
        try {
            List<Return> returns = getAllReturns();
            displayReturns(form, returns);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_LOAD_ERROR + ": " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addReturnToTable(ReturnServiceForm form, Return returnObj) {
        if (returnObj == null) return;
        DefaultTableModel tableModel = (DefaultTableModel) form.getTbReturn().getModel();
        Map<String, String> statusTranslation = form.getStatusTranslation();
        DateTimeFormatter dateFormatter = form.getDateFormatter();

        String status = returnObj.getStatus();
        String translatedStatus = statusTranslation.getOrDefault(status, status);

        Object[] rowData = {
            returnObj.getReturnId(),
            returnObj.getInvoiceDetail().getProduct().getProductId(),
            returnObj.getInvoiceDetail().getProduct().getProductName(),
            returnObj.getQuantity(),
            returnObj.getReason(),
            returnObj.getReturnDate().format(dateFormatter),
            translatedStatus
        };
        tableModel.addRow(rowData);

        int lastRow = tableModel.getRowCount() - 1;
        if (lastRow >= 0) {
            form.getTbReturn().scrollRectToVisible(form.getTbReturn().getCellRect(lastRow, 0, true));
            form.getTbReturn().setRowSelectionInterval(lastRow, lastRow);
        }
    }

    public void displayReturns(ReturnServiceForm form, List<Return> returns) {
        DefaultTableModel tableModel = (DefaultTableModel) form.getTbReturn().getModel();
        tableModel.setRowCount(0);
        Map<String, String> statusTranslation = form.getStatusTranslation();
        DateTimeFormatter dateFormatter = form.getDateFormatter();

        for (Return returnObj : returns) {
            String status = returnObj.getStatus();
            String translatedStatus = statusTranslation.getOrDefault(status, status);

            Object[] rowData = {
                returnObj.getReturnId(),
                returnObj.getInvoiceDetail().getProduct().getProductId(),
                returnObj.getInvoiceDetail().getProduct().getProductName(),
                returnObj.getQuantity(),
                returnObj.getReason(),
                returnObj.getReturnDate().format(dateFormatter),
                translatedStatus
            };
            tableModel.addRow(rowData);
        }
        // Cập nhật lại kích thước cột nếu cần
        if (form.getTbReturn().getColumnCount() > 0) {
            form.getTbReturn().getColumnModel().getColumn(0).setPreferredWidth(70);
            form.getTbReturn().getColumnModel().getColumn(1).setPreferredWidth(100);
            form.getTbReturn().getColumnModel().getColumn(2).setPreferredWidth(200);
            form.getTbReturn().getColumnModel().getColumn(3).setPreferredWidth(70);
            form.getTbReturn().getColumnModel().getColumn(4).setPreferredWidth(200);
            form.getTbReturn().getColumnModel().getColumn(5).setPreferredWidth(150);
            form.getTbReturn().getColumnModel().getColumn(6).setPreferredWidth(120);
        }
    }

    public void searchReturns(ReturnServiceForm form) {
        String keyword = form.getTxtSearch().getText().trim();
        if (keyword.isEmpty()) {
            loadAllReturns(form);
            return;
        }
        try {
            List<Return> searchResults;
            if (keyword.matches("\\d+")) {
                Optional<Return> returnById = getReturnById(Integer.parseInt(keyword));
                searchResults = returnById.isPresent() ? List.of(returnById.get()) : List.of();
            } else if (keyword.matches("\\d{10,11}")) {
                searchResults = getReturnsByCustomer(keyword);
            } else if (keyword.matches("[A-Za-z0-9]+")) {
                searchResults = getReturnsByProduct(keyword);
            } else {
                searchResults = searchReturns(keyword);
            }
            displayReturns(form, searchResults);
            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(form,
                    String.format(ErrorMessage.RETURN_NOT_FOUND_WITH_KEYWORD.toString(), keyword),
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_SEARCH_ERROR + ": " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openAddReturnForm(ReturnServiceForm form) {
        AddReturnProductForm addForm = new AddReturnProductForm(form);
        JDialog dialog = new JDialog();
        dialog.setTitle("Thêm đơn trả hàng mới");
        dialog.setModal(true);
        dialog.setSize(1040, 700);
        dialog.setLocationRelativeTo(form);
        dialog.add(addForm);
        dialog.setVisible(true);
    }

    public void showReturnDetails(ReturnServiceForm form) {
        JTable tbReturn = form.getTbReturn();
        DefaultTableModel tableModel = (DefaultTableModel) tbReturn.getModel();
        int selectedRow = tbReturn.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_SELECT_ONE,
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tbReturn.convertRowIndexToModel(selectedRow);
        Integer returnId = (Integer) tableModel.getValueAt(modelRow, 0);
        try {
            Optional<Return> returnOpt = getReturnById(returnId);
            if (returnOpt.isPresent()) {
                showReturnDetailInDialog(form, returnOpt.get());
            } else {
                JOptionPane.showMessageDialog(form,
                    String.format(ErrorMessage.RETURN_NOT_FOUND_WITH_ID.toString(), returnId),
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_DETAIL_LOAD_ERROR.toString() + ": " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showReturnDetailInDialog(ReturnServiceForm form, Return returnObj) {
        JDialog detailDialog = new JDialog();
        detailDialog.setTitle("Chi tiết đơn trả hàng #" + returnObj.getReturnId());
        detailDialog.setModal(true);
        detailDialog.setSize(700, 500);
        detailDialog.setLocationRelativeTo(form);

        ReturnDetailForm detailForm = new ReturnDetailForm(returnObj);

        JPanel containerPanel = new JPanel(new java.awt.BorderLayout());
        containerPanel.add(detailForm, java.awt.BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(closeButton);

        containerPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        detailDialog.add(containerPanel);
        detailDialog.setVisible(true);
    }

    public void deleteSelectedReturn(ReturnServiceForm form) {
        JTable tbReturn = form.getTbReturn();
        DefaultTableModel tableModel = (DefaultTableModel) tbReturn.getModel();
        int selectedRow = tbReturn.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_SELECT_ONE,
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tbReturn.convertRowIndexToModel(selectedRow);
        Integer returnId = (Integer) tableModel.getValueAt(modelRow, 0);
        String productName = (String) tableModel.getValueAt(modelRow, 2);
        String status = (String) tableModel.getValueAt(modelRow, 6);
        if (!status.equals(form.getStatusTranslation().get("Pending"))) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_DELETE_ONLY_PENDING + "\n" +
                ErrorMessage.RETURN_CURRENT_STATUS + status,
                "Không thể xóa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(form,
            String.format(ErrorMessage.RETURN_DELETE_CONFIRM.toString(), returnId, productName),
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean result = deleteReturn(returnId);
            if (result) {
                tableModel.removeRow(modelRow);
                JOptionPane.showMessageDialog(form,
                    ErrorMessage.RETURN_DELETE_SUCCESS,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(form,
                    ErrorMessage.RETURN_DELETE_FAIL,
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_DELETE_ERROR + ": " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void handleUpdateReturnStatus(ReturnServiceForm form) {
        JTable tbReturn = form.getTbReturn();
        DefaultTableModel tableModel = (DefaultTableModel) tbReturn.getModel();
        int selectedRow = tbReturn.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_SELECT_ONE,
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tbReturn.convertRowIndexToModel(selectedRow);
        Integer returnId = (Integer) tableModel.getValueAt(modelRow, 0);
        String productName = (String) tableModel.getValueAt(modelRow, 2);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 6);

        try {
            Optional<Return> returnOptional = getReturnById(returnId);
            if (returnOptional.isEmpty()) {
                JOptionPane.showMessageDialog(form,
                    String.format(ErrorMessage.RETURN_NOT_FOUND_WITH_ID.toString(), returnId),
                    "Thông báo", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Return returnObj = returnOptional.get();
            String[] availableStatuses = {"Đang chờ xử lý", "Đã phê duyệt", "Đã từ chối", "Đã hoàn thành"};
            String newStatus = (String) JOptionPane.showInputDialog(
                form,
                "Chọn trạng thái mới cho đơn trả hàng #" + returnId + " - " + productName,
                "Cập nhật trạng thái",
                JOptionPane.QUESTION_MESSAGE,
                null,
                availableStatuses,
                currentStatus
            );
            if (newStatus == null || newStatus.equals(currentStatus)) {
                return;
            }
            String englishStatus = null;
            for (Map.Entry<String, String> entry : form.getStatusTranslation().entrySet()) {
                if (entry.getValue().equals(newStatus)) {
                    englishStatus = entry.getKey();
                    break;
                }
            }
            if (englishStatus == null) {
                JOptionPane.showMessageDialog(form,
                    ErrorMessage.RETURN_STATUS_CONVERT_ERROR,
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ("Approved".equals(returnObj.getStatus()) && "Completed".equals(englishStatus)) {
                int option = JOptionPane.showConfirmDialog(form,
                    "Khi chuyển sang trạng thái 'Đã hoàn thành', hệ thống sẽ điều chỉnh kho.\n"
                    + "- Hoàn trả số lượng " + returnObj.getQuantity() + " sản phẩm vào kho.\n"
                    + "Bạn có chắc chắn muốn tiếp tục?",
                    "Xác nhận điều chỉnh kho",
                    JOptionPane.YES_NO_OPTION);
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            boolean result = updateReturnStatus(returnId, englishStatus);
            if (result) {
                JOptionPane.showMessageDialog(form,
                    ErrorMessage.RETURN_STATUS_UPDATE_SUCCESS,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllReturns(form);
            } else {
                JOptionPane.showMessageDialog(form,
                    ErrorMessage.RETURN_STATUS_UPDATE_FAIL,
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.RETURN_STATUS_UPDATE_ERROR + ": " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}