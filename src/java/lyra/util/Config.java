/*
 * Config.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * The configuration file management API.
 *
 * Written by Josh English.
 */

// define package space

package lyra.util;

// import external packages

import java.util.Hashtable;
import java.util.regex.PatternSyntaxException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.ParseException;

// define class

public class Config 
{
	// define private class constants

	private static final String CLASS_NAME = Config.class.getName();

	// define private data members

	private int totalOrder = 0;
	private int lineCount = 0;
	private int sectionCount = 0;
	private String filename = null;
	private Hashtable sections = null;

	// define private classes

	private class OrderedString implements Comparable
	{
		public int order = 0;
		public String contents = null;

		public OrderedString(String text) 
		{
			contents = text;
			order = totalOrder;
			totalOrder++;
		}

		public int hashCode()
		{
			return contents.hashCode();
		}

		public boolean equals(Object otherIn)
		{
			OrderedString other = null;

			other = (OrderedString)otherIn;

			return contents.equals(other.contents);
		}

		public int compareTo(Object otherIn)
		{
			OrderedString other = null;

			other = (OrderedString)otherIn;

			if(order == other.order) {
				return 0;
			}
			else if(order < other.order) {
				return -1;
			}
			return 1;
		}
	}

	// define private methods

	private String processLine(String inLine, String currentSection)
			throws ParseException
	{
		String[] keyValue = { null, null };

		if(inLine == null) {
			throw new ParseException("error - tried to process null line", 
					lineCount);
		}

		// trim

		String line = inLine.trim();

		// comments

		if(line.equals("") || line.startsWith("#") || line.startsWith(";") ||
				line.startsWith("'") || line.startsWith("//")) {
			return currentSection;
		}

		// new section

		if(line.startsWith("[")) {
			if(line.indexOf("]") == -1) {
				throw new ParseException("error - section line missing " +
						"closing ']'", lineCount);
			}
			currentSection = line.substring(1, line.indexOf("]"));
			addSection(currentSection);
			return currentSection;
		}

		if(currentSection == null) {
			throw new ParseException("error - property / value line occurred " +
					"before section heading", lineCount);
		}

		// else key-value for current section

		try {
			keyValue = line.split(":?=");
		}
		catch(PatternSyntaxException e) {
			throw new ParseException("error with pattern (unlikely)",
					lineCount);
		}

		if((keyValue == null) || (keyValue.length < 2) ||
				(keyValue[0] == null) || (keyValue[1] == null)) {
			System.err.println("{" + CLASS_NAME + 
					"} error with key-value line #" + lineCount);
			return currentSection;
		}

		keyValue[0] = keyValue[0].trim();
		keyValue[1] = keyValue[1].trim();
		if(keyValue[1].indexOf(";") != -1) {
			keyValue[1] = keyValue[1].substring(0, keyValue[1].indexOf(";"));
		}

		add(currentSection, keyValue[0], keyValue[1]);

		return currentSection;
	}

	private void cleanConfig()
	{
		this.filename = null;
		this.lineCount = 0;
		this.sectionCount = 0;
		this.sections = null;
	}

	// define public methods

	public Config(String filename) throws IOException, ParseException
	{
		this.filename = filename;

		totalOrder = 0;
		lineCount = 0;
		sectionCount = 0;
		sections = null;

		loadPropertyFile(filename);
	}

