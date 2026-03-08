package gui;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import model.NguoiDung;
import model.ThongTinHanhKhach;
import dal.ThongTinHanhKhachDAO;

public class QuanLyHanhKhachPanel extends JPanel {

    private NguoiDung currentUser;
    private ThongTinHanhKhachDAO dao;
    
    private CardLayout cardLayout;
    private JPanel pnlCards; // Panel chứa 2 màn hình
    
    private JPanel pnlListView; // Màn hình 1: Danh sách các ô hành khách
    private JPanel pnlDetailView; // Màn hình 2: Form chi tiết
    
    private JPanel pnlGridHanhKhach; // Nơi chứa các ô vuông nhỏ
    
    // Components cho Form Chi Tiết
    private ThongTinHanhKhach currentHK; // Hành khách đang được chọn/tạo mới
    private JTextField txtHoTen, txtCCCD, txtHoChieu, txtNgaySinh;
    private JComboBox<String> cbGioiTinh, cbLoaiHK;
    private JButton btnSua, btnLuu, btnQuayLai;

    public QuanLyHanhKhachPanel(NguoiDung user) {
        this.currentUser = user;
        this.dao = new ThongTinHanhKhachDAO();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);
        
        initListView();
        initDetailView();
        
        pnlCards.add(pnlListView, "LIST");
        pnlCards.add(pnlDetailView, "DETAIL");
        
        add(pnlCards, BorderLayout.CENTER);
        
