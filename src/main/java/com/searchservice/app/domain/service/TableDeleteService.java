
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;

@Service

@Transactional
public class TableDeleteService implements TableDeleteServicePort {

	@Value("${table-delete-file.path}")
	String deleteRecordFilePath;

	@Value("${table-delete-duration.days}")
	long tableDeleteDuration;
	SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	@Autowired
	ManageTableServicePort manageTableServicePort;

	
	public TableDeleteService(ManageTableServicePort manageTableServicePort) {
		this.manageTableServicePort = manageTableServicePort;

	}

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String TABLE_DELETE_INITIALIZE_ERROR_MSG = "Error While Initializing Deletion For Table: {}";
	private static final String TABLE_DELETE_UNDO_ERROR_MSG = "Undo Table Delete Failed , Invalid CLient ID Provided";
	private static final String TABLE_FILE_CREATE_ERROR = "Error File Creating File {}";

	@Override
	public Response initializeTableDelete(int tenantId, String tableName) {

		Response deleteRecordInsertionResponse = new Response();
		String actualTableName = "";
		if ((tenantId > 0) && (tableName != null && tableName.length() != 0)) {
			File file = new File(deleteRecordFilePath + ".csv");
			checkIfTableDeleteFileExist(file);
			try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw);) {
				actualTableName = tableName.substring(0, tableName.lastIndexOf("_"));
				String newRecord = tenantId + "," + tableName + "," + formatter.format(Calendar.getInstance().getTime())
						+ "\n";
				fw.write(newRecord);
				fw.flush();

				deleteRecordInsertionResponse.setStatusCode(200);

				deleteRecordInsertionResponse
						.setMessage("Table:" + actualTableName + " Successfully Initialized For Deletion ");

			} catch (Exception e) {
				logger.error(TABLE_DELETE_INITIALIZE_ERROR_MSG, actualTableName, e);
				deleteRecordInsertionResponse.setStatusCode(400);

				deleteRecordInsertionResponse
						.setMessage("Error While Initializing Deletion For Table: " + actualTableName);
			}
		} else {
			logger.debug(TABLE_DELETE_INITIALIZE_ERROR_MSG, actualTableName);
			deleteRecordInsertionResponse.setStatusCode(400);
			deleteRecordInsertionResponse.setMessage("Invalid Client ID or Table Name Provided");
		}
		return deleteRecordInsertionResponse;
	}

	@Override
	public int checkDeletionofTable() {

		File existingFile = new File(deleteRecordFilePath + ".csv");
		File newFile = new File(deleteRecordFilePath + "Temp.csv");
		int lineNumber = 0;
		int delRecordCount = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(existingFile));
				PrintWriter pw = new PrintWriter(new FileWriter(newFile))) {
			String currentDeleteRecord;
			while ((currentDeleteRecord = br.readLine()) != null) {
				if (lineNumber != 0) {
					long diff = checkDatesDifference(currentDeleteRecord);
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

			makeDeleteTableFileChangesForDelete(newFile, existingFile, delRecordCount);

		} catch (IOException exception) {
			logger.error("Error While Performing Table Deletion ", exception);
			delRecordCount = -1;

		}
		return delRecordCount;
	}

	public void makeDeleteTableFileChangesForDelete(File newFile, File existingFile, int delRecordCount) {
		File deleteRecordFile = new File(deleteRecordFilePath + ".csv");
		if (existingFile.delete() && newFile.renameTo(deleteRecordFile)) {
			checkTableDeletionStatus(delRecordCount);
		}
	}

	@Override
	public Response undoTableDeleteRecord(String tableName) {

		Response performUndoDeleteResponse = new Response();

		if (tableName != null) {

			performUndoDeleteResponse = performUndoTableDeletion(tableName);
		} else {
			logger.debug(TABLE_DELETE_UNDO_ERROR_MSG);

			performUndoDeleteResponse.setStatusCode(400);
			performUndoDeleteResponse.setMessage(TABLE_DELETE_UNDO_ERROR_MSG);
		}

		return performUndoDeleteResponse;
	}

	public long checkDatesDifference(String currentDeleteRecord) {
		try {
			String date = currentDeleteRecord.split(",")[2];
			Date requestDate = formatter.parse(date);
			Date currentDate = formatter.parse(formatter.format(Calendar.getInstance().getTime()));
			long diffInMillies = Math.abs(requestDate.getTime() - currentDate.getTime());
			return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error("Error!", e);
			return 0;
		}
	}

	public Response performUndoTableDeletion(String tableName) {
		String actualTableName = tableName.substring(0, tableName.lastIndexOf("_"));
		Response undoTableDeletionResponse = new Response();
		File existingFile = new File(deleteRecordFilePath + ".csv");
		checkIfTableDeleteFileExist(existingFile);
		File newFile = new File(deleteRecordFilePath + "Temp.csv");
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
			File deleteRecordFile = new File(deleteRecordFilePath + ".csv");
			if (existingFile.delete() && newFile.renameTo(deleteRecordFile)) {
				undoTableDeletionResponse = getUndoDeleteResponse(undoRecord, actualTableName);
			}
		} catch (Exception e) {
			undoTableDeletionResponse.setStatusCode(400);
			undoTableDeletionResponse.setMessage(e.getLocalizedMessage());
		}
		return undoTableDeletionResponse;
	}

	public boolean performTableDeletion(String tableRecord) {
		String tableName = tableRecord.split(",")[1];
		Response tableDeleteResponse = manageTableServicePort.deleteTable(tableName);

		return (tableDeleteResponse.getStatusCode() == 200);
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
	public boolean checkTableExistensce(String tableName) {
		return manageTableServicePort.isTableExists(tableName);
	}

	@Override
	public boolean isTableUnderDeletion(String tableName) {
		boolean res = false;
		List<String> listofTablesUnderDeletion;

		listofTablesUnderDeletion = getTableUnderDeletion();
		if (listofTablesUnderDeletion.contains(tableName))
			res = true;

		return res;
	}

	@Override
	public List<String> getTableUnderDeletion() {
		List<String> tableUnderDeletionList = new ArrayList<>();
		File existingFile = new File(deleteRecordFilePath + ".csv");
		checkIfTableDeleteFileExist(existingFile);
		int lineNumber = 0;
		try (FileReader fr = new FileReader(existingFile); BufferedReader br = new BufferedReader(fr);) {
			String st;
			while ((st = br.readLine()) != null) {
				if (lineNumber != 0) {
					String currentTableName = st.split(",")[1];
					tableUnderDeletionList.add(currentTableName.split("_")[0]);
				}
				lineNumber++;
			}

		} catch (Exception e) {
			logger.error("Some Error Occured While Getting Table", e);
		}
		return tableUnderDeletionList;
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
}