	public Config(String sourceName, byte[] data) throws IOException,
		   ParseException
	{
		this.filename = sourceName;

		totalOrder = 0;
		lineCount = 0;
		sectionCount = 0;
		sections = null;

		loadFromRawData(data);
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public void loadPropertyFile(String filename) throws IOException,
		   ParseException
	{
		int length = 0;
		byte[] buffer = null;

		FileInputStream inputStream = null;

		cleanConfig();

		this.filename = filename;

		inputStream = new FileInputStream(filename);
		length = (int)inputStream.getChannel().size();
		buffer = new byte[length];
		length = inputStream.read(buffer);

		loadFromRawData(buffer);

		inputStream.close();
	}

	public void loadFromRawData(byte[] data) throws IOException, ParseException
	{
		boolean parseFlag = false;

		String line = null;
		String section = null;
		ByteArrayInputStream inputStream = null;
		BufferedReader reader = null;

		if((data == null) || (data.length < 1)) {
			return;
		}

		cleanConfig();

		inputStream = new ByteArrayInputStream(data);
		reader = new BufferedReader(new InputStreamReader(inputStream));
		line = reader.readLine();
		while(line != null) {
			if(!parseFlag) {
				if(line.length() == 0) {
					parseFlag = true;
				}
			}
			else {
				section = processLine(line, section);
			}
			line = reader.readLine();
		}

		inputStream.close();
	}

	public String getString(String sectionNameIn, String propertyIn)
	{
		OrderedString sectionName = null;
		OrderedString property = null;
		Hashtable section = null;

		if((sections == null) || (sectionNameIn == null) ||
				(propertyIn == null)) {
			return null;
		}

		sectionName = new OrderedString(sectionNameIn);
		property = new OrderedString(propertyIn);
		section = (Hashtable)sections.get(sectionName);

		if(section == null) {
			return null;
		}

		return (String)section.get(property);
	}

	public String getString(String sectionName, String property,
			String defaultValue)
	{
		String result = null;

		result = getString(sectionName, property);

		if(result == null) {
			return defaultValue;
		}

		return result;
	}

	public Boolean getBoolean(String sectionName, String property)
	{
		String result = null;

		result = getString(sectionName, property);

		if(result == null) {
			return null;
		}
		return new Boolean(result);
	}

	public Boolean getBoolean(String sectionName, String property, 
			boolean defaultValue)
	{
		Boolean result = null;

		result = getBoolean(sectionName, property);

		if(result == null) {
			return new Boolean(defaultValue);
		}
		return result;
	}

	public Integer getInteger(String sectionName, String property)
	{
		String result = null;

		result = getString(sectionName, property);

		if(result == null) {
			return null;
		}
		return new Integer(result);
	}

	public Integer getInteger(String sectionName, String property, 
			int defaultValue)
	{
		Integer result = null;

		result = getInteger(sectionName, property);

		if(result == null) {
			return new Integer(defaultValue);
		}
		return result;
	}

	public Long getLong(String sectionName, String property)
	{
		String result = null;

		result = getString(sectionName, property);

		if(result == null) {
			return null;
		}
		return new Long(result);
	}

	public Long getLong(String sectionName, String property, long defaultValue)
	{
		Long result = null;

		result = getLong(sectionName, property);
		if(result == null) {
			return new Long(defaultValue);
		}
		return result;
	}

	public Double getDouble(String sectionName, String property)
	{
		String result = null;

		result = getString(sectionName, property);

		if(result == null) {
			return null;
		}
		return new Double(result);
	}

	public Double getDouble(String sectionName, String property, 
			double defaultValue)
	{
		Double result = null;

		result = getDouble(sectionName, property);

		if(result == null) {
			return new Double(defaultValue);
		}
		return result;
	}

	public void addSection(String sectionNameIn)
	{
		if(sectionCount == 0) {
			sections = new Hashtable();
		}
		
		OrderedString sectionName = new OrderedString(sectionNameIn);
		
		if(sections.containsKey(sectionName)) {
			return;
		}
		
		sectionCount++;
		sections.put(sectionName, new Hashtable());
	}

	public void add(String sectionNameIn, String propertyIn, String value)
	{
		if(sections == null) {
			addSection(sectionNameIn);
		}
		
		OrderedString sectionName = new OrderedString(sectionNameIn);

		Hashtable section = (Hashtable)sections.get(sectionName);
		if(section == null) {
			addSection(sectionNameIn);
			section = (Hashtable)sections.get(sectionName);
		}
		OrderedString property = new OrderedString(propertyIn);
		section.put(property, value);
	}
}

