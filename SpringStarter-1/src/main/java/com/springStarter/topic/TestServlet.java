package com.springStarter.topic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 1L;

	@RequestMapping( value="download2")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		performTask(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		performTask(request, response);
	}
	@RequestMapping( value="download3")
	private void performTask(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		String pdfFileName = "test.pdf";
		//getResourceAsStream("/Users/lauchiulam/Downloads/emailAttachment/
		//String contextPath = getServletContext().getRealPath(File.separator);
		String contextPath = getServletContext().getRealPath("/Users/lauchiulam/Downloads/emailAttachment/");
		
		File pdfFile = new File(contextPath + pdfFileName);
		
		
		System.out.println(contextPath);
		System.out.println(pdfFileName);
		System.out.println(pdfFile);
		
		
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + pdfFileName);
		response.setContentLength((int) pdfFile.length());

		FileInputStream fileInputStream = new FileInputStream(pdfFile);
		OutputStream responseOutputStream = response.getOutputStream();
		int bytes = 0;
		while ((bytes = fileInputStream.read()) != -1) {
			responseOutputStream.write(bytes);
		}

	}

}
