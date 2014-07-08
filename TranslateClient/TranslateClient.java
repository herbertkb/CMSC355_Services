/******************************************************************************
    TranslateClient.java
    
    Interactive console to prompt user for an English word and return its 
    translation. Connects to external server to lookup translations. 
    
*******************************************************************************
    To run:
        java TranslateClient *ServiceCode* *ServerIP* *port*
    
        Enter a word to translate or !!! to quit: boy
        мальчик
        Enter a word to translate or !!! to quit: fnord
        FNORD not found in dictionary. 
        
    To run with audit file:
        java TranslateClient *ServiceCode* *ServerIP* *port* *auditFilename*
    
*******************************************************************************
    Maintenance Log
    Fix #   Date        Name    Description
    0001    20 May 2014 Keith   First draft. Many back and forth revisions to 
                                fix issues with Cyrillic output to Windows
                                console. 
    0002    21 May 2014 Keith   More fiddling to fix Cyrillic output to 
                                Windows console. Verified that output is 
                                correct with temporary output file.
    0003    25 May 2014 Keith   Added audit file feature, close file handles
                                and variable name changes for clarity. 
    0004    26 May 2014 Keith   Switch from shell script to client-server model.
                                Translate acts as client, setting up Dictionary
                                which acts as server.
    0005    31 May 2014 Keith   Finally finish fix 0004. What should have been
                                a minor change was complicated by a missing "\n"
                                after the word being printed to Dictionary.jar's
                                input. 
    0006    11 Jun 2014 Keith   Branching to CMSC_Sockets. 
                                Translate is now a networked client app.
                                No longer takes wordfile as parameter.
                                Now requires IP address and port of Dictionary
                                server.
    0007    12 Jun 2014 Keith   Fiddling with the try-with-resources block.
    0008    12 Jun 2014 Keith   Force Unicode for the log file. 
    0009    04 Jul 2014 Keith   ServiceCode string added to work with  
                                EnterpiseServiceBus.
    0010    08 Jul 2014 Keith   serviceCode to be specified as CL argument.
                                Change name of class to TranslateClient.

******************************************************************************/

import java.util.Scanner;           // take in user iput
import java.io.BufferedReader;      // handle input from Dictionary.jar
import java.io.InputStreamReader;   // handle input from Dictionary.jar
import java.io.PrintStream;         // force printing to console in Unicode
import java.io.PrintWriter;         // print to external file & Dictionary.jar
import java.io.OutputStreamWriter;  // print to Dictionary.jar process
import java.io.BufferedOutputStream;// print to Dictionary.jar process
import java.io.IOException;         // lots of IO, lots of possible exceptions
import java.net.*;
import java.util.*;
import java.io.*;


public class TranslateClient {
    public static void main(String... args) {
        
        //  Parse commandline arguments.///////////////////////////////////////
        //  args[0] must be the serviceCode for desired service.
        //      For example, RUSSIAN_DICT or FRENCH_DICT for Russian or French
        //      dictionary services.  
        //  args[1] must be the IP address or hostname of the ESB server
        //  args[2] must be the port of the ESB server.
        //  args[3] could be a filename for an audit file (optional).
        //      If specified, store the filename and raise a flag to be checked
        //      throughout the rest of the program whenever output is needed.
        ///////////////////////////////////////////////////////////////////////
        String serviceCode = args[0];
        String dictIP = args[1];
        int dictPort = Integer.parseInt(args[2]);
        
        String auditfile = null; 
        boolean AUDIT = false;
        
        if (args.length == 4) {
            auditfile = args[3];
            AUDIT = true;
        }
        
        // Hard strings. //////////////////////////////////////////////////////
        // escape is entered by user to end the program
        // prompt is presented to user on every pass of main loop.
        ///////////////////////////////////////////////////////////////////////
        String escape = "!!!";
        String prompt = "Enter a word or " + escape + " to quit: ";
        
        // auditOut must be declared outside of try block to be in scope for the
        // finally block. 
        PrintWriter auditOut = null;
        
        try (
            /* Create objects for input and output to user console and to audit
            // file. Unicode must be forced to override local encodings for
            // non-Latin characters. */
            Scanner userIn = new Scanner(System.in);
            PrintStream userOut = new PrintStream(System.out, true, "UTF-8");
            
            /* Connect to dictionary server. */
            Socket dictSocket = new Socket( dictIP, dictPort );
            BufferedReader dictReader = new BufferedReader( 
                new InputStreamReader( dictSocket.getInputStream() ) );
            PrintWriter dictLookup = new PrintWriter( 
                dictSocket.getOutputStream(), true); 
        ) {
            // auditOut instantiated outside of the try-with-resources resource
            // block because try-with-resources doesn't work with conditional 
            // resources.
            if (AUDIT) auditOut = new PrintWriter(auditfile, "UTF-8"); //fix 0008
            
            //  Main loop of program. /////////////////////////////////////////
            //  On each pass, take English word from the user, pass it to the 
            //  Dictionary server and return the output from the server to the 
            //  user.
            ///////////////////////////////////////////////////////////////////
            while( true ) {
            
                // Fix0009 - ServiceCode string to specify Dictionary service
                // from the EnterpriseServiceBus
                dictLookup.println( serviceCode );

                // Prompt user for input.//////////////////////////////////////
                System.out.print( prompt );
                if (AUDIT) { auditOut.print( prompt ); }
                
                // Store input and add to log file if specified. //////////////
                String word = userIn.next();
                if (AUDIT) { auditOut.println( word ); }
                
                // Exit loop if user entered the escape word.//////////////////
                if (word.equals( escape )) { break; }

                // Lookup word in Dictionary
                dictLookup.println( word );
                dictLookup.flush();
                
                // Get the translated word and output to console and audit
                // file if specified. /////////////////////////////////////////
                String transWord = dictReader.readLine();
                userOut.println( transWord );
                if (AUDIT) { auditOut.println( transWord ); }
            }
        } catch (UnknownHostException e) {
            System.out.println("Cannot connect to server at " + dictIP);
            System.exit(1);
        } catch (IOException e) {
            System.out.println( "Error communicating with server. \n" 
                + e.toString() );
            System.exit(1);
        } finally {
            if (AUDIT) { auditOut.close(); }
        }
    }
}
