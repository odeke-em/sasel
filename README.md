Server Manager
================================================================================

Server Manager is an implementation of a server administrator's book.
ie it supports documentation of information of various servers.
Each server supports multiple applications, and even 
multiple users. Each user(login account) on a server
has a username, password and role eg
  user: Foo, password: Bar, role: Chip runner

The book enables information about the servers to be entered, encrypted, then 
stored on a cloud. Any time you need to modify your data, the encrypted data-dump
is downloaded and decrypted with your keys.

For encryption you'll need an encryption key and an initialization vector. 

This inception of this project was the necessity to
manage an overwhelming amount of servers by Francois Brochu who
felt that there are possibly other server administrators that would
appreciate a tool to aid their work, from the comfort of any place with
an internet connection, but still maintaining a sense of security. Using a 
mobile device


SetUp
-----------------------------------------------------------------------------------
You'll need to create your project-configuration on
	code.google.com

Once given access to the API console:
+Visit the Google APIS console:
  ++Create a project eg testAPP. 
  ++Enable the Drive SDK and Drive API for use with your newly created project. 
    To set up your android project for authorization with the above APIs, 
    you will need to provide info from your keytool signing certificate. 
    Follow the startup demo and instructions at:
     https://developers.google.com/drive/quickstart-android

+Make sure that you have downloaded the jar file json-simple-\*.jar. 
 Get the latest version at:
  http://code.google.com/p/json-simple/
 You will have to include the json-simple-\*.jar file in your build-path: Click: 
  Properties -> Java Build Path -> Add External JARs -> Path to json-simple\*.jar

+Clone the latest project from here.

+After your project is setup, make sure that you have added, 
 the Google Play Services jar  and Google Drive Api SDK by clicking on the 
   Google Plugin icon -> Add Google APIs -> <Name of API>


Miscellaneous Information:
------------------------------------------------------------------------------------
In our case we'll be using Google's Drive as our cloud service.

Authentication of accounts via Google Drive is with OAuth2.0.
For starters we've designed it for use with the Nexus 7

Currently our cipher is based off encryption scheme:  AES/CBC/PKCS5Padding
16 byte/character encryption key, 16 byte initialization vector

And now you are set!
