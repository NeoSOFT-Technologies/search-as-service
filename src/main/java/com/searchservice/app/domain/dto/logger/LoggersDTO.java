package com.searchservice.app.domain.dto.logger;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggersDTO {
	private String servicename;
	private String username;
	private String correlationid;
	private String ipaddress;
	private String timestamp;
	private String nameofmethod;
	private List<Object> listOfParameters;

}
