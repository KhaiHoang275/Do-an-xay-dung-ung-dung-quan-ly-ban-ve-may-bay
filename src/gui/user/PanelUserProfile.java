package gui.user;

import bll.ThuHangBUS;
import dal.ThongTinHanhKhachDAO;
import model.ThuHang;

import javax.swing.*;
import java.awt.*;

public class PanelUserProfile extends JPanel {

    public PanelUserProfile(String maHK) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.decode("#F4F4F6"));

        ThongTinHanhKhachDAO dao = new ThongTinHanhKhachDAO();
        ThuHangBUS bus = new ThuHangBUS();

        int diem = dao.getDiemTichLuy(maHK);
        ThuHang th = bus.xacDinhThuHang(diem);

        JLabel lbl = new JLabel("Điểm: " + diem);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblHang = new JLabel("Hạng: " + th.getTenThuHang());

        JProgressBar bar = new JProgressBar();
        bar.setValue(diem % 100);

        add(Box.createVerticalStrut(40));
        add(lbl);
        add(lblHang);
        add(bar);
    }
}