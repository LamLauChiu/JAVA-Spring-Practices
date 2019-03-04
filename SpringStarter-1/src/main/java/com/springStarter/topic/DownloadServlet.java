package com.springStarter.topic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@WebServlet("/download")
@RestController
public class DownloadServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet{
    private final int ARBITARY_SIZE = 1048;
 
    @RequestMapping( value="download")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
     
        resp.setContentType("text/plain");
        //resp.setHeader("Content-disposition", "attachment; filename=sample.txt");
        resp.setContentType("application/pdf");
        resp.setHeader("Content-disposition", "attachment; filename=sample.txt");
        
        //
        try(InputStream in = req.getServletContext().getResourceAsStream("/WEB-INF/sample.txt");
        //try(InputStream in = req.getServletContext().getResourceAsStream("/Users/lauchiulam/Downloads/emailAttachment/sample.txt");
       //try(InputStream in = req.getServletContext().getResourceAsStream("/Users/lauchiulam/Downloads/emailAttachment/test.pdf");  
        OutputStream out = resp.getOutputStream()) {
 
            byte[] buffer = new byte[ARBITARY_SIZE];
         
            int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }
        }
    }
}
