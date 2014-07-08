/******************************************************************************
    SessionManager.java
    Component class of EnterpriseServiceBus.
    

*******************************************************************************
    Maintenance Log
    Fix 0001    2 Jul 2014  Keith Herbert
    Source copied from Dictionary.java and cut down to just the client thread. 
    
    Fix 0002    4 Jul 2014  Keith Herbert
    Fleshed out to mostly working state.
    
    Fix 0003    7 Jul 2014 Keith Herbert
    Fixed crashing from ProcessBuilder. Worked fine for awhile but small changes
    made it crash again. 

******************************************************************************/

import java.net.*;
import java.util.*;
import java.io.*;
    
class SessionManager implements Runnable {
    
    private Socket socket;      // the socket for the client connection.
    private int clientID;       // ID# for client. Useful for logging.
    
    // Hard strings
    private final String serviceFile = "services.txt";
    
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
        /* Primary loop
  	    /* Parse requested service from client.
	    /* If a new service, call ServiceBroker for command string to use the 
        /*  requested service.
        /* Forward the client's parameters to the service and return result to
        /*  client.
        /*********************************************************************/
            String previousService = "";
            Socket serviceSocket = null;
	        while ( true ) {
		        
		        // Read in the serviceCode from the client.
		        String currentService = clientIn.readLine();
		        System.out.println(
		            new Date() + " Client" + clientID +  " requests " 
		            + currentService);
		            
		        // If the client returns null, then it has disconnected.
		        // Exit the loop and end the session. 
		        if (currentService == null ) { 
		            System.out.println(
		                new Date() + " Client" + clientID + " disconnected.");
		            break;    
		        } 		        
		        
		        // If the client requests a different service than before,
		        // or is requesting for the first time, then use 
		        // connectService() to return a socket to connect to the service. 
                if (!currentService.equals( previousService )) {
                    serviceSocket = connectService( currentService );
                    previousService = currentService;               
                }
                
                
                // Open I/O handles to the service
                BufferedReader serviceReader = new BufferedReader( 
                                            new InputStreamReader( 
                                            serviceSocket.getInputStream() ) );
                PrintWriter serviceWriter = new PrintWriter( 
                                            serviceSocket.getOutputStream(), true); 
                
                // Pass input from client to the service.
                String clientInput = clientIn.readLine();
                serviceWriter.println( clientInput );
                System.out.println(
                    new Date() + "Client" + clientID + " input: " + clientInput );  
                
                // Pass output from the service to the client.
                String serviceOutput = serviceReader.readLine(); 
                clientOut.println( serviceOutput );
                System.out.println(
                    new Date() + currentService + " output: " + serviceOutput );
       
            }
       } catch (IOException ioe) { System.err.println( ioe.toString() ); }
        
    } // end run()
    
    /**************************************************************************
    /*  callServiceBroker
    /*  Input:  the service code supplied by the client
    /*  Output: A socket object constructed with the ip address and port of the 
    /*          server providing the service requested.
    /*          null if unsuccessful. 
    /*************************************************************************/
    
    private Socket connectService(String clientService) {
        try {            

            // Setup the external call of ServiceBroker    
            ProcessBuilder builder = new ProcessBuilder( 
                "java", "-jar", "ServiceBroker.jar", serviceFile, clientService );
            
            final Process process = builder.start();
            BufferedReader serviceBrokerOut =   new BufferedReader( 
                                                new InputStreamReader( 
                                                process.getInputStream() ));
                                            
            // ServiceBroker prints the hostname and port on seperate lines.
            String hostname = serviceBrokerOut.readLine();
            int port = Integer.parseInt( serviceBrokerOut.readLine() );
            
            System.out.println(
                new Date() + " " clientService + " at " + hostname + " port " + port);  
            
            // Stop reading from ServiceBroker.
            serviceBrokerOut.close();
            
            // Make a socket from the hostname and port provided by
            // ServiceBroker and return it.
            Socket service = new Socket(hostname, port);
            return service;
            
        } catch (IOException ioe) { 
            System.err.println( ioe.toString() );
        }
        
        // Return null in the event of failure.
        return null;
         
    } // end callServiceBroker method
    
    
} // end SessionManager class 
