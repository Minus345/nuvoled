# Nuvoled Presenter#

Nuvoled Application in Java

Also supports sending video over the network using the Newtek NDI® SDK.
For more information about NDI®, see:  
http://NDI.NewTek.com/

- **Java 21.x.x required**
- Tested on:
  - Windows
  - Raspberry PI
  - ~~(Mac not supported)~~  
See `OS specific settings` further below
***

## Usage 

`java -jar nuvoled.jar <path to config file>`  
`java -jar nuvolde.jar create <path where you want your default config file>`


***

## Known Issues

### Rotation

If you use **rotation**:
* configure your panels resolution in _Nuvoled Home_ **AND** _Nuvoled Presenter_ as if they were not rotated in reality
* then configure your rotation start parameter (_-r_)
* if you use a NDI Source: Configure the resolution with the rotation -> like in reality

| rotation degree | P4 normal               | P4 Ndi | P5 normal | P5 Ndi |
|-----------------|-------------------------|--------|-----------|--------|
| 90              | ❓  (old implementation) | ❓      | ✅         | ✅      |
| 180             | ❓                       | ❌      | ❌         | ❌      |
| 270             | ❓  (old implementation) | ❓      | ✅         | ✅      |

#### RGB565
* Normal ✅ 
* NDI ✅

#### Brightness
* Normal ✅
* NDI ❌

#### FPS Display
* Normal ✅
* NDI ✅

### TODO:
    * Add config file ✅
    * Testing P4
    * RGB565 fix array length (should be shorter)
    * make NID more configurable

***

## Settings Documentation 

    TODO

***

## NDI Supprt 

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

### Mac  

use  
`-b` to binde the interface  
`-s 60` to set the delay between two frames

### Linux 

you need to set the sleep  
`-s 60` to set the delay between two frames

### Windows

should work out of the box
***
    
