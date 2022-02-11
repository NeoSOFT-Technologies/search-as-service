
package com.searchservice.app.domain.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.port.api.ManageTableServicePort;

@Service
@Transactional
public class TableDeleteService implements TableDeleteServicePort{

	@Value("${table-delete-file.path}")
	String deleteRecordFilePath;
	
	@Value("${table-delete-duration.days}")
	long tableDeleteDuration;
	SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

	private ManageTableServicePort manageTableServicePort;
	
	public TableDeleteService(ManageTableServicePort manageTableServicePort) {
		this.manageTableServicePort = manageTableServicePort;
	}
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String TABLE_DELETE_INITIALIZE_ERROR_MSG = "Error While Initializing Deletion For Table: {}"; 
	private static final String TABLE_DELETE_UNDO_ERROR_MSG = "Undo Table Delete Failed , Invalid CLient ID Provided";
	@Override
	public ResponseDTO initializeTableDelete(int clientId, String tableName) {
		ResponseDTO deleteRecordInsertionResponse = new ResponseDTO();
		  File file=new File(deleteRecordFilePath + ".txt");
		  if((clientId>0) && (tableName!=null)) {
		  try(FileWriter fw = new FileWriter(file, true);
		   BufferedWriter bw = new BufferedWriter(fw)) {
		      String newRecord = String.format("%d %18s %20s",clientId,tableName,formatter.format(Calendar.getInstance().getTime()))+"\n";
		      bw.write(newRecord);
		      logger.debug("Table {} Successfully Initialized for Deletion ",tableName);
		      deleteRecordInsertionResponse.setResponseStatusCode(200);
		      deleteRecordInsertionResponse.setResponseMessage("Table:" +tableName+" Successfully Initialized For Deletion ");
		  }catch(Exception e)
		  {
			  logger.error(TABLE_DELETE_INITIALIZE_ERROR_MSG ,tableName,e);
			  deleteRecordInsertionResponse.setResponseStatusCode(400);
			  deleteRecordInsertionResponse.setResponseMessage("Error While Initializing Deletion For Table: "+tableName);
		  }
		}else {
			  logger.debug(TABLE_DELETE_INITIALIZE_ERROR_MSG ,tableName);
			  deleteRecordInsertionResponse.setResponseStatusCode(400);
			  deleteRecordInsertionResponse.setResponseMessage("Invalid Client ID or Table Name Provided");
		}
		  return deleteRecordInsertionResponse;
	}

	@Override
	public int checkDeletionofTable() {
			File existingFile = new File(deleteRecordFilePath + ".txt");
		    File newFile = new File(deleteRecordFilePath + "Temp.txt");
			int lineNumber = 0;
			int delRecordCount=0;
			try (BufferedReader br = new BufferedReader(new FileReader(existingFile));
			     PrintWriter pw =  new PrintWriter(new FileWriter(newFile)) ){
			   String currentDeleteRecord;
			   while ((currentDeleteRecord = br.readLine()) != null) {
			       if(lineNumber!=0) {
			        long diff = checkDatesDifference(currentDeleteRecord);
			        if(diff < tableDeleteDuration) {
			        	 pw.println(currentDeleteRecord);
			         }else{
			        	if(performTableDeletion(currentDeleteRecord)) {
			        		 delRecordCount++;
			        	}else {
			        		pw.println(currentDeleteRecord);	
			        	}}}
			       else {
			        	pw.println(currentDeleteRecord);	
			       }
			        lineNumber++;	
			    }
			    pw.flush();pw.close();br.close();
			    existingFile.delete();
			    File deleteRecordFile = new File(deleteRecordFilePath + ".txt");
			    newFile.renameTo(deleteRecordFile);
			    checkTableDeletionStatus(delRecordCount);
			   } catch (IOException exception) {
				  logger.error("Error While Performing Table Deletion ",exception);
				  delRecordCount=-1;
			} 
		return delRecordCount;
	}

	@Override
	public ResponseDTO undoTableDeleteRecord(int clientId)  {
		ResponseDTO performUndoDeleteResponse = new ResponseDTO();
		if(clientId>0) {
			performUndoDeleteResponse = performUndoTableDeletion(clientId);
		}
		else {
			logger.debug(TABLE_DELETE_UNDO_ERROR_MSG);
			performUndoDeleteResponse.setResponseStatusCode(400);
			performUndoDeleteResponse.setResponseMessage(TABLE_DELETE_UNDO_ERROR_MSG);
		}
		
          return performUndoDeleteResponse;
	 } 
	
