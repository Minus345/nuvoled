# Nuvoled #
Nuvoled Application in Java  
 - **Java 19.0.1 required**
 - Tested on Windows / Mac / Linux (Jetson)
***
## Start Parameters ##
### New Version (from 1.6) ###
`java -jar nuvoled.jar -h` for help

Chose Panel: `-p`: 
*     "P4"  -> NuvoLED P4
*     "P5"  -> NuvoLED P5    (Outdoor)

### Known Issues
* rotation in rgb565 mode
* dimming in rgb565 mode


### Other Parameters ###
`-a,--artnet <<ip>>                  enables artnet`  
`-ac,--artnetChannel << 0 - 513 >>   artnet channel`  
`-ad,--artnetDebug                   enables artnet debug`  
`-as,--artnetSubnet << 0 - 16 >>     artnet subnet`  
`-au,--artnetUniverse << 0 - 16 >>   artnet universe`  
`-b,--bind                           bind to interface 169.254`  
`-br,--brightness <0.6>              brightness value with 0.x -1.x`  
`-h,--help                           Help Message`  
`-l,--list                           list available webcams`  
`-o,--offset <0>                     offset (Contrast)`  
`-p,--Panel <arg>                    choose Panel`  
`-px,--panelsx <1>                   Number of Panels horizontal`  
`-py,--panelsy <1>                   Number of Panels vertical`  
`-r,--rotation <0>                   rotation degree 0/90/180/270`  
`-s,--sleep <0>                      sleep ime in ms`  
`-sn,--screennr <0>                  number of screen`  
`-sx,--startx <0>                    Pixel start horizontal`  
`-sy,--starty <0>                    Pixal start vertical`  
`-w,--webcam <<webcam name>>         use webcam as input`
`-rgb565<true/false>                 rgb565 mode`
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
    
