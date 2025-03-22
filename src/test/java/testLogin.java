import java.awt.Font;

import javax.swing.*;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import net.miginfocom.swing.MigLayout;

public class testLogin extends JFrame{
    
    public testLogin(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1346, 728);
        setLocationRelativeTo(null);
        setLayout(new MigLayout("al center center"));
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("login.themes");
        FlatMacLightLaf.setup();
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        new testLogin().setVisible(true);   
    }
}
