package com.pcstore.controller;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

/**
 * Controller để quản lý các thao tác trả hàng
 */
public class ReturnController {
    private final ReturnService returnService;
    private final InvoiceService invoiceService;
    private final ProductService productService;
    private final InvoiceDetailService invoiceDetailService;

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
    }

    /**
     * Tạo đơn trả hàng mới
     * 
     * @param invoiceDetailId ID chi tiết hóa đơn muốn trả
     * @param quantity Số lượng muốn trả
     * @param reason Lý do trả hàng
     * @param notes Ghi chú bổ sung (có thể null)
     * @return Đơn trả hàng đã được tạo
     */
    public Return createReturn(Integer invoiceDetailId, int quantity, String reason, String notes) {
        try {
            // Kiểm tra chi tiết hóa đơn tồn tại
            Optional<InvoiceDetail> invoiceDetailOpt = invoiceDetailService.findInvoiceDetailById(invoiceDetailId);
            if (!invoiceDetailOpt.isPresent()) {
                throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại");
            }
            
            InvoiceDetail invoiceDetail = invoiceDetailOpt.get();
            
            // Tính toán số lượng đã trả trước đó
            List<Return> existingReturns = getReturnsByInvoiceDetail(invoiceDetailId);
            int alreadyReturnedQuantity = 0;
            
            for (Return existingReturn : existingReturns) {
                // Chỉ tính những đơn ở trạng thái đã phê duyệt hoặc đã hoàn thành
                if ("Approved".equals(existingReturn.getStatus()) || 
                    "Completed".equals(existingReturn.getStatus())) {
                    alreadyReturnedQuantity += existingReturn.getQuantity();
                }
            }
            
            // Tính số lượng còn lại có thể trả
            int remainingQuantity = invoiceDetail.getQuantity() - alreadyReturnedQuantity;
            
            // Kiểm tra số lượng trả không vượt quá số lượng còn lại
            if (quantity <= 0 || quantity > remainingQuantity) {
                throw new IllegalArgumentException("Số lượng trả không hợp lệ. Số lượng tối đa có thể trả là: " + remainingQuantity);
            }
            
            // Tạo đối tượng Return
            Return returnObj = new Return();
            returnObj.setInvoiceDetail(invoiceDetail);
            returnObj.setQuantity(quantity);
            returnObj.setReason(reason);
            returnObj.setReturnDate(LocalDateTime.now());
            returnObj.setStatus("Pending");
            
            // Thêm ghi chú nếu có
            if (notes != null && !notes.isEmpty()) {
                returnObj.setNotes(notes);
            }
            
            return returnService.createReturn(returnObj);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo đơn trả hàng: " + e.getMessage(), e);
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
     * Tìm đơn trả hàng theo chi tiết hóa đơn
     * 
     * @param invoiceDetailId ID chi tiết hóa đơn
     * @return Danh sách đơn trả hàng thuộc chi tiết hóa đơn
     */
    public List<Return> getReturnsByInvoiceDetail(Integer invoiceDetailId) {
        try {
            // Giả định có phương thức này trong returnService
            return returnService.findReturnsByInvoiceDetail(invoiceDetailId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo chi tiết hóa đơn: " + e.getMessage(), e);
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
                throw new IllegalArgumentException("Đơn trả hàng không tồn tại");
            }
            
            Return returnObj = returnOpt.get();
            
            // Chỉ cho phép cập nhật khi đơn hàng ở trạng thái Pending
            if (!returnObj.canUpdate()) {
                throw new IllegalStateException("Không thể cập nhật đơn trả hàng ở trạng thái hiện tại");
            }
            
            // Cập nhật các trường nếu được chỉ định
            if (quantity > 0) {
                // Kiểm tra số lượng không vượt quá số lượng trong hóa đơn ban đầu
                InvoiceDetail invoiceDetail = returnObj.getInvoiceDetail();
                if (quantity > invoiceDetail.getQuantity()) {
                    throw new IllegalArgumentException("Số lượng trả không thể lớn hơn số lượng trong hóa đơn");
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
            throw new RuntimeException("Lỗi khi cập nhật đơn trả hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa đơn trả hàng
     * 
     * @param returnId ID đơn trả hàng
     * @return true nếu xóa thành công
     */
    public boolean deleteReturn(Integer returnId) {
        try {
            return returnService.deleteReturn(returnId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa đơn trả hàng: " + e.getMessage(), e);
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
                throw new IllegalArgumentException("Đơn trả hàng không tồn tại");
            }
            
            Return returnObj = returnOpt.get();
            
            // Chỉ cho phép đổi sản phẩm khi đơn hàng ở trạng thái Pending hoặc Approved
            if (!"Pending".equals(returnObj.getStatus()) && !"Approved".equals(returnObj.getStatus())) {
                throw new IllegalStateException("Không thể đổi sản phẩm với đơn trả hàng ở trạng thái hiện tại");
            }
            
            // Kiểm tra sản phẩm mới tồn tại
            if (!productService.productExists(newProductId)) {
                throw new IllegalArgumentException("Sản phẩm mới không tồn tại");
            }
            
            // Cập nhật thông tin đổi sản phẩm trong CSDL (cần thêm phương thức trong RepairService)
            // Đây là giả định để giữ mã đơn giản, trong thực tế phức tạp hơn
            
            // Đánh dấu đơn trả hàng là hoàn thành
            returnService.completeReturn(returnId, processorId, "Đã đổi sang sản phẩm " + newProductId + ". " + notes);
            
            // Giảm số lượng tồn kho của sản phẩm mới
            // productService.decreaseProductStock(newProductId, returnObj.getQuantity());
            
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đổi sản phẩm: " + e.getMessage(), e);
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
}