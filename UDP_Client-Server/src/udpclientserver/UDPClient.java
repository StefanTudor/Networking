package udpclientserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JOptionPane;


public class UDPClient {

    private DatagramSocket s;
  
    public UDPClient() {
        initSocketCon();
    }

    private void initSocketCon() {

		 try {
		    System.out.println("Creare socket UDP");
		    s = new DatagramSocket();
		    if (s.isConnected()) 
		    	System.out.println("Conectat la " + s.getLocalSocketAddress().toString());
		    
		    Thread fir = new Thread(new Runnable() {
			    @Override
			    public void run() {
			        cerere();
			    }
		    });
		
		    fir.start();
		 } catch (Exception ex) {
		
		 }
    }
    
    private void cerere() {
    	try {
	    	//pentru a prelua mesaje de la tastatura 
    		System.out.println("Scrie cota cartii sau 'exit' pentru inchide clientul");
	        BufferedReader commandLine = new java.io.BufferedReader(new InputStreamReader(System.in));
	        
	        
	        //repeta pana cand este scris "exit"
	        while(true)
	        {
	        	//citeste mesaje de la tastatura
	        	String cota = commandLine.readLine().trim();
	        	
	            if (cota.equalsIgnoreCase("exit"))
	            {
	            	System.out.println("A fost inchisa conexiunea sochet UDP de catre client");
	            	s.close(); //inchide conexiunea socket
	                System.exit(0); //iesi din program
	            } else {
	            	ByteArrayOutputStream out = new ByteArrayOutputStream();
	            	ObjectOutputStream fout = new ObjectOutputStream(out);
	            	fout.writeObject(Integer.parseInt(cota));
		            byte[] bOut = out.toByteArray();
		            DatagramPacket pOut = new DatagramPacket(bOut, bOut.length, InetAddress.getByName("localhost"), 8888);
		            s.send(pOut);
		            
			        out.close();
			        fout.close();
	            }
	            
	            byte[] bIn = new byte[10000];
	            DatagramPacket pIn = new DatagramPacket(bIn, bIn.length);
	            s.receive(pIn);
	            ByteArrayInputStream in = new ByteArrayInputStream(pIn.getData());
	            ObjectInputStream fin = new ObjectInputStream(in);
	             
	            int n = (Integer) fin.readObject();
		        System.out.println("Am primit " + n + " inregistrari");
		        for (int i = 0; i < n; i++) {
		        	Carte carte = (Carte) fin.readObject();
		            System.out.println(carte.toString());
		        }
		        
		        in.close();
		        fin.close();
	        }
    	} catch(IOException e) {
    		e.printStackTrace();
    	} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    	new UDPClient();
    }
                  
}
