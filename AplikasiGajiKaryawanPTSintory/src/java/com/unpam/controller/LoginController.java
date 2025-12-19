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
 * @author Randi Abi Fahrezi
 */
@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

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
        String userName = "";
        
        try {
            userName = session.getAttribute("userName").toString();
        } catch (Exception ex) {}

        // Jika belum login (userName kosong)
        if ((userName == null) || userName.equals("")) {
            String userId = request.getParameter("userId");
            String password = request.getParameter("password");

            // Siapkan tampilan Form Login
            String konten = "<br><form action='LoginController' method='post'>"
                    + "<table>"
                    + "<tr>"
                    + "<td>User ID</td><td><input type='text' name='userId'></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td>Password</td><td><input type='password' name='password'></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td colspan=2 align=center><input type='submit' value='Login'></td>"
                    + "</tr>"
                    + "</table>"
                    + "</form>";
            
            String pesan = "";

            if (userId == null) {
                // Tampilan awal (belum klik login), pesan kosong
                pesan = "";
                new MainForm().tampilkan(konten, request, response);
            } else if (userId.equals("")) {
                // Jika User ID tidak diisi
                pesan = "<br><br><font style='color:red'>User ID harus diisi</font>";
                new MainForm().tampilkan(konten + pesan, request, response);
            } else {
                // Proses Pengecekan Login
                Karyawan karyawan = new Karyawan();
                Enkripsi enkripsi = new Enkripsi();
                
                pesan = "<br><br><font style='color:red'>User ID atau password salah</font>";

                if (karyawan.baca(userId)) { 
                    String passwordEncrypted = "";
                    try {
                        passwordEncrypted = enkripsi.hashMD5(password);
                    } catch (Exception ex) {}

                    if (passwordEncrypted.equals(karyawan.getPassword())) {
                        // --- LOGIN BERHASIL ---
                        
                        // 1. Simpan Nama User ke Session
                        session.setAttribute("userName", karyawan.getNama().equals("") ? "No Name" : karyawan.getNama());

                        // 2. Buat Menu Samping untuk User yang sudah login
                        String menu = "<br><b>Master Data</b><br>"
                                + "<a href='./KaryawanController'>Karyawan</a><br>"
                                + "<a href='./PekerjaanController'>Pekerjaan</a><br><br>"
                                + "<b>Transaksi</b><br>"
                                + "<a href='./GajiController'>Gaji</a><br><br>"
                                + "<b>Laporan</b><br>"
                                + "<a href='./LaporanGajiController'>Gaji</a><br><br>"
                                + "<a href='./LogoutController'>Logout</a><br><br>";
                        session.setAttribute("menu", menu);

                        // 3. Buat Menu Atas
                        String topMenu = "<nav><ul>"
                                + "<li><a href=#>Home</a></li>"
                                + "<li><a href=#>Master Data</a>"
                                + "<ul>"
                                + "<li><a href='./KaryawanController'>Karyawan</a></li>"
                                + "<li><a href='./PekerjaanController'>Pekerjaan</a></li>"
                                + "</ul>"
                                + "</li>"
                                + "<li><a href=#>Transaksi</a>"
                                + "<ul>"
                                + "<li><a href='./GajiController'>Gaji</a></li>"
                                + "</ul>"
                                + "</li>"
                                + "<li><a href=#>Laporan</a>"
                                + "<ul>"
                                + "<li><a href='./LaporanGajiController'>Gaji</a></li>"
                                + "</ul>"
                                + "</li>"
                                + "<li><a href='./LogoutController'>Logout</a></li>"
                                + "</ul></nav>";
                        session.setAttribute("topMenu", topMenu);

                        // 4. Set waktu timeout session (misal 15 menit)
                        session.setMaxInactiveInterval(15 * 60);
                        
                        // Redirect ke halaman utama agar tampilan berubah
                        response.sendRedirect(".");
                        return; // Keluar agar kode di bawah tidak dijalankan
                    }
                }
                
                // Jika login gagal, tampilkan form lagi dengan pesan error
                new MainForm().tampilkan(konten + pesan, request, response);
            }

        } else {
            // Jika sudah login, langsung redirect ke halaman utama
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
