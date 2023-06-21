package com.hakolab.posyandu;

public class Antrian {
    private String no_antrian;
    private String nama_lengkap;
    private String status_antrian;
    private String nik;

    public Antrian(String no_antrian, String nama_lengkap, String status_antrian, String nik) {
        this.no_antrian = no_antrian;
        this.nama_lengkap = nama_lengkap;
        this.status_antrian = status_antrian;
        this.nik = nik;
    }

    public String getNoAntrian() {
        return no_antrian;
    }

    public String getNamaLengkap() { return nama_lengkap; }

    public String getStatusAntrian() {
        return status_antrian;
    }
    public String getNik() {
        return nik;
    }

}

