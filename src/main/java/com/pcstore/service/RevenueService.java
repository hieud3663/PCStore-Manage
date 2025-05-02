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

import com.pcstore.repository.iRevenueRepository;
import com.pcstore.repository.impl.RevenueRepository;

/**
 * Implementation of RevenueService interface for handling revenue-related operations
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
     * @param toDate End date
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
     * @param fromDate Start date
     * @param toDate End date
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
     * @param fromDate Start date
     * @param toDate End date
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
     * @param limit Number of top products to retrieve
     * @param fromDate Start date
     * @param toDate End date
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
     * @param endDate the end date
     * @return list of maps containing employee revenue data
     */
    public List<Map<String, Object>> getEmployeeRevenueData(LocalDateTime startDate, LocalDateTime endDate) {
        return revenueRepository.getEmployeeRevenueData(startDate, endDate);
    }

    /**
     * Get revenue for a specific employee within a date range
     * 
     * @param employeeId the employee ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the revenue amount
     */
    public BigDecimal getRevenueByEmployee(String employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return revenueRepository.getRevenueByEmployee(employeeId, startDate, endDate);
    }

    /**
     * Get the number of sales made by a specific employee within a date range
     * 
     * @param employeeId the employee ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the number of sales
     */
    public int getNumberOfSalesByEmployee(String employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return revenueRepository.getNumberOfSalesByEmployee(employeeId, startDate, endDate);
    }

    /**
     * Get the top performing employees based on revenue within a date range
     * 
     * @param limit the maximum number of employees to return
     * @param startDate the start date
     * @param endDate the end date
     * @return list of maps containing top employee data
     */
    public List<Map<String, Object>> getTopPerformingEmployees(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        return revenueRepository.getTopPerformingEmployees(limit, startDate, endDate);
    }

    /**
     * Lấy dữ liệu doanh thu theo ngày của một nhân viên cụ thể
     * @param employeeId ID của nhân viên
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Danh sách dữ liệu doanh thu theo ngày
     */
    public List<Map<String, Object>> getEmployeeDailyRevenueData(String employeeId,  LocalDateTime fromDate, LocalDateTime toDate) {
        return revenueRepository.getEmployeeDailyRevenueData(employeeId, fromDate, toDate);
    }

    /**
     * Lấy dữ liệu doanh thu theo ngày trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
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
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
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
}