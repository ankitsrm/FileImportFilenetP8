package com.filenetp8.batchimport.services.util;

public class CEConfigDTO {
	private String _ceURL;
	private String _userID;
	private String _encryptPassw;
	private String _jaasStanza;
	private String _objStore;
	private int _dbConnFlow;
	private String _DBDataSource;
	private String _DBType;
	private String _dbHostName;
	private String _dbInsertQueryTransact;
	private String _dbInsertQueryAudit;
	private int _dbPortNum;
	private String _dbName;
	private String _dbUserID;
	private String _dbEncryptPassw;
	
	public String get_dbInsertQueryTransact() {
		return _dbInsertQueryTransact;
	}
	public void set_dbInsertQueryTransact(String _dbInsertQueryTransact) {
		this._dbInsertQueryTransact = _dbInsertQueryTransact;
	}
	public String get_dbInsertQueryAudit() {
		return _dbInsertQueryAudit;
	}
	public void set_dbInsertQueryAudit(String _dbInsertQueryAudit) {
		this._dbInsertQueryAudit = _dbInsertQueryAudit;
	}
	public int get_dbConnFlow() {
		return _dbConnFlow;
	}
	public void set_dbConnFlow(int _dbConnFlow) {
		this._dbConnFlow = _dbConnFlow;
	}
	

	
	public String get_DBDataSource() {
		return _DBDataSource;
	}
	public void set_DBDataSource(String _DBDataSource) {
		this._DBDataSource = _DBDataSource;
	}
	public String get_DBType() {
		return _DBType;
	}
	public void set_DBType(String _DBType) {
		this._DBType = _DBType;
	}
	public String get_dbHostName() {
		return _dbHostName;
	}
	public void set_dbHostName(String _dbHostName) {
		this._dbHostName = _dbHostName;
	}
	public int get_dbPortNum() {
		return _dbPortNum;
	}
	public void set_dbPortNum(int _dbPortNum) {
		this._dbPortNum = _dbPortNum;
	}
	public String get_dbName() {
		return _dbName;
	}
	public void set_dbName(String _dbName) {
		this._dbName = _dbName;
	}
	public String get_dbUserID() {
		return _dbUserID;
	}
	public void set_dbUserID(String _dbUserID) {
		this._dbUserID = _dbUserID;
	}
	public String get_dbEncryptPassw() {
		return _dbEncryptPassw;
	}
	public void set_dbEncryptPassw(String _dbEncryptPassw) {
		this._dbEncryptPassw = _dbEncryptPassw;
	}
	
	

	public String get_ceURL() {
		return _ceURL;
	}
	public void set_ceURL(String _ceURL) {
		this._ceURL = _ceURL;
	}
	public String get_userID() {
		return _userID;
	}
	public void set_userID(String _userID) {
		this._userID = _userID;
	}
	public String get_encryptPassw() {
		return _encryptPassw;
	}
	public void set_encryptPassw(String _encryptPassw) {
		this._encryptPassw = _encryptPassw;
	}
	public String get_jaasStanza() {
		return _jaasStanza;
	}
	public void set_jaasStanza(String _jaasStanza) {
		this._jaasStanza = _jaasStanza;
	}
	public String get_objStore() {
		return _objStore;
	}
	public void set_objStore(String _objStore) {
		this._objStore = _objStore;
	}
}