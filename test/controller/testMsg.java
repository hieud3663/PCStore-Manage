import java.util.Properties;



public class testMsg {
    public static void main(String[] args) {
        Properties prop = new Properties(); 
        try {
            prop.load(testMsg.class.getResourceAsStream("msg.properties"));
            String value = prop.getProperty("error.notfound");
            System.out.println("Value: " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
