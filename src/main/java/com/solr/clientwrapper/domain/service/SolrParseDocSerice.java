package com.solr.clientwrapper.domain.service;


import com.solr.clientwrapper.domain.port.api.SolarDocParseServicePort;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SolrParseDocSerice implements SolarDocParseServicePort {
	
	   private final Logger log = LoggerFactory.getLogger(SolrParseDocSerice.class);

	@Override
	public String multipartUploder(MultipartFile file) {
		
		if (!file.isEmpty()) {
	        try {
	            byte[] bytes = null;
				try {
					bytes = file.getBytes();
				} catch (IOException e) {
					log.debug(e.toString());
				}
	            String completeData = new String(bytes);
	            
	         JSONObject jsonObject = new JSONObject(completeData);
	   
	        
	         log.debug(jsonObject.toString());
	           
	         
	        }finally {
	        	log.debug("Solr field");
	        }
		}
		return null;
	
		
	}

}