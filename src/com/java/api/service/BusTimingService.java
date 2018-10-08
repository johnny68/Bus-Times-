package com.java.api.service;

import javax.ws.rs.core.Response;

public interface BusTimingService {
	
	Response getRoutes();
	Response getSpecific(String busID);

}
