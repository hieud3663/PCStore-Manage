package com.pcstore.utils;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.k33ptoo.components.*;
import javax.swing.border.Border;

/**
 * Lớp tiện ích để xử lý các thao tác phổ biến với JButton
 * như thay đổi màu sắc khi enable/disable
 */
public class ButtonUtils {
    // Màu mặc định cho disable state
    private static final Color DEFAULT_DISABLED_BG = new Color(220, 220, 220); // Xám nhạt
    private static final Color DEFAULT_DISABLED_FG = new Color(120, 120, 120); // Xám đậm
    

    
    /**
     * Thiết lập trạng thái của button, giữ lại màu hiện tại khi enable
     * @param button Button cần thiết lập
     * @param enabled trạng thái enable/disable
     */
    public static void setButtonEnabled(JButton button, boolean enabled) {
        // Lấy màu hiện tại của button để sử dụng khi enabled
        Color currentBg = button.getBackground();
        Color currentFg = button.getForeground();
        
        setButtonEnabled(button, enabled, currentBg, currentFg, 
                DEFAULT_DISABLED_BG, DEFAULT_DISABLED_FG);
    }
    
    /**
     * Thiết lập trạng thái của button với màu tuỳ chỉnh
     * @param button Button cần thiết lập
     * @param enabled trạng thái enable/disable
     * @param enabledBg Màu nền khi enabled
     * @param enabledFg Màu chữ khi enabled
     * @param disabledBg Màu nền khi disabled
     * @param disabledFg Màu chữ khi disabled
     */
    public static void setButtonEnabled(JButton button, boolean enabled, 
            Color enabledBg, Color enabledFg, Color disabledBg, Color disabledFg) {
        // Lưu trạng thái màu sắc hiện tại nếu chưa được lưu
        if (button.getClientProperty("original_bg_color") == null) {
            button.putClientProperty("original_bg_color", button.getBackground());
            button.putClientProperty("original_fg_color", button.getForeground());
        }
        
        button.setEnabled(enabled);
        
        if (enabled) {
            // Sử dụng màu đã lưu hoặc màu được truyền vào
            Color bgToUse = (button.getClientProperty("original_bg_color") != null) ?
                    (Color)button.getClientProperty("original_bg_color") : enabledBg;
            Color fgToUse = (button.getClientProperty("original_fg_color") != null) ?
                    (Color)button.getClientProperty("original_fg_color") : enabledFg;
                    
            button.setBackground(bgToUse);
            button.setForeground(fgToUse);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            button.setBackground(disabledBg);
            button.setForeground(disabledFg);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    /**
     * Thiết lập trạng thái của KButton, giữ lại màu hiện tại khi enable
     * @param button KButton cần thiết lập
     * @param enabled trạng thái enable/disable
     */
    public static void setKButtonEnabled(KButton button, boolean enabled) {
        // Lưu trạng thái màu sắc khi lần đầu disable
        if (button.getClientProperty("original_colors") == null) {
            // Lưu toàn bộ màu sắc hiện tại vào một mảng
            Color[] colors = new Color[] {
                button.kBackGroundColor,
                button.kForeGround,
                button.kStartColor,
                button.kEndColor,
                button.kHoverStartColor,
                button.kHoverEndColor,
                button.kHoverColor
            };
            button.putClientProperty("original_colors", colors);
        }
        
        // Thiết lập trạng thái enabled/disabled
        button.setEnabled(enabled);
        
        if (enabled) {
            Color[] colors = (Color[]) button.getClientProperty("original_colors");
            if (colors != null) {
                button.setkBackGroundColor(colors[0]);
                button.setkForeGround(colors[1]);
                button.setkStartColor(colors[2]);
                button.setkEndColor(colors[3]);
                button.setkHoverStartColor(colors[4]);
                button.setkHoverEndColor(colors[5]);
                button.setkHoverColor(colors[6]);
            }
            
            SwingUtilities.invokeLater(() -> {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            });

            button.setkAllowGradient(false);
            
        } else {
            Color disabledBg = new Color(220, 220, 220); // Xám nhạt
            Color disabledFg = new Color(120, 120, 120); // Xám đậm
            
            button.setkBackGroundColor(disabledBg);
            button.setkForeGround(disabledFg);
            button.setkStartColor(disabledBg);
            button.setkEndColor(disabledBg);
            
            button.setkAllowGradient(false);
            button.setkHoverColor(new Color(220, 220, 220));
            
        }
        
        // Cập nhật giao diện
        button.repaint();
    }
    
    /**
     * Thiết lập style cho button với màu tuỳ chỉnh
     * @param button Button cần thiết lập
     * @param bgColor Màu nền
     * @param fgColor Màu chữ
     */
    public static void setupButtonStyle(JButton button, Color bgColor, Color fgColor) {
        // Tạo border bo tròn
        Border roundedBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        );
        
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(roundedBorder);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Không vẽ nền mặc định của Look and Feel
        button.setContentAreaFilled(true);
        button.setOpaque(true);
    }
    
    /**
     * Thiết lập style cho button hành động (như Thêm, Sửa, Xoá)
     * @param button Button cần thiết lập
     * @param actionType Loại hành động ("add", "edit", "delete", "search", "refresh")
     */
    public static void setupActionButton(JButton button, String actionType) {
        Color bgColor;
        Color fgColor = Color.WHITE;
        
        switch (actionType.toLowerCase()) {
            case "add":
                bgColor = new Color(46, 125, 50); // Xanh lá
                break;
            case "edit":
                bgColor = new Color(25, 118, 210); // Xanh dương
                break;
            case "delete":
                bgColor = new Color(211, 47, 47); // Đỏ
                break;
            case "search":
                bgColor = new Color(81, 45, 168); // Tím
                break;
            case "refresh":
                bgColor = new Color(255, 152, 0); // Cam
                break;
            case "cancel":
                bgColor = new Color(117, 117, 117); // Xám
                break;
            case "save":
                bgColor = new Color(0, 137, 123); // Xanh ngọc
                break;
            default:
                bgColor = new Color(69, 90, 100); // Xám đậm
        }
        
        setupButtonStyle(button, bgColor, fgColor);
    }
    
    /**
     * Tạo hiệu ứng hover cho button (áp dụng cho tất cả button trong form)
     * Lưu ý: Phương thức này nên được gọi sau khi tất cả button đã được thiết lập style
     * @param button Button cần tạo hiệu ứng
     */
    public static void setupHoverEffect(JButton button) {
        Color originalBg = button.getBackground();
        Color hoverBg = new Color(
                Math.min(originalBg.getRed() + 20, 255),
                Math.min(originalBg.getGreen() + 20, 255),
                Math.min(originalBg.getBlue() + 20, 255)
        );
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(hoverBg);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(originalBg);
                }
            }
        });
    }
}