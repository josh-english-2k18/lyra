# lyra
[<img src="https://github.com/josh-english-2k18/lyra/blob/main/logo.png">](logo)

lyra - a 2D game engine in Java

## Overview

Lyra is a simple 2D game engine written in Java and intended for use as an Applet.

## Features

### 2D Animation System

* Frame-based animation sequences.
* Camera-aware animation rendering.
* Animation movement within the scene.

### Asset Caching System

* Images
* Textures
* Configuration Files
* Binary Streams
* Java Audio & MP3 Streams

### Tiling System

* Isomorphic tiling.
* Isometric tiling.
* Non-isomorphic tiling.

### 2D Sprite System

* 16-direction sprite representation and movement.
* Automated direction-change blending.
* User selectable.
* Highlightable.

### Scene Management System

* Automatically tracks keyboard and mouse events
* Maintains camera state
* 2D rendering pipeline: tiles, sprites, animations, and physics-objects.

### Sprite Physics Systems
Automatically calculates sprite properties, including:

* movement
* direction
* speed and angular velocity

Automtically applies friction, thrust, gravity and bounce.
Enables temporary physics state suspension in a "turbo" mode.

### Drivers

* Networking (TCP/IP, UDP, HTTP)
* User Input (keyboard, mouse)

### Plugin System

* Plugin API
* MP3 Codec

### Trigger System

* Events
* Actions
* Event-Consumer
* Event-Management System

### Utilities

* Animation Sequence Loader
* Cached Input Streaming
* Configuration Files
* Assets

### GUI Widgets

* Auto-Scroller
* Button
* Check Box
* Cornered Box
* Drop-Down Box
* Graphic Equalizer
* File Browser
* Hyper Link
* Numeric Box
* Progress Bar
* Scroll Bar
* Text Box
* Widget API

## Tests

* Animation System
* Audio Player
* GUI System
* Scene System
* Side Scroller Camera
* Sprite Physics Camera
* Sprite Physics
* Sprite System
* Textures
* Triggers

## Games

* lyra - an isometric tiled buggy racing game
* galaxik - a game of galactic conquest
* spincycle - tetris with a twist

## Build
Uses the Apache Ant build system. Project can be built with `ant`, e.g.
```
$ ant -f build.xml clean build
Buildfile: D:\files\projects\git\lyra\build.xml

clean:

clean:

compile:

jar:
      [jar] Building jar: D:\files\projects\git\lyra\system\lyra.jar
     [echo] Lyra .jar built!

sign:
     [exec] jar signed.
     [exec]
     [exec] Warning:
     [exec] The signer's certificate is self-signed.
     [exec] The RSA signing key has a keysize of 1024 which is considered a security risk. This key size will be disabled in a future update.
     [exec] POSIX file permission and/or symlink attributes detected. These attributes are ignored when signing and are not protected by the signature.
     [exec] Certificate stored in file <LyraTheGameCert.cer>
     [exec]
     [exec] Warning:
     [exec] The certificate uses the SHA1withRSA signature algorithm which is considered a security risk.
     [exec] The certificate uses a 1024-bit RSA key which is considered a security risk. This key size will be disabled in a future update.
     [exec] The JKS keystore uses a proprietary format. It is recommended to migrate to PKCS12 which is an industry standard format using "keytool -importkeystore -sr
ckeystore ../assets/keys/lyraKeys.private -destkeystore ../assets/keys/lyraKeys.private -deststoretype pkcs12".
     [exec] Certificate was added to keystore
     [exec]
     [exec] Warning:
     [exec] The input uses the SHA1withRSA signature algorithm which is considered a security risk.
     [exec] The input uses a 1024-bit RSA key which is considered a security risk. This key size will be disabled in a future update.
     [echo] Lyra .jar signed!

build:
     [echo] Lyra Game Engine built!

BUILD SUCCESSFUL
Total time: 5 seconds
```

### Build Files

* lyra - `build.xml`
* galaxik - `galaxik.xml`
* spincycle - `spincycle.xml`
