/******************************************************************************
    SessionManager.java
    
    
*******************************************************************************
    To run:

*******************************************************************************
    Maintenance Log
    Fix #   Date        Name    Description
    Fix#0001    2 Jul 2014  Keith Herbert
    Source copied from Dictionary.java and cut down to just the client thread.  

******************************************************************************/
package EnterpriseServiceBus;

import java.net.*;
import java.util.*;
import java.io.*;



/******************************************************************************
/* SessionManager - inner class required to create runnable threads.
/* Having each client on its own thread allows the Dictionary server to handle
/* many clients at once. 
/*****************************************************************************/    
class SessionManager implements Runnable {
    
    private Socket socket;      // the socket for the client connection.
    private int clientID;       // ID# for client. Useful for logging.
    
    // Constructor
    public SessionManager(Socket socket, int clientID ) {
        this.socket = socket;
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
  	    /* Parse requested service from client.
	    /* If a new service, call ServiceBroker for command string to use the 
        /*  requested service.
        /* Forward the client's parameters to the service and return result to
        /*  client.
        /*********************************************************************/
            while ( true ) {
                // write code        
            }
       } catch (IOException ioe) {}
        
        // Log disconnect message.
        //String disconnectMsg = new Date() + " Client " + clientID + " disconnected.";
        //System.out.println( disconnectMsg );
        
    } // end run()
} // end SessionManager class 
