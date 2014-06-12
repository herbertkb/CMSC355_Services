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

First start the dictionary server. You must specify a path to the translation
file and the TCP/IP port on which to listen for incoming connections. 
java -jar Dictionary.jar <translation file> <port>

Optionally, you may include a path to a logfile to record the server's activity.
java -jar Dictionary.jar <translation file> <port> <log file>

Then start the translate client. You must specify the IP address and port of the
Dictionary server. Optionally, you may include a path to a log file to record
your interaction with the server. 
java -jar Translate.jar <Dictionary Server IP> <port> 
java -jar Translate.jar <Dictionary Server IP> <port> <log file>

Now that Translate is running, enter a word at the prompt to get a translation
or "!!!" to quit.

Example
>java -jar Dictionary.jar wordlist.txt 8000 DictLog.txt
Thu Jun 12 18:32:58 EDT 2014 Processing wordlist.txt.
Thu Jun 12 18:32:58 EDT 2014 wordlist.txt processed.
Thu Jun 12 18:32:58 EDT 2014 Dictionary server listening on port 8000.

On a separate console:
>java -jar Translate.jar localhost 8000 TransLog.txt
Enter a word to translate or !!! to quit: boy
мальчик
Enter a word to translate or !!! to quit: girl
девушка
Enter a word to translate or !!! to quit: arm
рука
Enter a word to translate or !!! to quit: hand
рука
Enter a word to translate or !!! to quit: foo
bar
Enter a word to translate or !!! to quit: fnord
No translation for FNORD
Enter a word to translate or !!! to quit: !!!

This will show on the Dictionary console as:
Thu Jun 12 18:34:43 EDT 2014 Client 1 (127.0.0.1) connected.
Thu Jun 12 18:35:21 EDT 2014 Client 1 query: boy
Thu Jun 12 18:35:21 EDT 2014 Client 1 translation: мальчик
Thu Jun 12 18:35:23 EDT 2014 Client 1 query: girl
Thu Jun 12 18:35:23 EDT 2014 Client 1 translation: девушка
Thu Jun 12 18:35:25 EDT 2014 Client 1 query: arm
Thu Jun 12 18:35:25 EDT 2014 Client 1 translation: рука
Thu Jun 12 18:35:27 EDT 2014 Client 1 query: hand
Thu Jun 12 18:35:27 EDT 2014 Client 1 translation: рука
Thu Jun 12 18:35:34 EDT 2014 Client 1 query: foo
Thu Jun 12 18:35:34 EDT 2014 Client 1 translation: bar
Thu Jun 12 18:35:39 EDT 2014 Client 1 query: fnord
Thu Jun 12 18:35:39 EDT 2014 Client 1: No translation for FNORD
Thu Jun 12 18:35:57 EDT 2014 Client 1 query: null
Thu Jun 12 18:35:57 EDT 2014 Client 1 disconnected.

Dictionary also supports multiple clients connected at once:
Thu Jun 12 18:41:54 EDT 2014 Processing wordlist.txt.
Thu Jun 12 18:41:54 EDT 2014 wordlist.txt processed.
Thu Jun 12 18:41:54 EDT 2014 Dictionary server listening on port 8000.
Thu Jun 12 18:42:10 EDT 2014 Client 1 (127.0.0.1) connected.
Thu Jun 12 18:42:37 EDT 2014 Client 1 query: car
Thu Jun 12 18:42:37 EDT 2014 Client 1 translation: машина
Thu Jun 12 18:43:09 EDT 2014 Client 2 (127.0.0.1) connected.
Thu Jun 12 18:43:12 EDT 2014 Client 2 query: tea
Thu Jun 12 18:43:12 EDT 2014 Client 2 translation: чай
Thu Jun 12 18:43:35 EDT 2014 Client 1 query: dog
Thu Jun 12 18:43:35 EDT 2014 Client 1 translation: собака
Thu Jun 12 18:43:40 EDT 2014 Client 2 query: book
Thu Jun 12 18:43:40 EDT 2014 Client 2 translation: книга
Thu Jun 12 18:43:44 EDT 2014 Client 2 query: null
Thu Jun 12 18:43:44 EDT 2014 Client 2 disconnected.
Thu Jun 12 18:43:48 EDT 2014 Client 1 query: null
Thu Jun 12 18:43:48 EDT 2014 Client 1 disconnected.




///////////////////////////////////////////////////////////////////////////////

Compilation Instructions

From a directory containing Translate and Dictionary directories, each 
containing the java source files and the jar manifest file:

>javac Translate\Translate.java
>javac Dictionary\Dictionary.java
>jar vmcf Translate\MANIFEST.MF Translate.jar Translate\*.class
>jar vmcf Dictionary\MANIFEST.MF Dictionary.jar Dictionary\*.class

///////////////////////////////////////////////////////////////////////////////