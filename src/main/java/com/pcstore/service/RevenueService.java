package com.pcstore.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pcstore.repository.impl.RevenueRepository;

/**
 * Implementation of RevenueService interface for handling revenue-related
 * operations
 */
public class RevenueService {
    private final RevenueRepository revenueRepository;
    private static final Logger logger = Logger.getLogger(RevenueService.class.getName());

    /**
     * Constructor with repository parameter
     * 
     * @param revenueRepository Revenue repository implementation
     */
    public RevenueService(RevenueRepository revenueRepository) {
        this.revenueRepository = revenueRepository;
    }

    /**
     * Constructor with database connection (for specific connection contexts)
     * 
     * @param connection Database connection
     */
    public RevenueService(Connection connection) {
        this.revenueRepository = new RevenueRepository(connection);
    }

    /**
     * Get revenue data for products within a date range
     * 
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of maps containing revenue data
     */

    public List<Map<String, Object>> getRevenueData(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        return revenueRepository.getRevenueData(fromDateTime, toDateTime);
    }

    /**
     * Get revenue for a specific product within a date range
     * 
     * @param productId Product ID
     * @param fromDate  Start date
     * @param toDate    End date
     * @return Total revenue for the product
     */

    public BigDecimal getRevenueByProduct(String productId, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        return revenueRepository.getRevenueByProduct(productId, fromDateTime, toDateTime);
    }

    /**
     * Get quantity sold for a specific product within a date range
     * 
     * @param productId Product ID
     * @param fromDate  Start date
     * @param toDate    End date
     * @return Total quantity sold for the product
     */

    public int getQuantitySoldByProduct(String productId, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        return revenueRepository.getQuantitySoldByProduct(productId, fromDateTime, toDateTime);
    }

    /**
     * Get top selling products within a date range
     * 
     * @param limit    Number of top products to retrieve
     * @param fromDate Start date
     * @param toDate   End date
     * @return List of maps containing top selling products data
     */

    public List<Map<String, Object>> getTopSellingProducts(int limit, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        return revenueRepository.getTopSellingProducts(limit, fromDateTime, toDateTime);
    }

    /**
     * Calculate total revenue from a list of revenue data
     * 
     * @param revenueData List of maps containing revenue data
     * @return Total revenue
     */

    public BigDecimal calculateTotalRevenue(List<Map<String, Object>> revenueData) {
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Map<String, Object> data : revenueData) {
            BigDecimal revenue = (BigDecimal) data.get("revenue");
            if (revenue != null) {
                totalRevenue = totalRevenue.add(revenue);
            }
        }

