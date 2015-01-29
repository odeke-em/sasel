# SASEL

SASEL stand for `Sys Admin Secured Encrypted Ledger`.

It is an implementation of a server administrator's book ie it supports documentating

information about various servers for example:

+ Each server supports multiple applications, and even multiple users.

+ Each user(login account) on a server has a username, password and role eg

   	`user: Foo, password: Bar, role: Chip runner`


SASEL encrypts this information and then stores it on the cloud.

Any time you need to modify your data, it downloads the encrypted data dump

and then decrypts it with your keys.

To perform the encryption you'll need an encryption key and an initialization vector. 


Project Inception:
---

The inception of this project was the necessity to

manage an overwhelming amount of servers by the system administrator

at the Department of Electrical and Computer Engineering at the University of Alberta.

The sys admin felt that it would make life easier for sys-admins to access their server books

from the comfort of any place with an internet connection, but still 

maintain a sense of security, using their mobile device.


SetUp
-----

You'll need to create your project-configuration from https://code.google.com

+ Get access to your API console.

+ Visit the Google APIS console:

+ Create a project eg testAPP. 
  
+ Enable the Drive SDK and Drive API for use with your newly created project. 

+ To set up your Android project for authorization with the above APIs, 
    	
  you will need to provide info from your keytool signing certificate. 
    	
+ Follow the startup demo and instructions at: https://developers.google.com/drive/quickstart-android

+ Make sure that you have downloaded the jar file json-simple-\*.jar. 

  * You can get the latest version at http://code.google.com/p/json-simple/
  
 Note:
 
    You will have to include the json-simple-\*.jar file in your build-path. 
    
    If using Eclipse:

	`Properties -> Java Build Path -> Add External JARs -> Path to json-simple\*.jar`

+ Clone this project from: https://github.com/odeke-em/sasel

+ After your project is setup, make sure that you have added

 the Google Play Services jar as well as the Google Drive Api SDK by clicking:
 
   `Google Plugin icon -> Add Google APIs -> <Name of API>`



Miscellaneous Information:
====

+ In our case we'll be using Google's Drive as our cloud service.

+ Authentication of accounts via Google Drive is with OAuth2.0.

+ For starters we've designed it for use with the Nexus 7

+ Currently our cipher is based off encryption scheme:  AES/CBC/PKCS5Padding

  16 byte/character encryption key, 16 byte initialization vector
