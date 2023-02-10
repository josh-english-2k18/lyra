/*
 * GameScene.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik scene extension.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// import external packages

import java.util.Iterator;
import java.util.HashMap;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

// import internal packages

import lyra.*;
import lyra.drivers.MouseDriver;

// define class

public class GameScene extends Scene
{
	// define private class constants

	private static final String CLASS_NAME = GameScene.class.getName();

	// define public class constants

	public static final double ZOOM_MIN = 40.0;
	public static final double ZOOM_MAX = 200.0;
	public static final double ZOOM_INCREMENT = 10.0;
	public static final double ZOOM_DEFAULT = 150.0;

	public static final int PLANET_PIXELS = 32;

	// define protected class variables

	protected boolean wasZoomed = false;
	protected boolean hasSnapshot = false;
	protected int backgroundWidth = 0;
	protected int backgroundHeight = 0;
	protected int spriteWidth = 0;
	protected int spriteHeight = 0;
	protected double zoomLevel = 0.0;
	protected Image background = null;
	protected Game game = null;
	protected HashMap spriteNameIndex = null;

	// define class private functions

	private void updateBackgroundZoom()
	{
		int localX = 0;
		int localY = 0;
		double localWidth = 0.0;
		double localHeight = 0.0;

		try {
			// center the camera coords based upon the new map size

			localWidth = ((double)gameEngine.getImageWidth(background) *
					(zoomLevel / 100.0));
			localHeight = ((double)gameEngine.getImageHeight(background) *
					(zoomLevel / 100.0));
			localX = (int)((localWidth - (double)width) / 2.0);
			if(localX < 0) {
				localX = 0;
			}
			localY = (int)((localHeight - (double)height) / 2.0);
			if(localY < 0) {
				localY = 0;
			}
			camera.setCameraCoords(localX, localY);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void updatePlanetZoom(Sprite sprite, Planet planet)
	{
		int localX = 0;
		int localY = 0;
		double localWidth = 0.0;
		double localHeight = 0.0;
		double localPixels = 0.0;

		try {
			// obtain mipmapped sprite texture

			localWidth = ((double)PLANET_PIXELS * (zoomLevel / 100.0));
			localHeight = ((double)PLANET_PIXELS * (zoomLevel / 100.0));
			sprite.setSizes((int)localWidth, (int)localHeight);
			sprite.setImageTexture(Sprite.DIRECTION_EAST,
					planet.getImageName());

			// set the sprite location

			localPixels = ((double)PLANET_PIXELS * (zoomLevel / 100.0));
			localX = (int)((double)planet.getX() * localPixels);
			localY = (int)((double)planet.getY() * localPixels);
			sprite.setLocation(localX, localY);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	// define class public functions

	public GameScene(String name, int x, int y, int width, int height,
			Game game, GameEngine gameEngine)
	{
		super(name, x, y, width, height, gameEngine);

		this.game = game;

		wasZoomed = false;
		hasSnapshot = false;
		backgroundWidth = 0;
		backgroundHeight = 0;
		spriteWidth = 0;
		spriteHeight = 0;
		zoomLevel = ZOOM_DEFAULT;
		background = null;
		spriteNameIndex = new HashMap();
	}

	public boolean wasZoomed()
	{
		if(wasZoomed) {
			wasZoomed = false;
			return true;
		}
		return false;
	}

	public double getZoom()
	{
		return zoomLevel;
	}

	public int getZoomWidth()
	{
		double result = 0.0;

		if(background != null) {
			result = ((double)gameEngine.getImageWidth(background) *
					(zoomLevel / 100.0));
		}

		return (int)result;
	}

	public int getZoomHeight()
	{
		double result = 0.0;

		if(background != null) {
			result = ((double)gameEngine.getImageHeight(background) *
					(zoomLevel / 100.0));
		}

		return (int)result;
	}

	public int getSpriteZoomWidth()
	{
		double result = 0.0;

		result = ((double)PLANET_PIXELS * (zoomLevel / 100.0));

		return (int)result;
	}

	public int getSpriteZoomHeight()
	{
		double result = 0.0;

		result = ((double)PLANET_PIXELS * (zoomLevel / 100.0));

		return (int)result;
	}

	public int zoomConvertCameraX(int cameraX)
	{
		int result = 0;
		double localWidth = 0.0;

		try {
			localWidth = ((double)gameEngine.getImageWidth(background) *
					(zoomLevel / 100.0));
			result = (int)((double)cameraX * (localWidth / (double)width));
			if(result < 0) {
				result = 0;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int zoomConvertCameraY(int cameraY)
	{
		int result = 0;
		double localHeight = 0.0;

		try {
			localHeight = ((double)gameEngine.getImageHeight(background) *
					(zoomLevel / 100.0));
			result = (int)((double)cameraY * (localHeight / (double)height));
			if(result < 0) {
				result = 0;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void setSpriteSize(int width, int height)
	{
		spriteWidth = width;
		spriteHeight = height;
	}

	public void commitSprite(Sprite sprite, int x, int y)
	{
		sprite.setLocation(x, y);
		spriteSet.add(sprite);
		spriteNameIndex.put(sprite.getName(), sprite);
	}

	public Sprite getSprite(String name)
	{
		return (Sprite)spriteNameIndex.get(name);
	}

	public void commitBackground(Image image, int width, int height)
	{
		backgroundWidth = width;
		backgroundHeight = height;
		background = image;
	}

	public void reset()
	{
		Iterator iterator = null;
		Sprite sprite = null;
		Planet planet = null;


		try {
			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				planet = game.getPlanet(sprite.getName());
				if(planet == null) {
					throw new Exception("failed to locate planet '" +
							sprite.getName() + "' in index");
				}

				updatePlanetZoom(sprite, planet);

				sprite.applyCamera(camera.getCameraX(), camera.getCameraY(),
						camera.getWindowWidth(), camera.getWindowHeight());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void handleZoom(int direction, boolean isLaunchMode,
			Planet selectedPlanet)
	{
		boolean hasChanged = false;
		boolean hasSelected = false;

		Iterator iterator = null;
		Sprite sprite = null;
		Sprite selectedSprite = null;
		Planet planet = null;

		try {
			if(direction > 0) {
				zoomLevel += ZOOM_INCREMENT;
				if(zoomLevel > ZOOM_MAX) {
					zoomLevel = ZOOM_MAX;
				}
				hasChanged = true;
			}
			else if(direction < 0) {
				zoomLevel -= ZOOM_INCREMENT;
				if(zoomLevel < ZOOM_MIN) {
					zoomLevel = ZOOM_MIN;
				}
				hasChanged = true;
			}

			if(hasChanged) {
				wasZoomed = true;
			}
			else {
				return;
			}

			if((hasChanged) && (background != null)) {
				updateBackgroundZoom();
			}

			if(isLaunchMode) {
				selectedSprite = getSprite(selectedPlanet.getName());
			}

			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				if(hasChanged) {
					planet = game.getPlanet(sprite.getName());
					updatePlanetZoom(sprite, planet);
				}

				sprite.applyCamera(camera.getCameraX(), camera.getCameraY(),
						camera.getWindowWidth(), camera.getWindowHeight());

				if(!sprite.isVisible()) {
					continue;
				}

				if(sprite.isSelected()) {
					if(sprite != selectedSprite) {
						isLaunchMode = false;
					}
					hasSelected = true;
				}
			}

			if(hasSelected) {
				iterator = spriteSet.iterator();
				while(iterator.hasNext()) {
					sprite = (Sprite)iterator.next();
					if(sprite == null) {
						break;
					}

					if(!sprite.isVisible()) {
						continue;
					}

					if((isLaunchMode) && (sprite.hasOutline()) &&
							(sprite != selectedSprite)) {
						planet = game.getPlanet(sprite.getName());
						if(planet.getOwnerId() !=
								selectedPlanet.getOwnerId()) {
							sprite.setOutlineColor(Color.red);
						}
						else {
							sprite.setOutlineColor(Color.green);
						}
						continue;
					}

					if(!sprite.isSelected()) {
						sprite.setHasOutline(false);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void processMouseEvent(int type, int mouseX, int mouseY,
			MouseEvent event, boolean isLaunchMode, Planet selectedPlanet)
	{
		boolean hasChanged = false;
		boolean hasSelected = false;
		int localX = 0;
		int localY = 0;
		double localWidth = 0.0;
		double localHeight = 0.0;
		double localPixels = 0.0;

		MouseWheelEvent wheelEvent = null;
		Iterator iterator = null;
		Sprite sprite = null;
		Sprite selectedSprite = null;
		Planet planet = null;

		if(type == MouseDriver.EVENT_WHEEL_MOVED) {
			try {
				wheelEvent = (MouseWheelEvent)event;
				if(wheelEvent.getWheelRotation() < 0) {
					zoomLevel += ZOOM_INCREMENT;
					if(zoomLevel > ZOOM_MAX) {
						zoomLevel = ZOOM_MAX;
					}
					hasChanged = true;
				}
				else if(wheelEvent.getWheelRotation() > 0) {
					zoomLevel -= ZOOM_INCREMENT;
					if(zoomLevel < ZOOM_MIN) {
						zoomLevel = ZOOM_MIN;
					}
					hasChanged = true;
				}
			}
			catch(Exception e) {
				// do nothing - handles oddity in non-wheel events being sent
				// as wheel events
			}

			// if a zoom has occured, re-position the camera to center of map

			if((hasChanged) && (background != null)) {
				updateBackgroundZoom();
			}
		}
		else {
			camera.processMouseEvent(camera.MODE_NORMAL, type, mouseX, mouseY,
					event);
		}

		if(hasChanged) {
			wasZoomed = true;
		}

		try {
			if(isLaunchMode) {
				selectedSprite = getSprite(selectedPlanet.getName());
			}

			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				if(hasChanged) {
					planet = game.getPlanet(sprite.getName());
					updatePlanetZoom(sprite, planet);
				}

				sprite.applyCamera(camera.getCameraX(), camera.getCameraY(),
						camera.getWindowWidth(), camera.getWindowHeight());

				if(!sprite.isVisible()) {
					continue;
				}

				sprite.processMouseEvent(type, mouseX, mouseY, event);
				if(sprite.isSelected()) {
					if(sprite != selectedSprite) {
						isLaunchMode = false;
					}
					hasSelected = true;
				}
			}

			if(hasSelected) {
				iterator = spriteSet.iterator();
				while(iterator.hasNext()) {
					sprite = (Sprite)iterator.next();
					if(sprite == null) {
						break;
					}

					if(!sprite.isVisible()) {
						continue;
					}

					if((isLaunchMode) && (sprite.hasOutline()) &&
							(sprite != selectedSprite)) {
						planet = game.getPlanet(sprite.getName());
						if(planet.getOwnerId() !=
								selectedPlanet.getOwnerId()) {
							sprite.setOutlineColor(Color.red);
						}
						else {
							sprite.setOutlineColor(Color.green);
						}
						continue;
					}

					if(!sprite.isSelected()) {
						sprite.setHasOutline(false);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void resetSelections()
	{
		Iterator iterator = null;
		Sprite sprite = null;

		try {
			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				sprite.setIsSelected(false);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void resetOutlineColor(Color color)
	{
		Iterator iterator = null;
		Sprite sprite = null;

		try {
			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				sprite.setOutlineColor(color);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void triggerNewSnapshot()
	{
		hasSnapshot = false;
	}

	public Image snapshot(int sceneWidth, int sceneHeight)
	{
		int counter = 0;
		int currentX = 0;
		int currentY = 0;
		double currentZoomLevel = 0.0;

		Image result = null;
		Graphics2D render2D = null;
		Iterator iterator = null;
		Sprite sprite = null;
		Planet planet = null;

		try {
			if(hasSnapshot) {
				result = (Image)renderBuffer;
				return result;
			}

			// setup rendering buffer

			if((renderBuffer == null) || (renderBuffer.getGraphics() == null)) {
				renderBuffer = (BufferedImage)gameEngine.createImage(width,
						height);
				counter = 0;
				while((counter < 8) &&
						((renderBuffer == null) ||
						 (renderBuffer.getGraphics() == null))) {
					counter++;
					renderBuffer = (BufferedImage)gameEngine.createImage(width,
							height);
				}
				if((renderBuffer == null) ||
						(renderBuffer.getGraphics() == null)) {
					throw new Exception("failed to build rendering buffer");
				}
			}

			// test rendering buffer

			if((renderBuffer == null) || (renderBuffer.getGraphics() == null)) {
				throw new Exception("unable to perform snapshot render");
			}

			// setup the scene

			currentX = camera.getCameraX();
			currentY = camera.getCameraY();
			currentZoomLevel = zoomLevel;
			zoomLevel = (((((double)width / (double)sceneWidth) * 100.0) +
						(((double)height / (double)sceneHeight) * 100.0)) /
					2.0);
			camera.setCameraCoords(0, 0);

			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				planet = game.getPlanet(sprite.getName());
				if(planet == null) {
					throw new Exception("failed to locate planet '" +
							sprite.getName() + "' in index");
				}

				updatePlanetZoom(sprite, planet);

				sprite.applyCamera(camera.getCameraX(), camera.getCameraY(),
						camera.getWindowWidth(), camera.getWindowHeight());
			}

			// perform scene render to buffer

			render2D = (Graphics2D)renderBuffer.getGraphics();
			render2D.setColor(gameEngine.getBackground());
			render2D.fillRect(0, 0, width, height);
			render2D.setColor(gameEngine.getForeground());
			this.render2D(render2D);

			// reset the scene back to baseline

			zoomLevel = currentZoomLevel;
			camera.setCameraCoords(currentX, currentY);

			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				planet = game.getPlanet(sprite.getName());
				if(planet == null) {
					throw new Exception("failed to locate planet '" +
							sprite.getName() + "' in index");
				}

				updatePlanetZoom(sprite, planet);

				sprite.applyCamera(camera.getCameraX(), camera.getCameraY(),
						camera.getWindowWidth(), camera.getWindowHeight());
			}

			// obtain result

			result = (Image)renderBuffer;

			hasSnapshot = true;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void render2D(Graphics2D render)
	{
		double localWidth = 0.0;
		double localHeight = 0.0;

		Iterator iterator = null;
		Sprite sprite = null;

		try {
			offsetX = (0 - camera.getCameraX());
			offsetY = (0 - camera.getCameraY());

			// render background

			if(background != null) {
				localWidth = ((double)backgroundWidth * (zoomLevel / 100.0));
				localHeight = ((double)backgroundHeight * (zoomLevel / 100.0));
				render.drawImage(background, offsetX, offsetY, (int)localWidth,
						(int)localHeight, gameEngine);
			}

			// render sprite set

			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}
				if(sprite.isVisible()) {
					sprite.render2D(render);
				}
			}

			if(debugMode) {
				render.setColor(Color.blue);
				render.fillOval(((spriteCenterX - 4) + offsetX),
						((spriteCenterY - 4) + offsetY), 8, 8);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

