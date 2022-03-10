package com.filenetp8.batchimport.services.exception;
/**
@author Ankit Utkarsh
*/
import java.io.PrintWriter;
import java.io.StringWriter;

public class FBIU_Exception extends Exception {

		private static final long serialVersionUID = 1L;
		private String message = null;
		
		public FBIU_Exception(String message) {
			super(message);
			this.message = message;
			
		}

		public FBIU_Exception(Throwable  cause) {
			super(cause);
		}

		public FBIU_Exception(String message, Throwable  cause) {
			super(message, cause);
			this.message = message;
		}

		public String getMessage()  {
			return message;
		}
		
		public String getExceptionTrace() {
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			this.printStackTrace(pw);
			return sw.toString();
		}
		
	
}
