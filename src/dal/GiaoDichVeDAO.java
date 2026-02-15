package dal;
import model.GiaoDichVe;
import model.TrangThaiGiaoDich;
import db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiaoDichVeDAO {
    // === insert ===  : lưu 1 một giao dịch vé mới vào database (sql)
    public boolean insert(GiaoDichVe gd){
        String sql = """
            INSERT INTO GiaoDichVe 
            (maGD, maVeCu, maVeMoi, trangThai, phi, phiChenhLech, lyDoDoi, ngayYeuCau, ngayXuLi)
            VALUES(?, ?, ?, ?, ?, ?, ?, ? ,?)
        """;
        // '?' : placeholder(chỗ trống), mỗi ? tương ứng 1 cột

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)){
            // ket noi sql
            ps.setString(1, gd.getMaGD()); // gán chuỗi vào từng dấu ?
            ps.setString(2, gd.getMaVeMoi());
            ps.setString(3, gd.getMaVeCu());
            ps.setString(4, gd.getTrangThai().name()); // gán enum
            ps.setBigDecimal(5, gd.getPhi());
            ps.setBigDecimal(6, gd.getPhiChenhLech());
            ps.setString(7, gd.getLyDo());

            if(gd.getNgayYeuCau() != null)
                ps.setDate(8, Date.valueOf(gd.getNgayYeuCau()));
            else
                ps.setNull(8, Types.DATE);

            if(gd.getNgayXuLi() != null)
                ps.setDate(9, Date.valueOf(gd.getNgayXuLi()));
            else
                ps.setNull(9, Types.DATE);

            return ps.executeUpdate() > 0; //--> true
            // excuteUpdadte: Gửi câu SQL sang SQL Server --> trả về 1 nếu insert thành công 1 dòng, 0 nếu không có dòng nào bị ảnh hường
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //executeUpdate dung cho INSERT, UPDATE, DELETE
    //executeQuery dung cho SELECT

    //=== find all ===
    public List<GiaoDichVe> findAll(){
        List<GiaoDichVe> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichVe"; // lấy toàn bộ lịch sử giao dịch

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){
            // executeQuery: Gửi SELECT sang SQL Server, SQL Server trả về bảng kết quả, JDBC lưu bảng đó trong ResultSet

            while(rs.next()){
                // next còn true là còn dòng tiếp theo
                list.add(mapResultSet(rs));  //mapResultSet(rs) chuyển SQL --> JAVA
            }

        }catch (SQLException e){
        e.printStackTrace();
        }
        return list;
    }

    // === find by maVeCu ===
    public List<GiaoDichVe> findByMaVeCu(String maVeCu){
        List<GiaoDichVe> list = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichVe WHERE maVeCu = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maVeCu);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(mapResultSet(rs));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    // ===find by trang thai===
    public List<GiaoDichVe> findByTrangThai(TrangThaiGiaoDich tt){
        List<GiaoDichVe> list  = new ArrayList<>();
        String sql = "SELECT * FROM GiaoDichVe WHERE trangThai = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, tt.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public GiaoDichVe findById(String maGD){  // trả về 1 object mà maGD là khóa chính
        String sql = "SELECT * FROM GiaoDichVe WHERE maGD = ?"; // ? là placeholder (tránh SQL Injection) Chỉ lấy bản ghi có maGD tương ứng

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, maGD); // gán giá trị cho dấu ?, 1 là ? đầu tiên
             try (ResultSet rs  = ps.executeQuery()){
                 if(rs.next()) return mapResultSet(rs);
             }
             //executeQuery() dùng cho SELECT
            //
            //Trả về ResultSet
            //
            //ResultSet chứa bảng kết quả
            //
            //Lại dùng try-with-resources để tự đóng rs
        }catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(GiaoDichVe gd){
        String sql = """
                UPDATE GiaoDichVe
                SET maVeCu = ?,
                    maVeMoi = ?,
                    trangThai = ?,
                    phi = ?,
                    phiChenhLech = ?,
                    lyDoDoi = ?,
                    ngayYeuCau = ?,
                    ngayXuLi = ?
                 WHERE maGD = ?
               """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){


            // ve
            ps.setString(1, gd.getMaVeCu());
            ps.setString(2, gd.getMaVeMoi());
            //enum
            ps.setString(3, gd.getTrangThai().name());
            //BigDecimal
            ps.setBigDecimal(4, gd.getPhi());
            ps.setBigDecimal(5, gd.getPhiChenhLech());
            //lydo
            ps.setString(6, gd.getLyDo());
            //ngayyeucau
            if(gd.getNgayYeuCau() != null){
                ps.setDate(7, Date.valueOf(gd.getNgayYeuCau()));
            }else {
                ps.setNull(7, Types.DATE);
            }
            //ngay xu ly
            if(gd.getNgayXuLi() != null){
                ps.setDate(8, Date.valueOf(gd.getNgayXuLi()));
            }else{
                ps.setNull(8, Types.DATE);
            }

            //WHERE maGD = ?
            ps.setString(9, gd.getMaGD());

            return ps.executeUpdate() > 0;

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maGD) {
        String sql = "DELETE FROM GiaoDichVe WHERE maGD = ?";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){

            ps.setString(1, maGD);
            int rowAffected = ps.executeUpdate();

            return rowAffected > 0; // =1: xoa thanh cong, =0: khong co mã đó

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private GiaoDichVe mapResultSet(ResultSet rs) throws SQLException{
        // chuyển 1 dòng dữ liệu sql thành 1 object java
        GiaoDichVe gd = new GiaoDichVe(); // tạo object rỗng
        gd.setMaGD(rs.getString("maGD"));
        gd.setMaVeCu(rs.getString("maVeCu"));
        gd.setMaVeMoi(rs.getString("maVeMoi"));
        gd.setTrangThai(TrangThaiGiaoDich.valueOf(rs.getString("trangThai")));
        gd.setPhi(rs.getBigDecimal("phi"));
        gd.setPhiChenhLech(rs.getBigDecimal("phiChenhLech"));
        gd.setLyDo(rs.getString("lyDoDoi"));

        Date ngayYC = rs.getDate("ngayYeuCau");
        if(ngayYC != null)
            gd.setNgayYeuCau(ngayYC.toLocalDate());

        Date ngayXL = rs.getDate("ngayXuLi");
        if(ngayXL != null)
            gd.setNgayXuLi(ngayXL.toLocalDate());

        return gd;
    }
}
