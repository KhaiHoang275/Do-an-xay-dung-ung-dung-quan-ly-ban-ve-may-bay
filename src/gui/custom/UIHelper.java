package gui.custom;

import model.SanBay;
import bll.SanBayBUS;
import java.util.ArrayList;
import javax.swing.JComboBox;

public class UIHelper {
    
    public static void loadSanBayToComboBox(JComboBox... comboBoxes) {

        SanBayBUS sanBayBUS = new SanBayBUS();
        ArrayList<SanBay> listSanBay = sanBayBUS.getAllSanBay(); 

        System.out.println("========== KIỂM TRA DATABASE ==========");
        System.out.println("Số lượng sân bay lấy được từ SQL: " + listSanBay.size());

        SanBay defaultItem = new SanBay("", "", "", "Chọn thành phố");
      
        for (JComboBox cb : comboBoxes) {
            cb.removeAllItems();
            cb.addItem(defaultItem);
            
            for (SanBay sb : listSanBay) {
                cb.addItem(sb);
            }
        }
    } 

    public static java.awt.image.BufferedImage createDefaultAvatar(String text, int size) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2.setColor(new java.awt.Color(45, 72, 140));
        g2.fillOval(0, 0, size, size);
        
        g2.setColor(java.awt.Color.WHITE);
        g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, size / 2));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int x = (size - fm.stringWidth(text)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);
        
        g2.dispose();
        return img;
    }
}