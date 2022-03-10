package com.filenetp8.batchimport.service;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.filenetp8.batchimport.services.exception.FBIU_Exception;
import com.filenetp8.batchimport.services.util.CEConfigDTO;
import com.filenetp8.batchimport.services.util.DocumentPropDTO;
import com.filenetp8.batchimport.services.util.LoggerUtil;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadXml {
    private static Logger log = LoggerUtil.getLogger(ReadXml.class);

    public Document get_XmlDocBuilder (String xmlFilePath){
        
        log.trace("Getting xml file document object for '"+ xmlFilePath+"'.");
        Document xmlFileDoc = null;
        DocumentBuilder docBuilder = null;
        DocumentBuilderFactory doBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            docBuilder= doBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
           FBIU_Exception fbiu_Exception = new FBIU_Exception("Documnet builder for '"+ xmlFilePath+ "' isnt build.", e);
           log.error(fbiu_Exception.getExceptionTrace());
        }

        try {
            xmlFileDoc = docBuilder.parse(xmlFilePath);
        } catch (SAXException e) {
            FBIU_Exception fbiu_Exception = new FBIU_Exception("Documnet object for '"+ xmlFilePath+ "' isnt build.", e);
           log.error(fbiu_Exception.getExceptionTrace());
        } catch (IOException e) {
            FBIU_Exception fbiu_Exception = new FBIU_Exception("Documnet object for '"+ xmlFilePath+ "' isnt build.", e);
            log.error(fbiu_Exception.getExceptionTrace());
        }
        
        return xmlFileDoc;
    }

    public CEConfigDTO get_CEConfigCredential (Document xmlFileDoc){
        
        CEConfigDTO ceConfigDTO = new CEConfigDTO();
        NodeList nlLoginCredential = xmlFileDoc.getElementsByTagName("loginCredential");
        int len_nlLoginCredential = nlLoginCredential.getLength();

        if (len_nlLoginCredential == 0 ){
            log.error("The tab 'login credential' is mandatory but missing.");
        }else if(len_nlLoginCredential >1){
            log.error("There are more then one 'loginCredential' mentioned but only one is allowed.");
        }else{
            Element elLoginCred = (Element)nlLoginCredential.item(0);
            
            ceConfigDTO.set_ceURL(elLoginCred.getAttribute("ceURL"));
            ceConfigDTO.set_encryptPassw(elLoginCred.getAttribute("encryptPasswd"));
            ceConfigDTO.set_jaasStanza(elLoginCred.getAttribute("jaasStanza"));
            ceConfigDTO.set_objStore(elLoginCred.getAttribute("objStore"));
            ceConfigDTO.set_userID(elLoginCred.getAttribute("userID"));
            ceConfigDTO.set_dbConnFlow(Integer.parseInt(elLoginCred.getAttribute("dbConnFlow")));

        }

        NodeList nlDBCOnn = xmlFileDoc.getElementsByTagName("DBCredential");
        Element elDBCredenti = (Element) nlDBCOnn.item(0);
        Element elDBConnDS = (Element) elDBCredenti.getElementsByTagName("DBConnDataSourc").item(0);
        Element elDBConnCredenti = (Element) elDBCredenti.getElementsByTagName("DBConnCredential").item(0);
        Element elDBInsertQueryTrans = (Element) elDBCredenti.getElementsByTagName("DBInsetQueryTrans").item(0);
        Element elDBInsetQueryAudit = (Element) elDBCredenti.getElementsByTagName("DBInsetQueryAudit").item(0);

        ceConfigDTO.set_DBDataSource(elDBConnDS.getAttribute("dbConnDataSource"));
        ceConfigDTO.set_DBType(elDBConnCredenti.getAttribute("dbType"));
        ceConfigDTO.set_dbEncryptPassw(elDBConnCredenti.getAttribute("dbEncryptPassw"));
        ceConfigDTO.set_dbHostName(elDBConnCredenti.getAttribute("dbHostName"));
        ceConfigDTO.set_dbPortNum(Integer.parseInt(elDBConnCredenti.getAttribute("dbPortNum")));
        ceConfigDTO.set_dbUserID(elDBConnCredenti.getAttribute("dbUserID"));
        ceConfigDTO.set_dbName(elDBConnCredenti.getAttribute("dbName"));
        ceConfigDTO.set_dbInsertQueryTransact(elDBInsertQueryTrans.getAttribute("dbInsetQueryTransact"));
        ceConfigDTO.set_dbInsertQueryAudit(elDBInsetQueryAudit.getAttribute("dbInsertQueryAudit"));
        
        System.out.println(">>>> " + ceConfigDTO.get_dbInsertQueryTransact());
        System.out.println(">>>>>>>>>>>>>> + " +ceConfigDTO.get_dbInsertQueryAudit());
        System.out.println(" DB Data source : "+ceConfigDTO.get_DBDataSource());
        System.out.println(" DB Data Type : "+ ceConfigDTO.get_DBType());
        System.out.println("DB Encrypted Passw : " + ceConfigDTO.get_dbEncryptPassw());
        System.out.println(" DB Host Name : " + ceConfigDTO.get_dbHostName());
        System.out.println("DB Port Number : " + ceConfigDTO.get_dbPortNum());
        System.out.println("DB User ID : " + ceConfigDTO.get_dbUserID());

        return ceConfigDTO;
    }

    public DocumentPropDTO get_DocPropRequirment (Document xmlFileDoc){
        DocumentPropDTO dPropDTO = new DocumentPropDTO();
        ArrayList<String> listofBatchSrcDirec = new ArrayList<String>();
        NodeList nlVendorConfig  = xmlFileDoc.getElementsByTagName("VendorFileConfig");
        int len_nlVendorConfig = nlVendorConfig.getLength();

        if(len_nlVendorConfig ==0){
            log.error("The tab 'VendorFileConfig' is mandatory but missing.");
        }else if( len_nlVendorConfig > 1){
            log.error("The tab 'VendorFileConfig' mentioned more then one but only one is allowed.");
        }else{
            Element elVendorConfig = (Element) nlVendorConfig.item(0);
            NodeList nlDocProp = elVendorConfig.getElementsByTagName("batchSrcDirec");
			 
			 for (int j = 0 ; j < nlDocProp.getLength() ; j++) {
				 Element elNlDocProp = (Element) nlDocProp.item(j);
				 System.out.println("ssss");
				 System.out.println(elNlDocProp.getAttribute("batchDirec"));
				 listofBatchSrcDirec.add(elNlDocProp.getAttribute("batchDirec"));	 
			 }
            
            Element elDataType = (Element) elVendorConfig.getElementsByTagName("DataType").item(0);
            Element elEnviRequiredFiledList = (Element) elVendorConfig.getElementsByTagName("EnviornmentConfig").item(0);
            Element elDocPropertRequiredList = (Element) elVendorConfig.getElementsByTagName("DocumentProperty").item(0);
            Element elmimeType = (Element) elVendorConfig.getElementsByTagName("MimeType").item(0);
            Element elFileExtention  = (Element) elVendorConfig.getElementsByTagName("FileExtention").item(0);

            dPropDTO.set_docpropDetails(elDocPropertRequiredList.getAttribute("docProp"));
            dPropDTO.set_envDetails(elEnviRequiredFiledList.getAttribute("envProp"));
            dPropDTO.set_fileExten(elFileExtention.getAttribute("fileExtention"));
            dPropDTO.set_mimeType(elmimeType.getAttribute("mimeType"));
            dPropDTO.set_dataTypeDetails(elDataType.getAttribute("dataType"));
            dPropDTO.set_listOfBatchDirect(listofBatchSrcDirec);
        
        }
        return dPropDTO;
    }

    public static void main(String[] args){
        ReadXml readXml = new ReadXml();
        
        Document doc = readXml.get_XmlDocBuilder("D:\\FileNetBatchImportUtility\\FileNetBatchImportConfig.xml");
        CEConfigDTO ceConfigDTO = readXml.get_CEConfigCredential(doc);

        System.out.println("CE URL : "+ceConfigDTO.get_ceURL());
        System.out.println("ENCRYPT PASSW  : " + ceConfigDTO.get_encryptPassw());
        System.out.println("USER ID : " + ceConfigDTO.get_userID());
        System.out.println("JASS STNZA : " + ceConfigDTO.get_jaasStanza());
        System.out.println("OBJECT STORE : " +ceConfigDTO.get_objStore());

        DocumentPropDTO dPropDTO = readXml.get_DocPropRequirment(doc);

        System.out.println("===================================================");
        System.out.println("Document properties : " + dPropDTO.get_docpropDetails());
        System.err.println("Document data Type : " + dPropDTO.get_dataTypeDetails());
        System.out.println("Mime type : " + dPropDTO.get_mimeType());
        System.out.println("File Extention : "+ dPropDTO.get_fileExten());
        System.out.println("Enviornment Details : " + dPropDTO.get_envDetails());
        System.out.println("List of Batch Direc : " + dPropDTO.get_listOfBatchDirect());
         
    }

    

}
