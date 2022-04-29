package com.searchservice.app.domain.utils;

import java.io.IOException;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.searchservice.app.rest.errors.CustomException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import lombok.Data;

@Data
@Component
public class UploadDocumentUtil {
	  private static String CONTENT_TYPE="application/json";
	private final Logger log = LoggerFactory.getLogger(UploadDocumentUtil.class);

	private String baseSearchUrl;
	private String tableName;
	private String content;// "[{'name': 'karthik1'},{'name': 'karthik2'}]"
  
	public UploadDocumentSearchUtilRespnse commit() {

		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse(CONTENT_TYPE);
		RequestBody body = RequestBody.create(mediaType, content);

		String url = baseSearchUrl + "/" + tableName + "/update?";
		url += "commit=true";

		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", CONTENT_TYPE).build();

		try {

			Response response = client.newCall(request).execute();
			if (response.code() != 400) {
				return new UploadDocumentSearchUtilRespnse(true, "Document Added Successfully!");
			} else {
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),HttpStatusCode.BAD_REQUEST_EXCEPTION, "Document not uploaded!");
			}
		} catch (IOException e) {
			log.error(e.toString());

			return new UploadDocumentSearchUtilRespnse(false, "Document not uploaded! IOException.");

		}

	}

	public UploadDocumentSearchUtilRespnse softcommit() {

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse(CONTENT_TYPE);

		RequestBody body = RequestBody.create(mediaType, content);

		String url = baseSearchUrl + "/" + tableName + "/update?";

		url += "softCommit=true";

		log.debug("SOFT COMMIT");

		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", CONTENT_TYPE).build();

		try {
			// Response response =
			client.newCall(request).execute();

			return new UploadDocumentSearchUtilRespnse(true, "Document Added Successfully!");

		} catch (IOException e) {
			log.error(e.toString());

			return new UploadDocumentSearchUtilRespnse(false, "Document not uploaded! IOException.");

		}

	}
	
	private UploadDocumentSearchUtilRespnse processUploadDocumentRequest(OkHttpClient client, RequestBody body, String url) {
		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", CONTENT_TYPE).build();
		try {
			Response response = client.newCall(request).execute();
			if (response.code() != 400) {
				return new UploadDocumentSearchUtilRespnse(true, "Document Added Successfully!");
			} else {
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),HttpStatusCode.BAD_REQUEST_EXCEPTION, "Document not uploaded! Possibly, something is wrong with the data");
			}
		} catch (IOException e) {
			log.error(e.toString());
			return new UploadDocumentSearchUtilRespnse(true, "Document upload operation completed.");
		}
	}

	@Data
	public static class UploadDocumentSearchUtilRespnse {
		boolean isDocumentUploaded;
		String message;

		public UploadDocumentSearchUtilRespnse(boolean isDocumentUploaded, String message) {
			this.isDocumentUploaded = isDocumentUploaded;
			this.message = message;
		}
	}

}
