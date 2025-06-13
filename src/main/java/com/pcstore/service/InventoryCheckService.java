package com.pcstore.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Employee;
import com.pcstore.model.InventoryCheck;
import com.pcstore.model.InventoryCheckDetail;
import com.pcstore.model.Product;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.EmployeeRepository;
import com.pcstore.repository.impl.InventoryCheckDetailRepository;
import com.pcstore.repository.impl.InventoryCheckRepository;
import com.pcstore.repository.impl.ProductRepository;

/**
 * Service xử lý logic nghiệp vụ liên quan đến kiểm kê
 */
public class InventoryCheckService {
    private final InventoryCheckRepository inventoryCheckRepository;
    private final InventoryCheckDetailRepository inventoryCheckDetailRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Khởi tạo service với connection
     * @param connection Kết nối đến database
     */
    public InventoryCheckService(Connection connection) {
        try {
            this.inventoryCheckRepository = RepositoryFactory.getInstance(connection)
                .getInventoryCheckRepository();
            this.inventoryCheckDetailRepository = RepositoryFactory.getInstance(connection).getInventoryCheckDetailRepository();
            this.productRepository = RepositoryFactory.getInstance(connection).getProductRepository();
            this.employeeRepository = RepositoryFactory.getInstance(connection).getEmployeeRepository();
        } catch (Exception e) {
            System.err.println("Lỗi không xác định khi khởi tạo InventoryCheckService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi không xác định khi khởi tạo InventoryCheckService", e);
        }
    }
    /**
     * Tạo phiếu kiểm kê mới
     * @param checkName Tên phiếu kiểm kê
     * @param employeeId ID nhân viên thực hiện
     * @param checkType Loại kiểm kê
     * @param notes Ghi chú
     * @return Phiếu kiểm kê đã tạo
     */
    public InventoryCheck createInventoryCheck(String checkName, String employeeId, 
                                             String checkType, String notes) {
        // Tìm nhân viên
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (!employee.isPresent()) {
            throw new IllegalArgumentException("Nhân viên không tồn tại");
        }

        InventoryCheck inventoryCheck = new InventoryCheck();
        inventoryCheck.setCheckCode(inventoryCheckRepository.generateCheckCode());
        inventoryCheck.setCheckName(checkName);
        inventoryCheck.setEmployee(employee.get());
        inventoryCheck.setCheckDate(LocalDateTime.now());
        inventoryCheck.setCheckType(checkType != null ? checkType : "FULL");
        inventoryCheck.setStatus("DRAFT");
        inventoryCheck.setNotes(notes);

        return inventoryCheckRepository.add(inventoryCheck);
    }

