package com.searchservice.app.domain.port.api;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadServicePort {

	String multipartUploader(MultipartFile file);
	
}
