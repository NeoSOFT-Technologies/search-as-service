package com.searchservice.app.domain.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.utils.DateUtil;
import com.searchservice.app.domain.utils.ManageTableUtil;
import com.searchservice.app.infrastructure.adaptor.SearchJAdapter;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

@Service
public class TableDeleteService implements TableDeleteServicePort {

	@Value("${table-delete-file.path}")
	String deleteRecordFilePath;

	@Value("${table-delete-duration.days}")
	long tableDeleteDuration;

	ManageTableServicePort manageTableServicePort;
	
	@Autowired
	SearchJAdapter searchJAdapter;

	@Autowired
	public TableDeleteService(@Lazy ManageTableServicePort manageTableServicePort) {
		this.manageTableServicePort = manageTableServicePort;

	}
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String TABLE_RESPONSE_MSG = "Table %s Having TenantID: %d %s%s";
	private static final String TABLE_DELETE_INITIALIZE_ERROR_MSG = "Error While Initializing Deletion For Table: {}";
	private static final String TABLE_FILE_CREATE_ERROR = "Error File Creating File {}";
	private   SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	
	
	@Override
	public Response initializeTableDelete(int tenantId, String tableName) {
      if(!isTableUnderDeletion(tableName)) {  	
    	  if(manageTableServicePort.isTableExists(tableName)) {
    		  Response deleteRecordInsertionResponse = new Response();
    		  String actualTableName = "";
    		  File file = new File(deleteRecordFilePath);
    		  checkIfTableDeleteFileExist(file);
    		  try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw);) {
    			  actualTableName = tableName.substring(0, tableName.lastIndexOf("_"));
    			  String newRecord = tenantId + "," + tableName + "," + DateUtil.getFormattedDate(formatter)
						+ "\n";
    			  fw.write(newRecord);
    			  fw.flush();
    			  deleteRecordInsertionResponse.setStatusCode(200);
    			  deleteRecordInsertionResponse
						.setMessage("Table:" + actualTableName + " Having TenantID: "+tenantId+" is Successfully Initialized For Deletion ");

    		  } catch (Exception e) {
    			  setIntializeDeleteExceptionResponse(e, tableName, deleteRecordInsertionResponse);
    		  }
    		  return deleteRecordInsertionResponse;
    	  }
    	  else {
    		  throw new CustomException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),HttpStatusCode.TABLE_NOT_FOUND,
					String.format(TABLE_RESPONSE_MSG, tableName.split("_")[0], tenantId, "", HttpStatusCode.TABLE_NOT_FOUND.getMessage()));
    	  }
         }else {
        	 throw new CustomException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					HttpStatusCode.UNDER_DELETION_PROCESS,String.format(TABLE_RESPONSE_MSG, tableName.split("_")[0], tenantId, "is ", HttpStatusCode.UNDER_DELETION_PROCESS.getMessage()));
		}
	}

	@Override
	public int checkDeletionofTable() {
		File existingFile = new File(deleteRecordFilePath);
		File newFile = new File(deleteRecordFilePath.substring(0, deleteRecordFilePath.length()-4)+"Temp.csv");
		int lineNumber = 0;
		int delRecordCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(existingFile));
				PrintWriter pw = new PrintWriter(new FileWriter(newFile))) {
			String currentDeleteRecord;
			while ((currentDeleteRecord = br.readLine()) != null) {
				if (lineNumber != 0) {
					long diff = DateUtil.checkDatesDifference(currentDeleteRecord,formatter);
					if (diff < tableDeleteDuration) {
						pw.println(currentDeleteRecord);
					} else {
						if (performTableDeletion(currentDeleteRecord)) {
							delRecordCount++;
						} else {
							pw.println(currentDeleteRecord);
						}
					}
				} else {
					pw.println(currentDeleteRecord);
				}
				lineNumber++;
			}
			pw.flush();
			pw.close();
			br.close();
			makeDeleteTableFileChangesForDelete(newFile, existingFile, delRecordCount);

		} catch (IOException exception) {
			logger.error("Error While Performing Table Deletion ", exception);
			delRecordCount = -1;

		}
		return delRecordCount;
	}

	public void makeDeleteTableFileChangesForDelete(File newFile, File existingFile, int delRecordCount) {
		if (existingFile.delete() && newFile.renameTo(existingFile)) {
			checkTableDeletionStatus(delRecordCount);
		}
	}

	@Override
	public Response undoTableDeleteRecord(String tableName) {
	 if(isTableUnderDeletion(tableName)) {
		return performUndoTableDeletion(tableName);
	 }else {
        	throw new CustomException(HttpStatusCode.TABLE_NOT_UNDER_DELETION.getCode(),HttpStatusCode.TABLE_NOT_UNDER_DELETION,
        			String.format(TABLE_RESPONSE_MSG, tableName.split("_")[0], Integer.parseInt(tableName.split("_")[1]), "is ", HttpStatusCode.TABLE_NOT_UNDER_DELETION.getMessage()));
        }
	}

	public Response performUndoTableDeletion(String tableName) {
		String actualTableName = tableName.substring(0, tableName.lastIndexOf("_"));
		Response undoTableDeletionResponse = new Response();
		File existingFile = new File(deleteRecordFilePath);
		checkIfTableDeleteFileExist(existingFile);
		File newFile = new File(deleteRecordFilePath.substring(0, deleteRecordFilePath.length()-4)+"Temp.csv");
		int lineNumber = 0;
		int undoRecord = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(existingFile));
				PrintWriter pw = new PrintWriter(new FileWriter(newFile))) {
			String currentDeleteRecordLine;
			while ((currentDeleteRecordLine = br.readLine()) != null) {
				if (lineNumber > 0) {
					String[] currentRecordData = currentDeleteRecordLine.split(",");

					if (!currentRecordData[1].equalsIgnoreCase(tableName)) {
						pw.println(currentDeleteRecordLine);
					} else {
						undoRecord++;
					}
				} else {
					pw.println(currentDeleteRecordLine);
				}
				lineNumber++;
			}
			pw.flush();
			pw.close();
			br.close();
			File deleteRecordFile = new File(deleteRecordFilePath);
			if (existingFile.delete() && newFile.renameTo(deleteRecordFile)) {
				undoTableDeletionResponse = getUndoDeleteResponse(undoRecord, actualTableName);
			}
		} catch (Exception e) {
			undoTableDeletionResponse.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			undoTableDeletionResponse.setMessage(e.getLocalizedMessage());
		}
		return undoTableDeletionResponse;
	}

	public boolean performTableDeletion(String tableRecord) {
		String tableName = tableRecord.split(",")[1];
		try {
		 Response tableResponseDTO = manageTableServicePort.deleteTable(tableName);
		 logger.debug("{}",tableResponseDTO.getMessage());
		 return (tableResponseDTO.getStatusCode() == 200);
		}
		catch(CustomException e) {
			logger.error("Error Occured While Performing Deletion : {}",e.getExceptionMessage());
			return false;
		}	
	}

	public Response getUndoDeleteResponse(int undoRecordNumber, String tableName) {
		Response undoDeleteResponseDTO = new Response();
		if (undoRecordNumber > 0) {

			undoDeleteResponseDTO.setStatusCode(200);
			undoDeleteResponseDTO.setMessage("Restore Deletion of Table " + tableName + " Performed Successfully");
		} else {
			logger.debug("No Records Were Found For Table: {} ", tableName);
			undoDeleteResponseDTO.setStatusCode(400);
			undoDeleteResponseDTO
					.setMessage("Restore Deletion for Table " + tableName + " was Failed , No Records Were Found");
		}
		return undoDeleteResponseDTO;
	}

	@Override
	public boolean checkTableDeletionStatus(int deleteRecordCount) {
		return (deleteRecordCount > 0);
	}

	@Override
	public boolean isTableUnderDeletion(String tableName) {
		boolean res = false;
		List<String> listofTablesUnderDeletion;
		if(tableName.contains("_")) {
			listofTablesUnderDeletion = getTablesUnderDeletion(true).getData();
		}else {
			listofTablesUnderDeletion = getTablesUnderDeletion(false).getData();
		}
		if (listofTablesUnderDeletion.contains(tableName))
			res = true;

		return res;
	}

	@Override
	public Response getTablesUnderDeletion(boolean getDeletedTableListWithTenantId) {
		Response deleteTablesResponse = new Response();
		
		File existingFile = new File(deleteRecordFilePath);
		try (FileReader fr = new FileReader(existingFile); BufferedReader br = new BufferedReader(fr);) {
			checkIfTableDeleteFileExist(existingFile);
			List<String> tableUnderDeletionList = prepareTablesUnderDeletionList(getDeletedTableListWithTenantId, br);
			
			deleteTablesResponse.setData(tableUnderDeletionList);
			deleteTablesResponse.setDataSize(tableUnderDeletionList.size());
			
			deleteTablesResponse.setStatusCode(200);
			deleteTablesResponse.setMessage("Successfully Retrieved All Tables Under Deletion");
			
			return deleteTablesResponse;
		}
		catch (Exception e) {
			deleteTablesResponse.setStatusCode(400);
			logger.error("Some Error Occured While Getting Tables Under Deletion ", e);
			return deleteTablesResponse;
		}
	}
	
	@Override
	public Response getTablesUnderDeletionPagination(boolean getDeletedTableListWithTenantId, int pageNumber, int pageSize) {
		Response deleteTablesResponse = new Response();
		
		File existingFile = new File(deleteRecordFilePath);
		try (FileReader fr = new FileReader(existingFile); BufferedReader br = new BufferedReader(fr);) {
			checkIfTableDeleteFileExist(existingFile);
			List<String> tableUnderDeletionList = prepareTablesUnderDeletionList(getDeletedTableListWithTenantId, br);
			
			// Prepare tables list with corresponding tenantName (fetched from server)
			Map<String, String> tableTenantMap = manageTableServicePort.getAllTableTenantMap(tableUnderDeletionList);

			// Prepare paginated list of tables under dleetion for given tenant
			deleteTablesResponse.setTableList(
					ManageTableUtil.getPaginatedTableList(tableUnderDeletionList, tableTenantMap, pageNumber, pageSize));
			deleteTablesResponse.setData(null);
			deleteTablesResponse.setDataSize(tableUnderDeletionList.size());
			
			deleteTablesResponse.setStatusCode(200);
			deleteTablesResponse.setMessage("Successfully Retrieved All Tables Under Deletion");
		}
		catch (Exception e) {
			deleteTablesResponse.setStatusCode(400);
			logger.error("Some Error Occured While Getting Tables Under Deletion ", e);
		}
		return deleteTablesResponse;
	}
	
	public List<String> prepareTablesUnderDeletionList(boolean getDeletedTableListWithTenantId, BufferedReader br)
			throws IOException {
		List<String> tableUnderDeletionList = new ArrayList<>();
		
		int lineNumber = 0;
		String st;
		while ((st = br.readLine()) != null) {
			if (lineNumber != 0) {
				String currentTableName = st.split(",")[1];
				if(getDeletedTableListWithTenantId) {
					tableUnderDeletionList.add(currentTableName);
				}else {
					tableUnderDeletionList.add(currentTableName.split("_")[0]);
				}
			}
			lineNumber++;
		}
		return tableUnderDeletionList;
	}
	
	@Override
	public Response getTablesUnderDeletionForTenant(int tenantId, int pageNumber, int pageSize) {
		Response deleteTablesByTenantNameResponse = new Response();
		List<String> tableUnderDeletionList = new ArrayList<>();
		File existingFile = new File(deleteRecordFilePath);
		checkIfTableDeleteFileExist(existingFile);
		
		int lineNumber = 0;
		try (FileReader fr = new FileReader(existingFile); BufferedReader br = new BufferedReader(fr);) {
			String st;
			while ((st = br.readLine()) != null) {
				if (lineNumber != 0) {
					// Get the tenantName for the current Table
					String tableName = st.split(",")[1];
					if(tenantId == Integer.parseInt(st.split(",")[0])) {
						tableUnderDeletionList.add(tableName);
					}
				}
				lineNumber++;
			}
			
			// Prepare tables list with corresponding tenantName (fetched from server)
			Map<String, String> tableTenantMap = manageTableServicePort.getAllTableTenantMap(tableUnderDeletionList);

			// Prepare paginated list of tables under dleetion for given tenant
			deleteTablesByTenantNameResponse.setTableList(
					ManageTableUtil.getPaginatedTableList(tableUnderDeletionList, tableTenantMap, pageNumber, pageSize));
			deleteTablesByTenantNameResponse.setData(null);
			deleteTablesByTenantNameResponse.setDataSize(tableUnderDeletionList.size());
			
			deleteTablesByTenantNameResponse.setStatusCode(200);
			deleteTablesByTenantNameResponse.setMessage("Successfully Retrieved All Tables Under Deletion For TenantId: "+tenantId);
			
		}
		catch (Exception e) {
			deleteTablesByTenantNameResponse.setStatusCode(400);
			logger.error("Some Error Occured While Getting Tables Under Deletion For Tenant: "+tenantId, e);
		}
		return deleteTablesByTenantNameResponse;
	}

	@Override
	public boolean checkIfTableDeleteFileExist(File file) {
		if (!file.exists()) {
			try {
				boolean createFile = file.createNewFile();
				if (createFile) {
					logger.debug("File With Name: {} Created Succesfully", file.getName());
				}
				return true;
			} catch (IOException e) {
				logger.error(TABLE_FILE_CREATE_ERROR, file.getName(), e);
				return false;
			}
		} else {
			return false;
		}
	}
	
	public void setIntializeDeleteExceptionResponse(Exception e , String tableName, Response deleteRecordInsertionResponse) {
		logger.error(TABLE_DELETE_INITIALIZE_ERROR_MSG, tableName, e);
		deleteRecordInsertionResponse.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		deleteRecordInsertionResponse
				.setMessage("Error While Initializing Deletion For Table: " + tableName);
	}

}
