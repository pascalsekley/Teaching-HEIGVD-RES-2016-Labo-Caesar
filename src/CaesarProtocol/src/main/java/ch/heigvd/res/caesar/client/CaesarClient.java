package ch.heigvd.res.caesar.client;

import ch.heigvd.res.caesar.protocol.Protocol;
import ch.heigvd.res.caesar.server.Filter;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Olivier Liechti (olivier.liechti@heig-vd.ch)
 * Modified by: Pascal Sekley & Annie Sandra
 */
public class CaesarClient {

  private static final Logger LOG = Logger.getLogger(CaesarClient.class.getName());
  
  final static int BUFFER_SIZE = 1024;
  private int delta;
  

  public void start(){
     Socket clientSocket = null;
     PrintWriter writer;
     BufferedReader reader;
     Filter filtre = new Filter();
     String line;
     
     
     try {
        clientSocket = new Socket("www.heig-vd.ch", Protocol.A_CONSTANT_SHARED_BY_CLIENT_AND_SERVER);
	reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	writer = new PrintWriter(clientSocket.getOutputStream());
        
        System.out.println(reader.readLine());
        System.out.println(reader.readLine());
        delta = reader.read();
        
        

           String encryptedByeCommand = filtre.encrypt("bye", delta);
        while(true){
           line = reader.readLine();
           
           if(line.equalsIgnoreCase(encryptedByeCommand)){
              clientSocket.close();
              reader.close();
              writer.close();
           }
           else{
              writer.println(filtre.decrypt(reader.readLine(), delta));
              writer.flush();
           }
              
        }

       
     } catch (IOException ex) {
         LOG.log(Level.SEVERE, null, ex);
      } 
   
  }
  
  


  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tH:%1$tM:%1$tS::%1$tL] Client > %5$s%n");
    LOG.info("Caesar client starting...");
    LOG.info("Protocol constant: " + Protocol.A_CONSTANT_SHARED_BY_CLIENT_AND_SERVER);
    
    CaesarClient client = new CaesarClient();
    client.start();
  }
  
}
