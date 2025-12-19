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

public class Pekerjaan {

    private String kodePekerjaan, namaPekerjaan;
    private int jumlahTugas;
    private String pesan;
    private Object[][] list;

    private final Koneksi koneksi = new Koneksi();
    private final PesanDialog pesanDialog = new PesanDialog();

    // Getter dan Setter
    public String getKodePekerjaan() {
        return kodePekerjaan;
    }

    public void setKodePekerjaan(String kodePekerjaan) {
        this.kodePekerjaan = kodePekerjaan;
    }

    public String getNamaPekerjaan() {
        return namaPekerjaan;
    }

    public void setNamaPekerjaan(String namaPekerjaan) {
        this.namaPekerjaan = namaPekerjaan;
    }

    public int getJumlahTugas() {
        return jumlahTugas;
    }

    public void setJumlahTugas(int jumlahTugas) {
        this.jumlahTugas = jumlahTugas;
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

    // Method baca() - untuk cari data
    public boolean baca(String kodeCari) {
        boolean adaKesalahan = false;
        Connection connection;

        if ((connection = koneksi.getConnection()) != null) {
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;
            ResultSet rset = null;

            try {
                SQLStatement = "SELECT kodepekerjaan, namapekerjaan, jumlahtugas FROM tbpekerjaan WHERE kodepekerjaan = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, kodeCari);
                rset = preparedStatement.executeQuery();

                if (rset.next()) {
                    this.kodePekerjaan = rset.getString("kodepekerjaan");
                    this.namaPekerjaan = rset.getString("namapekerjaan");
                    this.jumlahTugas = rset.getInt("jumlahtugas");
                } else {
                    adaKesalahan = true;
                    pesan = "Data pekerjaan tidak ditemukan";
                }

                rset.close();
                preparedStatement.close();
                connection.close();

            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat membaca data pekerjaan\n" + ex + "\n" + SQLStatement;
            }

        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }

        return !adaKesalahan;
    }

    // Method simpan() - DIPERBAIKI menggunakan PreparedStatement
    public boolean simpan() {
        boolean adaKesalahan = false;
        Connection connection;

        if ((connection = koneksi.getConnection()) != null) {

            int jumlahSimpan = 0;
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;

            try {
                // Cek apakah data sudah ada (untuk update)
                SQLStatement = "SELECT COUNT(*) as jml FROM tbpekerjaan WHERE kodepekerjaan = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, kodePekerjaan);
                ResultSet rset = preparedStatement.executeQuery();
                
                int jumlahData = 0;
                if (rset.next()) {
                    jumlahData = rset.getInt("jml");
                }
                rset.close();
                preparedStatement.close();

                if (jumlahData > 0) {
                    // Update data yang sudah ada
                    SQLStatement = "UPDATE tbpekerjaan SET namapekerjaan = ?, jumlahtugas = ? WHERE kodepekerjaan = ?";
                    preparedStatement = connection.prepareStatement(SQLStatement);
                    preparedStatement.setString(1, namaPekerjaan);
                    preparedStatement.setInt(2, jumlahTugas);
                    preparedStatement.setString(3, kodePekerjaan);
                } else {
                    // Insert data baru
                    SQLStatement = "INSERT INTO tbpekerjaan (kodepekerjaan, namapekerjaan, jumlahtugas) VALUES (?, ?, ?)";
                    preparedStatement = connection.prepareStatement(SQLStatement);
                    preparedStatement.setString(1, kodePekerjaan);
                    preparedStatement.setString(2, namaPekerjaan);
                    preparedStatement.setInt(3, jumlahTugas);
                }

                jumlahSimpan = preparedStatement.executeUpdate();

                if (jumlahSimpan < 1) {
                    adaKesalahan = true;
                    pesan = "Gagal menyimpan data pekerjaan";
                }
                
                preparedStatement.close();
                connection.close();

            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat menyimpan data pekerjaan\n" + ex.getMessage();
            }

        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }
        
        return !adaKesalahan;
    }
    
    // Method hapus()
    public boolean hapus() {
        boolean adaKesalahan = false;
        Connection connection;

        if ((connection = koneksi.getConnection()) != null) {
            String SQLStatement = "";
            PreparedStatement preparedStatement = null;

            try {
                SQLStatement = "DELETE FROM tbpekerjaan WHERE kodepekerjaan = ?";
                preparedStatement = connection.prepareStatement(SQLStatement);
                preparedStatement.setString(1, kodePekerjaan);
                
                int jumlahHapus = preparedStatement.executeUpdate();

                if (jumlahHapus < 1) {
                    adaKesalahan = true;
                    pesan = "Data tidak ditemukan atau gagal dihapus";
                }

                preparedStatement.close();
                connection.close();

            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat menghapus data pekerjaan\n" + ex + "\n" + SQLStatement;
            }

        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }

        return !adaKesalahan;
    }
    
    // Method bacaData() - DIPERBAIKI
    public boolean bacaData(int mulai, int jumlah){
        boolean adaKesalahan = false;
        Connection connection;
        list = new Object[0][0];

        if ((connection = koneksi.getConnection()) != null){
            String SQLStatemen = "";
            PreparedStatement preparedStatement = null;
            ResultSet rset = null;

            try {
                // Ambil data dengan LIMIT menggunakan PreparedStatement
                SQLStatemen = "SELECT kodepekerjaan, namapekerjaan FROM tbpekerjaan ORDER BY kodepekerjaan LIMIT ?, ?";
                preparedStatement = connection.prepareStatement(
                    SQLStatemen,
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
                    list[i][0] = rset.getString("kodepekerjaan");
                    list[i][1] = rset.getString("namapekerjaan");
                    i++;
                }
                
                rset.close();
                preparedStatement.close();
                connection.close();
                
            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat membuka tabel tbpekerjaan\n" + ex + "\n" + SQLStatemen;
            }
            
        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }

        return !adaKesalahan;
    }
}