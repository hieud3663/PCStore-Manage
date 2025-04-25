import java.util.Properties;
import com.pcstore.utils.LocaleManager;

public class test {

    public static void main(String[] args) {
        Properties prop = LocaleManager.getInstance().getProperties();
        System.out.println(prop.getProperty("titleLogo"));
    }
}