package com.searchservice.app.domain.service;


import com.searchservice.app.domain.port.api.FileUploadServicePort;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileUploadService implements FileUploadServicePort {
	
	   private final Logger log = LoggerFactory.getLogger(FileUploadService.class);

	@Override
	public String multipartUploader(MultipartFile file) {
		
		if (!file.isEmpty()) {
	        try {
	            byte[] bytes = null;
				try {
					bytes = file.getBytes();
				} catch (IOException e) {
					//e.printStackTrace();
					log.debug(e.toString());
				}
	            String completeData = new String(bytes);
	            //log.debug(completeData);
	            
	         	JSONObject jsonObject = new JSONObject(completeData);

	            System.out.println(jsonObject);

	        }finally {}
		}
		return null;
	
		
	}

}