package com.pcstore.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.enums.EmployeePositionEnum;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;

public class PayFormTest {


    

    public static void main(String[] args) {
        Customer customer = new Customer("Test Khách", "0987654321", null);
        
        Employee employee = new Employee("NV01", "Test Nhân Viên", "0123456789", null, EmployeePositionEnum.SALES);

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(1);
        invoice.setCustomer(customer);
        invoice.setEmployee(employee);
        invoice.setTotalAmount(new BigDecimal(1000000));
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setStatus(InvoiceStatusEnum.PAID);
        invoice.setPaymentMethod(PaymentMethodEnum.CASH);
        invoice.setInvoiceDetails(new ArrayList<>());
        
    }
    
}