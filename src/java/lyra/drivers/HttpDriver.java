/*
 * HttpDriver.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple interface for HTTP communications.
 *
 * Written by Josh English.
 */

// define package space

package lyra.drivers;

// define class

public class HttpDriver extends TcpIpDriver
{
	// define private class constants

	private static final String CLASS_NAME = HttpDriver.class.getName();

	// define public class constants

	public static final int HTTP_TIMEOUT_MILLIS = 4096;

	// define public class functions

	public HttpDriver(String hostname, int port)
	{
		super(hostname, port);

		timeoutMillis = HTTP_TIMEOUT_MILLIS;
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public byte[] execute(String fileName)
	{
		byte result[] = null;
		String request = null;

		try {
			request = new String("GET /" + fileName.trim() + " HTTP/1.0\n\n\n");
			result = this.communicate(request.getBytes());
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}

