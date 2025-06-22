package com.pcstore.model.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

/**
 * Class biểu diễn dữ liệu hiệu suất nhân viên cho analytics
 */
public class EmployeePerformanceData {
    private String employeeId;
    private String employeeName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalRevenue;
    private int totalInvoices;
    private BigDecimal averageInvoiceValue;
    private int customersServed;
    private BigDecimal growthRate; // Tỷ lệ tăng trưởng so với kỳ trước
    private int rank; // Xếp hạng trong kỳ
    
    // Constructor mặc định
    public EmployeePerformanceData() {}
    
    // Constructor đầy đủ
    public EmployeePerformanceData(String employeeId, String employeeName, 
                                 LocalDate periodStart, LocalDate periodEnd,
                                 BigDecimal totalRevenue, int totalInvoices,
                                 BigDecimal averageInvoiceValue, int customersServed) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalRevenue = totalRevenue;
        this.totalInvoices = totalInvoices;
        this.averageInvoiceValue = averageInvoiceValue;
        this.customersServed = customersServed;
        this.growthRate = BigDecimal.ZERO;
        this.rank = 0;
    }
    
    // Getters và Setters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public LocalDate getPeriodStart() {
        return periodStart;
    }
    
    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }
    
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }
    
    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public int getTotalInvoices() {
        return totalInvoices;
    }
    
    public void setTotalInvoices(int totalInvoices) {
        this.totalInvoices = totalInvoices;
    }
    
    public BigDecimal getAverageInvoiceValue() {
        return averageInvoiceValue;
    }
    
    public void setAverageInvoiceValue(BigDecimal averageInvoiceValue) {
        this.averageInvoiceValue = averageInvoiceValue;
    }
    
    public int getCustomersServed() {
        return customersServed;
    }
    
    public void setCustomersServed(int customersServed) {
        this.customersServed = customersServed;
    }
    
    public BigDecimal getGrowthRate() {
        return growthRate;
    }
    
    public void setGrowthRate(BigDecimal growthRate) {
        this.growthRate = growthRate;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
      // Phương thức tính toán tỷ lệ tăng trưởng
    public void calculateGrowthRate(BigDecimal previousPeriodRevenue) {
        if (previousPeriodRevenue != null && previousPeriodRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal difference = this.totalRevenue.subtract(previousPeriodRevenue);
            this.growthRate = difference.divide(previousPeriodRevenue, 4, java.math.RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));
        } else {
            this.growthRate = BigDecimal.ZERO;
        }
    }
    
    // Comparator để sắp xếp theo doanh thu
    public static Comparator<EmployeePerformanceData> byRevenue() {
        return (e1, e2) -> e2.getTotalRevenue().compareTo(e1.getTotalRevenue());
    }
    
    // Comparator để sắp xếp theo số hóa đơn
    public static Comparator<EmployeePerformanceData> byInvoiceCount() {
        return (e1, e2) -> Integer.compare(e2.getTotalInvoices(), e1.getTotalInvoices());
    }
    
    // Comparator để sắp xếp theo tỷ lệ tăng trưởng
    public static Comparator<EmployeePerformanceData> byGrowthRate() {
        return (e1, e2) -> e2.getGrowthRate().compareTo(e1.getGrowthRate());
    }
    
    @Override
    public String toString() {
        return String.format("EmployeePerformanceData{employeeId='%s', employeeName='%s', " +
                           "totalRevenue=%s, totalInvoices=%d, rank=%d, growthRate=%s%%}",
                           employeeId, employeeName, totalRevenue, totalInvoices, rank, growthRate);
    }
}
