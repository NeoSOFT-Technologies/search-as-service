package com.searchservice.app.domain.port.api;

import org.springframework.web.multipart.MultipartFile;

public interface SolrFileUploadServicePort {

	String multipartUploader(MultipartFile file);
	
}
