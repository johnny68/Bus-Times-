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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.java.DataBaseTimings;


@Path("/timings")
public class BusTimingsServiceImpl implements BusTimingService {

	@Override
	@GET
	@Path("/getAllRoutes")
	public Response getRoutes() {
		try {
			
			DataBaseTimings dataBaseTimings = new DataBaseTimings();
			Connection connection = dataBaseTimings.getConnection();
			if(connection != null) {
				try {
					String sql = "SELECT * from timings";
					PreparedStatement statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery();
					List<Map<String, Object>> listOfMap = null;
					listOfMap = getEntitiesFromResultSet(resultSet);
					Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd MMM yyyy HH:mm:ss").create();
					String res = gson.toJson(listOfMap);
					return Response.status(200)
							.entity("{\"status\":\"success\",\"status_code\":\"200\",\"data\":" + res + "}")
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							.build();
				} catch (SQLException e) {
					e.printStackTrace();
					return Response.status(500)
							.entity("{\"status\":\"error\",\"status_code\":\"500\",\"error_msg\":\"Could not connect to the database. Please verify the issue with a system administrator\"}")
							.header("Access-Control-Allow-Origin", "*")
							.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
							.build();
				}
			}
			else {
				return Response.status(500)
						.entity("{\"status\":\"error\",\"status_code\":\"501\",\"error_msg\":\"Could not connect to the database. Please verify the issue with a system administrator\"}")
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
						.build();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@GET
	@Path("/getSpecific/{bus_id}")
	@Consumes(MediaType.TEXT_HTML)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSpecific(@PathParam("bus_id")String busID) {
	
		DataBaseTimings dataBaseTimings = new DataBaseTimings();
		try {
			Connection connection = dataBaseTimings.getConnection();
			String sql = "SELECT * FROM timings WHERE";
			if (!isNumeric(sql)) {
				sql = sql + " fromStop = '" + busID + "'";
			} else {
				sql = sql + " id = " + busID;

			}
			try {
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery();
				List<Map<String, Object>> listofMap = null;
				listofMap = getEntitiesFromResultSet(resultSet);
				Gson gson = new GsonBuilder().serializeNulls().setDateFormat("dd MMM yyyy HH:MM:SS").create();
				String res = gson.toJson(listofMap);
				dataBaseTimings.endConnection();
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
			
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(500)
					.entity("{\"status\":\"error\",\"status_code\":\"500\",\"error_msg\":\"Could not connect to the database. Please verify the issue with a system administrator\"}")
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
