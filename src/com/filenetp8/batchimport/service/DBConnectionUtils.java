package com.filenetp8.batchimport.service;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.filenetp8.batchimport.services.exception.FBIU_Exception;
import com.filenetp8.batchimport.services.util.BatchThreadPool;
import com.filenetp8.batchimport.services.util.CEConfigDTO;
import com.filenetp8.batchimport.services.util.DocumentPropDTO;
import com.filenetp8.batchimport.services.util.LoggerUtil;

import org.apache.log4j.Logger;


public class DBConnectionUtils {
    private static final Logger log = LoggerUtil.getLogger(DBConnectionUtils.class);
    private String s_DriverName = "oracle.jdbc.driver.OracleDriver";

    public Connection getDBConnectionProcess(CEConfigDTO ceConfigDTO){
        DBConnectionUtils dbConnUtils = new DBConnectionUtils();

        Connection getDBConnection = null;
        log.info("Getting database connection.");
        

        try{
            if (ceConfigDTO.get_dbConnFlow() == 1){
                log.info("Getting DB connection by data source flow.");
                getDBConnection= dbConnUtils.getDataSourceDBConn(ceConfigDTO.get_DBDataSource());
            }else {
                log.info("Getting DB connection by db user ID and password.");
                getDBConnection = getDBConnectionUserIDPass(ceConfigDTO.get_DBType(),ceConfigDTO.get_dbHostName(), ceConfigDTO.get_dbPortNum(), ceConfigDTO.get_dbName(), ceConfigDTO.get_dbUserID(),ceConfigDTO.get_dbEncryptPassw());
                
            }
        }catch(Exception e ){
            FBIU_Exception fbiu_Exception = new FBIU_Exception(BatchThreadPool.get()+" | "+" Getting database connection is failed.", e);
            log.error(fbiu_Exception.getExceptionTrace());
            
        }
        return getDBConnection;
    }

    private Connection getDataSourceDBConn(String datasourceName){
        Connection getDBConnection = null;
        DataSource dataSource = null;
        InitialContext iContext = null;
        Properties prop = new Properties();
        prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NameingContextFactory");
        prop.put(Context.URL_PKG_PREFIXES, " org.jboss.ejb.client.naming");
        prop.put(Context.PROVIDER_URL, "jnp://localhost:1099");

        try{
            iContext = new InitialContext();
            dataSource= (DataSource) iContext.lookup(datasourceName);
            getDBConnection= dataSource.getConnection();
            log.info("DB connection established by data source '"+ datasourceName+"'.");

        }catch(NamingException ne){
            FBIU_Exception fException = new FBIU_Exception("Data source connection get failed.", ne);
            log.info(fException.getExceptionTrace());
            
        } catch (SQLException e) {
            FBIU_Exception fException = new FBIU_Exception("Data source connection get failed.", e);
            log.info(fException.getExceptionTrace());
            
        }
        return getDBConnection;
    }

    private Connection getDBConnectionUserIDPass(String dbType,String dbHostName , int dbPortNumber,String dbName, String dbUserName, String dbUserPwd){
       
        String s_Conn = "";
        Connection dbConn = null;
        if (dbType.equals("ORACLE")){
            s_Conn= "jdbc:oracle:thin:@"+ dbHostName+ ":"+ dbPortNumber+"/"+dbName;
        }else {
            log.error("DataBase type isnt Oracle.");
        }

        try {
            Class.forName(s_DriverName);
            dbConn = DriverManager.getConnection(s_Conn, dbUserName, dbUserPwd);
        }catch(ClassNotFoundException cnfe){
            FBIU_Exception fException = new FBIU_Exception("Class not found for DB Connection. ", cnfe);
            log.error(fException.getExceptionTrace());
        } catch (SQLException e) {
            FBIU_Exception fException = new FBIU_Exception("Sql excetption in db connection.", e);
            log.error(fException.getExceptionTrace());
        }
        return dbConn;
    }
    public void closeConnection (Connection dbConn) {
        try{
            if(null != dbConn && !dbConn.isClosed()){
                dbConn.close();
                log.info("DB Connection is closed.");
            }
        }catch(SQLException e){
            FBIU_Exception fException = new FBIU_Exception("DB Connection closing process failed.",e);
            log.error(fException.getExceptionTrace());
        }
    }

