package com.java.api.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.java.DataBaseUsers;


@Path("/user")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class PersonServiceImpl implements PersonService {
	
	public PersonServiceImpl() {
	}
	
	@POST
	@Path("/users")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(@FormParam("firstname")String firstname, @FormParam("middlename")String middlename , @FormParam("lastname")String lastname, @FormParam("mobilenumber")String mobilenumber, @FormParam("emailid")String emailID, @FormParam("password")String password) {
		
		System.out.println(emailID + password);
		
		if(emailID == null || password == null) {
			return Response.status(400)
					.entity("{\"status\":\"error\",\"status_code\":\"400\",\"error_msg\":\"There was an error passing in parameters. Please try again. Contact an administrator if the problem persists\"}")
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
					.build();
		}
		else {
			try {
				DataBaseUsers dataBase = new DataBaseUsers();
				Connection connection = dataBase.getConnection();
				
				if (connection != null) {
					
					String sql = "INSERT INTO users_info( firstname, middlename, lastname, mobilenumber, emailid, password) VALUES(?,?,?,?,?,?)";
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, firstname);
					preparedStatement.setString(2, middlename);
					preparedStatement.setString(3, lastname);
					preparedStatement.setString(4, mobilenumber);
					preparedStatement.setString(5, emailID);
					preparedStatement.setString(6, password);
					
					int rn = preparedStatement.executeUpdate();
					dataBase.endConnection();
					return Response.status(200).entity("{\"status\":\"success\",\"status_code\":\"200\",\"updated_rows\":\"" + rn + "\"}")
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							.build();
					
				} else {
					return Response.status(500)
							.entity("{\"status\":\"error\",\"status_code\":\"500\",\"error_msg\":\"Could not connect to the database. Please verify the issue with a system administrator\"}")
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							.build();

				}
				
			} catch (SQLException e) {
				e.printStackTrace();
				return Response.status(500)
						.entity("{\"status\":\"error\",\"status_code\":\"500\",\"error_msg\":\"Could not connect to the database. Please verify the issue with a system administrator\"}")
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
						.build();

			}
		}
	}
	
	@Override
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response loginUser(@FormParam("user_email")String userEmail, @FormParam("password")String password){
		
		if(userEmail == null || userEmail.trim().equals("") || password == null || password.trim().equals("") ) {
			
			System.out.println("Empty Request");
			return javax.ws.rs.core.Response.status(400)
					.entity("Message").header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers","Content-Type, Accept, X-Requested-With")
					.build();
			
		}
		else if (userEmail != null && password != null) {
			try {
				DataBaseUsers dataBase = new DataBaseUsers();
				Connection connection = dataBase.getConnection();
				if(connection != null) {
					try {
						String sql = "SELECT emailid, password from users_info where emailid = ?";
						PreparedStatement preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, userEmail.trim());
						//System.out.println(userEmail.trim());
						ResultSet resultSet = preparedStatement.executeQuery();
						if(!resultSet.next()) {
							dataBase.endConnection();
							return javax.ws.rs.core.Response.status(500)
									.entity("{\"status\":\"error\",\"status_code\":\"403\",\"error_msg\":\"The user provided was not found in our database, make sure you have registered the correct email address\"}")
									.header("Access-Control-Allow-Origin", "*")
									.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
									.build();
							
						}
						else {
							String password_temp = resultSet.getString("password");
							if (!password.trim().equals(password_temp)) {
								dataBase.endConnection();
								return javax.ws.rs.core.Response.status(500)
										.entity("{\"status\":\"error\",\"status_code\":\"403\",\"error_msg\":\"The provided password does not match. Please try again\"}")
										.header("Access-Control-Allow-Origin", "*")
										.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
										.build();
							}
							else {
								dataBase.endConnection();
								return javax.ws.rs.core.Response.status(200).entity("{\"status\":\"success\",\"status_code\":\"200\",\"data\":\"Successful login\"}")
										.header("Access-Control-Allow-Origin", "*")
										.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
										.build();
							}
						}
					} catch (SQLException e) {
						dataBase.endConnection();
					e.printStackTrace();
					}
				}
				else {
					return javax.ws.rs.core.Response.status(500)
							.entity("{\"status\":\"error\",\"status_code\":\"500\",\"error_msg\":\"Could not connect to the database. Please verify the issue with a system administrator\"}")
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							.build();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@GET
	@Path("/users")
	@Consumes(MediaType.TEXT_HTML)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		
		try {
			DataBaseUsers dataBase = new DataBaseUsers();
			Connection connection = dataBase.getConnection();
			if (connection != null) {
				try {
					String sql = "Select * FROM users_info";
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					ResultSet resultSet = preparedStatement.executeQuery();
					List<Map<String, Object>> listofMap = null;
					listofMap = getEntitiesFromResultSet(resultSet);
					Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd MMM yyyy HH:mm:ss").create();
					String res = gson.toJson(listofMap);
					return Response.status(200)
							.entity("{\"status\":\"success\",\"status_code\":\"200\",\"data\":" + res + "}")
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							.build();
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return null;
		
	}
	
	
	@GET
	@Path("/users/{user_id}")
	@Consumes(MediaType.TEXT_HTML)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("user_id")String userID) {
		
		try {
			
			DataBaseUsers dataBase = new DataBaseUsers();
			Connection connection = dataBase.getConnection();
			if (connection != null) {
				String query = "SELECT * FROM users_info WHERE";
				if(!isNumeric(query)) {
					query = query + " emailid = '" + userID + "'";
				}
				else {
					//query = query + " user_id = '" + userID + "'";
					query = query + " user_id = " + userID;
				}
				
				try {
					
					PreparedStatement preparedStatement = connection.prepareStatement(query);
					ResultSet resultSet = preparedStatement.executeQuery();
					List<Map<String, Object>> listofMap = null;
					listofMap = getEntitiesFromResultSet(resultSet);
					Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd MMM yyyy HH:MM:SS").create();
					String res = gson.toJson(listofMap);
					dataBase.endConnection();
					return Response.status(200).entity("{\"status\":\"success\",\"status_code\":\"200\",\"data\":" + res + "}")
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							.build();
					
				} catch (SQLException e) {
					e.printStackTrace();
					return Response.status(500)
							.entity("{\"status\":\"error\",\"status_code\":\"500\",\"error_msg\":\"There was an error in the query to the database. Please verify your query or contact a system administrator for help with the issue\"}")
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							.build();
				}
				}
				
			else {
				return Response.status(500)
						.entity("{\"status\":\"error\",\"status_code\":\"500\",\"error_msg\":\"Could not connect to the database. Please verify the issue with a system administrator\"}")
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
						.build();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500)
					.entity("{\"status\":\"error\",\"status_code\":\"500\",\"error_msg\":\"There was an error in the query to the database. Please verify your query or contact a system administrator for help with the issue\"}")
					.header("Access-Control-Allow-Origin", "*")
					.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
					.build();
		}
		
	}
	
	
	protected List<Map<String, Object>> getEntitiesFromResultSet(ResultSet resultSet) throws SQLException {
        ArrayList<Map<String, Object>> entities = new ArrayList<>();
        while (resultSet.next()) {
        	entities.add(getEntityFromResultSet(resultSet));
        }
        return entities;
    }

    protected Map<String, Object> getEntityFromResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        Map<String, Object> resultsMap = new HashMap<>();
        for (int i = 1; i <= columnCount; ++i) {
            String columnName = metaData.getColumnName(i).toLowerCase();
            Object object = resultSet.getObject(i);
            // This converts the timestamp to human readable standards before sending it in the json string
            if (columnName.equals("created_date") || columnName.equals("last_update_date") || columnName.equals("last_login_date")) {
                Timestamp ts = (Timestamp)object;
                if (ts != null) {
                    DateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                    String timeStamp = df.format(ts);
                    object = (String)timeStamp;
                } else {
                    object = null;
                }
            }
            if (object != null) {
            	resultsMap.put(columnName, object.toString());
            } else {
            	resultsMap.put(columnName, object);
            }
        }
        return resultsMap;
    }
	
    public static boolean isNumeric(String str) {
    	try {  
    		@SuppressWarnings("unused")
			double d = Double.parseDouble(str);  
    	} catch(NumberFormatException nfe) {  
    		return false;  
    	}
    	return true;
    }
	
	
	
}
