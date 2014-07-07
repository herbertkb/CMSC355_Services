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
//package EnterpriseServiceBus;

import java.net.*;
import java.util.*;
import java.io.*;
    
class SessionManager implements Runnable {
    
    private Socket socket;      // the socket for the client connection.
    private int clientID;       // ID# for client. Useful for logging.
    
    // Hard strings
    //private String serviceFile = "services.txt";
    //private String callServiceBroker = "java -jar ServiceBroker.jar ";
    
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
            String previousService = "";
            Socket serviceSocket = null;
	        while ( true ) {
		        
		        // Read in the serviceCode from the client.
		        String currentService = clientIn.readLine();
		        
		        // If the client returns null, then it has disconnected. 
		        if (currentService == null ) { 
		            System.out.println(new Date() + " Client" + clientID + " disconnected.");
		            break;    
		        } 		        
		        
		        System.out.println(
		            new Date() + " Client" + clientID +  " requests " + currentService);
		            
		            
               

                if (!currentService.equals( previousService )) {
                    
                    serviceSocket = connectService(currentService);


                    previousService = currentService;               
                }
                
                
                // Open I/O handles to the service
                BufferedReader serviceReader = new BufferedReader( 
                                            new InputStreamReader( 
                                            serviceSocket.getInputStream() ) );
                PrintWriter serviceWriter = new PrintWriter( 
                                            serviceSocket.getOutputStream(), true); 
                
                // Pass input from client to the service
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
        
        // Log disconnect message.
        //String disconnectMsg = new Date() + " Client " + clientID + " disconnected.";
        //System.out.println( disconnectMsg );
        
    } // end run()
    
    /**************************************************************************
    /*  callServiceBroker
    /*  Input:  the service code supplied by the client
    /*  Output: A socket object constructed with the ip address and port of the 
    /*          server providing the service requested.
    /*          null if unsuccessful. 
    /*************************************************************************/
    
    private static Socket connectService(String clientService) {
        try {
        
            // Setup the external call of ServiceBroker    
            ProcessBuilder builder = new ProcessBuilder( 
                "java", "-jar", "ServiceBroker.jar", "services.txt", clientService );
            
            final Process process = builder.start();
            BufferedReader serviceBrokerOut =   new BufferedReader( 
                                                new InputStreamReader( 
                                                process.getInputStream() ));
                                            
            // ServiceBroker prints the hostname and port on seperate lines.
            String hostname = serviceBrokerOut.readLine();
            int port = Integer.parseInt( serviceBrokerOut.readLine() );
            
            serviceBrokerOut.close();
            
            
            Socket service = new Socket(hostname, port);
            
            return service;
            
        } catch (IOException ioe) { 
            System.err.println( ioe.toString() );
        }
        
        return null;
         
    } // end callServiceBroker method
    
    
} // end SessionManager class 
