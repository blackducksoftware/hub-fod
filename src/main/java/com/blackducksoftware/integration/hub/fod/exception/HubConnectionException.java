package com.blackducksoftware.integration.hub.fod.exception;

import com.blackducksoftware.integration.hub.exception.HubIntegrationException;

public class HubConnectionException extends HubIntegrationException{

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		public HubConnectionException() {
	        super();
	    }

	    public HubConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	        super(message, cause, enableSuppression, writableStackTrace);
	    }

	    public HubConnectionException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public HubConnectionException(String message) {
	        super(message);
	    }

	    public HubConnectionException(Throwable cause) {
	        super(cause);
	    }
	
}
