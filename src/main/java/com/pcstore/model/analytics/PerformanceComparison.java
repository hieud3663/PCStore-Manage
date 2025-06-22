package com.pcstore.model.analytics;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class biểu diễn kết quả so sánh hiệu suất nhân viên giữa các kỳ
 */
public class PerformanceComparison {
    private EmployeePerformanceData currentPeriod;
    private EmployeePerformanceData previousPeriod;
    private ComparisonMetrics metrics;
    
    public PerformanceComparison() {}
    
    public PerformanceComparison(EmployeePerformanceData currentPeriod, 
                               EmployeePerformanceData previousPeriod) {
        this.currentPeriod = currentPeriod;
        this.previousPeriod = previousPeriod;
        this.metrics = new ComparisonMetrics();
        calculateMetrics();
    }
    
    private void calculateMetrics() {
        if (currentPeriod != null && previousPeriod != null) {
            // Tính toán các metrics so sánh
            metrics.revenueChange = currentPeriod.getTotalRevenue()
                                  .subtract(previousPeriod.getTotalRevenue());
            
            metrics.invoiceCountChange = currentPeriod.getTotalInvoices() 
                                       - previousPeriod.getTotalInvoices();
            
            metrics.averageInvoiceChange = currentPeriod.getAverageInvoiceValue()
                                         .subtract(previousPeriod.getAverageInvoiceValue());
            
            metrics.customerCountChange = currentPeriod.getCustomersServed() 
                                        - previousPeriod.getCustomersServed();
              // Tính tỷ lệ phần trăm thay đổi
            if (previousPeriod.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
                metrics.revenueGrowthPercent = metrics.revenueChange
                    .divide(previousPeriod.getTotalRevenue(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }
            
            if (previousPeriod.getTotalInvoices() > 0) {
                metrics.invoiceGrowthPercent = new BigDecimal(metrics.invoiceCountChange)
                    .divide(new BigDecimal(previousPeriod.getTotalInvoices()), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            }
        }
    }
    
    public EmployeePerformanceData getCurrentPeriod() {
        return currentPeriod;
    }
    
    public void setCurrentPeriod(EmployeePerformanceData currentPeriod) {
        this.currentPeriod = currentPeriod;
        calculateMetrics();
    }
    
    public EmployeePerformanceData getPreviousPeriod() {
        return previousPeriod;
    }
    
    public void setPreviousPeriod(EmployeePerformanceData previousPeriod) {
        this.previousPeriod = previousPeriod;
        calculateMetrics();
    }
    
    public ComparisonMetrics getMetrics() {
        return metrics;
    }
    
    public boolean isImproved() {
        return metrics != null && metrics.revenueChange.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isDeclined() {
        return metrics != null && metrics.revenueChange.compareTo(BigDecimal.ZERO) < 0;
    }
    
    public boolean isStable() {
        return metrics != null && metrics.revenueChange.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Class chứa các metrics so sánh
     */
    public static class ComparisonMetrics {
        public BigDecimal revenueChange = BigDecimal.ZERO;
        public int invoiceCountChange = 0;
        public BigDecimal averageInvoiceChange = BigDecimal.ZERO;
        public int customerCountChange = 0;
        public BigDecimal revenueGrowthPercent = BigDecimal.ZERO;
        public BigDecimal invoiceGrowthPercent = BigDecimal.ZERO;
        
        public String getRevenueChangeFormatted() {
            String prefix = revenueChange.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            return prefix + revenueChange.toString();
        }
        
        public String getInvoiceChangeFormatted() {
            String prefix = invoiceCountChange >= 0 ? "+" : "";
            return prefix + invoiceCountChange;
        }
        
        public String getRevenueGrowthFormatted() {
            String prefix = revenueGrowthPercent.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            return prefix + revenueGrowthPercent.toString() + "%";
        }
        
        public String getInvoiceGrowthFormatted() {
            String prefix = invoiceGrowthPercent.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            return prefix + invoiceGrowthPercent.toString() + "%";
        }
    }
}
