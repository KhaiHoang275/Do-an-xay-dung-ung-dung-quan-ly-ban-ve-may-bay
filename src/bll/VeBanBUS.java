package bll;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import dal.VeBanDAO;
import db.DBConnection;
import model.VeBan;

public class VeBanBUS {
    private VeBanDAO dao = new VeBanDAO();

    public boolean datVe(VeBan vb) throws Exception{
        if(vb == null) throw new Exception("Vé không hợp lệ!"); 

        if(vb.getMaChuyenBay() == null || vb.getMaChuyenBay().isEmpty()) throw new Exception("Chưa chọn chuyến bay!");

        if(vb.getMaGhe() == null || vb.getMaGhe().isEmpty()) throw new Exception("Chưa chọn ghế!");

        Connection conn = null;

        try{
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if(!dao.chuyenBayExists(conn, vb.getMaChuyenBay())) throw new Exception("Chuyến bay không tồn tại!");

            Timestamp tg = dao.getThoiGianKhoiHanh(conn, vb.getMaChuyenBay());
            if(tg.toLocalDateTime().isBefore(LocalDateTime.now())) throw new Exception("Chuyến bay này đã khởi hành!");

            if(!dao.checkSeatAvailable(vb.getMaChuyenBay(), vb.getMaGhe())) throw new Exception("Ghế này đã được đặt!");

            int soGheCon = dao.getSoGheCon(conn, vb.getMaChuyenBay());
            if(soGheCon <= 0) throw new Exception("Chuyến bay đã hết chỗ!");

            String maVe = dao.generateMaVe(conn);
            vb.setMaVe(maVe);
            vb.setTrangThaiVe("Đã xuất");

            boolean inserted = dao.insert(vb);
            if(!inserted){
                conn.rollback();
                return false;
            }

            boolean updated = dao.updateSoGheCon(conn, vb.getMaChuyenBay(), soGheCon-1);
            if(!updated){
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e){
            if(conn != null) conn.rollback();
            throw e;
        } finally {
            if(conn != null) conn.close();
        }
    }
}
