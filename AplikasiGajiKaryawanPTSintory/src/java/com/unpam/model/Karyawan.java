/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.unpam.view.PesanDialog;

public class Karyawan {

    private String ktp, nama, password;
    private int ruang;
    private String pesan;
    private Object[][] list;

    private final Koneksi koneksi = new Koneksi();
    private final PesanDialog pesanDialog = new PesanDialog();

    // Getter & Setter
    public String getKtp() {
        return ktp;
    }

    public void setKtp(String ktp) {
        this.ktp = ktp;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getRuang() {
        return ruang;
    }

    public void setRuang(int ruang) {
        this.ruang = ruang;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPesan() {
        return pesan;
    }

    public Object[][] getList() {
        return list;
    }

    public void setList(Object[][] list) {
        this.list = list;
    }

    // Method baca() - untuk login dan pencarian
    public boolean baca(String ktpCari) {
        boolean adaKesalahan = false;
        Connection connection;

        if ((connection = koneksi.getConnection()) != null) {
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            ResultSet rset = null;

            try {
                SQLStatement = "SELECT ktp, nama, ruang, password FROM tbkaryawan WHERE ktp = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, ktpCari);
                rset = preparedStatement.executeQuery();

                if (rset.next()) {
                    this.ktp = rset.getString("ktp");
                    this.nama = rset.getString("nama");
                    this.ruang = rset.getInt("ruang");
                    this.password = rset.getString("password");
                } else {
                    adaKesalahan = true;
                    pesan = "Data karyawan tidak ditemukan";
                }

                rset.close();
                preparedStatement.close();
                connection.close();

            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat membaca data karyawan\n" + ex + "\n" + SQLStatement;
            }

        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }

        return !adaKesalahan;
    }

    // Method simpan()
    public boolean simpan() {
        boolean adaKesalahan = false;
        Connection connection;

        if ((connection = koneksi.getConnection()) != null) {

            int jumlahSimpan = 0;
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;

            try {
                // Cek apakah data sudah ada
                SQLStatement = "SELECT COUNT(*) as jml FROM tbkaryawan WHERE ktp = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, ktp);
                ResultSet rset = preparedStatement.executeQuery();
                
                int jumlahData = 0;
                if (rset.next()) {
                    jumlahData = rset.getInt("jml");
                }
                rset.close();
                preparedStatement.close();

                if (jumlahData > 0) {
                    // Update data yang sudah ada
                    SQLStatement = "UPDATE tbkaryawan SET nama = ?, ruang = ?, password = ? WHERE ktp = ?";
                    preparedStatement = connection.prepareStatement(SQLStatement);
                    preparedStatement.setString(1, nama);
                    preparedStatement.setInt(2, ruang);
                    preparedStatement.setString(3, password);
                    preparedStatement.setString(4, ktp);
                } else {
                    // Insert data baru
                    SQLStatement = "INSERT INTO tbkaryawan (ktp, nama, ruang, password) VALUES (?, ?, ?, ?)";
                    preparedStatement = connection.prepareStatement(SQLStatement);
                    preparedStatement.setString(1, ktp);
                    preparedStatement.setString(2, nama);
                    preparedStatement.setInt(3, ruang);
                    preparedStatement.setString(4, password);
                }

                jumlahSimpan = preparedStatement.executeUpdate();

                if (jumlahSimpan < 1) {
                    adaKesalahan = true;
                    pesan = "Gagal menyimpan data karyawan";
                }

                preparedStatement.close();
                connection.close();

            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat menyimpan data karyawan\n" + ex.getMessage();
            }

        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }
        
        return !adaKesalahan;
    }
    
    // Method bacaData() - DIPERBAIKI untuk mengatasi error TYPE_FORWARD_ONLY
    public boolean bacaData(int mulai, int jumlah){
        boolean adaKesalahan = false;
        Connection connection;
        list = new Object[0][0];

        if ((connection = koneksi.getConnection()) != null){
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            ResultSet rset = null;

            try {
                // Gunakan PreparedStatement dengan tipe ResultSet yang tepat
                SQLStatement = "SELECT ktp, nama FROM tbkaryawan ORDER BY ktp LIMIT ?, ?";
                preparedStatement = connection.prepareStatement(
                    SQLStatement,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY
                );
                preparedStatement.setInt(1, mulai);
                preparedStatement.setInt(2, jumlah);
                rset = preparedStatement.executeQuery();
                
                // Hitung jumlah baris hasil query
                rset.last();
                int baris = rset.getRow();
                rset.beforeFirst();
                
                // Inisialisasi array
                list = new Object[baris][2];
                
                // Isi array dengan data
                int i = 0;
                while (rset.next()) {
                    list[i][0] = rset.getString("ktp");
                    list[i][1] = rset.getString("nama");
                    i++;
                }
                
                rset.close();
                preparedStatement.close();
                connection.close();
                
            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat membuka tabel tbkaryawan\n" + ex + "\n" + SQLStatement;
            }
            
        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }

        return !adaKesalahan;
    }

    // Method hapus() - IMPLEMENTASI LENGKAP
    public boolean hapus() {
        boolean adaKesalahan = false;
        Connection connection;

        if ((connection = koneksi.getConnection()) != null) {
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;

            try {
                SQLStatement = "DELETE FROM tbkaryawan WHERE ktp = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, ktp);
                
                int jumlahHapus = preparedStatement.executeUpdate();

                if (jumlahHapus < 1) {
                    adaKesalahan = true;
                    pesan = "Data tidak ditemukan atau gagal dihapus";
                }

                preparedStatement.close();
                connection.close();

            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat menghapus data karyawan\n" + ex + "\n" + SQLStatement;
            }

        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }

        return !adaKesalahan;
    }
}