package com.pcstore.model.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class xử lý xếp hạng nhân viên theo các tiêu chí khác nhau
 */
public class EmployeeRanking {
    
    public enum RankingCriteria {
        REVENUE("Doanh thu"),
        INVOICE_COUNT("Số lượng hóa đơn"),
        AVERAGE_INVOICE_VALUE("Giá trị hóa đơn trung bình"),
        CUSTOMERS_SERVED("Số khách hàng phục vụ"),
        GROWTH_RATE("Tỷ lệ tăng trưởng");
        
        private final String displayName;
        
        RankingCriteria(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private List<EmployeePerformanceData> employees;
    private RankingCriteria criteria;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    
    public EmployeeRanking() {
        this.employees = new ArrayList<>();
    }    public EmployeeRanking(List<EmployeePerformanceData> employees, RankingCriteria criteria,
                          LocalDate periodStart, LocalDate periodEnd) {
        this.employees = new ArrayList<>(employees);
        this.criteria = criteria;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }
    
    /**
     * Khởi tạo và tính toán ranking
     */
    public static EmployeeRanking createWithRanking(List<EmployeePerformanceData> employees, 
                                                   RankingCriteria criteria,
                                                   LocalDate periodStart, LocalDate periodEnd) {
        EmployeeRanking ranking = new EmployeeRanking(employees, criteria, periodStart, periodEnd);
        ranking.calculateRanking();
        return ranking;
    }
    
    /**
     * Tính toán xếp hạng dựa trên tiêu chí đã chọn
     */
    public void calculateRanking() {
        if (employees == null || employees.isEmpty() || criteria == null) {
            return;
        }
        
        Comparator<EmployeePerformanceData> comparator = getComparatorForCriteria(criteria);
        employees.sort(comparator);
        
        // Gán rank cho từng nhân viên
        for (int i = 0; i < employees.size(); i++) {
            employees.get(i).setRank(i + 1);
        }
    }
    
    /**
     * Lấy comparator tương ứng với tiêu chí xếp hạng
     */
    private Comparator<EmployeePerformanceData> getComparatorForCriteria(RankingCriteria criteria) {
        switch (criteria) {
            case REVENUE:
                return EmployeePerformanceData.byRevenue();
            case INVOICE_COUNT:
                return EmployeePerformanceData.byInvoiceCount();
            case AVERAGE_INVOICE_VALUE:
                return (e1, e2) -> e2.getAverageInvoiceValue().compareTo(e1.getAverageInvoiceValue());
            case CUSTOMERS_SERVED:
                return (e1, e2) -> Integer.compare(e2.getCustomersServed(), e1.getCustomersServed());
            case GROWTH_RATE:
                return EmployeePerformanceData.byGrowthRate();
            default:
                return EmployeePerformanceData.byRevenue();
        }
    }
    
    /**
     * Lấy top N nhân viên
     */
    public List<EmployeePerformanceData> getTopEmployees(int n) {
        int limit = Math.min(n, employees.size());
        return new ArrayList<>(employees.subList(0, limit));
    }
    
    /**
     * Lấy nhân viên theo rank
     */
    public EmployeePerformanceData getEmployeeByRank(int rank) {
        if (rank > 0 && rank <= employees.size()) {
            return employees.get(rank - 1);
        }
        return null;
    }
    
    /**
     * Tìm rank của nhân viên theo ID
     */
    public int getEmployeeRank(String employeeId) {
        for (EmployeePerformanceData emp : employees) {
            if (emp.getEmployeeId().equals(employeeId)) {
                return emp.getRank();
            }
        }
        return -1; // Không tìm thấy
    }
    
    /**
     * Lấy danh sách tất cả nhân viên đã xếp hạng
     */
    public List<EmployeePerformanceData> getAllRankedEmployees() {
        return new ArrayList<>(employees);
    }
    
    /**
     * Thống kê tổng quan
     */
    public RankingSummary getSummary() {
        if (employees.isEmpty()) {
            return new RankingSummary();
        }
        
        RankingSummary summary = new RankingSummary();
        summary.totalEmployees = employees.size();
        summary.topPerformer = employees.get(0);
        summary.worstPerformer = employees.get(employees.size() - 1);
        
        // Tính toán các thống kê
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalInvoices = 0;
        int totalCustomers = 0;
        
        for (EmployeePerformanceData emp : employees) {
            totalRevenue = totalRevenue.add(emp.getTotalRevenue());
            totalInvoices += emp.getTotalInvoices();
            totalCustomers += emp.getCustomersServed();
        }
        
        summary.totalRevenue = totalRevenue;
        summary.totalInvoices = totalInvoices;
        summary.totalCustomers = totalCustomers;        summary.averageRevenue = totalRevenue.divide(
            new BigDecimal(employees.size()), 2, RoundingMode.HALF_UP);
        
        return summary;
    }
    
    // Getters and Setters
    public List<EmployeePerformanceData> getEmployees() {
        return employees;
    }
    
    public void setEmployees(List<EmployeePerformanceData> employees) {
        this.employees = employees;
        calculateRanking();
    }
    
    public RankingCriteria getCriteria() {
        return criteria;
    }
    
    public void setCriteria(RankingCriteria criteria) {
        this.criteria = criteria;
        calculateRanking();
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
    
    /**
     * Class chứa thống kê tổng quan của ranking
     */
    public static class RankingSummary {
        public int totalEmployees;
        public EmployeePerformanceData topPerformer;
        public EmployeePerformanceData worstPerformer;
        public BigDecimal totalRevenue;
        public int totalInvoices;
        public int totalCustomers;
        public BigDecimal averageRevenue;
    }
}
