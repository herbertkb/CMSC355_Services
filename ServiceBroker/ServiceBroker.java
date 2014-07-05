import java.net.*;
import java.util.*;
import java.io.*;


public class ServiceBroker {
    public static void main(String args[]) {

        String serviceFilename = args[0];
        String serviceCode = args[1];
        
        String hostname = "";
        int port = -1;
        Socket serviceSocket = null;
        
        try (
            Scanner reader = new Scanner(new File( serviceFilename ));
        ) {
            while ( reader.hasNextLine() ) {
                
                String[] fields = reader.nextLine().split(",");
                
                if ( fields[0].equals( serviceCode ) ) {
                    hostname = fields[1];
                    port = Integer.parseInt( fields[2] );
                    break;
                }            
            
            }
        } catch(FileNotFoundException e) {
            System.err.println( e.toString() );
        }
        
        
        System.out.println( hostname );
        System.out.println( port ); 
    }
}
