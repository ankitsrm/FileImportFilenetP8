package com.filenetp8.batchimport.services.util;
import java.util.ArrayList;
public class DocumentPropDTO {
	private String _envDetails;
	private String _dataTypeDetails;
	private String _docpropDetails;
	private ArrayList<String> _listOfBatchDirect;
	private String _fileExten;
	private String _fileNetFolderPath;
	private String _mimeType;
	private String _responsePath;
	private String _errorPath;
	private String _errorMessage;
	private String _GUID;
	private String _shortErrorMessage ;
	private long _ReqRecvdDateTime;
	private String _status;
	public String get_status() {
		return _status;
	}
	public void set_status(String _status) {
		this._status = _status;
	}
	public long get_ReqRecvdDateTime() {
		return _ReqRecvdDateTime;
	}
	public void set_ReqRecvdDateTime(long _ReqRecvdDateTime) {
		this._ReqRecvdDateTime = _ReqRecvdDateTime;
	}
	private String _DocClass;
	private String _FileName;
	private String _ReqCompletedDateTime;
	private String _FileSize;
			
	
	public String get_DocClass() {
		return _DocClass;
	}
	public void set_DocClass(String _DocClass) {
		this._DocClass = _DocClass;
	}
	public String get_FileName() {
		return _FileName;
	}
	public void set_FileName(String _FileName) {
		this._FileName = _FileName;
	}
	public String get_ReqCompletedDateTime() {
		return _ReqCompletedDateTime;
	}
	public void set_ReqCompletedDateTime(String _ReqCompletedDateTime) {
		this._ReqCompletedDateTime = _ReqCompletedDateTime;
	}
	public String get_FileSize() {
		return _FileSize;
	}
	public void set_FileSize(String _FileSize) {
		this._FileSize = _FileSize;
	}
	public String get_shortErrorMessage() {
		return _shortErrorMessage;
	}
	public void set_shortErrorMessage(String _shortErrorMessage) {
		this._shortErrorMessage = _shortErrorMessage;
	}
	
	public String get_fileNetFolderPath() {
		return _fileNetFolderPath;
	}
	public void set_fileNetFolderPath(String _fileNetFolderPath) {
		this._fileNetFolderPath = _fileNetFolderPath;
	}
	
	public String get_errorMessage() {
		return _errorMessage;
	}
	public void set_errorMessage(String _errorMessage) {
		this._errorMessage = _errorMessage;
	}
	
	public String get_GUID() {
		return _GUID;
	}
	public void set_GUID(String _GUID) {
		this._GUID = _GUID;
	}
	
	public String get_responsePath() {
		return _responsePath;
	}
	public void set_responsePath(String _responsePath) {
		this._responsePath = _responsePath;
	}
	public String get_errorPath() {
		return _errorPath;
	}
	public void set_errorPath(String _errorPath) {
		this._errorPath = _errorPath;
	}
	public String get_envDetails() {
		return _envDetails;
	}
	public void set_envDetails(String _envDetails) {
		this._envDetails = _envDetails;
	}
	public String get_dataTypeDetails() {
		return _dataTypeDetails;
	}
	public void set_dataTypeDetails(String _dataTypeDetails) {
		this._dataTypeDetails = _dataTypeDetails;
	}
	public String get_docpropDetails() {
		return _docpropDetails;
	}
	public void set_docpropDetails(String _docpropDetails) {
		this._docpropDetails = _docpropDetails;
	}
	public ArrayList<String> get_listOfBatchDirect() {
		return _listOfBatchDirect;
	}
	public void set_listOfBatchDirect(ArrayList<String> _listOfBatchDirect) {
		this._listOfBatchDirect = _listOfBatchDirect;
	}
	public String get_fileExten() {
		return _fileExten;
	}
	public void set_fileExten(String _fileExten) {
		this._fileExten = _fileExten;
	}
	public String get_mimeType() {
		return _mimeType;
	}
	public void set_mimeType(String _mimeType) {
		this._mimeType = _mimeType;
	}
	
	
}
