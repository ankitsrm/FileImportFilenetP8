package com.filenetp8.batchimport.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentReference;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenetp8.batchimport.services.exception.FBIU_Exception;
import com.filenetp8.batchimport.services.util.BatchThreadPool;
import com.filenetp8.batchimport.services.util.DocumentPropDTO;
import com.filenetp8.batchimport.services.util.LoggerUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class AddDoctoFileNet {
    private static Logger log = LoggerUtil.getLogger(AddDoctoFileNet.class);

    public DocumentPropDTO getAddDocToFilenet(File dropReq_xmlFilePath, DocumentPropDTO dPropDTO, ObjectStore objectStore, File batchFileName){
        
        String docClass= null;
		String errorPath = null;
		String responsePath = null;
		String documentTitle = null;
        String guid= null;
        ContentReference cref = null;
        log.trace(BatchThreadPool.get() +  " | "+" Inside the getAddDocToFileNet");
        log.info(BatchThreadPool.get() + " | "+"Trying to create Document object for dropRequest xml for add the document in filenet and further process.");
        Document dropReq_XMlDoc = getDropReuestXMLDoc(dropReq_xmlFilePath);
        InputStream is = null;			
			try {
				is = new FileInputStream(batchFileName);
			} catch (FileNotFoundException e2) {
				FBIU_Exception fbiuExc = new FBIU_Exception("Input stream for Batch file got failed to create.", e2);
				log.error(fbiuExc.getExceptionTrace());
                dPropDTO.set_errorMessage(fbiuExc.getExceptionTrace());
                dPropDTO.set_shortErrorMessage(e2.getMessage());
			}
        if(dropReq_XMlDoc != null && is != null){
            log.debug(BatchThreadPool.get()+" | "+" Creating node list as well as element for 'environment' tag for getting needfull resource for further process.");
            NodeList nlEnviornment = dropReq_XMlDoc.getElementsByTagName("environment");
			Element elEnviornment = (Element) nlEnviornment.item(0);
			
            log.debug(BatchThreadPool.get()+" | "+" Creating node list as well as element for 'DocumentProperties'");
			NodeList nldocProp = dropReq_XMlDoc.getElementsByTagName("DocumentProperties");
			Element eldocProp = (Element) nldocProp.item(0);

            log.info(BatchThreadPool.get() +" | "+ " Collecting required information.");
            String[] listofEnvDetails = dPropDTO.get_envDetails().split("[|]");
			String[] listofDocProp = dPropDTO.get_docpropDetails().split("[|]");
			String[] listofDataType = dPropDTO.get_dataTypeDetails().split("[|]");

            log.trace(BatchThreadPool.get()+ " | "+ " List of Enviornment Details : " + listofEnvDetails);
            log.trace(BatchThreadPool.get()+ " | "+ " List of DocumentProperties Details : " + listofDocProp);
            log.trace(BatchThreadPool.get()+ " | "+ " List of DataType Details : " + listofDataType);

            documentTitle = eldocProp.getElementsByTagName("DocumentTitle").item(0).getTextContent();
			
            for (String envReqProp: listofEnvDetails) {
				
				if (envReqProp.contains("DocumentClass")) {
                    docClass = elEnviornment.getElementsByTagName(envReqProp).item(0).getTextContent();
                    log.debug(BatchThreadPool.get() + " | "+ "Document Class : " + docClass);

				}else if(envReqProp.contains("ResponseRequired")) {
					responsePath = elEnviornment.getElementsByTagName(envReqProp).item(0).getTextContent();
                    dPropDTO.set_responsePath(responsePath);
                    log.debug(BatchThreadPool.get() + " | "+" Report Response Path : " + dPropDTO.get_responsePath());
                
                }else if (envReqProp.contains("ErrorPath")) {
					errorPath = elEnviornment.getElementsByTagName(envReqProp).item(0).getTextContent();
                    dPropDTO.set_errorPath(errorPath);
                    log.debug(BatchThreadPool.get() + " Report Error Path : " + dPropDTO.get_errorPath());
				
                }else {
					log.debug("Not found : "+  envReqProp);
				}
			}
            log.info("Object Store : " + objectStore);
			log.info("Document Class : " + docClass);
			dPropDTO.set_DocClass(docClass);
			
            
			Folder folder = null;
            try{
                folder = Factory.Folder.fetchInstance(objectStore, dPropDTO.get_fileNetFolderPath(), null);
                log.trace(BatchThreadPool.get()+" | "+" Folder instance is fetched successfully");
            
            }catch(Exception e){
                FBIU_Exception fException = new FBIU_Exception(BatchThreadPool.get()+" | "+" Folder object for filenet factory fetch instance is failed to create.",e);
                log.error(fException.getExceptionTrace());
                dPropDTO.set_errorMessage(fException.getExceptionTrace());
                dPropDTO.set_shortErrorMessage(e.getMessage());
            }
            com.filenet.api.core.Document ceDoc = null;
            if (docClass != null){
                try{
                    ceDoc = Factory.Document.createInstance(objectStore, docClass);
                    log.trace(BatchThreadPool.get()+" | "+"Document object of filenet create instance is created.");
                
                }catch (Exception e) {
                    FBIU_Exception fbiu_Exception = new FBIU_Exception(BatchThreadPool.get() + " | "+" Document object of filenet create instance is failed to creat.", e);
                    log.error(fbiu_Exception.getExceptionTrace());
                    dPropDTO.set_errorMessage(fbiu_Exception.getExceptionTrace());
                    dPropDTO.set_shortErrorMessage(e.getMessage());
                }
                
                if (folder != null && ceDoc != null){
                    cref = Factory.ContentReference.createInstance();
                    ContentElementList contElemList = Factory.ContentElement.createList();
                    ContentTransfer contTran = Factory.ContentTransfer.createInstance();
                    
                    contTran.setCaptureSource(is);
                    contTran.set_ContentType("");
                    contTran.set_RetrievalName(documentTitle);
                    contElemList.add(contTran);
                    ceDoc.set_ContentElements(contElemList);
                    log.trace("Content element is established.");
                    
                    try {
                        for (int i = 0 ; i < listofDocProp.length ; i ++) {
                            String docPropName = listofDocProp[i];
                            String docPropValues = eldocProp.getElementsByTagName(docPropName).item(0).getTextContent();
                            log.info("Document Property Name : " + docPropName + "\t Document Property Value : " + docPropValues);
                            
                            if (listofDataType[i].equalsIgnoreCase("Date")) {
                                Date dateProp = null;
                                docPropValues = docPropValues.substring(0, docPropValues.lastIndexOf("."));
                                try {
                                    dateProp = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).parse(docPropValues);
                                } catch (ParseException e) {
                                    FBIU_Exception fbiu_Exception = new FBIU_Exception(BatchThreadPool.get() + " | "+"Failed to get date conversion. " , e);
                                    log.error(fbiu_Exception.getExceptionTrace());
                                    dPropDTO.set_errorMessage(fbiu_Exception.getExceptionTrace());
                                    dPropDTO.set_shortErrorMessage(e.getMessage());                            
                                }
                                ceDoc.getProperties().putValue(docPropName, dateProp);
                                
                            }else if (listofDataType[i].equalsIgnoreCase("int")) {
                                int intdocPropValues = Integer.parseInt(docPropValues);
                                ceDoc.getProperties().putValue(docPropName, intdocPropValues);
                                
                            }else if (listofDataType[i].equalsIgnoreCase("Float")) {
                                float floatdocPropValues = Float.parseFloat(docPropValues);
                                ceDoc.getProperties().putValue(docPropName, floatdocPropValues);
                            }else {
                                ceDoc.getProperties().putValue(docPropName, docPropValues);
                            }
                        }
                    }catch(Exception e){
                        FBIU_Exception fbiu_Exception = new FBIU_Exception(BatchThreadPool.get()+" | "+" Document properties isnt defined in proper way.", e);
                        log.error(fbiu_Exception.getExceptionTrace());
                        dPropDTO.set_errorMessage(fbiu_Exception.getExceptionTrace());
                        dPropDTO.set_shortErrorMessage(e.getMessage());
                    }                    
                    
                    try{
                        ceDoc.set_MimeType(dPropDTO.get_mimeType());
                        ceDoc.checkin(AutoClassify.AUTO_CLASSIFY	,CheckinType.MAJOR_VERSION);
                        ceDoc.save(RefreshMode.REFRESH);
                    
                        ReferentialContainmentRelationship referContainRelation = folder.file(ceDoc, AutoUniqueName.AUTO_UNIQUE,documentTitle,DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
                    
                        referContainRelation.save(RefreshMode.REFRESH);
                    }catch(Exception e){
                        FBIU_Exception fbiu_Exception = new FBIU_Exception(BatchThreadPool.get()+" | "+" Document properties isnt defined in proper way.", e);
                        log.error(fbiu_Exception.getExceptionTrace());
                        dPropDTO.set_errorMessage(fbiu_Exception.getExceptionTrace());
                        dPropDTO.set_shortErrorMessage(e.getMessage());
                    }
                    try {
                        guid = ceDoc.get_Id().toString();
                        dPropDTO.set_GUID(guid);
                    }catch(Exception e){
                        FBIU_Exception fbiu_Exception = new FBIU_Exception(e) ;
                        log.error(fbiu_Exception.getExceptionTrace());
                    }
                    log.info(BatchThreadPool.get() + " | " + " Document is inserted : " + batchFileName.getPath());
                    log.info(BatchThreadPool.get()+" | " + " Doc GUID : " + dPropDTO.get_GUID());
                                    
                }else{
                    log.error(BatchThreadPool.get() + " | "+" Folder fetch instance or Document create instance is null.");
                    dPropDTO.set_errorMessage(" Folder fetch instance or Document create instance is null.");
                    dPropDTO.set_shortErrorMessage("Folder fetch instance or Document create instance is null.");
                }
                
            }else {
                log.error(BatchThreadPool.get()+ " | "+" Document class for filenet is null not able to fetch from the drop request source xml file.");
                dPropDTO.set_errorMessage(" Document class for filenet is null not able to fetch from the drop request source xml file.");
                dPropDTO.set_shortErrorMessage("DocClass not fetched from DropRequestSrc_XML file");
            }
        }else{
            log.error(BatchThreadPool.get()+" | "+ " dropReq_xmlDoc is not found. Input Strema for that file also not created.");
            dPropDTO.set_errorMessage(" dropReq_xmlDoc is not found. Input Strema for that file also not created.");
            dPropDTO.set_shortErrorMessage("dropReq_xmlDoc and Input Stream not found");
        }
        return dPropDTO;
                
    }

    private Document getDropReuestXMLDoc (File dropReq_xmlFilePath){
        Document dropReq_XmlDOC = null;
        DocumentBuilder dBuilder = null;

        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();

        try {
            dBuilder = dFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            FBIU_Exception fbiu_Except = new FBIU_Exception(BatchThreadPool.get() + " | "+ " Reading dropRequest xml document builder is failed to create.", e);
			log.error(fbiu_Except.getExceptionTrace());
        }

        try {
            dropReq_XmlDOC = dBuilder.parse(dropReq_xmlFilePath);
        } catch (SAXException e) {
            FBIU_Exception fbiu_Except = new FBIU_Exception(BatchThreadPool.get() + " | "+ " Reading dropRequest xml document is failed to create.", e);
			log.error(fbiu_Except.getExceptionTrace());
        } catch (IOException e) {
            FBIU_Exception fbiu_Except = new FBIU_Exception(BatchThreadPool.get() + " | "+ " Reading dropRequest xml document is failed to create.", e);
			log.error(fbiu_Except.getExceptionTrace());
        }

        return dropReq_XmlDOC;

    }
}
