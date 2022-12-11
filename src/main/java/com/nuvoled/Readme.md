#NuvoLED Packet Sending

##Config
<-- 36 36 130 0 (refrech)  
<-- 36 36 160 0 0 0 (server init)
<-- 36 36 160 23 49 74 (aktiviert)  
<-- 36 36 120 2 32 8 8 1 23 49 74 8 8 0 0 (Config)  
<-- 36 36 155 0 (save config)

##Reset:  
36 36 160 0 0 0  
36 36 130 0  

##Frame:

36 36 20 2 **Frame: RGB888** :10 0 **MCounter:** 0 0 35 **Größe der RGB /32:** 45 **Pixel RGB:** 255 0 255 ... 255 0 255

##FrameFinisch

36 36 100 _curframe_
