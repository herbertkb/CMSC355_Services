/******************************************************************************
    ServiceBroker.java
    
    Simple program which takes a service code and the service file as command
    line arguments and returns the hostname and port for that service.
    
*******************************************************************************
    Service file must be formated as:
    SERVICE_CODE *tab* HOSTNAME *tab* PORT 
    
    Lines beginning with # are treated as comments.  
    
*******************************************************************************
    Usage:
        java -jar ServiceBroker.jar <SERVICE_FILE> <SERVICE_CODE>

*******************************************************************************
    Maintenance Log
    Fix #   Date        Name    Description
    0001    02 Jul 2014 Keith   First draft.
    0002    07 Jul 2014 Keith   Comments, both in this file and enabled for 
                                service file.        

******************************************************************************/

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class ServiceBroker {
    /**************************************************************************
    *   Main method:
    *   Takes command line arguments for service file and query service code.
    *   Linear search the file for the query service code.
    *   Print the hostname and port of the file to standard output.
    **************************************************************************/
    public static void main(String args[]) {
    
        /**********************************************************************  
            Parse commandline arguments:
            args[0] must be the filename for the service file.
            args[1] must be the service code to lookup in the service file.
        **********************************************************************/
        String serviceFilename = args[0];
        String serviceCode = args[1];
        
        String hostname = "";   // hostname for the target service
        int port = -1;          // port for the target service.
        
        try (
            // Open the service file.
            Scanner reader = new Scanner(new File( serviceFilename ));
        ) {
            // Iterate through to the end of the file.
            while ( reader.hasNextLine() ) {
                
                String line = reader.nextLine();
           
                // Skip this line if it begins with a # (comment).  
                if ( line.startsWith("#") ) { continue; }
               
                // Split the current line into an array as follows:
                // fields[0] is SERVICE_CODE.
                // fields[1] is HOSTNAME.
                // fields[2] is PORT.
                String[] fields = reader.nextLine().split("\t");
                
                // On a match, assign the hostname and port and exit the loop.
                if ( fields[0].equals( serviceCode ) ) {
                    hostname = fields[1];
                    port = Integer.parseInt( fields[2] );
                    break;
                }            
            
            }
        } catch(FileNotFoundException e) {
            System.err.println( e.toString() );
        }
        
        // Print the hostname and port to standard output on separate lines.
        System.out.println( hostname );
        System.out.println( port ); 
    }
}
