/******************************************************************************
	Translate.java
	
	Interactive console to prompt user for an English word and return its 
	translation. Depends on external program Dictionary.jar to perform 
	translation with the user specified translation file. 
	
*******************************************************************************
	To run:
		java Translate *translationFile*
	
		Enter a word to translate or CTRL-C to quit: boy
		мальчик
		Enter a word to translate or CTRL-C to quit: fnord
		FNORD not found in dictionary. 
		
	To run with audit file:
		java Translate *translationFilename* *auditFilename*
	
*******************************************************************************
	Maintenance Log
	Fix #	Date		Name	Description
	0001	20 May 2014	Keith	First draft. Many back and forth revisions to 
								fix issues with Cyrillic output to Windows
								console. 
	0002	21 May 2014	Keith	More fiddling to fix Cyrillic output to 
								Windows console. Verified that output is 
								correct with temporary output file.
	0003	25 May 2014	Keith	Added audit file feature, close file handles
								and variable name changes for clarity. 
	0004	26 May 2014	Keith	Switch from shell script to client-server model.
								Translate acts as client, setting up Dictionary
								which acts as server.
	0005	31 May 2014	Keith	Finally finish fix 0004. What should have been
								a minor change was complicated by a missing "\n"
								after the word being printed to Dictionary.jar's
								input. 

******************************************************************************/

import java.util.Scanner;			// take in user iput
import java.io.BufferedReader;		// handle input from Dictionary.jar
import java.io.InputStreamReader;	// handle input from Dictionary.jar
import java.io.PrintStream;			// force printing to console in Unicode
import java.io.PrintWriter;			// print to external file & Dictionary.jar
import java.io.OutputStreamWriter;	// print to Dictionary.jar process
import java.io.BufferedOutputStream;// print to Dictionary.jar process
import java.io.IOException;			// lots of IO, lots of possible exceptions


public class Translate {
	public static void main(String... args) 
		throws java.io.IOException, java.io.UnsupportedEncodingException {
		
		//	Parse commandline arguments.///////////////////////////////////////
		//	args[0] must be the path to the translation file.
		//	args[1] could be a filename for an audit file (optional).
		//		If specified, store the filename and raise a flag to be checked
		//		throughout the rest of the program whenever output is needed.
		///////////////////////////////////////////////////////////////////////
		String wordlist = args[0];
		
		String auditfile = null; 
		boolean AUDIT = false;
		
		if (args.length == 2) {
			auditfile = args[1];
			AUDIT = true;
		}
		
		// Hard strings. //////////////////////////////////////////////////////
		// escape is entered by user to end the program
		// prompt is presented to user on every pass of main loop.
		// command is used to call external program Dictionary.jar/////////////
		String escape = "!!!";
		String prompt = "Enter a word to translate or " + escape + " to quit: ";
		String command = "java -jar Dictionary.jar ";
		
		
		// Create objects for input and output to user console and to audit file. 
		// Unicode must be forced for all output to console.///////////////////
		Scanner userIn = new Scanner(System.in);
		PrintStream userOut = new PrintStream(System.out, true, "UTF-8");
		PrintWriter auditOut = null;
		if (AUDIT) auditOut = new PrintWriter(auditfile);
		
		// Run external program Dictionary.jar with command string and the
		// translation file specified on invocation.
		// Then, create a Writer object to give it words, a Reader object to
		// receive translations, and an Error reader just because.
		
		Process proc = Runtime.getRuntime().exec( command + wordlist);
		BufferedReader dictReader = new BufferedReader(
			new InputStreamReader( proc.getInputStream()));
		PrintWriter dictLookup = new PrintWriter( new OutputStreamWriter ( 
			new BufferedOutputStream( proc.getOutputStream() ))); 

		
		
		//	Main loop of program. /////////////////////////////////////////////
		//	On each pass, take English word from the user, pass it to 
		//	Dictionary.jar, return its translation to user.	For every output, 
		//	print to both console and the audit file, if specified.
		///////////////////////////////////////////////////////////////////////
		while( true ) {
			
			// Prompt user for input.//////////////////////////////////////////
			System.out.print( prompt );
			if (AUDIT) { auditOut.print( prompt ); }
			
			// Store input and add to log file if specified. //////////////////
			String word = userIn.next();
			if (AUDIT) { auditOut.println( word ); }
			
			// Exit loop if user entered the escape word.//////////////////////
			if (word.equals( escape )) { break; }

			// Lookup word in Dictionary
			dictLookup.println( word );
			dictLookup.flush();
			
			// Store the translated word and output to console and audit file
			// if specified. //////////////////////////////////////////////////
			String transWord = dictReader.readLine();
			userOut.println( transWord );
			if (AUDIT) { auditOut.println( transWord ); }
		}
		
		// Terminate the Dictionary process now that the user loop is finished.
		dictLookup.close();
		dictReader.close();
		proc.destroy();
		
		// Close the handle for the audit file before exiting the program./////
		if (AUDIT) { auditOut.close(); }
		
	}
}