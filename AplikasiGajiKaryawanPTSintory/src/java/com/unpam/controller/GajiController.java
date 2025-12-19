/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.unpam.controller;

import com.unpam.model.Gaji;
import com.unpam.model.Karyawan;
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
@WebServlet(name = "GajiController", urlPatterns = {"/GajiController"})
public class GajiController extends HttpServlet {

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
        Pekerjaan pekerjaan = new Pekerjaan();
        Gaji gaji = new Gaji(); 
        
        String userName = "";
        String tombol = request.getParameter("tombol");
        String tombolKaryawan = request.getParameter("tombolKaryawan");
        String ktp = request.getParameter("ktp");
        String namaKaryawan = request.getParameter("namaKaryawan");
        String ruang = request.getParameter("ruang");
        String mulaiParameter = request.getParameter("mulai");
        String jumlahParameter = request.getParameter("jumlah");
        String ktpDipilih = request.getParameter("ktpDipilih");
        
        String tombolPekerjaan = request.getParameter("tombolPekerjaan");
        String kodePekerjaan = request.getParameter("kodePekerjaan");
        String namaPekerjaan = request.getParameter("namaPekerjaan");
        String jumlahTugas = request.getParameter("jumlahTugas");
        String kodePekerjaanDipilih = request.getParameter("kodePekerjaanDipilih");
        
        String gajibersih = request.getParameter("gajibersih");
        String gajikotor = request.getParameter("gajikotor");
        String tunjangan = request.getParameter("tunjangan");

        if (tombol == null) tombol = "";
        if (tombolKaryawan == null) tombolKaryawan = "";
        if (ktp == null) ktp = "";
        if (namaKaryawan == null) namaKaryawan = "";
        if (ruang == null) ruang = "";
        if (ktpDipilih == null) ktpDipilih = "";
        if (tombolPekerjaan == null) tombolPekerjaan = "";
        if (kodePekerjaan == null) kodePekerjaan = "";
        if (namaPekerjaan == null) namaPekerjaan = "";
        if (jumlahTugas == null) jumlahTugas = "";
        if (kodePekerjaanDipilih == null) kodePekerjaanDipilih = "";
        if (gajibersih == null) gajibersih = "0";
        if (gajikotor == null) gajikotor = "0";
        if (tunjangan == null) tunjangan = "0";

        int mulai = 0;
        int jumlah = 10;
        try {
            if (mulaiParameter != null) mulai = Integer.parseInt(mulaiParameter);
            if (jumlahParameter != null) jumlah = Integer.parseInt(jumlahParameter);
        } catch (NumberFormatException ex) {}

        String keterangan = "<br>";
        try {
            if (session.getAttribute("userName") != null) {
                userName = session.getAttribute("userName").toString();
            }
        } catch (Exception ex) {}

