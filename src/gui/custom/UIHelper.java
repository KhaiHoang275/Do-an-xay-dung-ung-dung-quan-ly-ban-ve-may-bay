package gui.custom;

import model.SanBay;
import bus.SanBayBUS;
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
}