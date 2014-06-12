/******************************************************************************
    Dictionary.java
    
    Server which connects to Translate clients and returns translations to words.
    Must be instantiated with translation file containing words and their 
    translations.
    
    Translation file must be formated as:
    *QueryWord* *tab* *TranslatedWord*
    
*******************************************************************************
    To run:
        java -jar Dictionary.jar <translation file> <port>
        or
        java -jar Dictionary.jar <translation file> <port> <log file>

*******************************************************************************
    Maintenance Log
    Fix #   Date        Name    Description
    0001    20 May 2014 Keith   First draft. Many back and forth revisions to 
                                fix issues with Cyrillic output to Windows
                                console. 
    0002    21 May 2014 Keith   More fiddling to fix Cyrillic output to 
                                Windows console. 
    0003    25 May 2014 Keith   Minor variable name changes for clarity.
    0004    26 May 2014 Keith   Change from Unix style command line utility
                                to interactive server. Begin by reading whole 
                                word file into memory and sort it. Then 
                                binary search for word translation in response
                                to input.
    0005    10 Jun 2014 Keith   Tear out and replace changes from Fix#0004 with
                                simpler and faster Map data structure.
    0006    10 Jun 2014 Keith   Combined a few lines together in the loop which
                                reads the translation file. More comments for
                                main loop.
    0007    11 Jun 2014 Keith   Transition from local daemon server to network
                                server. 
                                Exception handling for reading in the translation
                                file.
    0008    12 Jun 2014 Keith   Add more console output.
                                Allow logging to external file.
                                Change tabs to spaces for github readability.
                                Minor change to commenting style

******************************************************************************/

import java.util.Scanner;           // to parse the translation file
import java.io.File;                // to work with scanner to open trans file
import java.io.PrintStream;         // to force printing in Unicode
import java.util.HashMap;           // implementation of Map. Fastest lookup.
import java.util.Map;               // Abstract data structure for mapping
import java.net.*;
import java.util.*;
import java.io.*;

public class Dictionary {

    private static boolean LOG = false; // flag for logging
    private static PrintWriter logOut;  // output to logfile if LOG is true

