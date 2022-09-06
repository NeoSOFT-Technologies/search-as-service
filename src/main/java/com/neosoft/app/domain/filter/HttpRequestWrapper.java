package com.neosoft.app.domain.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.neosoft.app.rest.errors.CustomException;
import com.neosoft.app.rest.errors.HttpStatusCode;

public class HttpRequestWrapper extends HttpServletRequestWrapper {

	private final String body;

	public HttpRequestWrapper(HttpServletRequest request) throws IOException {
		// So that other request method behave just like before
	    super(request);

		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try(InputStream inputStream = request.getInputStream()) {
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw new CustomException(400,HttpStatusCode.BAD_REQUEST_EXCEPTION,ex.getMessage());
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
		// Store request pody content in 'body' variable
		body = stringBuilder.toString();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		return new ServletInputStream() {
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener listener) {
				/*
				 * No need to override this method as per our current filters definitions
				 */
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}

	// Use this method to read the request body N times
	public String getBody() {
		return this.body;
	}
}