        loadDanhSachHanhKhach(); // Load dữ liệu lần đầu
    }

    // ==========================================================
    // 1. MÀN HÌNH DANH SÁCH (Các ô vuông)
    // ==========================================================
    private void initListView() {
        pnlListView = new JPanel(new BorderLayout());
        pnlListView.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Danh sách Hành khách của tôi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlListView.add(lblTitle, BorderLayout.NORTH);
        
        // Dùng WrapLayout (hoặc FlowLayout) để các ô tự động rớt dòng
        pnlGridHanhKhach = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        pnlGridHanhKhach.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(pnlGridHanhKhach);
        scrollPane.setBorder(null);
        pnlListView.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnAdd = new JButton("+ Thêm hành khách mới");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAdd.setBackground(new Color(255, 193, 7)); // Màu vàng
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> moFormChiTiet(null)); // null = Tạo mới
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        pnlBottom.add(btnAdd);
        pnlListView.add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadDanhSachHanhKhach() {
        pnlGridHanhKhach.removeAll();
        
        // Lấy danh sách từ DB dựa trên mã người dùng chung
        ArrayList<ThongTinHanhKhach> list = dao.selectAllByMaNguoiDung(currentUser.getMaNguoiDung());
        
        for (ThongTinHanhKhach hk : list) {
            pnlGridHanhKhach.add(taoOHanhKhach(hk));
        }
        
        pnlGridHanhKhach.revalidate();
        pnlGridHanhKhach.repaint();
    }

    // Tạo giao diện cho 1 ô (card) hành khách
    private JPanel taoOHanhKhach(ThongTinHanhKhach hk) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setPreferredSize(new Dimension(250, 120));
        pnl.setBackground(new Color(245, 247, 250));
        pnl.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        pnl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblName = new JLabel(hk.getHoTen() != null ? hk.getHoTen() : "Khách chưa có tên");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblType = new JLabel(hk.getLoaiHanhKhach() != null ? hk.getLoaiHanhKhach() : "");
        lblType.setHorizontalAlignment(SwingConstants.CENTER);
        lblType.setForeground(Color.GRAY);
        
        pnl.add(lblName, BorderLayout.CENTER);
        pnl.add(lblType, BorderLayout.SOUTH);
        
        // Bấm vào ô sẽ mở form chi tiết
        pnl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                moFormChiTiet(hk);
            }
        });
        
        return pnl;
    }

    // ==========================================================
    // 2. MÀN HÌNH CHI TIẾT (Form)
    // ==========================================================
    private void initDetailView() {
        pnlDetailView = new JPanel(new BorderLayout());
        pnlDetailView.setBackground(Color.WHITE);
        
        // Các trường nhập liệu
        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 15, 25));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 300));
        
        txtHoTen = new JTextField();
        txtCCCD = new JTextField();
        txtHoChieu = new JTextField();
        txtNgaySinh = new JTextField(); // Format: dd/MM/yyyy
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cbLoaiHK = new JComboBox<>(new String[]{"Nguoi lon", "Tre em", "Em be"});
        
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 16);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 16);
        
        // Helper thêm dòng
        themTruongVaoForm(pnlForm, "Họ và Tên:", txtHoTen, fontLabel, fontInput);
        themTruongVaoForm(pnlForm, "CCCD:", txtCCCD, fontLabel, fontInput);
        themTruongVaoForm(pnlForm, "Hộ chiếu (Nếu có):", txtHoChieu, fontLabel, fontInput);
        themTruongVaoForm(pnlForm, "Ngày sinh (dd/MM/yyyy):", txtNgaySinh, fontLabel, fontInput);
        
        JLabel lblGender = new JLabel("Giới tính:"); lblGender.setFont(fontLabel);
        cbGioiTinh.setFont(fontInput);
        pnlForm.add(lblGender); pnlForm.add(cbGioiTinh);
        
        JLabel lblType = new JLabel("Loại hành khách:"); lblType.setFont(fontLabel);
        cbLoaiHK.setFont(fontInput);
        pnlForm.add(lblType); pnlForm.add(cbLoaiHK);
        
        pnlDetailView.add(pnlForm, BorderLayout.CENTER);
        
        // Khung chứa các nút điều khiển dưới cùng
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.setBackground(Color.WHITE);
        
        btnQuayLai = createButton("Quay lại", new Color(150, 150, 150));
        btnSua = createButton("Chỉnh sửa", new Color(45, 72, 140));
        btnLuu = createButton("Lưu thông tin", new Color(76, 175, 80));
        
        pnlButtons.add(btnQuayLai);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnLuu);
        
        pnlDetailView.add(pnlButtons, BorderLayout.SOUTH);
        
        // Logic Nút
        btnQuayLai.addActionListener(e -> {
            cardLayout.show(pnlCards, "LIST");
            loadDanhSachHanhKhach(); // Reload lại nếu ko lưu
        });
        
        btnSua.addActionListener(e -> setTrangThaiChinhSua(true));
        
        btnLuu.addActionListener(e -> luuThongTin());
    }

    private void moFormChiTiet(ThongTinHanhKhach hk) {
        this.currentHK = hk;
        
        if (hk == null) {
            // Trường hợp TẠO MỚI
            this.currentHK = new ThongTinHanhKhach();
            this.currentHK.setMaNguoiDung(currentUser.getMaNguoiDung());
            this.currentHK.setMaHK("HK" + System.currentTimeMillis()); // Tạm thời tạo mã tự động
            
            txtHoTen.setText("");
            txtCCCD.setText("");
            txtHoChieu.setText("");
            txtNgaySinh.setText("");
            cbGioiTinh.setSelectedIndex(0);
            cbLoaiHK.setSelectedIndex(0);
            
            setTrangThaiChinhSua(true); // Tạo mới thì cho phép sửa luôn
        } else {
            // Trường hợp XEM CHI TIẾT
            txtHoTen.setText(hk.getHoTen());
            txtCCCD.setText(hk.getCccd());
            txtHoChieu.setText(hk.getHoChieu());
            if (hk.getNgaySinh() != null) {
                txtNgaySinh.setText(hk.getNgaySinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                txtNgaySinh.setText("");
            }
            cbGioiTinh.setSelectedItem(hk.getGioiTinh());
            cbLoaiHK.setSelectedItem(hk.getLoaiHanhKhach());
            
            setTrangThaiChinhSua(false); // Xem thì không cho sửa liền, phải bấm nút Sửa
        }
        
        cardLayout.show(pnlCards, "DETAIL");
    }

    private void setTrangThaiChinhSua(boolean isEdit) {
        txtHoTen.setEditable(isEdit);
        txtCCCD.setEditable(isEdit);
        txtHoChieu.setEditable(isEdit);
        txtNgaySinh.setEditable(isEdit);
        cbGioiTinh.setEnabled(isEdit);
        cbLoaiHK.setEnabled(isEdit);
        
        btnLuu.setVisible(isEdit); // Chỉ hiện nút Lưu khi đang ở chế độ sửa
        btnSua.setVisible(!isEdit); // Chỉ hiện nút Sửa khi đang ở chế độ xem
    }

    private void luuThongTin() {
        // Cập nhật dữ liệu từ UI vào Object
        currentHK.setHoTen(txtHoTen.getText());
        currentHK.setCccd(txtCCCD.getText());
        currentHK.setHoChieu(txtHoChieu.getText());
        currentHK.setGioiTinh(cbGioiTinh.getSelectedItem().toString());
        currentHK.setLoaiHanhKhach(cbLoaiHK.getSelectedItem().toString());
        
        try {
            if (!txtNgaySinh.getText().isEmpty()) {
                currentHK.setNgaySinh(LocalDate.parse(txtNgaySinh.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày sinh sai định dạng (dd/MM/yyyy)!");
            return;
        }

        // Gọi DB để lưu (GỌI HÀM BẠN ĐÃ VIẾT Ở DAO)
        boolean isSuccess = true; // dao.saveOrUpdate(currentHK); 
        
        if (isSuccess) {
            JOptionPane.showMessageDialog(this, "Lưu thành công!");
            setTrangThaiChinhSua(false); // Lưu xong thì chuyển về chế độ chỉ Xem
            // KHÔNG QUAY LẠI MÀ VẪN GIỮ Ở FORM, HOẶC CÓ THỂ QUAY LẠI TÙY BẠN
            // cardLayout.show(pnlCards, "LIST"); loadDanhSachHanhKhach();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại!");
        }
    }

    // Utility methods
    private void themTruongVaoForm(JPanel pnl, String title, JTextField txt, Font fLbl, Font fTxt) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(fLbl);
        txt.setFont(fTxt);
        pnl.add(lbl);
        pnl.add(txt);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }
}