# Nuvoled #

Nuvoled Application in Java

- **Java 19.0.1 required**
- Tested on Windows / (Mac) / Linux (Jetson)

New Version out v3.1 with rgb565 support

***

## Start Parameters ##

### New Version (from 1.6) ###

`java -jar nuvoled.jar -h` for help

Chose Panel: `-p`:

*     "P4"  -> NuvoLED P4
*     "P5"  -> NuvoLED P5    (Outdoor)

### Known Issues

* rotation in rgb565 mode
* rotation in ndi only 90,180
* rotation in p5 mode not working
* brightness in ndi mode
* no fps in ndi mode

### Tested:

#### Rotation

| rotation degree | P4 normal               | P4 Ndi | P5 normal | P5 Ndi |
|-----------------|-------------------------|--------|-----------|--------|
| 90              | ✅  (old implementation) | ❓      | ✅         | ✅      |
| 180             | ❓                       | ❌      | ❌         | ❌      |
| 270             | ✅  (old implementation) | ❓      | ✅         | ✅      |

#### RGB565
* Normal ✅
* NDI ✅

#### Brightness
* Normal ❓
* NDI ❓

#### FPS Display
* Normal ✅
* NDI ✅

### Other Parameters ###

#### Global ####

`-p,--Panel <arg>                    choose Panel`  
`-px,--panelsx <1>                   Number of Panels horizontal`  
`-py,--panelsy <1>                   Number of Panels vertical`  
`-br,--brightness <0.6>              brightness value with 0.x -1.x`  
`-o,--offset <0>                     offset (Contrast)`  
`-r,--rotation <0>                   rotation degree 0/90/180/270`  
`-rgb,--rgb565                       sets the mode to rgb565`  
`-s,--sleep <0>                      sleep ime in ms`  
`-sn,--screennr <0>                  number of screen`

#### ArtNet ####

Single Dimmer Channel for dimming the hole image:  
`-a,--artnet <<ip>>                  enables artnet`  
`-ac,--artnetChannel << 0 - 513 >>   artnet channel`  
`-ad,--artnetDebug                   enables artnet debug`  
`-as,--artnetSubnet << 0 - 16 >>     artnet subnet`  
`-au,--artnetUniverse << 0 - 16 >>   artnet universe`

#### Optional/Debugging

`-b,--bind                           bind to interface 169.254`  
`-fps,--fps                          prints out fps`  
`-sx,--startx <0>                    Pixel start horizontal`   
`-sy,--starty <0>                    Pixal start vertical`

### NDI Supprt ###

OBS Plugin:  
https://github.com/DistroAV/DistroAV

Java Lib:  
https://github.com/WalkerKnapp/devolay

NDI Tools:  
https://ndi.video/tools/

NDI Doc:  
https://docs.ndi.video/all/developing-with-ndi/sdk/frame-types  
https://docs.ndi.video/all/using-ndi/ndi-for-video/digital-video-basics

### Mac ###  

use  
`-b` to binde the interface  
`-s 60` to set the delay between two frames

### Linux ###  

you need to set the sleep  
`-s 60` to set the delay between two frames

### Windows ###

should work out of the box

### Old Version (up to 1.5) ###   

Start:
`java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y] screen [ 90/180/270] [screen number] [x] [y] [colorMode] [bind to interface true/false] [brightness] [offset]`
***
    
