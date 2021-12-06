package com.solr.clientwrapper.domain.service;


import java.io.IOException;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SolrParseDocSerice  {
	public String MultipartUploder(MultipartFile file) {
		// TODO Auto-generated method stub
		
		if (!file.isEmpty()) {
	        try {
	            byte[] bytes = null;
				try {
					bytes = file.getBytes();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            String completeData = new String(bytes);
	            System.out.println(completeData);
	            
	         JSONObject jsonObject = new JSONObject(completeData);
	   
	        
	            System.out.println(jsonObject);
	           
	         
	        }finally {
	        	
	          	System.out.println( );
	        	
	        }
		}
		return null;
	
		
	}

}