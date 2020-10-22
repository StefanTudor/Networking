package tcpmulticlientserver;

import java.net.*;
import java.io.*;
import static java.lang.Thread.sleep;
 

/**
 *
 * @author claudiu
 */
public class ServerThread extends Thread {
    private Socket socket = null;
    private static int clientNo = 0;
 
    public ServerThread(Socket socket) 
    {
//        super("ServerThread" + socket.getRemoteSocketAddress().toString());
        super("ServerThread");
        this.socket = socket;
//        clientNo++;
        this.setName(this.getName() + "-" + ++clientNo);
    }
     
    @Override
    public void run() 
    {
 
        try (
            DataInputStream in = new DataInputStream(socket.getInputStream()); 
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        ) {

            System.out.println("Conexiune noua de la " + socket.getRemoteSocketAddress()
                    + "; " + this.getName());
            String clientMsg = null;
            
            while(true) {
            	if (!socket.isConnected()) {
                    in.close();
                    out.close();
                    socket.close();
                    System.exit(0);
                }
                
                // ReadClientRequest( ... );
                clientMsg = in.readUTF();
                
                // ProcessClientRequest( ... );
                System.out.println("client: " + clientMsg + "; "+ this.getName());            
                sleep(300);
                
                // ReplyToClient( ... );
                out.writeUTF("ECHO server: " + clientMsg + "; " + this.getName());
            }      
        } catch (EOFException eofe) {
            System.out.println("Deconectare client al " + this.getName());
        } catch (InterruptedException i) {
            i.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
