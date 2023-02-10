/*
 * AudioPlayerTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test the Texture system.
 *
 * Written by Josh English.
 */

// define package space

package lyra.tests;

// import external packages

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;

// import internal packages

import lyra.*;
import lyra.drivers.KeyboardDriver;
import lyra.drivers.MouseDriver;
import lyra.audio.AudioPlayer;

// define class

public class AudioPlayerTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = AudioPlayerTest.class.getName();

	// define private class variables

	AudioPlayer playerWav = null;
	AudioPlayer playerMP3 = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		setColor(Color.white);
		drawString("ENGINE render (" + windowWidth + " x " +
				windowHeight + "), framerate: " + frameRate.get(),
				0, 12);
		drawString("ENGINE mouse (" + mouseX + ", " + mouseY + 
				"): button: " + mouseButton + ", clicks: " +
				mouseClicks, 0, 28);
		drawString("ENGINE keyboard (" + lastBinaryKeyPress +
				"), type: " + keyboardEventType + ", '" +
				keyPressBuffer + "'", 0, 44);
		drawString("Select 'a/z' to play, 's/x' to pause, 'd/c' to resume, " +
				"'f/v' to stop, and 'g/b' to loop", 0, 60);
		drawString("Select up/down for volume, left/right for balance", 0, 76);
		drawString("AudioPlayer(" + playerWav.getName() + ") playing: " +
				playerWav.isPlaying() + ", play millis remaining: " +
				playerWav.getPlayTimeRemaining(), 0, 92);
		drawString("AudioPlayer(" + playerMP3.getName() + ") playing: " +
				playerMP3.isPlaying() + ", play millis remaining: " +
				playerMP3.getPlayTimeRemaining(), 0, 108);
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		// do nothing
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
			if(event.getKeyChar() == 'a') {
				playerWav.play();
			}
			else if(event.getKeyChar() == 's') {
				playerWav.pause();
			}
			else if(event.getKeyChar() == 'd') {
				playerWav.resume();
			}
			else if(event.getKeyChar() == 'f') {
				playerWav.stop();
			}
			else if(event.getKeyChar() == 'g') {
				playerWav.loop();
			}
			else if(event.getKeyChar() == 'z') {
				playerMP3.play();
			}
			else if(event.getKeyChar() == 'x') {
				playerMP3.pause();
			}
			else if(event.getKeyChar() == 'c') {
				playerMP3.resume();
			}
			else if(event.getKeyChar() == 'v') {
				playerMP3.stop();
			}
			else if(event.getKeyChar() == 'b') {
				playerMP3.loop();
			}
			if(lastBinaryKeyPress == 38) { // up
				playerWav.modifyVolume(AudioPlayer.VOLUME_UP);
				playerMP3.modifyVolume(AudioPlayer.VOLUME_UP);
			}
			else if(lastBinaryKeyPress == 40) { // down
				playerWav.modifyVolume(AudioPlayer.VOLUME_DOWN);
				playerMP3.modifyVolume(AudioPlayer.VOLUME_DOWN);
			}
			else if(lastBinaryKeyPress == 37) { // left
				playerWav.modifyBalance(AudioPlayer.BALANCE_LEFT);
				playerMP3.modifyBalance(AudioPlayer.BALANCE_LEFT);
			}
			else  if(lastBinaryKeyPress == 39) { // right
				playerWav.modifyBalance(AudioPlayer.BALANCE_RIGHT);
				playerMP3.modifyBalance(AudioPlayer.BALANCE_RIGHT);
			}
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		assetCache.loadHttpDriver("192.168.0.132", 8080);

		playerWav = (AudioPlayer)assetCache.getAsset(AssetCache.TYPE_AUDIO,
				"assets/config/audio/tracks/track01.wav.config");
		playerMP3 = (AudioPlayer)assetCache.getAsset(AssetCache.TYPE_AUDIO,
				"assets/config/audio/tracks/track01.mp3.config");

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		// do nothing
	}
}

