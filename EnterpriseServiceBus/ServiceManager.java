/******************************************************************************
    ServiceManager.java
  
    Component class of EnterpriseServiceBus.jar.
    Accepts client connections and starts a SessionManager thread for each
    client.
    Prints status for connecting clients to standard output.
     
*******************************************************************************
    Maintenance Log
    
    Fix#0001    2 Jul 2014  Keith Herbert
    Source copied from Dictionary.java and cut down to just the server.
    
    Fix 0002    4 Jul 2014  Keith Herbert
    Fleshed out.
    
    Fix 0003    7 Jul 2014  Keith Herbert
    Imports narrowed down, comments added, and minor trimmings.
    

******************************************************************************/

import java.net.ServerSocket;   // Listens for incoming connections.
import java.net.Socket;         // Connection for each new client.
import java.net.InetAddress;    // Used for logging.
import java.util.Date;          // Used for logging.
import java.io.IOException;     // Handle network issues gracefully.

public class ServiceManager {
    
    /**************************************************************************
    *   Main method:
    *   Listens for incoming client connections on port from CL argument. 
    *   Starts SessionManager threads for each connecting client.
    **************************************************************************/
    public static void main(String[] args) {
 
       /**********************************************************************  
            Parse commandline arguments:
            args[0] must be the port number to listen for incoming connections.
        **********************************************************************/    
        int port = Integer.parseInt(args[0]);        
        

        /********************************************************************** 
        /* Open a port to listen for incoming connections. 
        /* On each connection, spawn a new thread to handle the client's needs.
        /*********************************************************************/
        try ( ServerSocket serverSocket = new ServerSocket( port )) {
            
            // Log opening of socket for listening. 
            String serverStart = 
                new Date() + " ServiceManager listening on port " + port + ".";
            System.out.println( serverStart );
            
            // Tracks the number of clients and gives them an ID for logging
            int clientCount = 0;
            
            while (true) {
                // New client connected. 
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                
                // Log new client connection. {} to narrow scope of temp variables
                {
                InetAddress clientIP = clientSocket.getInetAddress();
                String clientHostName = clientIP.getHostName();
                String clientConnected = 
                    new Date() + " Client " + clientCount + " (" + clientHostName 
                    + ") connected.";
                System.out.println( clientConnected );
                }
                
                // Start a new thread for the client. 
                Thread client = new Thread(
                    new SessionManager(clientSocket, clientCount));
                client.start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        
    } // end main method


} // end ServiceManager class
