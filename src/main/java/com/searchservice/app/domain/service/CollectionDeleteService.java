
package com.searchservice.app.domain.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.port.api.CollectionDeleteServicePort;

@Service
@Transactional
public class CollectionDeleteService implements CollectionDeleteServicePort{

	@Override
	public boolean insertCollectionDeleteRecord(int clientId, String tableName) {
		  boolean success=false;
		  if((clientId>0) && (tableName!=null)) {
		  try {
		  SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		  File file=new File("src\\main\\resources\\CollectionDeleteRecord.txt");
		  FileWriter fw = new FileWriter(file, true);
		  BufferedWriter bw = new BufferedWriter(fw);
		  String newRecord = String.format("%d %18s %20s",clientId,tableName,formatter.format(Calendar.getInstance().getTime()))+"\n";
		  bw.write(newRecord);
		  bw.close();
		  success=true;
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		}
		  return success;
	}

	@Override
	public int checkDeletionofCollection() {
		    SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss"); 
			File existingFile = new File("src\\main\\resources\\CollectionDeleteRecord.txt");
		    File newFile = new File("src\\main\\resources\\CollectionDeleteRecordtemp.txt");
			int lineNumber = 0;
			int delRecordCount=0;
			try {
				BufferedReader br = new BufferedReader(new FileReader(existingFile));
				PrintWriter pw = new PrintWriter(new FileWriter(newFile));
				  String st;
			        while ((st = br.readLine()) != null) {
			        	if(lineNumber!=0) {
			        	String[] data =  st.split(" ");
			        	int position = data.length - 2;
			        	String date = "";
			        	for(int i = position ; i<data.length;i++) {
			        		if( i!= data.length -1) {
			        		date += data[i] + " ";
			        		}
			        		else {
			        			date += data[i];
			        		}
			        	}
			          Date requestDate = formatter.parse(date);
			          Date currentDate = formatter.parse(formatter.format(Calendar.getInstance().getTime()));
			          long diffInMillies = Math.abs(requestDate.getTime() - currentDate.getTime());
					  long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			          if(!(diff >= 15)) {
			        	 pw.println(st);
			          }
			          else {
			        	  delRecordCount++;
			          }
			        }
			        else {
			        	pw.println(st);	
			        }
			        lineNumber++;	
			    }
			        pw.flush();
			        pw.close();
			        br.close();
			        existingFile.delete();
			        File dummy = new File("C:\\Users\\user\\git\\search-as-service\\src\\main\\resources\\CollectionDeleteRecord.txt");
			        newFile.renameTo(dummy);
                    if(delRecordCount >0) {
			        	System.out.println("Total Number of Records Found and Deleted: "+delRecordCount);
			        }
			        else {
			        	System.out.println("No Records Were Found and Deleted");
			        }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				delRecordCount=-1;
			} catch (IOException e) {
				e.printStackTrace();
				delRecordCount=-1;
			} catch (ParseException e) {
				e.printStackTrace();
				delRecordCount=-1;
			}
		return delRecordCount;
	}

	@Override
	public boolean undoCollectionDeleteRecord(int clientId) {
		File existingFile = new File("src\\main\\resources\\CollectionDeleteRecord.txt");
	    File newFile = new File("src\\main\\resources\\CollectionDeleteRecordtemp.txt");
		  int lineNumber=0;
		  boolean result=false;
		  int undoRecord=0;
		  try {
			BufferedReader br = new BufferedReader(new FileReader(existingFile));
			PrintWriter pw = new PrintWriter(new FileWriter(newFile));
		    String st;
		  
		  while((st = br.readLine()) != null)
		  {
			  if(lineNumber>0) {
			  String[] data =  st.split(" ");

			  String[] currentRecordData = st.split(" ");
     		 if(!(clientId==Integer.parseInt(currentRecordData[0]))) {
     			 pw.println(st);
     		 }else{
				  result=true;
				  undoRecord++;
			  }
			  
		   }else{
				  pw.println(st);
			  }
			  lineNumber++; 
		  }
		  pw.flush();
		  pw.close();
		  br.close();
		  existingFile.delete();
		  File dummy = new File("src\\main\\resources\\CollectionDeleteRecord.txt");
		  newFile.renameTo(dummy);
		  if(undoRecord > 0) {
	        	System.out.println("Undo Record Performed Succesfully");
		        System.out.println("Total Number of Records Found and Deleted: "+undoRecord);
		        result = true;
	        }
	        else {
	        	System.out.println("No Records Were Found For Client ID: "+clientId);
	        }       
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
		  }
		  return result;
	  }
	}
