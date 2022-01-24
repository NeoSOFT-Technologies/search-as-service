package com.searchservice.app.domain.utils;

import com.squareup.okhttp.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


@Data
public class UploadDocumentSolrUtil {

    private final Logger log = LoggerFactory.getLogger(UploadDocumentSolrUtil.class);

    private String baseSolrUrl;
    private String collectionName;
    private String content;//"[{'name': 'karthik1'},{'name': 'karthik2'}]"
    private boolean isCommit=false;


    public UploadDocumentSolrUtilRespnse commit(){

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, content);

        String url=baseSolrUrl+"/"+collectionName+"/update?";
        if(isCommit){
            url+="commit=true";
            log.debug("COMMIT");
        }else{
            url+="softCommit=true";
            log.debug("SOFT COMMIT");
        }

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            //Response response =
                    client.newCall(request).execute();

            return new UploadDocumentSolrUtilRespnse(true,"Document Added Successfully!");

        } catch (IOException e) {
            log.error(e.toString());

            return new UploadDocumentSolrUtilRespnse(false,"Document not uploaded! IOException.");

        }

    }


    @Data
    public static class UploadDocumentSolrUtilRespnse {
        boolean isDocumentUploaded;
        String message;
        public UploadDocumentSolrUtilRespnse(boolean isDocumentUploaded, String message) {
            this.isDocumentUploaded = isDocumentUploaded;
            this.message = message;
        }
    }



}
