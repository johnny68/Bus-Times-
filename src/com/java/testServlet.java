package com.java;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Servlet implementation class testServlet
 */
//@WebServlet("/testServlet")
public class testServlet extends ServletContainer {

	private static final long serialVersionUID = 1L;

    public testServlet() {
    }
	public void init(ServletConfig config) throws ServletException {
		
	}
	public void destroy() {
		DataBaseUsers dataBase = new DataBaseUsers();
		dataBase.endConnection();
		System.out.println("Inside Destroy");
	}
	public ServletConfig getServletConfig() {
		return null;
	}
	public String getServletInfo() {
		return null; 
	}

	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		System.out.println("Inside Service");
	}

}
