/*
 * AssetCache.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A system for loading & caching game assets.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.Image;
import javax.imageio.ImageIO;

// import internal packages

import lyra.audio.AudioPlayer;
import lyra.drivers.HttpDriver;
import lyra.util.Config;

// define class

public class AssetCache
{
	// define public class constants

	public static final int TYPE_INTERNAL_IMAGE = 1;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_TEXTURE = 3;
	public static final int TYPE_CONFIG = 4;
	public static final int TYPE_RAW_IMAGE = 5;
	public static final int TYPE_AUDIO = 6;
	public static final int TYPE_UNKNOWN = -1;

	// define private class constants

	private static final String CLASS_NAME = AssetCache.class.getName();

	// define private class class objects

	private class Asset
	{
		private int type = 0;
		private String name = null;
		private Object asset = null;

		public Asset(int type, String name, Object asset)
		{
			this.type = type;
			this.name = name;
			this.asset = asset;
		}

		public int getType()
		{
			return type;
		}

		public String getName()
		{
			return name;
		}

		public Object getAsset()
		{
			return asset;
		}
	}

	// define private class variables

	private HashMap index = null;
	private HttpDriver httpDriver = null;
	private GameEngine gameEngine = null;

	// define private class functions

	private String buildKey(int type, String name)
	{
		String result = null;

		result = new String("/" + type + "/" + name);

		return result;
	}

	private Image buildNewInternalImage(String assetName)
	{
		Image result = null;

		result = gameEngine.buildInternalImage(assetName);

		return result;
	}

	private FileInputStream obtainInputStream(String assetName)
	{
		FileInputStream result = null;

		try {
			if(assetName.startsWith(gameEngine.getPath())) {
				result = new FileInputStream(assetName);
			}
			else {
				result = new FileInputStream(gameEngine.getPath() + "/" +
						assetName);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	private byte[] obtainRemoteAsset(String assetName)
	{
		byte result[] = null;

		try {
			if(httpDriver != null) {
				if(assetName.startsWith(gameEngine.getPath())) {
					result = httpDriver.execute(assetName);
				}
				else {
					result = httpDriver.execute(gameEngine.getPath() + "/" +
							assetName);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	private Image buildNewImage(String assetName)
	{
		int length = 0;
		byte[] buffer = null;

		FileInputStream inputStream = null;
		ByteArrayInputStream byteInput = null;
		BufferedImage imageBuffer = null;
		ImageProducer producer = null;
		Image result = null;

		try {
			if(gameEngine.getAssetMode().equals("FILE")) {
				inputStream = obtainInputStream(assetName);
			}
			if(inputStream != null) {
				length = (int)inputStream.getChannel().size();
				if(length > 0) {
					buffer = new byte[length];
					length = inputStream.read(buffer);
					byteInput = new ByteArrayInputStream(buffer, 0, length);
					imageBuffer = ImageIO.read(byteInput);
				}
				else {
					imageBuffer = ImageIO.read(inputStream);
				}
				inputStream.close();
			}
			else if(gameEngine.getAssetMode().equals("HTTP")) {
				buffer = obtainRemoteAsset(assetName);
				byteInput = new ByteArrayInputStream(buffer, 0, buffer.length);
				imageBuffer = ImageIO.read(byteInput);
			}
			else {
				throw new Exception("unable to obtain asset from unknown " +
						"game engine mode");
			}
			producer = imageBuffer.getSource();
			if(producer != null) {
				result = gameEngine.createImage(producer);
			}
			else {
				result = (Image)imageBuffer;
			}

			byteInput.close();

			java.lang.Runtime.getRuntime().freeMemory();
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	private Texture buildNewTexture(String assetName)
	{
		Image image = null;
		Texture result = null;

		try {
			image = (Image)this.getAsset(TYPE_IMAGE, assetName);
			result = new Texture(assetName, image, gameEngine);
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	private Config buildNewConfig(String assetName)
	{
		byte[] buffer = null;

		Config result = null;

		try {
			if(gameEngine.getAssetMode().equals("FILE")) {
				result = new Config(gameEngine.getPath() + "/" + assetName);
			}
			else if(gameEngine.getAssetMode().equals("HTTP")) {
				buffer = obtainRemoteAsset(assetName);
				result = new Config(assetName, buffer);
			}
			else {
				throw new Exception("unable to obtain asset from unknown " +
						"game engine mode");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	private Image buildNewRawImage(String assetName)
	{
		int length = 0;
		byte[] buffer = null;

		FileInputStream inputStream = null;
		ByteArrayInputStream byteInput = null;
		BufferedImage imageBuffer = null;
		Image result = null;

		try {
			if(gameEngine.getAssetMode().equals("FILE")) {
				inputStream = obtainInputStream(assetName);
			}
			if(inputStream != null) {
				length = (int)inputStream.getChannel().size();
				if(length > 0) {
					buffer = new byte[length];
					length = inputStream.read(buffer);
					byteInput = new ByteArrayInputStream(buffer, 0, length);
					imageBuffer = ImageIO.read(byteInput);
				}
				else {
					imageBuffer = ImageIO.read(inputStream);
				}
				inputStream.close();
			}
			else if(gameEngine.getAssetMode().equals("HTTP")) {
				buffer = obtainRemoteAsset(assetName);
				byteInput = new ByteArrayInputStream(buffer, 0, buffer.length);
				imageBuffer = ImageIO.read(byteInput);
			}
			else {
				throw new Exception("unable to obtain asset from unknown " +
						"game engine mode");
			}

			result = (Image)imageBuffer;

			byteInput.close();

			java.lang.Runtime.getRuntime().freeMemory();
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	private Image finalizeRawImage(String assetName)
	{
		Image result = null;
		BufferedImage image = null;
		ImageProducer producer = null;

		try {
			image = (BufferedImage)this.getAsset(TYPE_RAW_IMAGE, assetName);
			producer = image.getSource();
			result = gameEngine.createImage(producer);
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	private AudioPlayer buildNewAudioPlayer(String assetName)
	{
		Config config = null;
		AudioPlayer result = null;

		try {
			config = (Config)this.getAsset(TYPE_CONFIG, assetName);
			result = new AudioPlayer(config, gameEngine);
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	// define public class functions

	public AssetCache(GameEngine gameEngine)
	{
		this.gameEngine = gameEngine;
		index = new HashMap();
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void loadHttpDriver(String hostname, int port)
	{
		httpDriver = new HttpDriver(hostname, port);
	}

	public boolean addAsset(int type, String assetName, Object object)
	{
		boolean result = false;
		String key = null;
		Asset asset = null;

		try {
			if((type != TYPE_IMAGE) && (type != TYPE_TEXTURE) &&
					(type != TYPE_CONFIG) && (type != TYPE_RAW_IMAGE) &&
					(type != TYPE_AUDIO)) {
				throw new Exception("{" + CLASS_NAME +
						"} invalid asset type " + type);
			}
			key = buildKey(type, assetName);
			asset = (Asset)index.get(key);
			if(asset == null) {
				asset = new Asset(type, assetName, object);
				index.put(key, asset);
				result = true;
			}
			else {
				throw new Exception("{" + CLASS_NAME +
						"} unable to add asset '" + assetName + "' type " +
						type + ", already exists");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public Object getAsset(int type, String assetName)
	{
		String key = null;
		Asset asset = null;
		Object newAsset = null;
		Object result = null;

		try {
			if((type != TYPE_INTERNAL_IMAGE) && (type != TYPE_IMAGE) &&
					(type != TYPE_TEXTURE) && (type != TYPE_CONFIG) &&
					(type != TYPE_RAW_IMAGE) && (type != TYPE_AUDIO)) {
				throw new Exception("{" + CLASS_NAME +
						"} invalid asset type " + type);
			}
			key = buildKey(type, assetName);
			asset = (Asset)index.get(key);
			if(asset == null) {
				if(type == TYPE_INTERNAL_IMAGE) {
					newAsset = buildNewInternalImage(assetName);
				}
				else if(type == TYPE_IMAGE) {
					newAsset = buildNewImage(assetName);
				}
				else if(type == TYPE_TEXTURE) {
					newAsset = buildNewTexture(assetName);
				}
				else if(type == TYPE_CONFIG) {
					newAsset = buildNewConfig(assetName);
				}
				else if(type == TYPE_RAW_IMAGE) {
					newAsset = buildNewRawImage(assetName);
				}
				else if(type == TYPE_AUDIO) {
					newAsset = buildNewAudioPlayer(assetName);
				}
				if(newAsset != null) {
					asset = new Asset(type, assetName, newAsset);
					index.put(key, asset);
				}
				else {
					throw new Exception("{" + CLASS_NAME +
							"} failed to obtain asset '" + assetName +
							"' type " + type);
				}
				result = newAsset;
			}
			else {
				result = asset.getAsset();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public boolean removeAsset(int type, String assetName)
	{
		boolean result = false;
		String key = null;

		try {
			if((type != TYPE_IMAGE) && (type != TYPE_TEXTURE)) {
				throw new Exception("{" + CLASS_NAME +
						"} invalid asset type " + type);
			}
			key = buildKey(type, assetName);
			if(index.remove(key) != null) {
				result = true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void finalizeRawAsset(int type, String assetName)
	{
		String key = null;
		Asset asset = null;
		Object newAsset = null;

		try {
			if((type != TYPE_IMAGE) && (type != TYPE_TEXTURE) &&
					(type != TYPE_CONFIG) && (type != TYPE_RAW_IMAGE)) {
				throw new Exception("{" + CLASS_NAME +
						"} invalid asset type " + type);
			}
			newAsset = finalizeRawImage(assetName);
			key = buildKey(TYPE_IMAGE, assetName);
			asset = new Asset(type, assetName, newAsset);
			index.put(key, asset);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public int getAssetCount()
	{
		return index.size();
	}

	public ByteArrayInputStream obtainAssetStream(String assetName)
	{
		int length = 0;
		byte[] buffer = null;

		FileInputStream inputStream = null;
		ByteArrayInputStream result = null;

		try {
			if(gameEngine.getAssetMode().equals("HTTP")) {
				buffer = obtainRemoteAsset(assetName);
				result = new ByteArrayInputStream(buffer, 0, buffer.length);
			}
			else if(gameEngine.getAssetMode().equals("FILE")) {
				inputStream = obtainInputStream(assetName);
				if(inputStream != null) {
					length = (int)inputStream.getChannel().size();
					if(length > 0) {
						buffer = new byte[length];
						length = inputStream.read(buffer);
						result = new ByteArrayInputStream(buffer, 0, length);
					}
				}
			}
			else {
				throw new Exception("unable to obtain asset from unknown " +
						"game engine mode");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}

