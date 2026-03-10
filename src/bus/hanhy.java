 btnThem.addActionListener(e -> {
            try {
                HanhLy hl = getFormInput();
                String result = hanhLyBUS.themHanhLy(hl);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                String maHL = txtMaHanhLy.getText().trim();
                if (maHL.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn hành lý trên bảng!");
                    return;
                }

                if (cboHienThi.getSelectedIndex() == 1) {
                    HanhLy hlKhPhuc = getFormInput();
                    hlKhPhuc.setTrangThai("Chưa Check-in"); 
                    String result = hanhLyBUS.capNhatHanhLy(hlKhPhuc);
                    JOptionPane.showMessageDialog(this, "Khôi phục " + result.toLowerCase());
                    if (result.contains("Thành công")) btnLamMoi.doClick();
                    return;
                }

                HanhLy hl = getFormInput();
                String result = hanhLyBUS.capNhatHanhLy(hl);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hành lý cần xóa!");
                return;
            }
            String maHL = txtMaHanhLy.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa hành lý " + maHL + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                String result = hanhLyBUS.xoaHanhLy(maHL);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            }
        });

 private HanhLy getFormInput() throws Exception {
        String maHL = txtMaHanhLy.getText().trim();
        if (maHL.isEmpty()) throw new Exception("Mã hành lý không được để trống!");
        
        String maVe = txtMaVe.getText().trim();
        
        BigDecimal soKg = BigDecimal.ZERO;
        try {
            soKg = new BigDecimal(txtSoKg.getText().trim().isEmpty() ? "0" : txtSoKg.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Số Kg phải là số hợp lệ!");
        }

        String kichThuoc = txtKichThuoc.getText().trim();
        
        BigDecimal giaTien = BigDecimal.ZERO;
        try {
            giaTien = new BigDecimal(txtGiaTien.getText().trim().isEmpty() ? "0" : txtGiaTien.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Giá tiền phải là số hợp lệ!");
        }

        String trangThai = cbTrangThai.getSelectedItem().toString();
        String ghiChu = txtGhiChu.getText().trim();

        return new HanhLy(maHL, maVe, soKg, kichThuoc, giaTien, trangThai, ghiChu);
    }