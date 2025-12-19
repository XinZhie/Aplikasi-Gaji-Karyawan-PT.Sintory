/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.unpam.view.PesanDialog;

// --- IMPORT WAJIB UNTUK BERBAGAI FORMAT ---
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter; 
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter; 
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.engine.export.JRXlsExporter; 

import java.io.ByteArrayOutputStream; 
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 *
 * @author Randi
 */
public class Gaji {
    private String ktp;
    private String pesan;
    private Object[][] listGaji;
    
    private byte[] pdfasbytes; 
    
    private final Koneksi koneksi = new Koneksi();
    private final PesanDialog pesanDialog = new PesanDialog();

    public String getKtp() { return ktp; }
    public void setKtp(String ktp) { this.ktp = ktp; }
    public String getPesan() { return pesan; }
    public byte[] getPdfasbytes() { return pdfasbytes; }
    public void setListGaji(Object[][] listGaji) { this.listGaji = listGaji; }
    public Object[][] getListGaji() { return listGaji; }

    // --- Metode Simpan ---
    public boolean simpan() {
        boolean adaKesalahan = false;
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            try {
                String SQLStatemen = "insert into tbgaji (ktp, kodepekerjaan, gajibersih, gajikotor, tunjangan) values (?,?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(SQLStatemen);
                preparedStatement.setString(1, ktp);
                preparedStatement.setString(2, listGaji[0][0].toString());
                preparedStatement.setString(3, listGaji[0][1].toString());
                preparedStatement.setString(4, listGaji[0][2].toString());
                preparedStatement.setString(5, listGaji[0][3].toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (Exception ex) {
                adaKesalahan = true;
                pesan = "Gagal simpan: " + ex;
            } finally {
                try { connection.close(); } catch (SQLException ex) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Gagal koneksi";
        }
        return !adaKesalahan;
    }

    // --- Metode Hapus ---
    public boolean hapus(String ktp, String kodePekerjaan) {
        boolean adaKesalahan = false;
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            try {
                // UPDATE: Pakai TRIM juga saat hapus agar aman
                String SQLStatemen = "delete from tbgaji where TRIM(ktp)=? and TRIM(kodepekerjaan)=?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQLStatemen);
                preparedStatement.setString(1, ktp);
                preparedStatement.setString(2, kodePekerjaan);
                int jumlah = preparedStatement.executeUpdate();
                if (jumlah < 1) { adaKesalahan = true; pesan = "Data tidak ditemukan"; }
                preparedStatement.close();
            } catch (Exception ex) {
                adaKesalahan = true;
                pesan = "Gagal hapus: " + ex;
            } finally {
                try { connection.close(); } catch (SQLException ex) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Gagal koneksi";
        }
        return !adaKesalahan;
    }

    // --- METODE BACA (AUTO-FILL) YANG DIPERBAIKI ---
    public boolean baca(String ktp, String kodePekerjaan) {
        boolean adaKesalahan = false;
        Connection connection;
        listGaji = null; // Reset list

        if ((connection = koneksi.getConnection()) != null) {
            try {
                // PERBAIKAN PENTING DI SINI:
                // Gunakan fungsi TRIM(...) di SQL untuk mengabaikan spasi di database
                String SQLStatemen = "select * from tbgaji where TRIM(ktp)=? and TRIM(kodepekerjaan)=?";
                
                PreparedStatement preparedStatement = connection.prepareStatement(SQLStatemen);
                preparedStatement.setString(1, ktp);
                preparedStatement.setString(2, kodePekerjaan);
                
                java.sql.ResultSet rset = preparedStatement.executeQuery();
                
                // HAPUS BARIS INI: preparedStatement.executeQuery(); 
                // (Ini bug di kode lama Anda, menjalankan query 2x bikin error di beberapa driver)

                if (rset.next()) {
                    listGaji = new Object[][]{{
                        rset.getString("kodepekerjaan"), 
                        rset.getString("gajibersih"), 
                        rset.getString("gajikotor"), 
                        rset.getString("tunjangan")
                    }};
                } else {
                    adaKesalahan = true;
                    pesan = "Data gaji tidak ditemukan";
                }
                
                preparedStatement.close();
                rset.close();
            } catch (Exception ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat membaca data: " + ex;
            } finally {
                try { connection.close(); } catch (SQLException ex) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Gagal koneksi";
        }
        return !adaKesalahan;
    }

    // --- Metode Update ---
    public boolean ubah() {
        boolean adaKesalahan = false;
        Connection connection;
        if ((connection = koneksi.getConnection()) != null) {
            try {
                // UPDATE: Pakai TRIM juga di sini agar aman
                String SQLStatemen = "update tbgaji set gajibersih=?, gajikotor=?, tunjangan=? where TRIM(ktp)=? and TRIM(kodepekerjaan)=?";
                PreparedStatement preparedStatement = connection.prepareStatement(SQLStatemen);
                preparedStatement.setString(1, listGaji[0][1].toString()); // gaji bersih
                preparedStatement.setString(2, listGaji[0][2].toString()); // gaji kotor
                preparedStatement.setString(3, listGaji[0][3].toString()); // tunjangan
                preparedStatement.setString(4, ktp);                       // WHERE ktp
                preparedStatement.setString(5, listGaji[0][0].toString()); // WHERE kodepekerjaan
                
                int jumlahUpdate = preparedStatement.executeUpdate();
                if (jumlahUpdate < 1) {
                    adaKesalahan = true;
                    pesan = "Data tidak ditemukan untuk diubah";
                }
                preparedStatement.close();
            } catch (Exception ex) {
                adaKesalahan = true;
                pesan = "Gagal ubah: " + ex;
            } finally {
                try { connection.close(); } catch (SQLException ex) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Gagal koneksi";
        }
        return !adaKesalahan;
    }
    
    // --- METODE CETAK (TIDAK BERUBAH) ---
    public boolean cetakLaporan(String opsi, String ktp, int ruang, String formatExport, String filePath) {
        boolean berhasil = false;
        Connection connection;
        
        if ((connection = koneksi.getConnection()) != null) {
            try {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("paramOpsi", opsi); 
                parameters.put("paramKtp", ktp);
                parameters.put("paramRuang", ruang);
                
                File reportFile = new File(filePath);
                if (!reportFile.exists()) {
                    pesan = "File .jrxml tidak ditemukan";
                    return false;
                }

                JasperDesign jasperDesign = JRXmlLoader.load(reportFile);
                JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                if (formatExport.equalsIgnoreCase("pdf")) {
                    JasperExportManager.exportReportToPdfStream(jasperPrint, out);
                } else if (formatExport.equalsIgnoreCase("xlsx")) {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                    exporter.exportReport();
                } else if (formatExport.equalsIgnoreCase("docx")) {
                    JRDocxExporter exporter = new JRDocxExporter();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                    exporter.exportReport();
                } else if (formatExport.equalsIgnoreCase("xls")) {
                    JRXlsExporter exporter = new JRXlsExporter();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                    exporter.exportReport();
                } else {
                    JasperExportManager.exportReportToPdfStream(jasperPrint, out);
                }

                pdfasbytes = out.toByteArray();
                berhasil = true;
            } catch (Exception ex) {
                pesan = "Gagal mencetak laporan: " + ex.getMessage();
                ex.printStackTrace(); 
            } finally {
                try { if (connection != null) connection.close(); } catch (SQLException ex) {}
            }
        } else {
            pesan = "Gagal koneksi database";
        }
        return berhasil;
    }
}
