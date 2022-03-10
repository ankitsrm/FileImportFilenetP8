package com.filenetp8.batchimport.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.filenetp8.batchimport.services.exception.FBIU_Exception;
import com.filenetp8.batchimport.services.util.BatchThreadPool;
import com.filenetp8.batchimport.services.util.DocumentPropDTO;
import com.filenetp8.batchimport.services.util.LoggerUtil;

public class CreateReport {
    private Logger log = LoggerUtil.getLogger(CreateReport.class);
    
    public String getReport(File dropReq_xmlFile , String guid,  boolean isInserted,String reportDeliveryPath, String errorMessage, DocumentPropDTO dPropDTO) {
    	String dateTimeStamp_Completion = getDateTimeStamp();
        String success_Status = "Filenet Ingestion Successfull";
        String error_Status = "FileNet Ingestion Failed";
        log.info( BatchThreadPool.get() + " | "+"Report Creation started.");
        Document reportXmlDoc = getXmlReportBuilder(dropReq_xmlFile);
        log.debug(" Document object for report creationg is created.");
        String reportFilePath = null;
        dPropDTO.set_ReqCompletedDateTime(dateTimeStamp_Completion);
        
        if (isInserted) {
        	log.info(BatchThreadPool.get() + " | "+" Batch is inserted successfully in FileNet");
        	addElementSuccess(success_Status,reportXmlDoc, guid, dateTimeStamp_Completion);
        	reportFilePath = writeXML(dropReq_xmlFile, reportXmlDoc, "_Success", reportDeliveryPath);
        }else {
        	log.info(BatchThreadPool.get() + " | "+" Batch insertion is failled in FileNet.");
        	addElementError(error_Status,reportXmlDoc, errorMessage, dateTimeStamp_Completion);
        	reportFilePath = writeXML(dropReq_xmlFile, reportXmlDoc, "_Error", reportDeliveryPath);
        }
        return reportFilePath;
        	       
    }
    
	private void addElementSuccess (String status, Document doc, String guid, String timeStamp) {
		
		try {
			
			log.debug(BatchThreadPool.get() + " | "+ "Adding in 'Message' element 'Response' tag for process completion report.");
			NodeList messageNl = doc.getElementsByTagName("Message");
			Element messageEL = (Element) messageNl.item(0);
			
			log.debug(BatchThreadPool.get() + " | "+"Creating Element for 'response' tag.");
			Element responseElement = doc.createElement("response");
			messageEL.appendChild(responseElement);
			
			log.debug(BatchThreadPool.get() + " | "+" Inside 'response' element creating 'status' tag. Text content is '" + status+"'.");
			Element statusEl = doc.createElement("Status");
			responseElement.appendChild(statusEl).setTextContent(status);
			
			log.debug(BatchThreadPool.get() + " | "+" Inside 'response' element creating 'guid' tag. Text content is '" + guid +"'." );
			Element guidEL = doc.createElement("GUID");
			responseElement.appendChild(guidEL).setTextContent(guid);
			
			log.debug(BatchThreadPool.get() + " | "+" Inside 'response' element creation 'ResponseTimestamp' tab. Text content is '" + timeStamp + "'.");
			Element responseTimeStamp = doc.createElement("ResponseTimestamp");
			responseElement.appendChild(responseTimeStamp).setTextContent(timeStamp);
		
		}catch(Exception e ) {
			FBIU_Exception fbiuExce = new FBIU_Exception(BatchThreadPool.get() +" | "+" Add success element for report creation is get failed." ,e);
			log.error(fbiuExce.getExceptionTrace());
		}
		
	}
	
	private String writeXML (File file, Document doc ,String status, String reportDeliverypath ){
		String newFile = null;
        try {
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			newFile = reportDeliverypath+File.separator + "report"+"_"+status+"_"+System.currentTimeMillis()+".xml";
			StreamResult result = new StreamResult(newFile);
			transformer.setOutputProperty(OutputKeys.INDENT	, "yes");
			transformer.transform(source, result);
		}catch(Exception e) {
			FBIU_Exception fbiu_EXC = new FBIU_Exception(BatchThreadPool.get() + " | "+" Report xml file creation get failled to write.", e);
			log.error(fbiu_EXC.getExceptionTrace());
		}
        return newFile;
		
		
		
	}
	
	
	private void addElementError (String status,Document doc, String exception, String timeStamp) {
		try {
			
			log.debug(BatchThreadPool.get() + " | "+ "Adding in 'Message' element 'Response' tag for process completion report.");
			NodeList messageNl = doc.getElementsByTagName("Message");
			Element messageEL = (Element) messageNl.item(0);
			
			log.debug(BatchThreadPool.get() + " | "+"Creating Element for 'response' tag.");
			Element responseElement = doc.createElement("response");
			messageEL.appendChild(responseElement);
			
			log.debug(BatchThreadPool.get() + " | "+" Inside 'response' element creating 'status' tag. Text content is '" + status+"'.");
			Element statusEl = doc.createElement("Status");
			responseElement.appendChild(statusEl).setTextContent(status);
			
			log.debug(BatchThreadPool.get() + " | "+" Inside 'response' element creating 'ResponseTimeStamp' tag. Text content is '" + timeStamp +"'." );
			Element responseTimeStamp = doc.createElement("ResponseTimestamp");
			responseElement.appendChild(responseTimeStamp).setTextContent(timeStamp);
			
			log.debug(BatchThreadPool.get() + " | "+" Inside 'response' element creating 'Error' tag.");
			Element errorEL = doc.createElement("Error");
			responseElement.appendChild(errorEL);
			
			log.debug(BatchThreadPool.get() + " | "+" Inside 'Error' element creating 'ErrorMessage' tag.");
			Element errorMessageEL = doc.createElement("ErrorMessage");
			errorEL.appendChild(errorMessageEL);
			
			log.debug(BatchThreadPool.get() + " | "+" Inside 'ErrorMessage' element creating 'Exception' tag.");
			Element exceptionEl = doc.createElement("Exception");
			errorMessageEL.appendChild(exceptionEl).setTextContent(exception);	
		}catch(Exception e ) {
			FBIU_Exception fbiuException = new FBIU_Exception(BatchThreadPool.get() +" | "+"", e);
			log.error(fbiuException.getExceptionTrace());
		}	
		
	} 
    private String getDateTimeStamp(){
        
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		time = sdf.format(new Date());

        return time;
    }
    
    private Document getXmlReportBuilder (File dropReq_XML){
        Document reportXmlDoc = null;
        DocumentBuilder reportDocBuilder = null;

        DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
			reportDocBuilder = dBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			FBIU_Exception fbiu_Except = new FBIU_Exception(BatchThreadPool.get() + " | "+ " Report creation xml document builder is failed to create.", e);
			log.error(fbiu_Except.getExceptionTrace());
		}
        
        try {
			reportXmlDoc = reportDocBuilder.parse(dropReq_XML);
		} catch (SAXException e) {
			FBIU_Exception fbiu_Except = new FBIU_Exception(BatchThreadPool.get() + " | "+ " Report creation xml document is failed to create.", e);
			log.error(fbiu_Except.getExceptionTrace());
		} catch (IOException e) {
			FBIU_Exception fbiu_Except = new FBIU_Exception(BatchThreadPool.get() + " | "+ " Report creation xml document is failed to create.", e);
			log.error(fbiu_Except.getExceptionTrace());
		}
        
        return reportXmlDoc;


    }
    
}
