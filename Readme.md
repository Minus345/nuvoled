# Nuvoled #

Nuvoled Application in Java

- **Java 21.x.x required**
- Tested on Windows / (Mac) / Linux (Jetson)

***

## Start Parameters ##

`java -jar nuvoled.jar -h` for help

Chose Panel: `-p`:

*     "P4"  -> NuvoLED P4
*     "P5"  -> NuvoLED P5    (Outdoor)

### Known Issues

#### Rotation

| rotation degree | P4 normal               | P4 Ndi | P5 normal | P5 Ndi |
|-----------------|-------------------------|--------|-----------|--------|
| 90              | ✅  (old implementation) | ❓      | ✅         | ✅      |
| 180             | ❓                       | ❌      | ❌         | ❌      |
| 270             | ✅  (old implementation) | ❓      | ✅         | ✅      |

#### RGB565
* Normal ❌ 
* NDI ❌
-> wrong colour

#### Brightness
* Normal ✅
* NDI ❌

#### FPS Display
* Normal ✅
* NDI ✅

### Other Parameters ###

`usage: java -jar nuvoled.jar`  
`-a,--artnet <<ip>>                  enables artnet`   
`-ac,--artnetChannel << 0 - 513 >>   artnet channel`  
`-ad,--artnetDebug                   enables artnet debug`  
`-as,--artnetSubnet << 0 - 16 >>     artnet subnet`  
`-au,--artnetUniverse << 0 - 16 >>   artnet universe`  
`-b,--bind                           bind to interface 169.254`  
`-br,--brightness <0.6>              brightness value with 0.x -1.x`  
`-fps,--fps                          prints out fps`  
`-h,--help                           Help Message`  
`-ndi,--ndi                          enables ndi mode`  
`-o,--offset <0>                     offset (Contrast)`  
`-p,--Panel <arg>                    choose Panel`  
`-px,--panelsx <1>                   Number of Panels horizontal`  
`-py,--panelsy <1>                   Number of Panels vertical`  
`-r,--rotation <0>                   rotation degree 0/90/180/270`  
`-rgb,--rgb565                       sets the mode to rgb565`  
`-s,--sleep <0>                      sleep ime in ms`  
`-sn,--screennr <0>                  number of screen`  
`-sx,--startx <0>                    Pixel start horizontal`  
`-sy,--starty <0>                    Pixal start vertical`  


### NDI Supprt ###

**NDI SDK has to be installed**  
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

### Mac ###  

use  
`-b` to binde the interface  
`-s 60` to set the delay between two frames

### Linux ###  

you need to set the sleep  
`-s 60` to set the delay between two frames

### Windows ###

should work out of the box
***
    
