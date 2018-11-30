package gaskudriver.funcode.funcode.com.gogasdriver.model;

/**
 * Created by funcode on 12/1/17.
 */

public class TransaksiModel {
    public String Lokasi;
    public String IDUser;
    public int TarifAntar;
    public int TotalBayar;
    public int Status;

    public TransaksiModel(){}

    public TransaksiModel(String lokasi, String IDUser, int tarifAntar, int totalBayar, int status) {
        Lokasi = lokasi;
        this.IDUser = IDUser;
        TarifAntar = tarifAntar;
        TotalBayar = totalBayar;
        Status = status;
    }
}
