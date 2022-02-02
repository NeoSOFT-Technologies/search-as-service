package com.searchservice.app.infrastructure.adaptor.versioning.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


public abstract class BaseClient {
    private static final String PROTOCOL_VERSION_HEADER = "X-Protocol-Version";
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    
    private final int protocolVersion;
    private final OkHttpClient client = new OkHttpClient();

    protected BaseClient(final int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public <T> T getAs(String url, Class<T> clazz) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header(PROTOCOL_VERSION_HEADER, Integer.toString(protocolVersion))
                .build();

        Response response = client.newCall(request).execute();
        
        System.out.println("base client > respEntDTO byteStream :: "
        		+objectMapper.writeValueAsString(response));
        
        return objectMapper.readValue(response.body().byteStream(), clazz);
    }
}
