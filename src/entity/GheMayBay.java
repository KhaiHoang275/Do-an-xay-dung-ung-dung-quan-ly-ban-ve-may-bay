package entity;

public class GheMayBay {
    private String maGhe;
    private String maMayBay;
    private String soGhe;
    private double giaGhe;

    public GheMayBay() {
    }

    public GheMayBay(String maGhe, String maMayBay, String soGhe, double giaGhe) {
        this.maGhe = maGhe;
        this.maMayBay = maMayBay;
        this.soGhe = soGhe;
        this.giaGhe = giaGhe;
    }

    public GheMayBay(GheMayBay other) {
        this.maGhe = other.maGhe;
        this.maMayBay = other.maMayBay;
        this.soGhe = other.soGhe;
        this.giaGhe = other.giaGhe;
    }

    public String getMaGhe() {
        return maGhe;
    }

    public void setMaGhe(String maGhe) {
        this.maGhe = maGhe;
    }

    public String getMaMayBay() {
        return maMayBay;
    }

    public void setMaMayBay(String maMayBay) {
        this.maMayBay = maMayBay;
    }

    public String getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(String soGhe) {
        this.soGhe = soGhe;
    }

    public double getGiaGhe() {
        return giaGhe;
    }

    public void setGiaGhe(double giaGhe) {
        this.giaGhe = giaGhe;
    }

    @Override
    public String toString() {
        return maGhe + " " + maMayBay + " " + soGhe + " " + giaGhe;
    }
}