	public long checkDatesDifference(String currentDeleteRecord) {
		try{
	    String[] data =  currentDeleteRecord.split(" ");
		StringBuilder date = new StringBuilder();
		int position = data.length - 2;
		for(int i = position ; i<data.length;i++) {
    		date.append( (i!= data.length -1) ? data[i] + " " : data[i] );
    	}
      Date requestDate = formatter.parse(date.toString());
      Date currentDate = formatter.parse(formatter.format(Calendar.getInstance().getTime()));
      long diffInMillies = Math.abs(requestDate.getTime() - currentDate.getTime());
	  return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		}catch(Exception e) {
			logger.error("Error!",e);
			return 0;
		}
	}
	

	public ResponseDTO performUndoTableDeletion(int clientId) {
		ResponseDTO undoTableDeletionResponse = new ResponseDTO();
		File existingFile = new File(deleteRecordFilePath + ".txt");
	    File newFile = new File(deleteRecordFilePath + "Temp.txt");
		  int lineNumber=0;
		  int undoRecord=0;
		  try (BufferedReader br = new BufferedReader(new FileReader(existingFile));
			   PrintWriter pw =  new PrintWriter(new FileWriter(newFile))){
		    String currentDeleteRecordLine;  
		    while((currentDeleteRecordLine = br.readLine()) != null) {
			  if(lineNumber>0) {
			  String[] currentRecordData = currentDeleteRecordLine.split(" ");
     		 if(!(currentRecordData[0].equalsIgnoreCase(String.valueOf(clientId)))) {
     			 pw.println(currentDeleteRecordLine);
     		 }else{
				  undoRecord++;
			  }
     		  }else{
				  pw.println(currentDeleteRecordLine);
			  }
			  lineNumber++; 
		  }
		  pw.flush();
		  pw.close();
		  br.close();
		  existingFile.delete();
		  File deleteRecordFile = new File(deleteRecordFilePath + ".txt");
		  newFile.renameTo(deleteRecordFile); 
		  undoTableDeletionResponse =  getUndoDeleteResponse(undoRecord, clientId);
		  }
		  catch(Exception e)
		  {
			  undoTableDeletionResponse.setResponseStatusCode(400);
			  undoTableDeletionResponse.setResponseMessage(e.getLocalizedMessage());
		 }
		  return undoTableDeletionResponse;
	}
	
	public boolean performTableDeletion(String tableRecord) {
		String tableName= "";
		String[] tableDeleteData = tableRecord.split(" ");
		for(int i=1; i<tableDeleteData.length; i++) {
			if(!tableDeleteData[i].equalsIgnoreCase("")) {
				tableName = tableDeleteData[i];
				break;
			}
		}
	    ResponseDTO tableDeleteResponse  = manageTableServicePort.deleteTable(tableName);
	    if(tableDeleteResponse.getResponseStatusCode() == 200) {
	    	logger.debug("Successfully Deleted Table : {}", tableName);
	    	return true;
	    }
	    else {
	    	logger.error("Failure While Deleting Table: {}", tableName);
	    	return false;
	    }
	}

	public ResponseDTO getUndoDeleteResponse(int undoRecordNumber,int clientId) {
		ResponseDTO undoDeleteResponseDTO = new ResponseDTO();
		if(undoRecordNumber > 0) {
			logger.debug("Undo Record Performed Succesfully For Client ID: {} ",clientId);
			logger.debug("Total Number of Tables Removed From Deletion: {} ",undoRecordNumber);
			undoDeleteResponseDTO.setResponseStatusCode(200);
			undoDeleteResponseDTO.setResponseMessage("Undo Deleteion of Table Performed Successfully for Client ID: "+clientId);
	        }
	        else {
	        	logger.debug("No Records Were Found For Client ID: {} ",clientId);
	        	undoDeleteResponseDTO.setResponseStatusCode(400);
				undoDeleteResponseDTO.setResponseMessage("Undo Deleteion Failed No Table Found With Client ID: "+clientId);
	        }   
		return undoDeleteResponseDTO;
	}
	
	@Override
	public boolean checkTableDeletionStatus(int deleteRecordCount) {
		if(deleteRecordCount >0) {
        	logger.debug("Total Number of Tables Found and Deleted: {}",deleteRecordCount);
        	return true;
	     }
	     else {
	      	logger.debug("No Records Were Found and Deleted With Request More Or Equal To 15 days");
	      	return false;
	      }
	}
	
	@Override
	public boolean checkTableExistensce(String tableName) {
		return manageTableServicePort.isTableExists(tableName);
	}
	}