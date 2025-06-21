package com.pcstore.components.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.k33ptoo.components.KButton;
import com.pcstore.components.menu.mode.ToolBarAccentColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Raven
 */
public class Menu extends JPanel {
    private final List<MenuEvent> events = new ArrayList<>();
    private final Map<String, KButton> mainMenuItemsList = new HashMap<>();
    private boolean menuFull = true;
    private final String headerName = "HAL-Store";

    protected final boolean hideMenuTitleOnMinimum = true;
    protected final int menuTitleLeftInset = 5;
    protected final int menuTitleVgap = 5;
    protected final int menuMaxWidth = 250;
    protected final int menuMinWidth = 60;
    protected final int headerFullHgap = 5;
    protected ToolBarAccentColor toolBarAccentColor;


    private String menuItemsStr[][] = {
            // {"~MAIN~"},
            // {"Dashboard"},
            // {"~WEB APP~"},
            // {"Email", "Inbox", "Read", "Compost"},
            // {"Chat"},
            // {"Calendar"},
            // {"~COMPONENT~"},
            // {"Advanced UI", "Cropper", "Owl Carousel", "Sweet Alert"},
            // {"Forms", "Basic Elements", "Advanced Elements", "Editors", "Wizard"},
            // {"~OTHER~"},
            // {"Charts", "Apex", "Flot", "Peity", "Sparkline"},
            // {"Icons", "Feather Icons", "Flag Icons", "Mdi Icons"},
            // {"Special Pages", "Blank page", "Faq", "Invoice", "Profile", "Pricing", "Timeline"},
            // {"Logout"}
    };

    public void setMenuItemsStr(String[][] menuItemsStr) {
        this.menuItemsStr = menuItemsStr;
        panelMenu.removeAll();
        createMenu();
        revalidate();
        repaint();
    }

    public boolean isMenuFull() {
        return menuFull;
    }

