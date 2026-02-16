package model;

public enum TrangThaiGiaoDich {
    CHO_XU_LY,  //0 - vua tao yeu cau
    DA_DUYET,   //1 - admin duyet cho doi
    DA_THANH_TOAN, // da thanh toan phi chenh lech
    TU_CHOI;     //3 - bi tu choi

    public static TrangThaiGiaoDich fromInt(int value){
        for(TrangThaiGiaoDich t : TrangThaiGiaoDich.values()){
            if(t.ordinal() == value){
                return t;
            }
        }
        throw new IllegalArgumentException("Gia tri trang thai khong hop le: " + value);
    }
}
