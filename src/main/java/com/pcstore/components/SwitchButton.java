package com.pcstore.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SwitchButton extends Component {

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        timer.start();
        runEvent();
    }
    
    // Thêm biến để lưu trạng thái enable/disable
    private boolean enabled = true;
    
    // Thêm các phương thức để kiểm tra và thiết lập trạng thái
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        // Thay đổi con trỏ chuột tùy thuộc vào trạng thái
        setCursor(enabled ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
        repaint(); // Vẽ lại để hiển thị trạng thái mới
    }

    private Timer timer;
    private float location;
    private boolean selected;
    private boolean mouseOver;
    private float speed = 1f;
    private List<EventSwitchSelected> events;

    public SwitchButton() {
        setBackground(new Color(0, 174, 255));
        setPreferredSize(new Dimension(50, 25));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        events = new ArrayList<>();
        location = 2;
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (isSelected()) {
                    int endLocation = getWidth() - getHeight() + 2;
                    if (location < endLocation) {
                        location += speed;
                        repaint();
                    } else {
                        timer.stop();
                        location = endLocation;
                        repaint();
                    }
                } else {
                    int endLocation = 2;
                    if (location > endLocation) {
                        location -= speed;
                        repaint();
                    } else {
                        timer.stop();
                        location = endLocation;
                        repaint();
                    }
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                // Chỉ thay đổi trạng thái mouseOver khi nút được kích hoạt
                if (enabled) {
                    mouseOver = true;
                }
            }

            @Override
            public void mouseExited(MouseEvent me) {
                mouseOver = false;
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                // Chỉ xử lý khi nút được kích hoạt
                if (enabled && SwingUtilities.isLeftMouseButton(me)) {
                    if (mouseOver) {
                        selected = !selected;
                        timer.start();
                        runEvent();
                    }
                }
            }
        });
    }

    @Override
    public void paint(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        float alpha = getAlpha();
        
        if (alpha < 1) {
            g2.setColor(Color.GRAY);
            g2.fillRoundRect(0, 0, width, height, 25, 25);
        }
        
        // Điều chỉnh độ mờ dựa trên trạng thái kích hoạt
        float compositeAlpha = enabled ? alpha : alpha * 0.6f;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, compositeAlpha));
        
        // Chọn màu nền dựa trên trạng thái kích hoạt
        Color backgroundColor = getBackground();
        if (!enabled) {
            // Tạo màu xám mờ khi nút bị vô hiệu hóa
            backgroundColor = new Color(backgroundColor.getRed(), 
                                        backgroundColor.getGreen(), 
                                        backgroundColor.getBlue(), 
                                        180);
        }
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, width, height, 25, 25);
        
        // Vẽ nút tròn (thumb) với màu thích hợp
        g2.setColor(enabled ? getForeground() : new Color(200, 200, 200));
        g2.setComposite(AlphaComposite.SrcOver);
        g2.fillOval((int) location, 2, height - 4, height - 4);
        
        super.paint(grphcs);
    }

    private float getAlpha() {
        float width = getWidth() - getHeight();
        float alpha = (location - 2) / width;
        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 1) {
            alpha = 1;
        }
        return alpha;
    }

    private void runEvent() {
        for (EventSwitchSelected event : events) {
            event.onSelected(selected);
        }
    }

    public void addEventSelected(EventSwitchSelected event) {
        events.add(event);
    }
}