    public void insetTransactRecord(Connection dbConn, CEConfigDTO ceConfigDTO , DocumentPropDTO dPropDTO){
    	Long reqDateTime = dPropDTO.get_ReqRecvdDateTime();
    	String transactQuery = ceConfigDTO.get_dbInsertQueryTransact().replace("[ObjectStore]", ceConfigDTO.get_objStore()).replace("[GUID]",dPropDTO.get_GUID()).replace("[ReqRecvdDateTime]",reqDateTime.toString()).replace("[DocClass]",dPropDTO.get_DocClass()).replace("[DataSource]",ceConfigDTO.get_DBDataSource()).replace("[Status]",dPropDTO.get_status()).replace("[FileName]",dPropDTO.get_FileName()).replace("[ReqCompletedDateTime]",dPropDTO.get_ReqCompletedDateTime()).replace("[Size]",dPropDTO.get_FileSize()).replace("[MimeType]", dPropDTO.get_mimeType());
    	log.info(BatchThreadPool.get() + "Transaction Query : " + transactQuery);
    	Statement stmt = null;
    	try{
    		stmt = dbConn.createStatement();
    		stmt.execute(transactQuery);
    		
    		int count = stmt.executeUpdate(transactQuery);
    	
    	}catch(SQLException e) {
    		FBIU_Exception fbiuExc = new FBIU_Exception(BatchThreadPool.get()+" | "+"Exception occured while inseting record in tansaction record.", e);
    		log.error(fbiuExc.getExceptionTrace());
    	}
    	finally {
    		try {
				stmt.close();
			} catch (SQLException e) {
				FBIU_Exception fbiuExc = new FBIU_Exception(BatchThreadPool.get()+" | "+"Exception occured while inseting record in tansaction record.", e);
	    		log.error(fbiuExc.getExceptionTrace());
			}
    		
    	}
    }
    
    
    public void insetAuditRecord(Connection dbConn, CEConfigDTO ceConfigDTO , DocumentPropDTO dPropDTO){
    	Long reqDateTime = dPropDTO.get_ReqRecvdDateTime();
    	String AuditQuery = ceConfigDTO.get_dbInsertQueryTransact().replace("[ObjectStore]", ceConfigDTO.get_objStore()).replace("[GUID]",dPropDTO.get_GUID()).replace("[ReqRecvdDateTime]",reqDateTime.toString()).replace("[DocClass]",dPropDTO.get_DocClass()).replace("[DataSource]",ceConfigDTO.get_DBDataSource()).replace("[Status]",dPropDTO.get_status()).replace("[ReqFileName]",dPropDTO.get_FileName()).replace("[AddedOn]",dPropDTO.get_ReqCompletedDateTime()).replace("[Size]",dPropDTO.get_FileSize()).replace("[Comments]", dPropDTO.get_shortErrorMessage());
    	log.info(BatchThreadPool.get() + "Transaction Query : " + AuditQuery);
    	Statement stmt = null;
    	try{
    		stmt = dbConn.createStatement();
    		stmt.execute(AuditQuery);
    		
    		int count = stmt.executeUpdate(AuditQuery);
    	
    	}catch(SQLException e) {
    		FBIU_Exception fbiuExc = new FBIU_Exception(BatchThreadPool.get()+" | "+"Exception occured while inseting record in tansaction record.", e);
    		log.error(fbiuExc.getExceptionTrace());
    	}
    	finally {
    		try {
				stmt.close();
			} catch (SQLException e) {
				FBIU_Exception fbiuExc = new FBIU_Exception(BatchThreadPool.get()+" | "+"Exception occured while inseting record in tansaction record.", e);
	    		log.error(fbiuExc.getExceptionTrace());
			}
    		
    	}
    }

    

}
