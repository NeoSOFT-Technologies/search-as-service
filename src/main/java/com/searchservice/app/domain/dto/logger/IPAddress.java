package com.searchservice.app.domain.dto.logger;
import javax.servlet.http.HttpServletRequest;

public class IPAddress{

	HttpServletRequest request;
	public String getIPAddress(){
		
        return request.getRemoteAddr();
    }
}
