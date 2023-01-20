# Nuvoled #
Nuvoled Application in Java  
 - **Java 19.0.1 required**
 - Tested on Windows / Mac / Linux (Jetson)
***
## Start Parameters ##
### New Version (from 1.6) ###
`java -jar nuvoled.jar -h` for help

### Other Parameters ###
 `-b,--bind` bind to interface 169.254  
 `-br,--brightness <0.6>` brightness value with 0.x -1.x  
 `-h,--help` Help Message  
 `-px,--panelsx <1>` Number of Panels horizontal  
 `-py,--panelsy <1>` Number of Panels vertical  
 `-r,--rotation <0>` rotation degree 0/90/180/270  
 `-s,--sleep <15>` sleep ime in ms  
 `-sn,--screennr <0>` number of screen  
 `-sx,--startx <0>` Pixel start horizontal  
 `-sy,--starty <0>` Pixal start vertical  
### Mac ###  
use `-b` to binde the interface -s 60

### Linux ###  
you need the sleep

### Windows ###
you can set the sleep to 0

### Old Version (up to 1.5) ###   
Start:
`java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y] screen [ 90/180/270] [screen number] [x] [y] [colorMode] [bind to interface true/false] [brightness] [offset]`
***
    
