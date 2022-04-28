package com.searchservice.app.domain.utils;

import java.io.IOException;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.searchservice.app.rest.errors.CustomExceptionHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import lombok.Data;

@Data
@Component
public class UploadDocumentUtil {

	private final Logger log = LoggerFactory.getLogger(UploadDocumentUtil.class);

	private String baseSearchUrl;
	private String tableName;
	private String content;// "[{'name': 'karthik1'},{'name': 'karthik2'}]"

	public UploadDocumentSearchUtilRespnse commit() {

		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, content);

		String url = baseSearchUrl + "/" + tableName + "/update?";
		url += "commit=true";

		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", "application/json").build();

		try {

			Response response = client.newCall(request).execute();
			if (response.code() != 400) {
				return new UploadDocumentSearchUtilRespnse(true, "Document Added Successfully!");
			} else {
				// return new UploadDocumentSolrUtilRespnse(false, "Document not uploaded!");
				throw new CustomExceptionHandler(400,HttpStatusCode.BAD_REQUEST_EXCEPTION, "Document not uploaded!");
			}
		} catch (IOException e) {
			log.error(e.toString());

			return new UploadDocumentSearchUtilRespnse(false, "Document not uploaded! IOException.");

		}

	}

	public UploadDocumentSearchUtilRespnse softcommit() {

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");

		RequestBody body = RequestBody.create(mediaType, content);

		String url = baseSearchUrl + "/" + tableName + "/update?";

		url += "softCommit=true";

		log.debug("SOFT COMMIT");

		Request request = new Request.Builder().url(url).method("POST", body)
				.addHeader("Content-Type", "application/json").build();

		try {
			// Response response =
			client.newCall(request).execute();

			return new UploadDocumentSearchUtilRespnse(true, "Document Added Successfully!");

		} catch (IOException e) {
			log.error(e.toString());

			return new UploadDocumentSearchUtilRespnse(false, "Document not uploaded! IOException.");

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
