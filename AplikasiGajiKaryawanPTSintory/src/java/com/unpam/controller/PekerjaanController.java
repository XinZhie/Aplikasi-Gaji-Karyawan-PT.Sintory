/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.unpam.controller;

import com.unpam.model.Pekerjaan;
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
@WebServlet(name = "PekerjaanController", urlPatterns = {"/PekerjaanController"})
public class PekerjaanController extends HttpServlet {

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
        Pekerjaan pekerjaan = new Pekerjaan();
        String userName = "";
        
        String tombol = request.getParameter("tombol");
        String kodePekerjaan = request.getParameter("kodePekerjaan");
        String namaPekerjaan = request.getParameter("namaPekerjaan");
        String jumlahTugas = request.getParameter("jumlahTugas");
        String mulaiParameter = request.getParameter("mulai");
        String jumlahParameter = request.getParameter("jumlah");
        
        if (tombol == null) tombol = "";
        if (kodePekerjaan == null) kodePekerjaan = "";
        if (namaPekerjaan == null) namaPekerjaan = "";
        if (jumlahTugas == null) jumlahTugas = "2";
        
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
                if (!kodePekerjaan.equals("")) {
                    pekerjaan.setKodePekerjaan(kodePekerjaan);
                    pekerjaan.setNamaPekerjaan(namaPekerjaan);
                    pekerjaan.setJumlahTugas(Integer.parseInt(jumlahTugas));
                    
                    if (pekerjaan.simpan()) {
                        kodePekerjaan = "";
                        namaPekerjaan = "";
                        jumlahTugas = "2";
                        keterangan = "Sudah tersimpan";
                    } else {
                        keterangan = "Gagal menyimpan: " + pekerjaan.getPesan();
                    }
                } else {
                    keterangan = "Kode pekerjaan tidak boleh kosong";
                }
            }
            
            // Tombol Cari
            else if (tombol.equals("Cari")) {
                if (!kodePekerjaan.equals("")) {
                    if (pekerjaan.baca(kodePekerjaan)) {
                        kodePekerjaan = pekerjaan.getKodePekerjaan();
                        namaPekerjaan = pekerjaan.getNamaPekerjaan();
                        jumlahTugas = String.valueOf(pekerjaan.getJumlahTugas());
                        keterangan = "Data ditemukan";
                    } else {
                        keterangan = "Data tidak ditemukan";
                    }
                } else {
                    keterangan = "Kode pekerjaan tidak boleh kosong";
                }
            }
            
            // Tombol Hapus
            else if (tombol.equals("Hapus")) {
                if (!kodePekerjaan.equals("")) {
                    pekerjaan.setKodePekerjaan(kodePekerjaan);
                    if (pekerjaan.hapus()) {
                        kodePekerjaan = "";
                        namaPekerjaan = "";
                        jumlahTugas = "2";
                        keterangan = "Data berhasil dihapus";
                    } else {
                        keterangan = pekerjaan.getPesan();
                    }
                } else {
                    keterangan = "Kode pekerjaan tidak boleh kosong";
                }
            }
            
            // Konten Lihat Data
            String kontenLihat = "";
            if (tombol.equals("Lihat") || tombol.equals("Sebelumnya") || 
                tombol.equals("Berikutnya") || tombol.equals("Tampilkan")) {
                
                if (tombol.equals("Sebelumnya")) {
                    mulai -= jumlah;
                    if (mulai < 0) mulai = 0;
                }
                if (tombol.equals("Berikutnya")) {
                    mulai += jumlah;
                }

                if (pekerjaan.bacaData(mulai, jumlah)) {
                    Object[][] listPekerjaan = pekerjaan.getList();
                    
                    if (listPekerjaan.length > 0) {
                        kontenLihat += "<br><br><h3>Daftar Pekerjaan</h3>";
                        kontenLihat += "<table border='1' cellpadding='5' cellspacing='0'>";
                        kontenLihat += "<tr bgcolor='#cccccc'><th>No</th><th>Kode</th><th>Nama Pekerjaan</th><th>Aksi</th></tr>";
                        
                        for (int i = 0; i < listPekerjaan.length; i++) {
                            kontenLihat += "<tr>";
                            kontenLihat += "<td align='center'>" + (mulai + i + 1) + "</td>";
                            kontenLihat += "<td>" + listPekerjaan[i][0] + "</td>";
                            kontenLihat += "<td>" + listPekerjaan[i][1] + "</td>";
                            kontenLihat += "<td align='center'>";
                            kontenLihat += "<a href='PekerjaanController?tombol=Cari&kodePekerjaan=" + listPekerjaan[i][0] + "'>Edit</a> | ";
                            kontenLihat += "<a href='PekerjaanController?tombol=Hapus&kodePekerjaan=" + listPekerjaan[i][0] + "' onclick='return confirm(\"Yakin hapus data ini?\")'>Hapus</a>";
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
                    keterangan = pekerjaan.getPesan();
                }
            }
            
            // Form Input
            String konten = "<h2>Master Data Pekerjaan</h2>";
            konten += "<form action='PekerjaanController' method='post'>";
            konten += "<table>";
            konten += "<tr><td align='right'>Kode Pekerjaan</td><td><input type='text' name='kodePekerjaan' value='"+kodePekerjaan+"'> <input type='submit' name='tombol' value='Cari'></td></tr>";
            konten += "<tr><td align='right'>Nama Pekerjaan</td><td><input type='text' name='namaPekerjaan' value='"+namaPekerjaan+"' size='40'></td></tr>";
            konten += "<tr><td align='right'>Jumlah Tugas</td><td><input type='text' name='jumlahTugas' value='"+jumlahTugas+"'></td></tr>";
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
