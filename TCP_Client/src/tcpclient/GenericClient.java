package tcpclient;


/**
 *
 * @author claudiu
 */

import java.net.*;
import java.io.*;

public class GenericClient {
    public static void main(String[] args)
    {
        

        String serverName = "localhost";
        int port = 8899;
      
        try
        {
            System.out.println("Conectare la " + serverName
                             + " pe portul " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Adresa client: " + client.getLocalPort());
            System.out.println("Conectat la "
                      + client.getPort());
            
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
         
            //pentru a prelua mesaje de la tastatura 
            BufferedReader commandLine = new java.io.BufferedReader(new InputStreamReader(System.in));
            System.out.println("Scrie 'exit' pentru inchide clientul");
            
            //repeta pana cand este scris "exit"
            while(true)
            {
            	//citeste mesaje de la tastatura
            	String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit"))
                {
                    in.close();
                    out.close();
                    client.close(); //inchide conexiunea socket
                    System.exit(0); //iesi din program
                } else {
                    out.writeUTF("Client:" + s + " (" + client.getLocalSocketAddress() + ")" );
                }
                
                System.out.println(in.readUTF());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}