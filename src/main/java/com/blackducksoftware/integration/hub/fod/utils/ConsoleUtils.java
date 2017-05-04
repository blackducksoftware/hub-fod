package com.blackducksoftware.integration.hub.fod.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class ConsoleUtils {
	
	private static final Logger logger = (Logger)LoggerFactory.getLogger(ConsoleUtils.class);
	
	public static String readLine(String format, Object... args) {
		  try {
			if (System.console() != null) {
		        return System.console().readLine(format, args);
		    }
		    System.out.print(String.format(format, args));
		    BufferedReader reader = new BufferedReader(new InputStreamReader(
		            System.in));
		  
				return reader.readLine();
		} catch (IOException e) {
			logger.error("Reading System Console:" + e.getMessage());
			return null;
		}
	}
	
	public static char[] readPassword(String format, Object... args)
	{
	if (System.console() != null)
	    return System.console().readPassword(format, args);
	return readLine(format, args).toCharArray();
	}

}
