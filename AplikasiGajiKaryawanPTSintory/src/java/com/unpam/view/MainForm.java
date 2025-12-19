/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.unpam.view;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author ajie
 */
@WebServlet(name = "MainForm", urlPatterns = {"/MainForm"})
public class MainForm extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void tampilkan(String konten, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        
        String menu = "<br><b>Master Data</b><br>"
                + "<a href=Karyawan>Karyawan</a><br>"
                + "<a href=Pekerjaan>Pekerjaan</a><br><br>"
                + "<b>Transaksi</b><br>"
                + "<a href=Gaji>Gaji</a><br>"
                + "<b>Laporan</b><br>"
                + "<a href=Gaji>Gaji</a><br><br>"
                + "<a href=LoginController>Login</a><br><br>";
        
        String topMenu = "<nav><ul>"
                + "<li><a href=#>Home</a></li>"
                + "<li><a href=#>Master Data</a>"
                + "<ul>"
                + "<li><a href=KaryawanController>Karyawan</a></li>"
                + "<li><a href=PekerjaanController>Pekerjaan</a></li>"
                + "</ul>"
                + "</li>"
                + "<li><a href=#>Transaksi</a>"
                + "<ul>"
                + "<li><a href=Gaji>Gaji</a></li>"
                + "</ul>"
                + "</li>"
                + "<li><a href=#>Laporan</a>"
                + "<ul>"
                + "<li><a href=Gaji>Gaji</a></li>"
                + "</ul>"
                + "</li>"
                + "<li><a href=LoginController>Login</a></li>"
                + "</ul>"
                + "</nav>";
        
        String userName = "";

        if (!session.isNew()) {
            try {
                userName = session.getAttribute("userName").toString();
            } catch (Exception ex) {}

            if (!((userName == null) || userName.equals(""))) {
                if (konten.equals("")) {
                    konten = "<br><h1>Selamat Datang</h1><h2>" + userName + "</h2>";
                }
                try {
                    menu = session.getAttribute("menu").toString();
                } catch (Exception ex) {}

                try {
                    topMenu = session.getAttribute("topMenu").toString();
                } catch (Exception ex) {}
            }
        }
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link href='style.css' rel='stylesheet' type='text/css' />");
            out.println("<title>Informasi Gaji Karyawan</title>");
            out.println("</head>");
            out.println("<body bgcolor=\"#808080\">");
            out.println("<center>");
            out.println("<table width=\"80%\" bgcolor=\"#eeeeee\">");
            
            // HEADER
            out.println("<tr>");
            out.println("<td colspan=\"3\" align=\"center\">");
            out.println("<br>");
            out.println("<h2 Style=\"margin-bottom:0px; margin-top:0px;\">");
            out.println("Informasi Gaji Karyawan");
            out.println("</h2>");
            out.println("<h1 Style=\"margin-bottom:0px; margin-top:0px;\">");
            out.println("PT. Sintory");
            out.println("</h1>");
            out.println("<h4 Style=\"margin-bottom:0px; margin-top:0px;\">");
            out.println("Jl. Surya Kencana No. 99 Pamulang, Tangerang Selatan, Banten");
            out.println("</h4>");
            out.println("<br>");
            out.println("</td>");
            out.println("</tr>");
            
            // TOP MENU (Full Width)
            out.println("<tr>");
            out.println("<td colspan=\"3\" align=\"center\" bgcolor=\"#ffffff\">");
            out.println(topMenu);
            out.println("</td>");
            out.println("</tr>");
            
            // CONTENT AREA
            out.println("<tr height=\"400\">");
            
            // LEFT SIDEBAR MENU
            out.println("<td width=\"200\" align=\"center\" valign=\"top\" bgcolor=\"#eeffee\">");
            out.println("<div id='menu'>");
            out.println(menu);
            out.println("</div>");
            out.println("</td>");
            
            // MAIN CONTENT (Center & Right merged)
            out.println("<td colspan=\"2\" align=\"center\" valign=\"top\" bgcolor=\"#ffffff\">");
            out.println(konten);
            out.println("</td>");
            
            out.println("</tr>");
            
            // FOOTER
            out.println("<tr>");
            out.println("<td colspan=\"3\" align=\"center\" bgcolor=\"#eeeeff\">");
            out.println("<small>");
            out.println("Copyright &copy; 2017 PT Sintory<br>");
            out.println("Jl. Surya Kencana No. 99 Pamulang, Tangerang Selatan, Banten<br>");
            out.println("</small>");
            out.println("</td>");
            out.println("</tr>");
            
            out.println("</table>");
            out.println("</center>");
            out.println("</body>");
            out.println("</html>");
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
        tampilkan("", request, response);
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
        tampilkan("", request, response);
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
