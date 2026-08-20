# Nuvoled Presenter

Nuvoled Application in Java

It captures your screen and sends the rgb values to your
P4/P5 [LED panels / Video Wall](https://www.nuvoled.de/).
This is a command line application in java. This exists, because the first
party [configuration software](https://www.nuvoled.de/download/) only works on windows.

- Java 21 required

## Known Issus:

- Wayland screencapture (robot) should work with java 21 https://bugs.openjdk.org/browse/JDK-8280982
- Wayland screencapture (robot) not working with Nvidia gpu

***

## Usage

### 1. Create Config file:

`java -jar nuvoled.jar create [<path where you want your default config file>]`

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

## Features

### Select Input Source

TODO

### Rotation

If you use **rotation**:

1. configure your panels resolution in _Nuvoled Home_ **AND** _Nuvoled Presenter_ as if they were not rotated in reality
2. then configure your rotation start parameter (_-r_)

| rotation degree | P4 normal | P5 normal | 
|-----------------|-----------|-----------|
| 90              | ✅         | ✅         |
| 180             | ❌         | ❌         | 
| 270             | ✅         | ✅         |

### RGB565

Currently broken

### Brightness

### FPS Display

if enabled displays the currant fps that are send out to your LED-Wall

### Select form multiple network cards

if multiple suitable network cards are found you can select wich one you want to use at startup


***

## Settings Documentation

| Name         | Datatype          | Description                                                                                                                                                                 | Default value |
|--------------|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| PanelVersion | "P4"/"P5"         | wich panel do you use [P4 or P5](https://www.nuvoled.de/kaufen/)                                                                                                            | -             |
| PanelCountX  | [1 - ...]         | number of panels in horizontal direction                                                                                                                                    | 1             |
| PanelCountY  | [1 - ...]         | number of panels in vertical direction                                                                                                                                      | 1             |
| brightness   | [0 - ...]         | brightness multiplier. 1 is normal                                                                                                                                          | 0.6           |
| rgb565       | boolean           | enables rgb565 mode: less colour accurate, more efficient -> you can have more panels with higher framerate connected                                                       | false         |
| mode         | "screen"/"camera" | switches content source:<br/> **screen**: screencapture (Parameters: screenNumber, PositionX, PositionY)<br/> **camera**: use a camera input as source  (Parameter: camera) | "screen"      |
| camera       | [0 - ...]         | select which camera to use                                                                                                                                                  | 0             |
| rotation     | 0, 90, 270        | rotates the image 90 or 270 degree -> see _Rotation Chapter_                                                                                                                | 0             |
| sleep        | [0 - ...]         | how many milliseconds the programm should wait before a new frame is sent. Can improve picture quality on linux systems                                                     | 0             |         |
| offSet       | double            | (currently not in use)                                                                                                                                                      | 0.0           |
| showFps      | boolean           | shows the fps that are send out, in the terminal                                                                                                                            | false         |
| timeout      | [0 - ...]         | how many milliseconds the programm should wait in the config CLI to listen for panels                                                                                       | 1000          |         |
| screenNumber | [0 - ...]         | when your machine has more than one screen, you can specify your screen, you want to share                                                                                  | 0             |
| PositionX    | [0 - ...]         | coordinates where to start the screen capture                                                                                                                               | 0             |
| PositionY    | [0 - ...]         | coordinates where to start the screen capture                                                                                                                               | 0             |

***

### TODO:

1. [ ] add Testing
2. [ ] add 180 degree rotation
3. [ ] RGB565 fix array length (should be shorter)

***

## OS specific settings

### Linux (Ubuntu)

you have to manually configure the network card, to be a local linke network (`169.254.255.255`)

you need to set the sleep  
`-s 60` to set the delay between two frames

### Windows

should work out of the box

## Used Libs:

https://github.com/sarxos/webcam-capture
***
    
