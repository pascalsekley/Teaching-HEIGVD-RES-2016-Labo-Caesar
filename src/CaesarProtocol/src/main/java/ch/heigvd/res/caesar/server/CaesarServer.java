
/**
 *
 * @author Olivier Liechti (olivier.liechti@heig-vd.ch)
 * Modified by: Pascal Sekley & Annie Sandra
 */


package ch.heigvd.res.caesar.server;

import ch.heigvd.res.caesar.protocol.Protocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a multi-threaded TCP server. It is able to interact
 * with several clients at the time, as well as to continue listening for
 * connection requests.
 *
 * @author Olivier Liechti
 * Modified by: Pascal Sekley & Annie Sandra
 */
public class CaesarServer {

   final static Logger LOG = Logger.getLogger(CaesarServer.class.getName());

   int port;

   /**
    * Constructor
    *
    * @param port the port to listen on
    */
   public CaesarServer(int port) {
      this.port = port;
   }

   /**
    * This method initiates the process. The server creates a socket and binds it to
    * the previously specified port. It then waits for clients in a infinite loop.
    * When a client arrives, the server will read its input line by line and send
    * back the data converted to uppercase. This will continue until the client sends
    * the "BYE" command.
    */
   public void serveClients() {
      LOG.info("Starting the Receptionist Worker on a new thread...");
      new Thread(new ReceptionistWorker()).start();
   }

   /**
    * This inner class implements the behavior of the "receptionist", whose
    * responsibility is to listen for incoming connection requests. As soon as a new
    * client has arrived, the receptionist delegates the processing to a "servant"
    * who will execute on its own thread.
    */
   private class ReceptionistWorker implements Runnable {

      @Override
      public void run() {
         ServerSocket serverSocket;

         try {
            serverSocket = new ServerSocket(port);
         } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
         }

         while (true) {
            LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
            try {
               Socket clientSocket = serverSocket.accept();
               LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
               new Thread(new ServantWorker(clientSocket)).start();
            } catch (IOException ex) {
               Logger.getLogger(CaesarServer.class.getName()).log(Level.SEVERE, null, ex);
            }
         }

      }

      /**
       * This inner class implements the behavior of the "servants", whose
       * responsibility is to take care of clients once they have connected. This is
       * where we implement the application protocol logic, i.e. where we read data
       * sent by the client and where we generate the responses.
       */
      private class ServantWorker implements Runnable {

         Socket clientSocket;
         BufferedReader in = null;
         PrintWriter out = null;
         
         int delta;

         public ServantWorker(Socket clientSocket) {
            
            try {
               
               delta = (int)(Math.random() * 25);
               this.clientSocket = clientSocket;
               in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
               out = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException ex) {
               Logger.getLogger(CaesarServer.class.getName()).log(Level.SEVERE, null, ex);
            }
         }

         @Override
         public void run() {
            String line;
            String encryptedLine, decryptedLine;
            boolean shouldRun = true;
            Filter filtre = new Filter();
            
            out.println(delta);
            out.flush();

            out.println("Welcome to the Multi-Threaded Server.\n Send me text lines and conclude with the BYE command.");
            out.flush();
            
            try {
               LOG.info("Reading until client sends BYE or closes the connection...");
               /* We decrypt de message we received from the client and then encrypt our response
                  before sending */
               while ((shouldRun) && (line = in.readLine()) != null) {
                  decryptedLine = filtre.decrypt(line, delta);
                  if(decryptedLine.equalsIgnoreCase("bye")){
                     shouldRun = false;
                  }
                  encryptedLine = filtre.encrypt(decryptedLine, delta);
                  out.write(encryptedLine +"\n");                  
                  out.flush();
               }

               LOG.info("Cleaning up resources...");
               clientSocket.close();
               in.close();
               out.close();

            } catch (IOException ex) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (IOException ex1) {
                     LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                  }
               }
               if (out != null) {
                  out.close();
               }
               if (clientSocket != null) {
                  try {
                     clientSocket.close();
                  } catch (IOException ex1) {
                     LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                  }
               }
               LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
         }
      }
   }
   public static void main(String[] args) {
      System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

      CaesarServer multi = new CaesarServer(Protocol.A_CONSTANT_SHARED_BY_CLIENT_AND_SERVER);
      multi.serveClients();
      
   }
}