    public static void main(String[] args) 
        throws java.io.FileNotFoundException, 
               java.io.UnsupportedEncodingException {
        
        /**********************************************************************  
            Parse commandline arguments:
            args[0] must be the filename for the translation file.
            args[1] must be the port number to listen for incoming connections.
            args[2] may be the filename for a log file (OPTIONAL)
        **********************************************************************/
        String transFile = args[0];
        int port = Integer.parseInt(args[1]);
        
        if (args.length == 3) {
            String logfile = args[2];
            LOG = true;
            logOut = new PrintWriter(logfile, "UTF-8");
        }
        
        
        /**********************************************************************
        /* Open the translation file and read every line into memory as a 
        /* key,value pair with the query word as the key and the translation
        /* as the value, represented in the translation file by two words
        /* separated by a tab.
        /*********************************************************************/
        // Map collection to store the word and translation pairs. 
        Map<String, String> wordMap = new HashMap<String, String>();
        
        // Open the translation file
        try (Scanner parser = new Scanner(new File(transFile), "UTF-8")) {
            
            // Log opening the file. 
            String processedStart = new Date() + " Processing " + transFile + ".";
            System.out.println( processedStart );
            if (LOG) { 
                logOut.println( processedStart ); 
                logOut.flush();
            }
            
            // Parse the file and extract the word, translation pairs on each
            // line into the key,value pairs of the Map. 
            while (parser.hasNextLine()) {
                String[] fields = parser.nextLine().trim().split("\t");
                wordMap.put(fields[0], fields[1]);
            }
           
           // Log successful parsing. 
           String processedWin = new Date() + " " + transFile + " processed.";     
           System.out.println( processedWin );
           if (LOG) { 
            logOut.println( processedWin ); 
            logOut.flush();
            }
        } catch (FileNotFoundException e) {
            
            // Log unsuccessful parsing.
            String processedFail = new Date() + "Error reading translation file "
                + transFile +"\n" + e.toString();
            System.out.println( processedFail );
            if (LOG) { 
                logOut.println( processedFail );
                logOut.flush();
            }
            
        }
        
        
        /********************************************************************** 
        /* Open a port to listen for incoming connections. 
        /* On each connection, spawn a new thread to handle the client's needs.
        /*********************************************************************/
        try ( ServerSocket serverSocket = new ServerSocket( port )) {
            
            // Log opening of socket for listening. 
            String serverStart = 
                new Date() + " Dictionary server listening on port " + port + ".";
            System.out.println( serverStart );
            if (LOG) {
                logOut.println( serverStart );
                logOut.flush();
            }
            
            // Tracks the number of clients and gives them an ID for logging
            int clientCount = 0;
            
            while (true) {
                // New client connected. 
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                
                // Log new client connection.
                InetAddress clientIP = clientSocket.getInetAddress();
                String clientHostName = clientIP.getHostName();
                String clientConnect = 
                    new Date() + " Client " + clientCount + " (" + clientHostName 
                    + ") connected.";
                System.out.println( clientConnect );
                logOut.println( clientConnect );
                
                // Start a new thread for the client. 
                Thread client = new Thread(
                    new HandleClient(clientSocket, wordMap, clientCount));
                client.start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        
        
        if (LOG) { logOut.close(); }
        
    } // end main method

/******************************************************************************
/* HandleClient - inner class required to create runnable threads.
/* Having each client on its own thread allows the Dictionary server to handle
/* many clients at once. 
/*****************************************************************************/    
private static class HandleClient implements Runnable {
    
    private Socket socket;      // the socket for the client connection.
    private Map<String, String> wordMap;        // collection of words and translations
    private int clientID;       // ID# for client. Useful for logging.
    
    // Constructor
    public HandleClient(Socket socket, Map<String, String> wordMap, int clientID ) {
        this.socket = socket;
        this.wordMap = wordMap;
        this.clientID = clientID;
    }
    
    
    /************************************************************************** 
    /* The run() method is called by JVM to start a new thread for each client
    /* connection. It's where all the magic happens after a client connects. 
    /*************************************************************************/
    public void run() {
        try (
            // Create objects to handle client input/output 
            BufferedReader clientIn = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
                
            PrintWriter clientOut = new PrintWriter(
                socket.getOutputStream(), true);    // 'true' is for autoFlush
        ) {
        
        /** *******************************************************************
        /* Main loop
        /* Take query word from System.in and check if it exists in the word
        /* map. If it does, print the translation out to the client. 
        /* Otherwise, print an error message. 
        /*********************************************************************/
            while ( true ) {
                // Grab the word from the user.
                String query = clientIn.readLine();
                
                // Log client's word
                String clientQueryMsg = 
                    new Date() + " Client " + clientID + " query: " + query;
                System.out.println( clientQueryMsg );
                if (LOG) {
                    logOut.println( clientQueryMsg );
                    logOut.flush();
                }
                
                // Look the word up and return translation to client.
                if (wordMap.containsKey( query )) {
                    
                    String translation =  wordMap.get( query.toString() );
                    clientOut.println( translation ); 
                    
                    // Log the translation
                    String clientTransMsg = 
                        new Date() + " Client " + clientID + " translation: " +
                        translation;
                    System.out.println( clientTransMsg );
                    if (LOG) {
                        logOut.println( clientTransMsg );
                        logOut.flush();
                    }
                }
                // null if client has disconnected. End the loop.
                else if (query == null ) { break; }
                
                // Error message if word not found in collection. 
                else { 
                    clientOut.println("No translation for " + query.toUpperCase());
                    
                    // Log not finding a translation.s
                    String queryFail = new Date() + " Client " + clientID 
                        + ": No translation for " + query.toUpperCase();
                    System.out.println(queryFail);
                    if (LOG) {
                        logOut.println(queryFail);
                        logOut.flush();
                    }
                }
            }
            
        } catch (IOException e) { 
            System.out.println(e); 
            if (LOG) { 
                logOut.println(e);
                logOut.flush();
            }
        }
        
        // Log disconnect message.
        String disconnectMsg = new Date() + " Client " + clientID + " disconnected.";
        System.out.println( disconnectMsg );
        if (LOG) {
            logOut.println(disconnectMsg);
            logOut.flush();
        }
    }
} // end HandleClient class

} // end Dictionary class