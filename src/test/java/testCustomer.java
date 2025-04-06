import java.sql.Connection;

import com.pcstore.model.Customer;
import com.pcstore.repository.impl.CustomerRepository;
import com.pcstore.utils.DatabaseConnection;

public class testCustomer {
    
    private CustomerRepository customerRepository;
    Connection conn ;

    public void setUp() {
        // Initialize repository before each test
        conn = DatabaseConnection.getInstance().getConnection();
        customerRepository = new CustomerRepository(conn);
    }

    public void close(){
        // Close the connection after each test
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    

    public void AddNewCustomer() {
        // Create a new customer
        Customer customer = new Customer("Khách test", "0765432110", null);
        
        customer.setCustomerId(customerRepository.generateCustomerId());

        System.out.println("Customer ID: " + customer.getCustomerId());

        
        try {
            // Add customer to repository
            Customer result = customerRepository.add(customer);  
            System.out.println("Customer added: " + result.getFullName());
            System.out.println("Customer ID: " + result.getCustomerId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testCustomer test = new testCustomer();
        test.setUp();
        test.AddNewCustomer();


        test.close();
    }
    

    
}