/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.unpam.controller;

import com.unpam.model.Gaji;
import com.unpam.view.MainForm;
import java.io.IOException;
import java.io.OutputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File; // Tambahan Import

/**
 *
 * @author Randi
 */
@WebServlet(name = "LaporanGajiController", urlPatterns = {"/LaporanGajiController"})
public class LaporanGajiController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Definisi Format Laporan
        String[][] formatTypeData = {
            {"PDF (Portable Document Format)", "pdf", "application/pdf"},
            {"XLSX (Microsoft Excel)", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {"XLS (Microsoft Excel 97-2003)", "xls", "application/vnd.ms-excel"},
            {"DOCX (Microsoft Word)", "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {"ODT (OpenDocument Text)", "odt", "application/vnd.oasis.opendocument.text"},
            {"RTF (Rich Text Format)", "rtf", "text/rtf"}
        };

        HttpSession session = request.getSession(true);
        String userName = "";
        
        String tombol = request.getParameter("tombol");
        String opsi = request.getParameter("opsi");
        String ktp = request.getParameter("ktp");
        String ruang = request.getParameter("ruang");
        String formatType = request.getParameter("formatType");

        if (tombol == null) tombol = "";
        if (ktp == null) ktp = "";
        if (opsi == null) opsi = "";
        if (ruang == null) ruang = "0";

        String keterangan = "<br>";
        int noType = 0;

        // Mencari index format type
        for (int i = 0; i < formatTypeData.length; i++) {
            if (formatTypeData[i][0].equals(formatType)) {
                noType = i;
                break;
            }
        }

        try {
            userName = session.getAttribute("userName").toString();
        } catch (Exception ex) {}

        // --- Logika Jika Tombol CETAK Ditekan ---
        if (!((userName == null) || userName.equals(""))) {
            if (tombol.equals("Cetak")) {
                Gaji gaji = new Gaji();
                int ruangDipilih = 0;
                try {
                    ruangDipilih = Integer.parseInt(ruang);
                } catch (NumberFormatException ex) {}

                // PERUBAHAN 1: Ubah ekstensi ke .jrxml karena kita akan compile on-the-fly
                String reportPath = getServletConfig().getServletContext().getRealPath("reports/GajiReport.jrxml");
                
                // Cek path untuk debugging
                File cekFile = new File(reportPath);
                if (!cekFile.exists()) {
                     keterangan += "File JRXML tidak ditemukan di: " + reportPath;
                     tampilkanForm(request, response, opsi, ktp, ruang, formatType, formatTypeData, keterangan);
                     return;
                }

                // Panggil cetakLaporan dengan path .jrxml
                if (gaji.cetakLaporan(opsi, ktp, ruangDipilih, formatTypeData[noType][1], reportPath)) {
                    byte[] pdfasbytes = gaji.getPdfasbytes();

                    // 1. Cek apakah byte PDF ada isinya
                    if (pdfasbytes != null && pdfasbytes.length > 0) {
                        
                        // PERUBAHAN 2: Reset Response & Set Header PDF yang Benar
                        response.reset(); // Hapus sisa HTML buffer (PENTING AGAR TIDAK JADI .HTM)
                        response.setContentType("application/pdf"); 
                        // inline = buka di browser, attachment = auto download
// Ubah 'inline' menjadi 'attachment' agar otomatis terdownload
                        response.setHeader("Content-Disposition", "inline; filename=\"LaporanGaji.pdf\"");
                        response.setContentLength(pdfasbytes.length);

                        // 4. Tulis file
                        try (OutputStream outStream = response.getOutputStream()) {
                            outStream.write(pdfasbytes);
                            outStream.flush();
                        }
                        
                        // PERUBAHAN 3: Return agar kode berhenti di sini (tidak lanjut load form di bawah)
                        return; 
                    } else {
                        keterangan += "Laporan berhasil diproses, tetapi data kosong (0 halaman).";
                        tampilkanForm(request, response, opsi, ktp, ruang, formatType, formatTypeData, keterangan);
                    }
                } else {
                    keterangan += gaji.getPesan();
                    tampilkanForm(request, response, opsi, ktp, ruang, formatType, formatTypeData, keterangan);
                }
            } else {
                tampilkanForm(request, response, opsi, ktp, ruang, formatType, formatTypeData, keterangan);
            }
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    private void tampilkanForm(HttpServletRequest request, HttpServletResponse response, 
            String opsi, String ktp, String ruang, String formatType, String[][] formatTypeData, String keterangan) 
            throws ServletException, IOException {
            
        String konten = "<h2>Mencetak Gaji</h2>";
        konten += "<form action='LaporanGajiController' method='post'>";
        konten += "<table>";
        
        // Pilihan KTP
        konten += "<tr>";
        if (opsi.equalsIgnoreCase("KTP")) {
            konten += "<td align='right'><input type='radio' checked name='opsi' value='KTP'></td>";
        } else {
            konten += "<td align='right'><input type='radio' name='opsi' value='KTP'></td>";
        }
        konten += "<td align='left'>KTP</td>";
        konten += "<td align='left'><input type='text' value='" + ktp + "' name='ktp' maxlength='15' size='15'></td>";
        konten += "</tr>";

        // Pilihan Ruang
        konten += "<tr>";
        if (opsi.equals("ruang")) {
            konten += "<td align='right'><input type='radio' checked name='opsi' value='ruang'></td>";
        } else {
            konten += "<td align='right'><input type='radio' name='opsi' value='ruang'></td>";
        }
        konten += "<td align='left'>Ruang</td>";
        konten += "<td align='left'><select name='ruang'>";
        konten += "<option selected value=0>Semua</option>";
        for (int i = 1; i <= 14; i++) {
            if (i == Integer.parseInt(ruang)) {
                konten += "<option selected value=" + i + ">" + i + "</option>";
            } else {
                konten += "<option value=" + i + ">" + i + "</option>";
            }
        }
        konten += "</select></td>";
        konten += "</tr>";

        // Pilihan Semua
        konten += "<tr>";
        if (!opsi.equals("KTP") && !opsi.equals("ruang")) { 
            konten += "<td align='right'><input type='radio' checked name='opsi' value='Semua'></td>";
        } else {
            konten += "<td align='right'><input type='radio' name='opsi' value='Semua'></td>";
        }
        konten += "<td align='left'>Semua</td>";
        konten += "<td><br></td>";
        konten += "</tr>";

        konten += "<tr><td colspan='3'><br></td></tr>";

        // Pilihan Format Laporan
        konten += "<tr>";
        konten += "<td>Format Laporan</td>";
        konten += "<td colspan=2><select name='formatType'>";
        for (String[] format : formatTypeData) {
            if (format[0].equals(formatType)) {
                konten += "<option selected value='" + format[0] + "'>" + format[0] + "</option>";
            } else {
                konten += "<option value='" + format[0] + "'>" + format[0] + "</option>";
            }
        }
        konten += "</select></td>";
        konten += "</tr>";

        konten += "<tr><td colspan='3'><b>" + keterangan.replaceAll("\n", "<br>") + "</b></td></tr>";
        konten += "<tr><td colspan='3' align='center'><input type='submit' name='tombol' value='Cetak' style='width: 100px'></td></tr>";
        konten += "</table>";
        konten += "</form>";

        new MainForm().tampilkan(konten, request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}