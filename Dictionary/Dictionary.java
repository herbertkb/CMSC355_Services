/******************************************************************************
	Dictionary.java
	
	Simple program that takes an English word and a translation file as input
	and returns the word's translation as output. 
	
	Translation file must be formated as:
	*QueryWord* *tab* *TranslatedWord*
	
*******************************************************************************
	To run:
		java Dictionary *englishWord* *translationFile*
		
	Example:
		> java Dictionary boy English2Russian.txt
		мальчик
		>
		> java Dictionary fnord Englist2Russian.txt
		FNORD not found in translation file "English2Russian.txt". 

*******************************************************************************
	Maintenance Log
	Fix #	Date		Name	Description
	0001	20 May 2014	Keith	First draft. Many back and forth revisions to 
								fix issues with Cyrillic output to Windows
								console. 
	0002	21 May 2014	Keith	More fiddling to fix Cyrillic output to 
								Windows console. 
	0003	25 May 2014	Keith	Minor variable name changes for clarity.
	0004	26 May 2014	Keith	Change from Unix style command line utility
								to interactive server. Begin by reading whole 
								word file into memory and sort it. Then 
								binary search for word translation in response
								to input.
	0005	10 Jun 2014	Keith	Tear out and replace changes from Fix#0004 with
								simpler and faster Map data structure.
	0006	10 Jun 2014	Keith	Combined a few lines together in the loop which
								reads the translation file. More comments for
								main loop.


******************************************************************************/

import java.util.Scanner;			// to parse the translation file
import java.io.File;				// to work with scanner to open trans file
import java.io.PrintStream; 		// to force printing in Unicode
import java.util.HashMap;			// implementation of Map. Fastest lookup.
import java.util.Map;				// Abstract data structure for mapping

public class Dictionary {
	public static void main(String[] args) 
		throws java.io.FileNotFoundException, 
			   java.io.UnsupportedEncodingException {
		
		/**	Parse commandline arguments.///////////////////////////////////////
		//	args[0] must be the filename for the translation file//////////////
		*/
		String transFile = args[0];
		
		///////////////////////////////////////////////////////////////////////
		/** Open the translation file and read every line into memory as a 
		// key,value pair with the query word as the key and the translation
		// as the value, represented in the translation file by two words
		// separated by a tab.
		*/
		
		Scanner parser = new Scanner(new File(transFile), "UTF-8");
		Map<String, String> wordMap = new HashMap<String, String>();
		
		while (parser.hasNextLine()) {
			String[] fields = parser.nextLine().trim().split("\t");
			wordMap.put(fields[0], fields[1]);
		}
		

		///////////////////////////////////////////////////////////////////////
		/** Create input and output objects as Unicode to/from System In/Out.
		// This is needed to force Unicode over local encodings and handle
		// non-Latin characters.
		*/
		
		Scanner input = new Scanner(System.in, "UTF-8");
		PrintStream output = new PrintStream(System.out, true, "UTF-8");
		
		/** Main loop //////////////////////////////////////////////////////////
		// Take query word from System.in and check if it exists in the word
		// map. If it does, print the translation out to the console. 
		// Otherwise, print an error message. 
		
		*/
		while ( true ) {
			String query = input.next();
			if (wordMap.containsKey( query )) {	
				output.println( wordMap.get( query.toLowerCase() ) ); 
			}
			else { 
				output.println("No translation for " + query.toUpperCase()); 
			}
		}
		
		
	}
}