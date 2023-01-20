# Nuvoled #
Nuvoled Application in Java  
 - **Java 190.1 require**
 - Tested on Windows / Mac / Linux (Jetson)
***
## Start Parameters ##
### Show Video ###   

### Command Line Options

Nuvoled Presenter
usage: java -jar nuvoled.jar
-b,--bind                bind to interface 169.254
-br,--brightness <0.6>   brightness value with 0.x -1.x
-h,--help                Help Message
-px,--panelsx <1>        Number of Panels horizontal
-py,--panelsy <1>        Number of Panels vertical
-r,--rotation <0>        rotation degree 0/90/180/270
-s,--sleep <15>          sleep ime in ms
-sn,--screennr <0>       number of screen
-sx,--startx <0>         Pixel start horizontal
-sy,--starty <0>         Pixal start vertical

## Example

### Windows/Linux

2*1   
java -jar -s 0 -px 2 -py 1   

## MAC

2*1
java -jar -b -px2 -py 1