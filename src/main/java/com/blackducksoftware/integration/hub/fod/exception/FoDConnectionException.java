package com.blackducksoftware.integration.hub.fod.exception;

import com.blackducksoftware.integration.hub.exception.HubIntegrationException;

public class FoDConnectionException extends HubIntegrationException{

	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		public FoDConnectionException() {
	        super();
	    }

	    public FoDConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
	        super(message, cause, enableSuppression, writableStackTrace);
	    }

	    public FoDConnectionException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public FoDConnectionException(String message) {
	        super(message);
	    }

	    public FoDConnectionException(Throwable cause) {
	        super(cause);
	    }
	
}