    /**
     * Thêm chi tiết kiểm kê
     * @param inventoryCheckId ID phiếu kiểm kê
     * @param productId ID sản phẩm
     * @param actualQuantity Số lượng thực tế
     * @param reason Lý do chênh lệch
     * @return Chi tiết kiểm kê đã thêm
     */
    public InventoryCheckDetail addCheckDetail(Integer inventoryCheckId, String productId, Integer actualQuantity, String reason) {
        // Tìm phiếu kiểm kê
        Optional<InventoryCheck> inventoryCheck = inventoryCheckRepository.findById(inventoryCheckId);
        if (!inventoryCheck.isPresent()) {
            throw new IllegalArgumentException("Phiếu kiểm kê không tồn tại");
        }

        // Tìm sản phẩm
        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent()) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại");
        }

        InventoryCheckDetail detail = new InventoryCheckDetail();
        detail.setInventoryCheck(inventoryCheck.get());
        detail.setProduct(product.get());
        detail.setSystemQuantity(product.get().getQuantityInStock());
        detail.setActualQuantity(actualQuantity);
        detail.setReason(reason);

        // Tính giá trị thất thoát nếu có
        int discrepancy = actualQuantity - product.get().getQuantityInStock();
        if (discrepancy < 0) {
            BigDecimal unitPrice = product.get().getPrice();
            detail.setLossValue(unitPrice.multiply(new BigDecimal(Math.abs(discrepancy))));
        } else {
            detail.setLossValue(BigDecimal.ZERO);
        }

        return inventoryCheckDetailRepository.add(detail);
    }

    /**
     * Hoàn thành kiểm kê và cập nhật tồn kho
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return true nếu hoàn thành thành công
     */
    public boolean completeInventoryCheck(Integer inventoryCheckId) {
        Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckRepository.findById(inventoryCheckId);
        if (!inventoryCheckOpt.isPresent()) {
            throw new IllegalArgumentException("Phiếu kiểm kê không tồn tại");
        }

        InventoryCheck inventoryCheck = inventoryCheckOpt.get();
        if (!"IN_PROGRESS".equals(inventoryCheck.getStatus()) && !"DRAFT".equals(inventoryCheck.getStatus())) {
            throw new IllegalStateException("Chỉ có thể hoàn thành phiếu đang thực hiện hoặc nháp");
        }

        List<InventoryCheckDetail> details = inventoryCheckDetailRepository.findByInventoryCheckId(inventoryCheckId);
        
        for (InventoryCheckDetail detail : details) {
            if (detail.getDiscrepancy() != 0) {
                Product product = detail.getProduct();
                product.setStockQuantity(detail.getActualQuantity());
                productRepository.update(product);
            }
        }

        return inventoryCheckRepository.updateStatus(inventoryCheckId, "COMPLETED");
    }

    /**
     * Lấy danh sách phiếu kiểm kê
     * @return Danh sách phiếu kiểm kê
     */
    public List<InventoryCheck> getAllInventoryChecks() {
        return inventoryCheckRepository.findAll();
    }

    /**
     * Lấy chi tiết kiểm kê theo ID phiếu
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return Danh sách chi tiết kiểm kê
     */
    public List<InventoryCheckDetail> getCheckDetails(Integer inventoryCheckId) {
        return inventoryCheckDetailRepository.findByInventoryCheckId(inventoryCheckId);
    }
    
    /**
     * Tìm phiếu kiểm kê theo ID
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return Optional chứa phiếu kiểm kê
     */
    public Optional<InventoryCheck> findInventoryCheckById(Integer inventoryCheckId) {
        try {
            return inventoryCheckRepository.findById(inventoryCheckId);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm phiếu kiểm kê theo ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Tìm phiếu kiểm kê theo mã code
     * @param checkCode Mã phiếu kiểm kê
     * @return Optional chứa phiếu kiểm kê
     */
    public Optional<InventoryCheck> findInventoryCheckByCode(String checkCode) {
        try {
            return inventoryCheckRepository.findByCheckCode(checkCode);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm phiếu kiểm kê theo mã: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Lấy danh sách phiếu kiểm kê theo trạng thái
     * @param status Trạng thái cần tìm
     * @return Danh sách phiếu kiểm kê
     */
    public List<InventoryCheck> getInventoryChecksByStatus(String status) {
        try {
            return inventoryCheckRepository.findByStatus(status);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy phiếu kiểm kê theo trạng thái: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Lấy danh sách phiếu kiểm kê theo nhân viên
     * @param employeeId ID nhân viên
     * @return Danh sách phiếu kiểm kê
     */
    public List<InventoryCheck> getInventoryChecksByEmployee(String employeeId) {
        try {
            return inventoryCheckRepository.findByEmployeeId(employeeId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy phiếu kiểm kê theo nhân viên: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Lấy danh sách phiếu kiểm kê theo loại
     * @param checkType Loại kiểm kê
     * @return Danh sách phiếu kiểm kê
     */
    public List<InventoryCheck> getInventoryChecksByType(String checkType) {
        try {
            return inventoryCheckRepository.findByCheckType(checkType);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy phiếu kiểm kê theo loại: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Cập nhật thông tin phiếu kiểm kê
     * @param inventoryCheck Phiếu kiểm kê cần cập nhật
     * @return Phiếu kiểm kê đã cập nhật
     */
    public InventoryCheck updateInventoryCheck(InventoryCheck inventoryCheck) {
        try {
            // Kiểm tra phiếu kiểm kê có tồn tại không
            if (!inventoryCheckRepository.exists(inventoryCheck.getId())) {
                throw new IllegalArgumentException("Phiếu kiểm kê không tồn tại");
            }

            // Cập nhật thời gian sửa đổi
            inventoryCheck.setUpdatedAt(LocalDateTime.now());
            
            return inventoryCheckRepository.update(inventoryCheck);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật phiếu kiểm kê: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật phiếu kiểm kê", e);
        }
    }

    /**
     * Cập nhật trạng thái phiếu kiểm kê
     * @param inventoryCheckId ID phiếu kiểm kê
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateInventoryCheckStatus(Integer inventoryCheckId, String status) {
        try {
            return inventoryCheckRepository.updateStatus(inventoryCheckId, status);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật trạng thái phiếu kiểm kê: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bắt đầu kiểm kê (chuyển từ DRAFT sang IN_PROGRESS)
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return true nếu bắt đầu thành công
     */
    public boolean startInventoryCheck(Integer inventoryCheckId) {
        try {
            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckRepository.findById(inventoryCheckId);
            if (!inventoryCheckOpt.isPresent()) {
                throw new IllegalArgumentException("Phiếu kiểm kê không tồn tại");
            }

            InventoryCheck inventoryCheck = inventoryCheckOpt.get();
            if (!"DRAFT".equals(inventoryCheck.getStatus())) {
                throw new IllegalStateException("Chỉ có thể bắt đầu phiếu kiểm kê ở trạng thái DRAFT");
            }

            return inventoryCheckRepository.updateStatus(inventoryCheckId, "IN_PROGRESS");
        } catch (Exception e) {
            System.err.println("Lỗi khi bắt đầu kiểm kê: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hủy phiếu kiểm kê
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return true nếu hủy thành công
     */
    public boolean cancelInventoryCheck(Integer inventoryCheckId) {
        try {
            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckRepository.findById(inventoryCheckId);
            if (!inventoryCheckOpt.isPresent()) {
                throw new IllegalArgumentException("Phiếu kiểm kê không tồn tại");
            }

            InventoryCheck inventoryCheck = inventoryCheckOpt.get();
            if ("COMPLETED".equals(inventoryCheck.getStatus())) {
                throw new IllegalStateException("Không thể hủy phiếu kiểm kê đã hoàn thành");
            }

            return inventoryCheckRepository.updateStatus(inventoryCheckId, "CANCELLED");
        } catch (Exception e) {
            System.err.println("Lỗi khi hủy phiếu kiểm kê: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa phiếu kiểm kê (chỉ cho phép xóa phiếu DRAFT hoặc CANCELLED)
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return true nếu xóa thành công
     */
    public boolean deleteInventoryCheck(Integer inventoryCheckId) {
        try {
            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckRepository.findById(inventoryCheckId);
            if (!inventoryCheckOpt.isPresent()) {
                throw new IllegalArgumentException("Phiếu kiểm kê không tồn tại");
            }

            InventoryCheck inventoryCheck = inventoryCheckOpt.get();
            if (!"DRAFT".equals(inventoryCheck.getStatus()) && !"CANCELLED".equals(inventoryCheck.getStatus())) {
                throw new IllegalStateException("Chỉ có thể xóa phiếu kiểm kê ở trạng thái DRAFT hoặc CANCELLED");
            }

            return inventoryCheckRepository.delete(inventoryCheckId);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa phiếu kiểm kê: " + e.getMessage());
            return false;
        }
    }

    // ===== PHƯƠNG THỨC CHO CHI TIẾT KIỂM KÊ =====

    /**
     * Tìm chi tiết kiểm kê theo ID
     * @param detailId ID chi tiết kiểm kê
     * @return Optional chứa chi tiết kiểm kê
     */
    public Optional<InventoryCheckDetail> findCheckDetailById(Integer detailId) {
        try {
            return inventoryCheckDetailRepository.findById(detailId);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm chi tiết kiểm kê theo ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Cập nhật chi tiết kiểm kê
     * @param detail Chi tiết kiểm kê cần cập nhật
     * @return Chi tiết kiểm kê đã cập nhật
     */
    public InventoryCheckDetail updateCheckDetail(InventoryCheckDetail detail) {
        try {
            if (!inventoryCheckDetailRepository.exists(detail.getId())) {
                throw new IllegalArgumentException("Chi tiết kiểm kê không tồn tại");
            }

            int discrepancy = detail.getActualQuantity() - detail.getSystemQuantity();
            detail.setDiscrepancy(discrepancy);
            
            if (discrepancy < 0) {
                BigDecimal unitPrice = detail.getProduct().getPrice();
                detail.setLossValue(unitPrice.multiply(new BigDecimal(Math.abs(discrepancy))));
            } else {
                detail.setLossValue(BigDecimal.ZERO);
            }
            
            return inventoryCheckDetailRepository.update(detail);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật chi tiết kiểm kê: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật chi tiết kiểm kê", e);
        }
    }

    /**
     * Xóa chi tiết kiểm kê
     * @param detailId ID chi tiết kiểm kê
     * @return true nếu xóa thành công
     */
    public boolean deleteCheckDetail(Integer detailId) {
        try {
            return inventoryCheckDetailRepository.delete(detailId);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa chi tiết kiểm kê: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy chi tiết kiểm kê có chênh lệch
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return Danh sách chi tiết có chênh lệch
     */
    public List<InventoryCheckDetail> getDiscrepancies(Integer inventoryCheckId) {
        try {
            return inventoryCheckDetailRepository.findDiscrepanciesByCheckId(inventoryCheckId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy chi tiết chênh lệch: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Lấy lịch sử kiểm kê của sản phẩm
     * @param productId ID sản phẩm
     * @return Danh sách lịch sử kiểm kê
     */
    public List<InventoryCheckDetail> getProductCheckHistory(String productId) {
        try {
            return inventoryCheckDetailRepository.findByProductId(productId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy lịch sử kiểm kê sản phẩm: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Tính tổng giá trị thất thoát của phiếu kiểm kê
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return Tổng giá trị thất thoát
     */
    public BigDecimal calculateTotalLossValue(Integer inventoryCheckId) {
        try {
            return inventoryCheckDetailRepository.calculateTotalLossValue(inventoryCheckId);
        } catch (Exception e) {
            System.err.println("Lỗi khi tính tổng giá trị thất thoát: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Xóa tất cả chi tiết kiểm kê của một phiếu
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return true nếu xóa thành công
     */
    public boolean deleteAllCheckDetails(Integer inventoryCheckId) {
        try {
            return inventoryCheckDetailRepository.deleteByInventoryCheckId(inventoryCheckId);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa tất cả chi tiết kiểm kê: " + e.getMessage());
            return false;
        }
    }

    // ===== PHƯƠNG THỨC KIỂM KÊ TỰ ĐỘNG =====

    /**
     * Tạo chi tiết kiểm kê tự động cho tất cả sản phẩm
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return true nếu tạo thành công
     */
    public boolean generateFullInventoryCheckDetails(Integer inventoryCheckId) {
        try {
            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckRepository.findById(inventoryCheckId);
            if (!inventoryCheckOpt.isPresent()) {
                throw new IllegalArgumentException("Phiếu kiểm kê không tồn tại");
            }

            // Lấy tất cả sản phẩm
            List<Product> allProducts = productRepository.findAll();
            
            for (Product product : allProducts) {
                InventoryCheckDetail detail = new InventoryCheckDetail();
                detail.setInventoryCheck(inventoryCheckOpt.get());
                detail.setProduct(product);
                detail.setSystemQuantity(product.getQuantityInStock());
                detail.setActualQuantity(product.getQuantityInStock()); // Mặc định bằng số lượng hệ thống
                detail.setLossValue(BigDecimal.ZERO);
                detail.setReason("");
                
                inventoryCheckDetailRepository.add(detail);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo chi tiết kiểm kê tự động: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tạo chi tiết kiểm kê theo danh mục
     * @param inventoryCheckId ID phiếu kiểm kê
     * @param categoryId ID danh mục
     * @return true nếu tạo thành công
     */
    public boolean generateCategoryInventoryCheckDetails(Integer inventoryCheckId, String categoryId) {
        try {
            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckRepository.findById(inventoryCheckId);
            if (!inventoryCheckOpt.isPresent()) {
                throw new IllegalArgumentException("Phiếu kiểm kê không tồn tại");
            }

            // Lấy sản phẩm theo danh mục
            List<Product> categoryProducts = productRepository.findByCategory(categoryId);
            
            for (Product product : categoryProducts) {
                InventoryCheckDetail detail = new InventoryCheckDetail();
                detail.setInventoryCheck(inventoryCheckOpt.get());
                detail.setProduct(product);
                detail.setSystemQuantity(product.getQuantityInStock());
                detail.setActualQuantity(product.getQuantityInStock());
                detail.setLossValue(BigDecimal.ZERO);
                detail.setReason("");
                
                inventoryCheckDetailRepository.add(detail);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo chi tiết kiểm kê theo danh mục: " + e.getMessage());
            return false;
        }
    }

    // ===== PHƯƠNG THỨC THỐNG KÊ =====

    /**
     * Đếm số lượng phiếu kiểm kê theo trạng thái
     * @param status Trạng thái
     * @return Số lượng phiếu kiểm kê
     */
    public long countInventoryChecksByStatus(String status) {
        try {
            return inventoryCheckRepository.findByStatus(status).size();
        } catch (Exception e) {
            System.err.println("Lỗi khi đếm phiếu kiểm kê theo trạng thái: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Tính tổng giá trị thất thoát trong khoảng thời gian
     * @param fromDate Từ ngày
     * @param toDate Đến ngày
     * @return Tổng giá trị thất thoát
     */
    public BigDecimal calculateTotalLossInPeriod(LocalDateTime fromDate, LocalDateTime toDate) {
        try {
            BigDecimal totalLoss = BigDecimal.ZERO;
            List<InventoryCheck> checks = inventoryCheckRepository.findAll();
            
            for (InventoryCheck check : checks) {
                if (check.getCheckDate().isAfter(fromDate) && check.getCheckDate().isBefore(toDate)) {
                    totalLoss = totalLoss.add(calculateTotalLossValue(check.getId()));
                }
            }
            
            return totalLoss;
        } catch (Exception e) {
            System.err.println("Lỗi khi tính tổng giá trị thất thoát: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Kiểm tra xem có thể thực hiện thao tác trên phiếu kiểm kê không
     * @param inventoryCheckId ID phiếu kiểm kê
     * @param requiredStatus Trạng thái yêu cầu
     * @return true nếu có thể thực hiện
     */
    public boolean canPerformAction(Integer inventoryCheckId, String requiredStatus) {
        try {
            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckRepository.findById(inventoryCheckId);
            if (!inventoryCheckOpt.isPresent()) {
                return false;
            }
            
            return requiredStatus.equals(inventoryCheckOpt.get().getStatus());
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra quyền thao tác: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sinh mã kiểm kê tự động
     * @return Mã kiểm kê mới
     */
    public String generateCheckCode() {
        try {
            return inventoryCheckRepository.generateCheckCode();
        } catch (Exception e) {
            System.err.println("Lỗi khi sinh mã kiểm kê: " + e.getMessage());
            return "KK001"; // Mã mặc định
        }
    }
}
