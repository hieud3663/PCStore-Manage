// BỎ COMMENT RỒI FIX CODE LẠI NHA, NHỚ ĐỌC CÁI REPOSITORY ĐỂ FIX CHO ĐÚNG NHA

// package com.pcstore.service;

// import com.pcstore.model.InvoiceDetail;
// import com.pcstore.repository.impl.InvoiceDetailRepository;
// import com.pcstore.repository.impl.ProductRepository;

// import java.util.List;
// import java.util.Optional;

// /**
//  * Service xử lý logic nghiệp vụ liên quan đến chi tiết hóa đơn
//  */
// public class InvoiceDetailService {
//     private final InvoiceDetailRepository invoiceDetailRepository;
//     private final ProductRepository productRepository;
    
//     /**
//      * Khởi tạo service với repository
//      * @param invoiceDetailRepository Repository chi tiết hóa đơn
//      * @param productRepository Repository sản phẩm để cập nhật tồn kho
//      */
//     public InvoiceDetailService(InvoiceDetailRepository invoiceDetailRepository, ProductRepository productRepository) {
//         this.invoiceDetailRepository = invoiceDetailRepository;
//         this.productRepository = productRepository;
//     }
    
//     /**
//      * Thêm chi tiết hóa đơn mới
//      * @param invoiceDetail Thông tin chi tiết hóa đơn
//      * @return Chi tiết hóa đơn đã được thêm
//      */
//     public InvoiceDetail addInvoiceDetail(InvoiceDetail invoiceDetail) {
//         // Kiểm tra số lượng tồn kho
//         String productId = invoiceDetail.getProduct().getProductId();
//         int quantity = invoiceDetail.getQuantity();
        
//         Optional<com.pcstore.model.Product> productOpt = productRepository.findById(productId);
//         if (productOpt.isPresent()) {
//             com.pcstore.model.Product product = productOpt.get();
//             if (product.getStockQuantity() < quantity) {
//                 throw new IllegalArgumentException("Số lượng sản phẩm vượt quá tồn kho");
//             }
            
//             // Giảm số lượng tồn kho
//             productRepository.updateStock(productId, -quantity);
//         } else {
//             throw new IllegalArgumentException("Sản phẩm không tồn tại");
//         }
        
//         return invoiceDetailRepository.add(invoiceDetail);
//     }
    
//     /**
//      * Cập nhật thông tin chi tiết hóa đơn
//      * @param invoiceDetail Thông tin chi tiết hóa đơn mới
//      * @return Chi tiết hóa đơn đã được cập nhật
//      */
//     public InvoiceDetail updateInvoiceDetail(InvoiceDetail invoiceDetail) {
//         // Tìm chi tiết hóa đơn cũ để so sánh số lượng
//         Optional<InvoiceDetail> oldDetailOpt = invoiceDetailRepository.findById(invoiceDetail.getInvoiceDetailId());
//         if (!oldDetailOpt.isPresent()) {
//             throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại");
//         }
        
//         InvoiceDetail oldDetail = oldDetailOpt.get();
//         String productId = invoiceDetail.getProduct().getProductId();
//         int oldQuantity = oldDetail.getQuantity();
//         int newQuantity = invoiceDetail.getQuantity();
//         int quantityDiff = newQuantity - oldQuantity;
        
//         // Nếu số lượng tăng, kiểm tra tồn kho và cập nhật
//         if (quantityDiff > 0) {
//             Optional<com.pcstore.model.Product> productOpt = productRepository.findById(productId);
//             if (productOpt.isPresent()) {
//                 com.pcstore.model.Product product = productOpt.get();
//                 if (product.getStockQuantity() < quantityDiff) {
//                     throw new IllegalArgumentException("Số lượng sản phẩm vượt quá tồn kho");
//                 }
//             } else {
//                 throw new IllegalArgumentException("Sản phẩm không tồn tại");
//             }
//         }
        
//         // Cập nhật tồn kho (giảm nếu quantityDiff > 0, tăng nếu quantityDiff < 0)
//         productRepository.updateStock(productId, -quantityDiff);
        
//         return invoiceDetailRepository.update(invoiceDetail);
//     }
    
//     /**
//      * Xóa chi tiết hóa đơn theo ID
//      * @param invoiceDetailId ID của chi tiết hóa đơn
//      * @return true nếu xóa thành công, ngược lại là false
//      */
//     public boolean deleteInvoiceDetail(Long invoiceDetailId) {
//         // Hoàn trả số lượng tồn kho
//         Optional<InvoiceDetail> detailOpt = invoiceDetailRepository.findById(invoiceDetailId);
//         if (detailOpt.isPresent()) {
//             InvoiceDetail detail = detailOpt.get();
//             String productId = detail.getProduct().getProductId();
//             int quantity = detail.getQuantity();
            
//             // Tăng lại số lượng tồn kho
//             productRepository.updateStock(productId, quantity);
            
//             return invoiceDetailRepository.delete(invoiceDetailId);
//         }
        
//         return false;
//     }
    
//     /**
//      * Tìm chi tiết hóa đơn theo ID
//      * @param invoiceDetailId ID của chi tiết hóa đơn
//      * @return Optional chứa chi tiết hóa đơn nếu tìm thấy
//      */
//     public Optional<InvoiceDetail> findInvoiceDetailById(Long invoiceDetailId) {
//         return invoiceDetailRepository.findById(invoiceDetailId);
//     }
    
//     /**
//      * Lấy danh sách tất cả chi tiết hóa đơn
//      * @return Danh sách chi tiết hóa đơn
//      */
//     public List<InvoiceDetail> findAllInvoiceDetails() {
//         return invoiceDetailRepository.findAll();
//     }
    
//     /**
//      * Tìm chi tiết hóa đơn theo mã hóa đơn
//      * @param invoiceId Mã hóa đơn
//      * @return Danh sách chi tiết hóa đơn
//      */
//     public List<InvoiceDetail> findInvoiceDetailsByInvoiceId(String invoiceId) {
//         return invoiceDetailRepository.findByInvoiceId(invoiceId);
//     }
    
//     /**
//      * Tìm chi tiết hóa đơn theo mã sản phẩm
//      * @param productId Mã sản phẩm
//      * @return Danh sách chi tiết hóa đơn
//      */
//     public List<InvoiceDetail> findInvoiceDetailsByProductId(String productId) {
//         return invoiceDetailRepository.findByProductId(productId);
//     }
    
//     /**
//      * Xóa tất cả chi tiết hóa đơn theo mã hóa đơn
//      * @param invoiceId Mã hóa đơn
//      */
//     public void deleteInvoiceDetailsByInvoiceId(String invoiceId) {
//         // Hoàn trả số lượng tồn kho cho từng chi tiết
//         List<InvoiceDetail> details = invoiceDetailRepository.findByInvoiceId(invoiceId);
//         for (InvoiceDetail detail : details) {
//             String productId = detail.getProduct().getProductId();
//             int quantity = detail.getQuantity();
            
//             // Tăng lại số lượng tồn kho
//             productRepository.updateStock(productId, quantity);
//         }
        
//         invoiceDetailRepository.deleteByInvoiceId(invoiceId);
//     }
    
//     /**
//      * Kiểm tra chi tiết hóa đơn có tồn tại không
//      * @param invoiceDetailId ID của chi tiết hóa đơn
//      * @return true nếu chi tiết hóa đơn tồn tại, ngược lại là false
//      */
//     public boolean invoiceDetailExists(Long invoiceDetailId) {
//         return invoiceDetailRepository.exists(invoiceDetailId);
//     }
// }