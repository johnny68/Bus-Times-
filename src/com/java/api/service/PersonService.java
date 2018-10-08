package com.java.api.service;

import javax.ws.rs.core.Response;

public interface PersonService {
	
	javax.ws.rs.core.Response loginUser(String userEmail, String password);
	Response createUser(String firstname, String middlename, String lastname, String mobilenumber, String emailID, String password );
	Response getAll();
	Response getUser(String userID);
}
