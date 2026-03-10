btnThem.addActionListener(e -> {
            try {
                HoaDon hd = getFormInput();
                String result = hoaDonBUS.themHoaDon(hd);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                if (txtMaHoaDon.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn trên bảng!");
                    return;
                }
                HoaDon hd = getFormInput();
                String result = hoaDonBUS.capNhatHoaDon(hd);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa!");
                return;
            }
            String maHD = txtMaHoaDon.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hóa đơn " + maHD + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = hoaDonBUS.xoaHoaDon(maHD);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            }
        });
 private HoaDon getFormInput() throws Exception {
        String maHD = txtMaHoaDon.getText().trim();
        if (maHD.isEmpty()) throw new Exception("Mã hóa đơn không được để trống!");

        String maPhieu = txtMaPhieuDatVe.getText().trim();
        if (maPhieu.isEmpty()) throw new Exception("Mã phiếu đặt vé không được để trống!");

        String maNV = txtMaNV.getText().trim();

        Date dateLap = (Date) spinnerNgayLap.getValue();
        LocalDateTime ngayLap = dateLap.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        BigDecimal tongTien = BigDecimal.ZERO;
        try {
            tongTien = new BigDecimal(txtTongTien.getText().trim().isEmpty() ? "0" : txtTongTien.getText().trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new Exception("Tổng tiền phải là số hợp lệ!");
        }
        
        BigDecimal thue = BigDecimal.ZERO;
        try {
            thue = new BigDecimal(txtThue.getText().trim().isEmpty() ? "0" : txtThue.getText().trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new Exception("Thuế phải là số hợp lệ!");
        }

        String phuongThuc = cboPhuongThuc.getSelectedItem().toString();
        String donViTienTe = cboDonViTienTe.getSelectedItem().toString();

        String trangThai = cboTrangThai.getSelectedItem().toString();

        return new HoaDon(maHD, maPhieu, maNV, ngayLap, tongTien, phuongThuc, donViTienTe, thue, trangThai);
    }

    // Lấy chuỗi văn bản từ ô nhập Tổng tiền, xóa khoảng trắng và xóa dấu phẩy phân cách
String input = txtTongTien.getText().trim().replace(",", "");

// Kiểm tra nếu ô nhập không trống mới tiến hành tính toán
if (!input.isEmpty()) {
    
    // Chuyển chuỗi văn bản thành kiểu số BigDecimal để tính toán chính xác
    BigDecimal tongTien = new BigDecimal(input);
    
    // Khai báo biến để chứa mức thuế suất sẽ áp dụng
    BigDecimal thueSuat; 
    
    // Khởi tạo mốc so sánh là 10,000,000 VNĐ
    BigDecimal moc10Trieu = new BigDecimal("10000000");
    
    // So sánh: tongTien.compareTo(moc10Trieu)
    // Nếu kết quả > 0 tức là Tổng tiền > 10 triệu
    if (tongTien.compareTo(moc10Trieu) > 0) {
        // Áp dụng mức thuế 0.5% (0.005)
        thueSuat = new BigDecimal("0.005"); 
    } else {
        // Ngược lại (nhỏ hơn hoặc bằng 10 triệu), áp dụng thuế 1% (0.01)
        thueSuat = new BigDecimal("0.01"); 
    }
    
    // Tính tiền thuế = Tổng tiền * Thuế suất
    BigDecimal vat = tongTien.multiply(thueSuat);
    
    // Hiển thị số tiền thuế lên ô txtThue, định dạng không lấy chữ số thập phân
    txtThue.setText(String.format("%.0f", vat));
}