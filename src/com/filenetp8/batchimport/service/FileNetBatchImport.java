package com.filenetp8.batchimport.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

import javax.security.auth.Subject;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.filenetp8.batchimport.services.exception.FBIU_Exception;
import com.filenetp8.batchimport.services.util.BatchThreadPool;
import com.filenetp8.batchimport.services.util.CEConfigDTO;
import com.filenetp8.batchimport.services.util.ContextThread;
import com.filenetp8.batchimport.services.util.DocumentPropDTO;
import com.filenetp8.batchimport.services.util.LoggerUtil;
import com.filenetp8.batchimport.services.util.PasswEncryptDecrypt;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public class FileNetBatchImport {

    private static Logger log = LoggerUtil.getLogger(FileNetBatchImport.class);
    private static ArrayList<File> batchContainer = null;

    public static void main(String[] args) {
        
        FileNetBatchImport fp = new FileNetBatchImport();
        UnzipBatch unzipBatch = new UnzipBatch();
        ReadXml readXml = new ReadXml();
        AddDoctoFileNet addDoctoFileNet = new AddDoctoFileNet();
        CreateReport createReport = new CreateReport();
    	File batchFile = null;
    	File unzipBatchDirec = null;
    	File xmlFile = null; 
        ObjectStore objectStore = null;
        Document xmlDoc_FilenetProp = null;
        String decrypt_Passw = null;
        String reportDeliveredPath = null;
        batchContainer = new ArrayList<File>();
        
        log.info("Initializing FileNet Batch Importing Utility.");
    	log.info("Getting essential information form FileNetProp.xml file.");

        try{
            xmlDoc_FilenetProp = readXml.get_XmlDocBuilder("FileNetProp.xml");
        }catch(Exception e ) {
            FBIU_Exception fException = new FBIU_Exception("Document object for FileNetProp.xml config file failed.", e);
            log.error(fException.getExceptionTrace());
        }

        if (xmlDoc_FilenetProp != null){
            CEConfigDTO ceConfigDto = readXml.get_CEConfigCredential(xmlDoc_FilenetProp);
    	    DocumentPropDTO propDTO = readXml.get_DocPropRequirment(xmlDoc_FilenetProp);

            log.info("CE URL : " + ceConfigDto.get_ceURL());
            log.info("CE USER : " + ceConfigDto.get_userID());
            log.info("CE ENCRYPT_PASSW : " + ceConfigDto.get_encryptPassw());
            log.info("CE OBJECT STORE : " + ceConfigDto.get_objStore());
            log.info("CE JAASSTANZA : " + ceConfigDto.get_jaasStanza());

            try {
                decrypt_Passw = PasswEncryptDecrypt.decryptPwd(ceConfigDto.get_encryptPassw());
            } catch (Exception e) {
                FBIU_Exception fbiu_Exception = new FBIU_Exception("Password failed in decrypt." , e);
                log.error(fbiu_Exception.getExceptionTrace());
            }

            if(decrypt_Passw != null ) {
                try{
                    objectStore = fp.getObjectStore(ceConfigDto.get_ceURL(), ceConfigDto.get_userID(), ceConfigDto.get_encryptPassw(), ceConfigDto.get_jaasStanza(), ceConfigDto.get_objStore());
                }catch(Exception e){
                    FBIU_Exception fbiu_Exception = new FBIU_Exception("Object Store connection fetch instance failed.",e);
                    log.error(fbiu_Exception.getExceptionTrace());
                }
                
                if ( objectStore != null){
                    log.info("Connection established for '"+ceConfigDto.get_objStore()+"' object store." );
    	            log.info("Collecting batches for processing from different source directory of batches.");

                    for(String batchDirecPath : propDTO.get_listOfBatchDirect()){
                        log.info("Batch Source Directory : " + batchDirecPath);
                        fp.getFiles(batchDirecPath, propDTO);
                    }

                    log.info("Total Batch For Processing : " + batchContainer.size());
                    
                    if(batchContainer.size() > 0 ){
                        Iterator<File> iter = batchContainer.iterator();
    		            while (iter.hasNext()) {
    			            File zipFile = fp.getbatch();
    			            propDTO.set_ReqRecvdDateTime(zipFile.lastModified());;
    			            propDTO.set_FileName(zipFile.getName());
    			            Long lSize = null;
							try {
								lSize = Files.size(zipFile.toPath());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    			            
    			            propDTO.set_FileSize(lSize.toString());
                            if (zipFile.exists()){
                                ContextThread cxt = new ContextThread();
                                cxt.setContext(zipFile.getName());
                                BatchThreadPool.set(cxt);

                                unzipBatchDirec = unzipBatch.getUnzipFile(zipFile);

                                if (unzipBatchDirec != null && unzipBatchDirec.exists()){
                                    xmlFile = fp.getxmlFile(unzipBatchDirec);
                                    batchFile = fp.getBatchFile(unzipBatchDirec);

                                    if(xmlFile != null && batchFile != null && xmlFile.exists() && batchFile.exists()){
                                        DocumentPropDTO  dPropDTO = addDoctoFileNet.getAddDocToFilenet(xmlFile, propDTO, objectStore, batchFile);

                                        if (dPropDTO.get_GUID()!= null ){
                                        	dPropDTO.set_status("success");
                                        	
                                           reportDeliveredPath = createReport.getReport(xmlFile, dPropDTO.get_GUID(), true, dPropDTO.get_responsePath(), dPropDTO.get_errorMessage(),dPropDTO);
                                            log.info("Report Delivered Path : " + reportDeliveredPath);
                                        }else{
                                        	dPropDTO.set_status("Failed");
                                        	
                                            reportDeliveredPath  =createReport.getReport(xmlFile, dPropDTO.get_GUID(), false, dPropDTO.get_responsePath(), dPropDTO.get_errorMessage(),dPropDTO);
                                            log.info("Report Delivered Path : " + reportDeliveredPath);
                                        }
                                        fp.getDBProcess(ceConfigDto, dPropDTO);
                                    }else {
                                        log.error(BatchThreadPool.get()+ " | " +"Drop request xmlfile or BatchFile is missing form the zip file. So stopping the further process.");
                                    }

                                }else{
                                    log.error("UnZip batch directory doen't exist or its not created.");
                                }
                            }else{
                                log.error("Zip File Doesnt Exist : " + zipFile.getPath());
                            }
    			   			
    		}
                    }else {
                        log.info("There isn't any batches in any batch source directory for processing. So, stopping the process.");
                    }
                }else {
                    log.error("Object Store connection fetch instance failed. So, stopping for further process.");
                }
            }else{
                log.error("Decrypting for password for Content Engine is failed. So, Stopping here.");
            }


        }else{
            log.error("Document object for FileNetProp.xml config file failed. So stopping the process.");
        }

    }

    private  ObjectStore getObjectStore(String ceURL, String userID, String passw, String jassStanz, String objStore) {
		ObjectStore objectStore= null;
        log.info("Getting Object Store Connection.");
        try{
            Connection ceConnection = Factory.Connection.getConnection(ceURL);
            log.trace("CE GET Connection by CE URL '" + ceURL+"' is connected.");

		    Subject sub = UserContext.createSubject(ceConnection, userID, passw, jassStanz);
		    UserContext.get().pushSubject(sub);
            log.trace("Subject for user context is created.");
		    
            Domain domain = Factory.Domain.getInstance(ceConnection, null);
            log.trace("GOT Domain instance.");
		    objectStore = Factory.ObjectStore.getInstance(domain, objStore);
            log.info("Object Store instance fetched and ready for further process.");
		    
        }catch(Exception e ) {
            FBIU_Exception fbiu_Exception = new FBIU_Exception("Object Store connection got failed.",e);
            log.error(fbiu_Exception.getExceptionTrace());
        }		
		
		return objectStore;
	}
    
    private void getFiles(String batchDirecPath ,DocumentPropDTO propertiesDTO) {
		
		File batchDirectory = new File(batchDirecPath);
		System.out.println(batchDirectory.exists());
		File[] listofFiles= batchDirectory.listFiles();
		System.out.println(listofFiles.length);
		
		if (listofFiles.length > 0) {
			for (File batch:listofFiles) {
				
				if (batch.getName().contains(propertiesDTO.get_fileExten())) {
					System.out.println(batch.getName());
					batchContainer.add(batch);
				}
			}
		}
	}
    private File getbatch() {
		String methodName = "getFile";
		log.debug("ENTRY : " + methodName);
		
		File file = null;
		if (batchContainer != null && !batchContainer.isEmpty()) {
			file = batchContainer.get(0);
			batchContainer.remove(0);
			return file;
		
		}
		
		log.debug("EXIT : " + methodName);
		return file;
	}

    private File getBatchFile (File batchDirec) {
		
        File batchfileName = null;		
		File[] listOfFiles = batchDirec.listFiles();
		
		for(File file: listOfFiles) {
			
			if (!file.getName().contains(".xml")) {
                log.info("Batch File in batchDirectory : " + file.getName());
				batchfileName = file;
			}
			
		}
		return batchfileName;
	}
	private File getxmlFile (File batchDirec) {
		
        File xmlfileName = null;	
		log.debug("Getting list of files in batch directory");
		File[] listOfFiles = batchDirec.listFiles();
		
		for(File file: listOfFiles) {
			log.debug("Files in BatchDirectory : " + file.getPath());
			if (file.getName().contains(".xml")) {
                log.info("Drop Request XML File : " + file.getPath());
				xmlfileName = file;
			}
			
		}
		return xmlfileName;
	}
	
	private void getDBProcess(CEConfigDTO ceConfigDTO, DocumentPropDTO dPropDTO) {
		DBConnectionUtils dbConnUtili = new DBConnectionUtils();
		java.sql.Connection dbConn  =  dbConnUtili.getDBConnectionProcess(ceConfigDTO);
		
		dbConnUtili.insetAuditRecord(dbConn, ceConfigDTO, dPropDTO);
		dbConnUtili.insetTransactRecord(dbConn, ceConfigDTO, dPropDTO);
		
		dbConnUtili.closeConnection(dbConn);
				
		
	}
    
	
    

}
