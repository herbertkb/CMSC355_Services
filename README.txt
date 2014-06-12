README - Calling Executable Programs
///////////////////////////////////////////////////////////////////////////////

Version:	0.2
Author:		Keith Herbert
Date:		26 May 2014

Description:
Translate is an interactive console prompt that takes a word from the user and
returns its translation. It requires a translation file as a command-line 
argument and may have an audit file specified to log its output.

Dictionary is a program used by Translate to process the translation file and 
lookup the translation for each word supplied by the user. 

A separate file containing the words to look up and their translations must be 
provided. This file must have each word and its translation together on a single
line, separated by a tab. 

///////////////////////////////////////////////////////////////////////////////

Usage

Translate.jar and Dictionary.jar must be in same directory. 

> java -jar Translate.jar wordlist.txt
Enter a word to translate or !!! to quit: boy
мальчик
Enter a word to translate or !!! to quit: girl
девушка
Enter a word to translate or !!! to quit: fnord
FNORD not found in dictionary.
Enter a word to translate or !!! to quit: !!!

>
>java -jar Dictionary.jar boy wordlist.txt
мальчик

>java -jar Dictionary.jar fnord wordlist.txt
FNORD not found in dictionary.

>

///////////////////////////////////////////////////////////////////////////////

Compilation Instructions

From CEP directory containing Translate and Dictionary directories, each 
containing the java source files and the jar manifest file:

CEP>javac Translate\Translate.java
CEP>javac Dictionary\Dictionary.java
CEP>jar vmcf Translate\MANIFEST.MF Translate.jar Translate\Translate.class
CEP>jar vmcf Dictionary\MANIFEST.MF Dictionary.jar Dictionary\Dictionary.class

///////////////////////////////////////////////////////////////////////////////