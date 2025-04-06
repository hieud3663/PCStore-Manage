import com.pcstore.model.enums.RepairEnum;

public class test {

    public static void main(String[] args) {
        String status = RepairEnum.CANCELLED.getStatus();
        System.out.println(status); // Output: CANCELLED
    }
}