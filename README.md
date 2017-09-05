# K-Mirror

## Project Description
This project aims to create a connected mirror which will use hand movement to control the IHM and a modular architecture to easily add new features.
I am currently working on the IHM and hand gesture recognition.

## Code organization
The code is splitted in two parts. The Hand-Recodgnition folder holds the hand gesture recognition module. It can be used on its own. It sends json data (POST) to an API when gesture event have been found. Refer to the wiki for proper documentation. You can use this module to control your own software, client, web page with your hand. 

## Proof of concept
The hand recognition module is working and postinv json event to the web-server. The web-server holds a single web pages which is thus controlable via hand gesture.
You can use this code to write your own ! 
