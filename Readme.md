# Nuvoled #
Nuvoled Application in Java  
 - **Java 17.0.1 require**
 - Teste on Windows / Mac / Linux (Jetson)
***
## Start Parameters ##
### Test Connection ###
`java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y]`  
[ip] = broadcast ip
### Send Color ###
`java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y] color [R] [G] [B] `  
[R] [G] [B] = 0 - 255
### Send Picture ###
`java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y] picture`
### Show Screen ###
`java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y] screen [0/90/180/270] [screen] [x] [y] [colorMode] [bind to interfac true/false]`  
[x] [y] = pixel
[true / false] = rotation  
[screen] = monitor starting from 0  
[colorMode] = 0 RGB 20 JPG
_only sending if picture change_
### Show Video ###
`java -jar nuvoled.jar start [ip] [Pannal x] [Pannel y] video [0/90/180/270] [screen] [x] [y] [colorMode] [bind to interfac true/false]`  
[x] [y] = pixel  
bind to interface -> mac true, others false
[true / false] = rotation  
[screen] = monitor starting from 0
[colorMode] = 0 RGB 20 JPG
_more fps_
***
    