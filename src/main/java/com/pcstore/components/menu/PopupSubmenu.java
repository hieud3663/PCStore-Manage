package com.pcstore.components.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.k33ptoo.components.KButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;

/**
 * @author Raven
 */
public class PopupSubmenu extends JPanel {

    private final Menu menu;
    private final int menuIndex;
    private final int subMenuLeftGap = 20;
    private final int subMenuItemHeight = 30;
    private final String menus[];
    private JPopupMenu popup;

    public PopupSubmenu(ComponentOrientation orientation, Menu menu, int menuIndex, String menus[]) {
        this.menu = menu;
        this.menuIndex = menuIndex;
        this.menus = menus;
        applyComponentOrientation(orientation);
        init();
    }

    private void init() {
        setLayout(new MenuLayout());
        popup = new JPopupMenu();
        popup.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.background;"
                + "borderColor:$Menu.background;");
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:0,3,0,3;"
                + "foreground:$Menu.lineColor");
        popup.setBackground(new Color(201, 195, 195));
        for (int i = 1; i < menus.length; i++) {
            KButton button = createButtonItem(menus[i]);
            final int subIndex = i;
            button.addActionListener((ActionEvent e) -> {
                menu.runEvent(menuIndex, subIndex);
                popup.setVisible(false);
            });

            add(button);
        }
        popup.add(this);
    }

    private KButton createButtonItem(String text) {
        KButton button = new KButton();
        button.setText(text);
        button.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.background;"
                + "foreground:$Menu.foreground;"
                + "selectedBackground:$Menu.button.selectedBackground;"
                + "selectedForeground:$Menu.button.selectedForeground;"
                + "borderWidth:0;"
                + "arc:10;"
                + "focusWidth:0;"
                + "iconTextGap:10;"
                + "margin:5,11,5,11");
        button.setkShowBorder(false);
        button.setkSelectedColor(new Color(150, 159, 171));
        button.setkForeGround(new Color(0, 0, 0));
        button.setFont(new Font("Segoe UI", Font.PLAIN, UIScale.scale(14)));
        return button;
    }

    public void show(Component com, int x, int y) {
        if (menu.getComponentOrientation().isLeftToRight()) {
            popup.show(com, x, y);
        } else {
            int px = getPreferredSize().width + UIScale.scale(5);
            popup.show(com, -px, y);
        }
        applyAlignment();
        SwingUtilities.updateComponentTreeUI(popup);
    }

    private void applyAlignment() {
        setComponentOrientation(menu.getComponentOrientation());
        for (Component c : getComponents()) {
            if (c instanceof KButton) {
                KButton button = (KButton) c;
                button.setHorizontalAlignment(menu.getComponentOrientation().isLeftToRight() ? KButton.LEFT : KButton.RIGHT);
                button.setkHoverForeGround(new Color(63, 132, 235));

            }
        }
    }

    protected void setSelectedIndex(int index) {
        int size = getComponentCount();
        for (int i = 0; i < size; i++) {
            Component com = getComponent(i);
            if (com instanceof KButton) {
                boolean selected = i == index - 1;
                ((KButton) com).setSelected(selected);
                ((KButton) com).setkFillButton(selected);
                ((KButton) com).setkHoverForeGround(selected ? new Color(63, 132, 235) : getForeground());
                ((KButton) com).setkSelectedColor(selected ? new Color(150, 159, 171, 90) : getBackground());

            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
        int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
        Path2D.Double p = new Path2D.Double();
        int last = getComponent(getComponentCount() - 1).getY() + (ssubMenuItemHeight / 2);
        boolean ltr = getComponentOrientation().isLeftToRight();
        int round = UIScale.scale(10);
        int x = ltr ? (ssubMenuLeftGap - round) : (getWidth() - (ssubMenuLeftGap - round));
        p.moveTo(x, 0);
        p.lineTo(x, last - round);
        for (int i = 0; i < getComponentCount(); i++) {
            int com = getComponent(i).getY() + (ssubMenuItemHeight / 2);
            p.append(createCurve(round, x, com, ltr), false);
        }
        g2.setColor(getForeground());
        g2.setStroke(new BasicStroke(UIScale.scale(1f)));
        g2.draw(p);
        g2.dispose();
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
                Insets insets = parent.getInsets();
                int maxWidth = UIScale.scale(150);
                int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
                int width = getMaxWidth(parent) + ssubMenuLeftGap;
                int height = (insets.top + insets.bottom);
                int size = parent.getComponentCount();
                for (int i = 0; i < size; i++) {
                    Component com = parent.getComponent(i);
                    if (com.isVisible()) {
                        height += UIScale.scale(subMenuItemHeight);
                        width = Math.max(width, com.getPreferredSize().width);
                    }
                }
                width += insets.left + insets.right;
                return new Dimension(Math.max(width, maxWidth), height);
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
                int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
                int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
                int x = insets.left + (ltr ? ssubMenuLeftGap : 0);
                int y = insets.top;
                int width = getMaxWidth(parent);
                int size = parent.getComponentCount();
                for (int i = 0; i < size; i++) {
                    Component com = parent.getComponent(i);
                    if (com.isVisible()) {
                        com.setBounds(x, y, width, ssubMenuItemHeight);
                        y += ssubMenuItemHeight;
                    }
                }
            }
        }

        private int getMaxWidth(Container parent) {
            int size = parent.getComponentCount();
            int maxWidth = UIScale.scale(150);
            int max = 0;
            for (int i = 0; i < size; i++) {
                Component com = parent.getComponent(i);
                if (com.isVisible()) {
                    max = Math.max(max, com.getPreferredSize().width);
                }
            }
            return Math.max(max, maxWidth);
        }
    }
}
