// Full code implementation for the GUI components as per the requirements.
// Improved to fix the type mismatch error in CustomTableRenderer: cannot set GradientPaint as background Color.
// Fixed by using a flag for gradient, setting background to transparent for gradient cells, and custom painting the gradient in paintComponent before calling super.
// Also, adjusted selected row left border to only apply to the first column (column == 0) for proper row highlighting.
// Added necessary imports if missing.

package gui.admin;

import bll.ThuHangBUS;
import dal.ThuHangDAO;
import model.ThuHang;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class QuanLyThuHangPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMa, txtTen, txtDiem, txtTiLe;

    private ThuHangDAO dao = new ThuHangDAO();

    public QuanLyThuHangPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F4F4F6"));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        add(createTablePanel(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.SOUTH);

        loadData();
    }

    private JScrollPane createTablePanel() {
        String[] cols = {"Mã", "Tên hạng", "Điểm tối thiểu", "Giảm (%)"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    jc.setToolTipText("Điều kiện: Điểm tối thiểu " + model.getValueAt(row, 2) + " để đạt hạng này.");
                }
                return c;
            }
        };

        table.setRowHeight(32); // Increased for better visibility
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(Color.decode("#1F2937"));

        // Custom renderer for badges and hover/selected
        table.setDefaultRenderer(Object.class, new CustomTableRenderer());

        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        // Hover highlight
        table.addMouseMotionListener(new MouseAdapter() {
            int lastRow = -1;
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != lastRow) {
                    if (lastRow >= 0) table.repaint(table.getCellRect(lastRow, 0, true));
                    lastRow = row;
                    if (row >= 0) table.repaint(table.getCellRect(row, 0, true));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E5E7EB")));
        return scroll;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.decode("#E5E7EB")),
                "Tạo / Chỉnh sửa hạng", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.decode("#1F2937")));
        panel.setPreferredSize(new Dimension(0, 250)); // Fixed height for form

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16); // Gaps 16px
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtMa = createStyledTextField();
        txtTen = createStyledTextField();
        txtDiem = createStyledTextField();
        txtTiLe = createStyledTextField();

        addField(panel, gbc, 0, "Mã hạng:", txtMa);
        addField(panel, gbc, 1, "Tên hạng:", txtTen);
        addField(panel, gbc, 2, "Điểm tối thiểu:", txtDiem);
        addField(panel, gbc, 3, "Tỉ lệ giảm (%):", txtTiLe);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnNew = createStyledButton("Tạo mới", "#D91E18", "#FFFFFF", true); // Outline red
        JButton btnSave = createStyledButton("Lưu", "#FFC107", "#1F2937", false); // Yellow solid
        JButton btnDelete = createStyledButton("Xóa", "#D91E18", "#FFFFFF", false); // Red solid

        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> save());
        btnDelete.addActionListener(e -> delete());

        buttonPanel.add(btnNew);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int y, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.decode("#1F2937"));

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E5E7EB")),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return field;
    }

    private JButton createStyledButton(String text, String bgHex, String fgHex, boolean outline) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));

        if (outline) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.decode(bgHex));
            btn.setBorder(BorderFactory.createLineBorder(Color.decode(bgHex), 2));
        } else {
            btn.setBackground(Color.decode(bgHex));
            btn.setForeground(Color.decode(fgHex));
            btn.setBorder(BorderFactory.createEmptyBorder());
        }

        // Shadow and hover elevation
        btn.setBorder(BorderFactory.createCompoundBorder(btn.getBorder(), BorderFactory.createEmptyBorder(0, 0, 4, 0))); // Base shadow space
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(btn.getBorder(), BorderFactory.createEmptyBorder(0, 0, 8, 0))); // Raise
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(btn.getBorder(), BorderFactory.createEmptyBorder(0, 0, 4, 0))); // Lower
            }
        });

        return btn;
    }

    private void loadData() {
        model.setRowCount(0);
        List<ThuHang> list = dao.getAll();
        for (ThuHang th : list) {
            model.addRow(new Object[]{
                    th.getMaThuHang(),
                    th.getTenThuHang(),
                    th.getDiemToiThieu(),
                    th.getTiLeGiam()
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtMa.setText(model.getValueAt(row, 0).toString());
            txtTen.setText(model.getValueAt(row, 1).toString());
            txtDiem.setText(model.getValueAt(row, 2).toString());
            txtTiLe.setText(model.getValueAt(row, 3).toString());
        }
    }

    private void clearForm() {
        txtMa.setText("");
        txtTen.setText("");
        txtDiem.setText("");
        txtTiLe.setText("");
        table.clearSelection();
    }

    private void save() {
        try {
            ThuHang th = new ThuHang(
                    txtMa.getText(),
                    txtTen.getText(),
                    Integer.parseInt(txtDiem.getText()),
                    Double.parseDouble(txtTiLe.getText())
            );

            if (dao.getAll().stream().anyMatch(t -> t.getMaThuHang().equals(th.getMaThuHang()))) {
                dao.update(th);
            } else {
                dao.insert(th);
            }

            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Lưu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE); // Toast-like
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa hạng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dao.delete(txtMa.getText());
            loadData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Custom renderer for table
    private class CustomTableRenderer extends DefaultTableCellRenderer {

        private boolean isGradient = false;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String rank = (String) table.getModel().getValueAt(row, 1);

            // Hover
            if (table.rowAtPoint(MouseInfo.getPointerInfo().getLocation()) == row) {
                c.setBackground(Color.decode("#FDECEC")); // Light red hover
            } else if (isSelected) {
                c.setBackground(Color.decode("#FDECEC"));
                if (column == 0) { // Only apply left border to the first column for row selection
                    ((JLabel) c).setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, Color.decode("#D91E18")));
                } else {
                    ((JLabel) c).setBorder(null);
                }
            } else {
                c.setBackground(Color.WHITE);
                ((JLabel) c).setBorder(null);
            }

            // Badge for rank column (column 1)
            if (column == 1) {
                JLabel label = (JLabel) c;
                label.setIcon(null); // Clear icon
                if (rank.equalsIgnoreCase("Silver")) {
                    label.setBackground(Color.decode("#E5E7EB")); // Gray
                    label.setOpaque(true);
                    isGradient = false;
                } else if (rank.equalsIgnoreCase("Gold")) {
                    label.setBackground(Color.decode("#FFC107")); // Yellow
                    label.setOpaque(true);
                    isGradient = false;
                } else if (rank.equalsIgnoreCase("Platinum")) {
                    isGradient = true;
                    label.setBackground(new Color(0, 0, 0, 0)); // Transparent
                    label.setOpaque(true);
                    label.setText(rank + " 👑"); // Crown icon simulation
                }
            }

            return c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isGradient) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.decode("#A78BFA"), getWidth(), 0, Color.decode("#E5E7EB"));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }
    }
}

// The rest of the code (DatVeTaiQuayPanel, PanelUserProfile, MainApp) remains the same as previously provided.
// Ensure to import java.awt.Graphics2D and java.awt.GradientPaint at the top if not already present.