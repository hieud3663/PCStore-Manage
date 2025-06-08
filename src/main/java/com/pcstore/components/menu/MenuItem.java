package com.pcstore.components.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import com.k33ptoo.components.KButton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * @author Raven
 */
public class MenuItem extends JPanel {

    public boolean isMenuShow() {
        return menuShow;
    }

    public void setMenuShow(boolean menuShow) {
        this.menuShow = menuShow;
    }

    public float getAnimate() {
        return animate;
    }

    public void setAnimate(float animate) {
        this.animate = animate;
    }

    public String[] getMenus() {
        return menus;
    }

    public int getMenuIndex() {
        return menuIndex;
    }

    private final List<MenuEvent> events;
    private KButton mainMenu;
    private final Menu menu;
    private final String menus[];
    private final int menuIndex;
    private final int menuItemHeight = 38;
    private final int subMenuItemHeight = 35;
    private final int subMenuLeftGap = 34;
    private final int firstGap = 5;
    private final int bottomGap = 5;
    private boolean menuShow;
    private float animate;

    private PopupSubmenu popup;

    public MenuItem(Menu menu, String menus[], int menuIndex, List<MenuEvent> events) {
        this.menu = menu;
        this.menus = menus;
        this.menuIndex = menuIndex;
        this.events = events;
        init();
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));  // Đặt màu nền hoàn toàn trong suốt
    }

    private Icon getIcon() {

        FlatSVGIcon icon = new FlatSVGIcon("com/pcstore/components/menu/icon/" + menuIndex + ".svg");
        return icon;
    }

    private void init() {
        setLayout(new MenuLayout());
        putClientProperty(FlatClientProperties.STYLE, ""
                + "foreground:$Menu.lineColor");
        for (int i = 0; i < menus.length; i++) {
            KButton menuItem = createButtonItem(menus[i]);
            menuItem.setHorizontalAlignment(menuItem.getComponentOrientation().isLeftToRight() ? KButton.LEADING : KButton.TRAILING);
            if (i == 0) {
                mainMenu = menuItem;
                menuItem.setIcon(getIcon());
                menuItem.addActionListener((ActionEvent e) -> {
                    if (menus.length > 1) {
                        if (menu.isMenuFull()) {
                            MenuAnimation.animate(MenuItem.this, !menuShow);
                        } else {
                            popup.show(MenuItem.this, (int) MenuItem.this.getWidth() + UIScale.scale(5), UIScale.scale(menuItemHeight) / 2);
                        }
                    } else {
                        menu.runEvent(menuIndex, 0);
                    }
                });

                menuItem.addMouseListener(new MouseAdapter(){
                    @Override
                    public void mouseEntered(MouseEvent e){
                        if (menus.length > 1 && !menu.isMenuFull()){
                            popup.show(MenuItem.this, (int) MenuItem.this.getWidth() + UIScale.scale(5), UIScale.scale(menuItemHeight) / 2);
                        }
                    }
                    // @Override
                    // public void mouseExited(MouseEvent e){
                    //     if (menus.length > 1 && !menu.isMenuFull()){
                    //         popup.setVisible(false);
                    //     }
                    // }
                });
            } else {
                final int subIndex = i;
                menuItem.addActionListener((ActionEvent e) -> {
                    menu.runEvent(menuIndex, subIndex);
                });
            }
            add(menuItem);
        }
        popup = new PopupSubmenu(getComponentOrientation(), menu, menuIndex, menus);
    }

    protected void setSelectedIndex(int index) {
        int size = getComponentCount();
        boolean selected = false;
        for (int i = 0; i < size; i++) {
            Component com = getComponent(i);
            if (com instanceof KButton) {
                if (i == index) {
                    selected = true;
                }
                boolean selectedSub = (i == index);
                ((KButton) com).setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? KButton.LEFT : KButton.RIGHT);
                ((KButton) com).setSelected(selectedSub);
                ((KButton) com).setkFillButton(selectedSub);
                ((KButton) getComponent(0)).setkHoverForeGround(selected ? new Color(63, 132, 235) : getForeground());
                ((KButton) com).setkSelectedColor(selectedSub ? new Color(150, 159, 171, 150) : getBackground());
            }
        }
        ((KButton) getComponent(0)).setSelected(selected);
        ((KButton) getComponent(0)).setkFillButton(selected);
        ((KButton) getComponent(0)).setkHoverForeGround(selected ? new Color(63, 132, 235) : getForeground());
        ((KButton) getComponent(0)).setkSelectedColor(selected ? new Color(150, 159, 171, 50) : getBackground());

        popup.setSelectedIndex(index);
    }

    private KButton createButtonItem(String text) {
        KButton button = new KButton();
        button.setText(text);
        button.putClientProperty(FlatClientProperties.STYLE, ""
                + "foreground:$Menu.foreground;"
                + "selectedBackground:$Menu.button.selectedBackground;"
                + "selectedForeground:$Menu.button.selectedForeground;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "arc:10;"
                + "iconTextGap:10;"
                + "margin:3,11,3,11");
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setkFillButton(false);
        button.setkShowBorder(false);
        button.setkHoverForeGround(new Color(63, 132, 235));
        button.setkSelectedColor(new Color(150, 159, 171));
        return button;
    }

    public void hideMenuItem() {
        animate = 0;
        menuShow = false;
    }

    public void setFull(boolean full) {
        if (full) {
            int size = getComponentCount();
            for (int i = 0; i < size; i++) {
                Component com = getComponent(i);
                if (com instanceof KButton) {
                    KButton button = (KButton) com;
                    button.setText(menus[i]);
                    button.setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? KButton.LEFT : KButton.RIGHT);
                }
            }
        } else {
            for (Component com : getComponents()) {
                if (com instanceof KButton) {
                    KButton button = (KButton) com;
                    button.setText("");
                    button.setHorizontalAlignment(KButton.CENTER);
                }
            }
            animate = 0f;
            menuShow = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Don't call super.paintComponent to avoid background painting
        // Only paint the menu line if needed
        if (animate > 0) {
            int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
            int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
            int smenuItemHeight = UIScale.scale(menuItemHeight);
            int sfirstGap = UIScale.scale(firstGap);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Path2D.Double p = new Path2D.Double();
            int last = getComponent(getComponentCount() - 1).getY() + (ssubMenuItemHeight / 2);
            boolean ltr = getComponentOrientation().isLeftToRight();
            int round = UIScale.scale(10);
            int x = ltr ? (ssubMenuLeftGap - round) : (getWidth() - (ssubMenuLeftGap - round));
            p.moveTo(x, smenuItemHeight + sfirstGap);
            p.lineTo(x, last - round);
            for (int i = 1; i < getComponentCount(); i++) {
                int com = getComponent(i).getY() + (ssubMenuItemHeight / 2);
                p.append(createCurve(round, x, com, ltr), false);
            }
            g2.setColor(getForeground());
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setStroke(new BasicStroke(UIScale.scale(1f)));
            g2.draw(p);
            g2.dispose();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (menus.length > 1) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(FlatUIUtils.getUIColor("Menu.arrowColor", getForeground()));
            int smenuItemHeight = UIScale.scale(menuItemHeight);
            boolean ltr = getComponentOrientation().isLeftToRight();
            g2.setStroke(new BasicStroke(UIScale.scale(1f)));
            if (menu.isMenuFull()) {
                int arrowWidth = UIScale.scale(10);
                int arrowHeight = UIScale.scale(5);
                int ax = ltr ? (getWidth() - arrowWidth * 2) : arrowWidth;
                int ay = (smenuItemHeight - arrowHeight) / 2;
                Path2D p = new Path2D.Double();
                p.moveTo(0, animate * arrowHeight);
                p.lineTo(arrowWidth / 2, (1f - animate) * arrowHeight);
                p.lineTo(arrowWidth, animate * arrowHeight);
                g2.translate(ax, ay);
                g2.draw(p);
            } else {
                int arrowWidth = UIScale.scale(4);
                int arrowHeight = UIScale.scale(8);
                int ax = ltr ? (getWidth() - arrowWidth - UIScale.scale(3)) : UIScale.scale(3);
                int ay = (smenuItemHeight - arrowHeight) / 2;
                Path2D p = new Path2D.Double();
                if (ltr) {
                    p.moveTo(0, 0);
                    p.lineTo(arrowWidth, arrowHeight / 2);
                    p.lineTo(0, arrowHeight);
                } else {
                    p.moveTo(arrowWidth, 0);
                    p.lineTo(0, arrowHeight / 2);
                    p.lineTo(arrowWidth, arrowHeight);
                }
                g2.translate(ax, ay);
                g2.draw(p);
            }
            g2.dispose();
        }
    }

    private Shape createCurve(int round, int x, int y, boolean ltr) {
        Path2D p2 = new Path2D.Double();
        p2.moveTo(x, y - round);
        p2.curveTo(x, y - round, x, y, x + (ltr ? round : -round), y);
        return p2;
    }

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
                Insets inset = parent.getInsets();
                int width = parent.getWidth();
                int height = inset.top + inset.bottom;
                int size = parent.getComponentCount();
                Component item = parent.getComponent(0);
                height += UIScale.scale(menuItemHeight);
                if (item.isVisible()) {
                    int subMenuHeight = size > 1 ? UIScale.scale(firstGap) + UIScale.scale(bottomGap) : 0;
                    for (int i = 1; i < size; i++) {
                        Component com = parent.getComponent(i);
                        if (com.isVisible()) {
                            subMenuHeight += UIScale.scale(subMenuItemHeight);
                        }
                    }
                    height += (subMenuHeight * animate);
                } else {
                    height = 0;
                }
                return new Dimension(width, height);
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
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int size = parent.getComponentCount();
                for (int i = 0; i < size; i++) {
                    Component com = parent.getComponent(i);
                    if (com.isVisible()) {
                        if (i == 0) {
                            int smenuItemHeight = UIScale.scale(menuItemHeight);
                            int sfirstGap = UIScale.scale(firstGap);
                            com.setBounds(x, y, width, smenuItemHeight);
                            y += smenuItemHeight + sfirstGap;
                        } else {
                            int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
                            int subMenuX = ltr ? ssubMenuLeftGap : 0;
                            int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
                            com.setBounds(x + subMenuX, y, width - ssubMenuLeftGap, ssubMenuItemHeight);
                            y += ssubMenuItemHeight;
                        }
                    }
                }
            }
        }
    }

    public KButton getMainMenu() {
        return mainMenu;
    }
}
