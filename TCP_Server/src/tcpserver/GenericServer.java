package tcpserver;

/**
 *
 * @author claudiu
 */

import java.net.*;
import java.io.*;


public class GenericServer {
    private static ServerSocket serverSocket;
    
    public static void main(String [] args) throws IOException
    {
       int port = 8899;
       
       serverSocket = new ServerSocket(port);
       
       DataInputStream in = null;
       DataOutputStream out = null;
   
        try
        {
           System.out.println("Asteapta conexiune client pe portul " +
           serverSocket.getLocalPort() + " ...");

           Socket client = serverSocket.accept();
           System.out.println("S-a conectat clientul "
                 + client.getRemoteSocketAddress());

           in = new DataInputStream(client.getInputStream()); 
           out = new DataOutputStream(client.getOutputStream());
           String clientMsg = null;

           while (true) {
               if (!client.isConnected()) {
                   client.close();
                   System.exit(0);
               }
               clientMsg = in.readUTF();

               System.out.println("Client: " + clientMsg);            
               Thread.currentThread().sleep(300);

               out.writeUTF(" Mesajul clientului: " + clientMsg);
           }
        } catch (EOFException eof) {
            System.out.println("Deconectare din partea clientului!");
        } catch (InterruptedException i)
        {
            i.printStackTrace();
        } catch(SocketTimeoutException s)
        {
           System.out.println("Socket timed out!");
        } catch(IOException e)
        {
           e.printStackTrace();
        } finally {
//            in.close();
//            out.close();
           serverSocket.close();
        }
    }
    
}