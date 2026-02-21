package gui.custom; // Sửa lại package cho đúng với project của bạn

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

    private int radius = 30; // Độ bo tròn (bạn có thể chỉnh số này)

    public RoundedPanel() {
        super();
        setOpaque(false); // Quan trọng: Để nền trong suốt, chỉ hiện phần bo tròn
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // Khử răng cưa cho đẹp
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Lấy màu nền hiện tại của Panel
        g2.setColor(getBackground());
        
        // Vẽ hình chữ nhật bo tròn
        // fillRoundRect(x, y, width, height, arcWidth, arcHeight)
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        super.paintComponent(g);
    }
    
    // Getter Setter để chỉnh độ bo tròn
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }
}