        return totalRevenue;
    }

    // Employee revenue methods

    /**
     * Get revenue data for all employees within a specified date range
     * 
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of maps containing employee revenue data
     */
    public List<Map<String, Object>> getEmployeeRevenueData(LocalDateTime startDate, LocalDateTime endDate) {
        return revenueRepository.getEmployeeRevenueData(startDate, endDate);
    }

    /**
     * Get revenue for a specific employee within a date range
     * 
     * @param employeeId the employee ID
     * @param startDate  the start date
     * @param endDate    the end date
     * @return the revenue amount
     */
    public BigDecimal getRevenueByEmployee(String employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return revenueRepository.getRevenueByEmployee(employeeId, startDate, endDate);
    }

    /**
     * Get the number of sales made by a specific employee within a date range
     * 
     * @param employeeId the employee ID
     * @param startDate  the start date
     * @param endDate    the end date
     * @return the number of sales
     */
    public int getNumberOfSalesByEmployee(String employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return revenueRepository.getNumberOfSalesByEmployee(employeeId, startDate, endDate);
    }

    /**
     * Get the top performing employees based on revenue within a date range
     * 
     * @param limit     the maximum number of employees to return
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of maps containing top employee data
     */
    public List<Map<String, Object>> getTopPerformingEmployees(int limit, LocalDateTime startDate,
            LocalDateTime endDate) {
        return revenueRepository.getTopPerformingEmployees(limit, startDate, endDate);
    }

    /**
     * Lấy dữ liệu doanh thu theo ngày của một nhân viên cụ thể
     * 
     * @param employeeId ID của nhân viên
     * @param fromDate   Ngày bắt đầu
     * @param toDate     Ngày kết thúc
     * @return Danh sách dữ liệu doanh thu theo ngày
     */
    public List<Map<String, Object>> getEmployeeDailyRevenueData(String employeeId, LocalDateTime fromDate,
            LocalDateTime toDate) {
        return revenueRepository.getEmployeeDailyRevenueData(employeeId, fromDate, toDate);
    }

    /**
     * Lấy dữ liệu doanh thu theo ngày trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách dữ liệu doanh thu theo ngày
     */
    public List<Map<String, Object>> getDailyRevenueData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return revenueRepository.getDailyRevenueData(startDate, endDate);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu doanh thu theo ngày", e);
            return new ArrayList<>();
        }
    }

    /**
     * Tính tổng doanh thu trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Tổng doanh thu
     */
    public BigDecimal getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return revenueRepository.calculateTotalRevenueByDateRange(startDate, endDate);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tính tổng doanh thu trong khoảng thời gian", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Lấy dữ liệu doanh thu nhân viên theo khoảng thời gian cho analytics
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Map với key là employeeId và value là Map chứa thông tin nhân viên và
     *         doanh thu
     */
    public Map<String, Map<String, Object>> getEmployeeRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return revenueRepository.getEmployeeRevenueByDateRange(startDate, endDate);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu doanh thu nhân viên theo khoảng thời gian", e);
            return new java.util.LinkedHashMap<>();
        }
    }

    /**
     * Lấy sản phẩm bán chạy nhất trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Map chứa thông tin sản phẩm bán chạy nhất
     */
    public Map<String, Object> getBestSellingProduct(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime fromDateTime = startDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = endDate.atTime(LocalTime.MAX);

            return revenueRepository.getBestSellingProduct(fromDateTime, toDateTime);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy sản phẩm bán chạy nhất", e);
            return new java.util.HashMap<>();
        }
    }

    /**
     * Lấy sản phẩm bán chậm nhất trong khoảng thời gian (có doanh thu > 0)
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Map chứa thông tin sản phẩm bán chậm nhất
     */
    public Map<String, Object> getSlowSellingProduct(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime fromDateTime = startDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = endDate.atTime(LocalTime.MAX);

            return revenueRepository.getSlowSellingProduct(fromDateTime, toDateTime);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy sản phẩm bán chậm nhất", e);
            e.printStackTrace();
            return new java.util.HashMap<>();
        }
    }

    /**
     * Tính tỷ suất lợi nhuận tổng trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Tỷ suất lợi nhuận (%)
     */
    public BigDecimal calculateProfitMargin(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime fromDateTime = startDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = endDate.atTime(LocalTime.MAX);

            return revenueRepository.calculateProfitMargin(fromDateTime, toDateTime);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tính tỷ suất lợi nhuận", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Tính lợi nhuận tổng trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Lợi nhuận tổng   
     */
    public Map<String, Object> calculateTotalProfit(LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime fromDateTime = startDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = endDate.atTime(LocalTime.MAX);

            return revenueRepository.calculateTotalProfit(fromDateTime, toDateTime);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi tính lợi nhuận tổng", e);
            return Map.of("totalProfit", BigDecimal.ZERO);
        }
    }




    /**
     * Lấy danh sách top sản phẩm bán chạy với thông tin chi tiết
     * 
     * @param limit     Số lượng sản phẩm
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách sản phẩm bán chạy
     */
    public List<Map<String, Object>> getTopBestSellingProducts(int limit, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime fromDateTime = startDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = endDate.atTime(LocalTime.MAX);

            return revenueRepository.getTopBestSellingProducts(limit, fromDateTime, toDateTime);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm bán chạy", e);
            return new ArrayList<>();
        }
    }

    /**
     * Lấy danh sách sản phẩm bán chậm với thông tin chi tiết
     * 
     * @param limit     Số lượng sản phẩm
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách sản phẩm bán chậm
     */
    public List<Map<String, Object>> getSlowSellingProducts(int limit, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime fromDateTime = startDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = endDate.atTime(LocalTime.MAX);

            return revenueRepository.getSlowSellingProducts(limit, fromDateTime, toDateTime);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm bán chậm", e);
            return new ArrayList<>();
        }
    }


    /*
     * Báo cáo hóa đơn doanh thu
     */
    public List<Map<String, Object>> getInvoiceStatistics(LocalDateTime startDate, LocalDateTime endDate){
        try {
            return revenueRepository.getInvoiceStatistics(startDate, endDate);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy thống kê hóa đơn", e);
            return new ArrayList<>();
        }
    }
}