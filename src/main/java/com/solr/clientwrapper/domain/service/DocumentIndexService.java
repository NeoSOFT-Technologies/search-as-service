package com.solr.clientwrapper.domain.service;



import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solr.clientwrapper.infrastructure.entity.DocumentIndex;
import com.solr.clientwrapper.infrastructure.repository.DocIndexRepo;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentIndexService {

    private DocIndexRepo docIndexRepo;
    private DocumentIndex docIndex;

    public DocumentIndexService(DocIndexRepo docRepository) {
        this.docIndexRepo = docRepository;
    }

	
	  public DocumentIndex save(DocumentIndex docIndex) { return
	  docIndexRepo.save(docIndex); }
	  
	  public void save(List<DocumentIndex> docs) { docIndexRepo.saveAll(docs); }
	 
    public Iterable<DocumentIndex> list() {
        return docIndexRepo.findAll();
    }

	/*
	 * public DocumentIndex getJson(String data, List<MultipartFile> file1) {
	 * DocumentIndex docIndexjson = new DocumentIndex(); try {
	 * 
	 * ObjectMapper objMapper = new ObjectMapper(); docIndexjson=
	 * objMapper.readValue(data, DocumentIndex.class); } catch (IOException err) {
	 * 
	 * }
	 * 
	 * 
	 * return docIndexjson; }
	 */
}
