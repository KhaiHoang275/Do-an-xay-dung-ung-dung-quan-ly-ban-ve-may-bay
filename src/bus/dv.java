 btnThem.addActionListener(e -> {
            try {
                DichVuBoSung dv = getFormInput();
                String result = dichVuBUS.themDichVu(dv);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            try {
                if (txtMaDichVu.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ trên bảng!");
                    return;
                }
                DichVuBoSung dv = getFormInput();
                String result = dichVuBUS.capNhatDichVu(dv);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa!");
                return;
            }
            String maDV = txtMaDichVu.getText();
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa dịch vụ " + maDV + " không?\nHành động này xóa vĩnh viễn khỏi CSDL!", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = dichVuBUS.xoaDichVu(maDV);
                JOptionPane.showMessageDialog(this, result);
                if (result.contains("Thành công")) btnLamMoi.doClick();
            }
        });
         private DichVuBoSung getFormInput() throws Exception {
        String maDV = txtMaDichVu.getText().trim();
        if (maDV.isEmpty()) throw new Exception("Mã dịch vụ không được để trống!");

        String tenDV = txtTenDichVu.getText().trim();
        if (tenDV.isEmpty()) throw new Exception("Tên dịch vụ không được để trống!");

        BigDecimal donGia = BigDecimal.ZERO;
        try {
            donGia = new BigDecimal(txtDonGia.getText().trim().isEmpty() ? "0" : txtDonGia.getText().trim());
        } catch (NumberFormatException e) {
            throw new Exception("Đơn giá phải là số hợp lệ!");
        }

        return new DichVuBoSung(maDV, tenDV, donGia);
    }