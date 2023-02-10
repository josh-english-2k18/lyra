/*
 * TcpIpDriver.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple interface for TCP/IP communications.
 *
 * Written by Josh English.
 */

// define package space

package lyra.drivers;

// import external packages

import java.io.ByteArrayOutputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.net.InetAddress;
import java.net.Socket;

// define class

public class TcpIpDriver
{
	// define private class constants

	private static final String CLASS_NAME = TcpIpDriver.class.getName();

	// define public class constants

	public static final int DEFAULT_PACKET_SIZE = 8192;
	public static final int DEFAULT_TIMEOUT_MILLIS = 1024;

	// define protected class variables

	protected int port = 0;
	protected int timeoutMillis = 0;
	protected int packetSize = 0;
	protected String hostname = null;
	protected Socket socket = null;

	// define public class functions

	public TcpIpDriver(String hostname, int port)
	{
		this.hostname = hostname;
		this.port = port;

		timeoutMillis = DEFAULT_TIMEOUT_MILLIS;
		packetSize = DEFAULT_PACKET_SIZE;
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getHostname()
	{
		return hostname;
	}

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getSocketTimeout()
	{
		return timeoutMillis;
	}

	public void setSocketTimeout(int millis)
	{
		timeoutMillis = millis;
	}

	public int getPacketSize()
	{
		return packetSize;
	}

	public void setPacketSize(int bytes)
	{
		packetSize = bytes;
	}

	public byte[] communicate(byte[] request)
	{
		long length = 0;
		byte[] packet = null;
		byte[] response = null;

		PrintWriter out = null;
		InputStream in = null;
		ByteBuffer order = null;
		ByteArrayOutputStream outputStream = null;

		try {
			if(request == null) {
				throw new Exception("unable to transmit null request");
			}

			// connect to host

			socket = new Socket(InetAddress.getByName(hostname), port);
			if(socket == null) {
				throw new Exception("failed to open connection to '" +
						hostname + ":" + port + "'");
			}

			// set socket connection properties

			socket.setSoTimeout(timeoutMillis);

			// write output stream

			out = new PrintWriter(
					new BufferedWriter(
						new OutputStreamWriter(
							socket.getOutputStream())), true);

			out.println(new String(request));

			// read input stream

			in = socket.getInputStream();
			packet = new byte[packetSize];
			outputStream = new ByteArrayOutputStream(packetSize);

			length = in.read(packet, 0, packet.length);
			while(length > -1) {
				order = ByteBuffer.wrap(packet, 0, (int)length);
				order.order(ByteOrder.LITTLE_ENDIAN);
				outputStream.write(order.array(), 0, (int)length);
				length = in.read(packet, 0, packet.length);
			}

			outputStream.close();
			response = outputStream.toByteArray();

			socket.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return response;
	}
}