    public void setMenuFull(boolean menuFull) {
        this.menuFull = menuFull;
        if (menuFull) {
            header.setText(headerName);
            header.setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? JLabel.LEFT : JLabel.RIGHT);
            header.setIcon(new FlatSVGIcon("com/pcstore/resources/icon/menu_left.svg", 40, 40));
        } else {
            header.setText("");
            header.setHorizontalAlignment(JLabel.CENTER);
            header.setIcon(new FlatSVGIcon("com/pcstore/resources/icon/menu_right.svg", 40, 40));

        }
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).setFull(menuFull);
            }
        }
        // lightDarkMode.setMenuFull(menuFull);
        toolBarAccentColor.setMenuFull(menuFull);
    }


    public Menu() {
        init();
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0)); // Hoàn toàn trong suốt
    }

    private void init() {
        setLayout(new MenuLayout());
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:20,2,2,2;"
                + "background:null;"
                + "arc:10");

        header = new KButton();
        header.setText(headerName);
        header.setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/logo.png")));
        header.setkBackGroundColor(new Color(0, 0, 0, 0));
        header.setkAllowGradient(false);
        header.setkHoverColor(new Color(192, 178, 178, 250));
        header.setkShowBorder(false);
        header.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.header.font;"
                + "foreground:$Menu.foreground");

        //  Menu
        scroll = new JScrollPane();
        panelMenu = new JPanel(new MenuItemLayout(this));
        panelMenu.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5;"
                + "background:null");
        panelMenu.setOpaque(false);

        scroll.setViewportView(panelMenu);
        scroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:null;"
                + "background:null");  // Thêm background null
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JScrollBar vscroll = scroll.getVerticalScrollBar();
        vscroll.setUnitIncrement(10);
        // vscroll.putClientProperty(FlatClientProperties.STYLE, ""
        //         + "width:$Menu.scroll.width;"
        //         + "trackInsets:$Menu.scroll.trackInsets;"
        //         + "thumbInsets:$Menu.scroll.thumbInsets;"
        //         + "background:$Menu.ScrollBar.background;"
        //         + "thumb:$Menu.ScrollBar.thumb");
        vscroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "width:0;"  // Set width to 0 to hide
                + "background:null;"
                + "track:null;"
                + "thumb:null");
        vscroll.setOpaque(false);
        createMenu();
        toolBarAccentColor = new ToolBarAccentColor(this);
        toolBarAccentColor.setVisible(FlatUIUtils.getUIBoolean("AccentControl.show", false));
        add(header);
        add(scroll);
        add(toolBarAccentColor);
    }

    private void createMenu() {
        int index = 0;
        for (int i = 0; i < menuItemsStr.length; i++) {
            String menuName = menuItemsStr[i][0];
            if (menuName.startsWith("~") && menuName.endsWith("~")) {
                panelMenu.add(createTitle(menuName));
            } else {
                MenuItem menuItem = new MenuItem(this, menuItemsStr[i], index++, events);
                menuItem.setOpaque(false);

                mainMenuItemsList.put(menuItem.getMainMenu().getName(), menuItem.getMainMenu());

                panelMenu.add(menuItem);
            }
        }
    }

    private JLabel createTitle(String title) {
        String menuName = title.substring(1, title.length() - 1);
        JLabel lbTitle = new JLabel(menuName);
        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.label.font;"
                + "foreground:$Menu.title.foreground");
        return lbTitle;
    }

    public void setSelectedMenu(int index, int subIndex) {
        runEvent(index, subIndex);
    }

    protected void setSelected(int index, int subIndex) {
        int size = panelMenu.getComponentCount();
        for (int i = 0; i < size; i++) {
            Component com = panelMenu.getComponent(i);
            if (com instanceof MenuItem) {
                MenuItem item = (MenuItem) com;
                if (item.getMenuIndex() == index) {
                    item.setSelectedIndex(subIndex);
                } else {
                    item.setSelectedIndex(-1);
                }
            }
        }
    }

    protected void runEvent(int index, int subIndex) {
        MenuAction menuAction = new MenuAction();
        for (MenuEvent event : events) {
            event.menuSelected(index, subIndex, menuAction);
        }
        if (!menuAction.isCancel()) {
            setSelected(index, subIndex);
        }
    }

    public void addMenuEvent(MenuEvent event) {
        events.add(event);
    }

    public void hideMenuItem() {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).hideMenuItem();
            }
        }
        revalidate();
    }

    public boolean isHideMenuTitleOnMinimum() {
        return hideMenuTitleOnMinimum;
    }

    public int getMenuTitleLeftInset() {
        return menuTitleLeftInset;
    }

    public int getMenuTitleVgap() {
        return menuTitleVgap;
    }

    public int getMenuMaxWidth() {
        return menuMaxWidth;
    }

    public int getMenuMinWidth() {
        return menuMinWidth;
    }

    private KButton header;
    private JScrollPane scroll;
    private JPanel panelMenu;
    // private LightDarkMode lightDarkMode;
    // private ToolBarAccentColor toolBarAccentColor;

    private class MenuLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int gap = UIScale.scale(5);
                int sheaderFullHgap = UIScale.scale(headerFullHgap);
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int iconWidth = width;
                int iconHeight = header.getPreferredSize().height;
                int hgap = menuFull ? sheaderFullHgap : 0;
                int accentColorHeight = 0;
                if (toolBarAccentColor.isVisible()) {
                    accentColorHeight = toolBarAccentColor.getPreferredSize().height + gap;
                }

                header.setBounds(x + hgap, y, iconWidth - (hgap * 2), iconHeight);
                int ldgap = UIScale.scale(10);
                int ldWidth = width - ldgap * 2;
                // int ldHeight = lightDarkMode.getPreferredSize().height;
                int ldx = x + ldgap;
                int ldy = y + height - ldgap - accentColorHeight;

                int menux = x;
                int menuy = y + iconHeight + gap;
                int menuWidth = width;
                int menuHeight = height - (iconHeight + gap) - (ldgap * 2) - (accentColorHeight);
                scroll.setBounds(menux, menuy, menuWidth, menuHeight);


                if (toolBarAccentColor.isVisible()) {
                    int tbheight = toolBarAccentColor.getPreferredSize().height;
                    int tbwidth = Math.min(toolBarAccentColor.getPreferredSize().width, ldWidth);
                    int tby = y + height - tbheight - ldgap;
                    int tbx = ldx + ((ldWidth - tbwidth) / 2);
                    toolBarAccentColor.setBounds(tbx, tby, tbwidth, tbheight);
                }
            }
        }
    }

    public Map<String, KButton> getMainMenuItemsList() {
        return mainMenuItemsList;
    }

    public JPanel getPanelMenu() {
        return panelMenu;
    }

    public KButton getHeader() {
        return header;
    }
}
