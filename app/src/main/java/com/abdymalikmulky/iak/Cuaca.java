package com.abdymalikmulky.iak;

/**
 * Created by abdymalikmulky on 11/13/16.
 */

public class Cuaca {
    String hari;
    String bulan;
    String tanggal;
    String jenis;
    String curahHujan;

    public Cuaca(String hari, String bulan, String tanggal, String jenis, String curahHujan) {
        this.hari = hari;
        this.bulan = bulan;
        this.tanggal = tanggal;
        this.jenis = jenis;
        this.curahHujan = curahHujan;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getBulan() {
        return bulan;
    }

    public void setBulan(String bulan) {
        this.bulan = bulan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getCurahHujan() {
        return curahHujan;
    }
    public void setCurahHujan(String curahHujan) {
        this.curahHujan = curahHujan;
    }
    @Override
    public String toString() {
        return "Cuaca{" +
                "hari='" + hari + '\'' +
                ", bulan='" + bulan + '\'' +
                ", tanggal='" + tanggal + '\'' +
                ", jenis='" + jenis + '\'' +
                ", curahHujan='" + curahHujan + '\'' +
                '}';
    }
}
