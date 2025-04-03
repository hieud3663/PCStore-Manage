/////////////////////////////////////////
// CODE LỖI RỒI NHÉ 
///////////////////////

//package com.pcstore.repository;
//
//import com.pcstore.repository.impl.WarrantyRepository;
//import com.pcstore.model.Warranty;
//
//import java.sql.Connection;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Repository for Warranty entity that delegates to WarrantyRepository
// */
//public class WarrantyRepository {
//    private final WarrantyRepository warrantyRepository;
//    
//    public WarrantyRepository(Connection connection) {
//        this.warrantyRepository = new WarrantyRepository(connection);
//    }
//    
//    public Warranty save(Warranty warranty) {
//        if (warranty.getWarrantyId() == null || warranty.getWarrantyId().isEmpty()) {
//            return warrantyRepository.add(warranty);
//        } else {
//            return warrantyRepository.update(warranty);
//        }
//    }
//    
//    public boolean delete(Integer id) {
//        return warrantyRepository.delete(id);
//    }
//    
//    public Optional<Warranty> findById(Integer id) {
//        return warrantyRepository.findById(id);
//    }
//    
//    public List<Warranty> findAll() {
//        return warrantyRepository.findAll();
//    }
//    
//    public boolean exists(Integer id) {
//        return warrantyRepository.exists(id);
//    }
//    
//    public Optional<Warranty> findByInvoiceDetailId(Integer invoiceDetailId) {
//        return warrantyRepository.findByInvoiceDetailId(invoiceDetailId);
//    }
//    
//    public List<Warranty> findByCustomerId(String customerId) {
//        return warrantyRepository.findByCustomerId(customerId);
//    }
//    
//    public List<Warranty> findByProductId(String productId) {
//        return warrantyRepository.findByProductId(productId);
//    }
//    
//    public List<Warranty> findActiveWarranties() {
//        return warrantyRepository.findActiveWarranties();
//    }
//    
//    public List<Warranty> findExpiredWarranties() {
//        return warrantyRepository.findExpiredWarranties();
//    }
//    
//    public List<Warranty> findWarrantiesAboutToExpire(int daysThreshold) {
//        return warrantyRepository.findWarrantiesAboutToExpire(daysThreshold);
//    }
//    
//    public List<Warranty> findUsedWarranties() {
//        return warrantyRepository.findUsedWarranties();
//    }
//    
//    public boolean linkToRepairService(Integer warrantyId, Integer repairServiceId) {
//        return warrantyRepository.linkToRepairService(warrantyId, repairServiceId);
//    }
//    
//    public boolean unlinkFromRepairService(Integer repairServiceId) {
//        return warrantyRepository.unlinkFromRepairService(repairServiceId);
//    }
//    
//    public boolean isUsed(Integer warrantyId) {
//        return warrantyRepository.isUsed(warrantyId);
//    }
//    
//    public List<Warranty> findByRepairServiceId(Integer repairServiceId) {
//        return warrantyRepository.findByRepairServiceId(repairServiceId);
//    }
//}