        // --- 3. Logika Utama ---
        if (!((userName == null) || userName.equals(""))) {
            
            // === LOGIKA KARYAWAN (Cari/Pilih) ===
            // Jika merah disini: Pastikan Karyawan.java punya method baca() dan getKtp()
            if (tombolKaryawan.equals("Cari")) {
                if (!ktp.equals("")) {
                    if (karyawan.baca(ktp)) { 
                        ktp = karyawan.getKtp();
                        namaKaryawan = karyawan.getNama();
                        ruang = Integer.toString(karyawan.getRuang());
                        keterangan = "<br>";
                    } else {
                        namaKaryawan = ""; ruang = ""; keterangan = "KTP " + ktp + " tidak ada";
                    }
                } else {
                    keterangan = "KTP harus diisi";
                }
            } else if (tombolKaryawan.equals("Pilih")) {
                if (!ktpDipilih.equals("")) {
                    if (karyawan.baca(ktpDipilih)) {
                        ktp = karyawan.getKtp();
                        namaKaryawan = karyawan.getNama();
                        ruang = Integer.toString(karyawan.getRuang());
                        keterangan = "<br>";
                    }
                }
            }

            // === TAMPILAN POPUP KARYAWAN ===
            String kontenLihat = "";
            if (tombolKaryawan.equals("Lihat") || tombolKaryawan.equals("Sebelumnya") || tombolKaryawan.equals("Berikutnya") || tombolKaryawan.equals("Tampilkan")) {
                // Logika Pagination Karyawan
                if (tombolKaryawan.equals("Sebelumnya")) mulai -= jumlah;
                if (tombolKaryawan.equals("Berikutnya")) mulai += jumlah;
                if (mulai < 0) mulai = 0;

                kontenLihat += "<tr><td colspan='2' align='center'><table>";
                
                // Jika merah disini: Pastikan Karyawan.java punya method bacaData() dan getList()
                if (karyawan.bacaData(mulai, jumlah)) {
                    Object[][] listKaryawan = karyawan.getList();
                    if (listKaryawan != null) {
                        for (Object[] k : listKaryawan) {
                            kontenLihat += "<tr><td><input type='radio' name='ktpDipilih' value='" + k[0] + "'></td>";
                            kontenLihat += "<td>" + k[0] + "</td><td>" + k[1] + "</td></tr>";
                        }
                    }
                } else {
                    keterangan = karyawan.getPesan();
                }
                kontenLihat += "</table></td></tr>";
                
                // Tombol Navigasi Karyawan
                kontenLihat += "<tr><td colspan='2' align='center'><input type='submit' name='tombolKaryawan' value='Sebelumnya'> ";
                kontenLihat += "<input type='submit' name='tombolKaryawan' value='Pilih'> ";
                kontenLihat += "<input type='submit' name='tombolKaryawan' value='Berikutnya'></td></tr>";
            }

            // === LOGIKA PEKERJAAN (Cari/Pilih) ===
            // Jika merah disini: Pastikan Pekerjaan.java punya method baca()
            if (tombolPekerjaan.equals("Cari")) {
                if (!kodePekerjaan.equals("")) {
                    if (pekerjaan.baca(kodePekerjaan)) {
                        kodePekerjaan = pekerjaan.getKodePekerjaan();
                        namaPekerjaan = pekerjaan.getNamaPekerjaan();
                        jumlahTugas = Integer.toString(pekerjaan.getJumlahTugas());
                    } else {
                        keterangan = "Kode pekerjaan tidak ada";
                    }
                }
            } else if (tombolPekerjaan.equals("Pilih")) {
                if (!kodePekerjaanDipilih.equals("")) {
                    if (pekerjaan.baca(kodePekerjaanDipilih)) {
                        kodePekerjaan = pekerjaan.getKodePekerjaan();
                        namaPekerjaan = pekerjaan.getNamaPekerjaan();
                        jumlahTugas = Integer.toString(pekerjaan.getJumlahTugas());
                    }
                }
            }

            // === TAMPILAN POPUP PEKERJAAN ===
            if (tombolPekerjaan.equals("Lihat") || tombolPekerjaan.equals("Sebelumnya") || tombolPekerjaan.equals("Berikutnya") || tombolPekerjaan.equals("Tampilkan")) {
                 if (tombolPekerjaan.equals("Sebelumnya")) mulai -= jumlah;
                 if (tombolPekerjaan.equals("Berikutnya")) mulai += jumlah;
                 if (mulai < 0) mulai = 0;

                 kontenLihat = "<tr><td colspan='2' align='center'><table>"; // Reset kontenLihat
                 // Jika merah disini: Pastikan Pekerjaan.java punya method bacaData()
                 if (pekerjaan.bacaData(mulai, jumlah)) {
                     Object[][] listPekerjaan = pekerjaan.getList();
                     if (listPekerjaan != null) {
                         for (Object[] p : listPekerjaan) {
                             kontenLihat += "<tr><td><input type='radio' name='kodePekerjaanDipilih' value='" + p[0] + "'></td>";
                             kontenLihat += "<td>" + p[0] + "</td><td>" + p[1] + "</td></tr>";
                         }
                     }
                 }
                 kontenLihat += "</table></td></tr>";
                 kontenLihat += "<tr><td colspan='2' align='center'><input type='submit' name='tombolPekerjaan' value='Sebelumnya'> ";
                 kontenLihat += "<input type='submit' name='tombolPekerjaan' value='Pilih'> ";
                 kontenLihat += "<input type='submit' name='tombolPekerjaan' value='Berikutnya'></td></tr>";
            }

            // === LOGIKA SIMPAN & HAPUS GAJI ===
            // Jika merah disini: Pastikan Gaji.java (yang saya berikan sebelumnya) sudah di-copy
            if (!tombol.equals("")) {
                if (tombol.equals("Simpan")) {
                    if (!ktp.equals("") && !kodePekerjaan.equals("")) {
                        gaji.setKtp(ktp);
                        // Membuat array object untuk data gaji
                        Object[][] dataGaji = {{kodePekerjaan, gajibersih, gajikotor, tunjangan}};
                        gaji.setListGaji(dataGaji); // Method ini harus ada di Gaji.java
                        
                        if (gaji.simpan()) { // Method ini harus ada di Gaji.java
                            keterangan = "Data gaji berhasil disimpan";
                            // Reset form
                            ktp = ""; namaKaryawan = ""; ruang = "";
                            kodePekerjaan = ""; namaPekerjaan = ""; jumlahTugas = "";
                            gajibersih = ""; gajikotor = ""; tunjangan = "";
                        } else {
                            keterangan = "Gagal Simpan: " + gaji.getPesan();
                        }
                    } else {
                        keterangan = "KTP dan Kode Pekerjaan harus diisi";
                    }
                } else if (tombol.equals("Hapus")) {
                    if (!ktp.equals("") && !kodePekerjaan.equals("")) {
                        if (gaji.hapus(ktp, kodePekerjaan)) { // Method ini harus ada di Gaji.java
                            keterangan = "Data gaji berhasil dihapus";
                            ktp = ""; namaKaryawan = ""; ruang = "";
                            kodePekerjaan = ""; namaPekerjaan = ""; jumlahTugas = "";
                        } else {
                            keterangan = "Gagal Hapus: " + gaji.getPesan();
                        }
                    }
                }
            }

            // === MEMBUAT FORM HTML ===
            String konten = "<h2>Input Gaji Karyawan</h2>";
            konten += "<form action='GajiController' method='post'><table>";
            
            // Input Karyawan
            konten += "<tr><td align='right'>KTP</td><td><input type='text' name='ktp' value='" + ktp + "'> ";
            konten += "<input type='submit' name='tombolKaryawan' value='Cari'> <input type='submit' name='tombolKaryawan' value='Lihat'></td></tr>";
            konten += "<tr><td align='right'>Nama</td><td><input type='text' readonly name='namaKaryawan' value='" + namaKaryawan + "'></td></tr>";
            konten += "<tr><td align='right'>Ruang</td><td><input type='text' readonly name='ruang' value='" + ruang + "'></td></tr>";
            
            // Tampilkan Tabel Popup jika ada
            if (!tombolKaryawan.equals("")) konten += kontenLihat;
            
            konten += "<tr><td colspan='2'><hr></td></tr>";
            
            // Input Pekerjaan
            konten += "<tr><td align='right'>Kode Pek</td><td><input type='text' name='kodePekerjaan' value='" + kodePekerjaan + "'> ";
            konten += "<input type='submit' name='tombolPekerjaan' value='Cari'> <input type='submit' name='tombolPekerjaan' value='Lihat'></td></tr>";
            konten += "<tr><td align='right'>Nama Pek</td><td><input type='text' readonly name='namaPekerjaan' value='" + namaPekerjaan + "'></td></tr>";
            konten += "<tr><td align='right'>Jml Tugas</td><td><input type='text' readonly name='jumlahTugas' value='" + jumlahTugas + "'></td></tr>";

            // Tampilkan Tabel Popup Pekerjaan jika ada
            if (!tombolPekerjaan.equals("")) konten += kontenLihat;

            // Input Angka Gaji
            konten += "<tr><td align='right'>Gaji Bersih</td><td><input type='text' name='gajibersih' value='" + gajibersih + "'></td></tr>";
            konten += "<tr><td align='right'>Gaji Kotor</td><td><input type='text' name='gajikotor' value='" + gajikotor + "'></td></tr>";
            konten += "<tr><td align='right'>Tunjangan</td><td><input type='text' name='tunjangan' value='" + tunjangan + "'></td></tr>";
            
            // Tombol Aksi
            konten += "<tr><td colspan='2' align='center'><input type='submit' name='tombol' value='Simpan'> <input type='submit' name='tombol' value='Hapus'></td></tr>";
            
            // Pesan Status
            konten += "<tr><td colspan='2'><b>" + keterangan + "</b></td></tr>";
            
            konten += "</table></form>";

            // Render ke View
            new MainForm().tampilkan(konten, request, response);

        } else {
            response.sendRedirect("."); // Redirect jika sesi habis
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
