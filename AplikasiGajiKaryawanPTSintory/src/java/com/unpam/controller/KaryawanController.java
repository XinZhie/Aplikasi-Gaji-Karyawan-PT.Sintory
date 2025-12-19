/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.unpam.controller;

import com.unpam.model.Enkripsi;
import com.unpam.model.Karyawan;
import com.unpam.view.MainForm;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Randi
 */
@WebServlet(name = "KaryawanController", urlPatterns = {"/KaryawanController"})
public class KaryawanController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        Karyawan karyawan = new Karyawan();
        Enkripsi enkripsi = new Enkripsi();
        
        String userName = "";
        String tombol = request.getParameter("tombol");
        String ktp = request.getParameter("ktp");
        String nama = request.getParameter("nama");
        String ruang = request.getParameter("ruang");
        String password = request.getParameter("password");
        String mulaiParameter = request.getParameter("mulai");
        String jumlahParameter = request.getParameter("jumlah");
        String ktpDipilih = request.getParameter("ktpDipilih");

        if (tombol == null) tombol = "";
        if (ktp == null) ktp = "";
        if (nama == null) nama = "";
        if (ruang == null) ruang = "1";
        if (password == null) password = "";
        if (ktpDipilih == null) ktpDipilih = "";
        
        int mulai = 0, jumlah = 10;
        try { mulai = Integer.parseInt(mulaiParameter); } catch (Exception ex) {}
        try { jumlah = Integer.parseInt(jumlahParameter); } catch (Exception ex) {}
        
        String keterangan = "<br>";
        
        try {
            userName = session.getAttribute("userName").toString();
        } catch (Exception ex) {}

        if (!((userName == null) || userName.equals(""))) {
            
            // Tombol Simpan
            if (tombol.equals("Simpan")) {
                if (!ktp.equals("")) {
                    karyawan.setKtp(ktp);
                    karyawan.setNama(nama);
                    karyawan.setRuang(Integer.parseInt(ruang));
                    String passwordEncrypted = "";
                    try {
                        passwordEncrypted = enkripsi.hashMD5(password);
                    } catch (Exception ex) {}
                    karyawan.setPassword(passwordEncrypted);
                    
                    if (karyawan.simpan()) {
                        ktp = "";
                        nama = "";
                        ruang = "1";
                        password = "";
                        keterangan = "Sudah tersimpan";
                    } else {
                        keterangan = karyawan.getPesan();
                    }
                } else {
                    keterangan = "KTP tidak boleh kosong";
                }
            } 
            
            // Tombol Cari
            else if (tombol.equals("Cari")) {
                if (!ktp.equals("")) {
                    if (karyawan.baca(ktp)) {
                        nama = karyawan.getNama();
                        ruang = String.valueOf(karyawan.getRuang());
                        password = ""; // Jangan tampilkan password
                        keterangan = "Data ditemukan";
                    } else {
                        keterangan = "Data tidak ditemukan";
                    }
                } else {
                    keterangan = "KTP tidak boleh kosong";
                }
            }
            
            // Tombol Hapus
            else if (tombol.equals("Hapus")) {
                if (!ktp.equals("")) {
                    karyawan.setKtp(ktp);
                    if (karyawan.hapus()) {
                        ktp = "";
                        nama = "";
                        ruang = "1";
                        password = "";
                        keterangan = "Data berhasil dihapus";
                    } else {
                        keterangan = karyawan.getPesan();
                    }
                } else {
                    keterangan = "KTP tidak boleh kosong";
                }
            }
            
            // Konten Lihat Data
            String kontenLihat = "";
            if (tombol.equals("Lihat") || tombol.equals("Sebelumnya") || tombol.equals("Berikutnya") || tombol.equals("Tampilkan")) {
                if (tombol.equals("Sebelumnya")) {
                    mulai -= jumlah;
                    if (mulai < 0) mulai = 0;
                }
                if (tombol.equals("Berikutnya")) {
                    mulai += jumlah;
                }

                if (karyawan.bacaData(mulai, jumlah)) {
                    Object[][] listKaryawan = karyawan.getList();
                    
                    if (listKaryawan.length > 0) {
                        kontenLihat += "<br><br><h3>Daftar Karyawan</h3>";
                        kontenLihat += "<table border='1' cellpadding='5' cellspacing='0'>";
                        kontenLihat += "<tr bgcolor='#cccccc'><th>No</th><th>KTP</th><th>Nama</th><th>Aksi</th></tr>";
                        
                        for (int i = 0; i < listKaryawan.length; i++) {
                            kontenLihat += "<tr>";
                            kontenLihat += "<td align='center'>" + (mulai + i + 1) + "</td>";
                            kontenLihat += "<td>" + listKaryawan[i][0] + "</td>";
                            kontenLihat += "<td>" + listKaryawan[i][1] + "</td>";
                            kontenLihat += "<td align='center'>";
                            kontenLihat += "<a href='KaryawanController?tombol=Cari&ktp=" + listKaryawan[i][0] + "'>Edit</a> | ";
                            kontenLihat += "<a href='KaryawanController?tombol=Hapus&ktp=" + listKaryawan[i][0] + "' onclick='return confirm(\"Yakin hapus data ini?\")'>Hapus</a>";
                            kontenLihat += "</td>";
                            kontenLihat += "</tr>";
                        }
                        
                        kontenLihat += "</table>";
                        
                        // Tombol Navigasi
                        kontenLihat += "<br>";
                        if (mulai > 0) {
                            kontenLihat += "<input type='submit' name='tombol' value='Sebelumnya'> ";
                        }
                        kontenLihat += "<input type='submit' name='tombol' value='Berikutnya'>";
                        kontenLihat += "<input type='hidden' name='mulai' value='" + mulai + "'>";
                        kontenLihat += "<input type='hidden' name='jumlah' value='" + jumlah + "'>";
                    } else {
                        kontenLihat += "<br><br><b>Tidak ada data</b>";
                    }
                } else {
                    keterangan = karyawan.getPesan();
                }
            }
            
            // Form Input
            String konten = "<h2>Master Data Karyawan</h2>";
            konten += "<form action='KaryawanController' method='post'>";
            konten += "<table>";
            konten += "<tr><td align='right'>KTP</td><td><input type='text' name='ktp' value='"+ktp+"'> <input type='submit' name='tombol' value='Cari'></td></tr>";
            konten += "<tr><td align='right'>Nama</td><td><input type='text' name='nama' value='"+nama+"'></td></tr>";
            konten += "<tr><td align='right'>Ruang</td><td><input type='text' name='ruang' value='"+ruang+"'></td></tr>";
            konten += "<tr><td align='right'>Password</td><td><input type='password' name='password' value='"+password+"'></td></tr>";
            konten += "<tr><td colspan='2' align='center'><b>"+keterangan+"</b></td></tr>";
            konten += "<tr><td colspan='2' align='center'><input type='submit' name='tombol' value='Simpan'> <input type='submit' name='tombol' value='Hapus'> <input type='submit' name='tombol' value='Lihat'></td></tr>";
            konten += "</table>";
            konten += kontenLihat;
            konten += "</form>";

            new MainForm().tampilkan(konten, request, response);
        } else {
            response.sendRedirect(".");
        }
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
