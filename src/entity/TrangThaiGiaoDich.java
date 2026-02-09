package entity;

public enum TrangThaiGiaoDich {
    CHO_XU_LY,  //0
    DA_XU_LY,   //1
    TU_CHOI;     //2

    public static TrangThaiGiaoDich fromInt(int value){
        return TrangThaiGiaoDich.values()[value];

    }
}
