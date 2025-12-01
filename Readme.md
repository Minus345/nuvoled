# Nuvoled Presenter

Nuvoled Application in Java

It captures your screen (or an NDI stream) and sends the rgb values to your
P4/P5 [LED panels / Video Wall](https://www.nuvoled.de/).
This is a command line application in java. This exists, because the first
party [configuration software](https://www.nuvoled.de/download/) only works on windows.

Also supports sending video over the network using the Newtek NDI® SDK.
For more information about NDI®, see:  
http://NDI.NewTek.com/

- **[Java 24.0.2 required](https://www.oracle.com/de/java/technologies/downloads/)**
    - updated java version, because of incompatibility with wayland on ubuntu
- Tested on:
    - Windows
    - Raspberry PI
        - currantly not working with wayland -> you have to change to x11 via the `raaspi-config`
    - Ubuntu   
      See `OS specific settings` further below

***

## Usage

### 1. Create Config file:

`java -jar nuvolde.jar create [<path where you want your default config file>]`

### 2. Configure your LED Wall:

`java -jar nuvoled.jar config <path to config file>`

P5 panels can only be configured vertically (use rotation if needed)

### Or load your panel-config file

`java -jar nuvoled.jar load <path to config file> <path to panel-config file>`

### 3. Normal Sender:

`java -jar nuvoled.jar start <path to config file>`

Java Parameter to force IPv4:  
`-Djava.net.preferIPv4Stack=true`

***

## Known Issues

### Rotation

If you use **rotation**:

1. configure your panels resolution in _Nuvoled Home_ **AND** _Nuvoled Presenter_ as if they were not rotated in reality
2. then configure your rotation start parameter (_-r_)
3. if you use a NDI Source: Configure the resolution with the rotation -> like in reality

| rotation degree | P4 normal | P4 Ndi | P5 normal | P5 Ndi |
|-----------------|-----------|--------|-----------|--------|
| 90              | ✅         | ❓      | ✅         | ✅      |
| 180             | ❌         | ❌      | ❌         | ❌      |
| 270             | ✅         | ❓      | ✅         | ✅      |

### RGB565

* Normal ✅
* NDI ✅

### Brightness

* Normal ✅
* NDI ❌

### FPS Display

* Normal ✅
* NDI ✅

***

## Settings Documentation

| Name            | Datatype       | Description                                                                                                             | Default value |
|-----------------|----------------|-------------------------------------------------------------------------------------------------------------------------|---------------|
| PanelVersion    | "P4"/"P5"      | wich panel do you use [P4 or P5](https://www.nuvoled.de/kaufen/)                                                        | -             |
| PanelCountX     | int            | number of panels in horizontal direction                                                                                | 1             |
| PanelCountY     | int            | number of panels in vertical direction                                                                                  | 1             |
| brightness      | [0 - ...]      | brightness multiplier. 1 is normal                                                                                      | 0.6           |
| rgb565          | boolean        | enables rgb565 mode: less colour accurate, more efficient -> you can have more panels with higher framerate connected   | false         |
| rotation        | 0, 90, 270     | rotates the image 90 or 270 degree -> see _Rotation Chapter_                                                            | 0             |
| sleep           | int            | how many milliseconds the programm should wait before a new frame is sent. Can improve picture quality on linux systems | 0             |         |
| offSet          | float          | (currently not in use)                                                                                                  | 0.0           |
| showFps         | boolean        | shows the fps that are send out, in the terminal                                                                        | false         |
| timeout         | int            | how many milliseconds the programm should wait in the config CLI to listen for panels                                   | 1000          |         |
| mode            | "screen"/"ndi" | "ndi" enables nid mode                                                                                                  | screen        |
| artnetEnabled   | boolean        | enables [ArtNet](https://art-net.org.uk/), to control the brightness with one channel                                   | false         |
| artnetDebug     | boolean        | enables debug information for ArtNet                                                                                    | false         |
| artnetSubnet    | [1 - 16]       | sets Subnet                                                                                                             | 0             |
| artnetUniversum | [1 - 16]       | sets Univers                                                                                                            | 0             |
| artnetChannel   | [1 - 255]      | sets Channel                                                                                                            | 0             |
| screenNumber    | int            | when your machine has more than one screen, you can specify your screen, you want to share                              | 0             |
| PositionX       | int            | coordinates where to start the screen capture                                                                           | 0             |
| PositionY       | int            | coordinates where to start the screen capture                                                                           | 0             |

***

### TODO:

1. [x] Add config file
2. [x] Testing P4
3. [x] Test ArtNet
4. [x] add network interface configurations
5. [x] rewrite network interface (refactoring old code - remove Mac support)
6. [x] add initial configuration for panels
7. [x] save currant configured panels to file, so you can load the config for your panels
8. [ ] Update to java 15
9. [ ] fix raspberry wayland
10. [ ] command line arg phraser user error handling
11. [ ] ME: Testing Switch VLan with config software -> enable broadcast?
12. [ ] remove and refactor unnecessary features
13. [ ] add Testing
14. [ ] add 180 degree rotation
15. [ ] RGB565 fix array length (should be shorter)
16. [ ] make NID more configurable

***

## NDI Support

https://ndi.video/for-developers/ndi-sdk/download/

OBS Plugin:  
https://github.com/DistroAV/DistroAV

Java Lib:  
https://github.com/WalkerKnapp/devolay

NDI Tools:  
https://ndi.video/tools/

NDI Doc:  
https://docs.ndi.video/all/developing-with-ndi/sdk/frame-types  
https://docs.ndi.video/all/using-ndi/ndi-for-video/digital-video-basics

***

## OS specific settings

### Linux (Ubuntu)

you have to manually configure the network card, to be a local linke network (`169.254.255.255`)

you need to set the sleep  
`-s 60` to set the delay between two frames

### Windows

should work out of the box
***
    
