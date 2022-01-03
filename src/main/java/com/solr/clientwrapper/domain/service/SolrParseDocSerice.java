package com.solr.clientwrapper.domain.service;


import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.solr.clientwrapper.domain.port.api.SolarDocParseServicePort;

@Service
public class SolrParseDocSerice implements SolarDocParseServicePort {
	
	   private final Logger log = LoggerFactory.getLogger(SolrParseDocSerice.class);

	@Override
	public String MultipartUploder(MultipartFile file) {
		
		if (!file.isEmpty()) {
	        try {
	            byte[] bytes = null;
				try {
					bytes = file.getBytes();
				} catch (IOException e) {

					e.printStackTrace();
				}
	            String completeData = new String(bytes);
	            log.debug(completeData);
	            
	         JSONObject jsonObject = new JSONObject(completeData);
	   
	        
	            System.out.println(jsonObject);
	           
	         
	        }finally {}
		}
		return null;
	
		
	}

}