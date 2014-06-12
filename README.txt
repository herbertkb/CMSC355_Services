README - Sockets
///////////////////////////////////////////////////////////////////////////////

Version:	0.3
Author:		Keith Herbert
Date:		11 June 2014

Description:
Translate is an interactive console client application that takes a word from
the user, forwards the word to an external Dictionary server and returns the 
translation from the Dictionary server to the user. The IP address and port of 
the Dictionary server must be specified on start up.
If specified, it can log all input, output, and server communication to an external file.

Dictionary is a server application which accepts connections from the Translate
client. When given a query word, it looks up its translation and returns the 
translation to the client. The port Dictionary listens to and a list of words
and their translations must be provided on startup. 
If specified, it can log all input, output, and client communications to an 
external file. 

A separate file containing the words to look up and their translations must be 
provided. This file must have each word and its translation together on a single
line, separated by a tab. 


///////////////////////////////////////////////////////////////////////////////

Usage


///////////////////////////////////////////////////////////////////////////////

Compilation Instructions

From a directory containing Translate and Dictionary directories, each 
containing the java source files and the jar manifest file:

>javac Translate\Translate.java
>javac Dictionary\Dictionary.java
>jar vmcf Translate\MANIFEST.MF Translate.jar Translate\Translate.class
>jar vmcf Dictionary\MANIFEST.MF Dictionary.jar Dictionary\Dictionary.class

///////////////////////////////////////////////////////////////////////////////