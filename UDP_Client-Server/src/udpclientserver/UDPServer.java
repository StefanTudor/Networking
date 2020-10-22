package udpclientserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Predicate;
import java.util.stream.Stream;


//import org.apache.derby.jdbc.ClientDataSource;


public class UDPServer {
    private DatagramSocket s;
    
//    private String dbURL = "jdbc:derby://localhost:1527/biblioteca;create=true;user=server;password=server";
//    private String dbURL = "jdbc:derby://localhost:1527/biblioteca;create=true";
    private String dbURL = "jdbc:derby:biblioteca;create=true";
    private String dbUser = "server";
    private String dbPass = "server";
    private String dbName = "biblioteca";
    private String tableName = "carte";
    
    // jdbc Connection
    private Connection conn = null;
    private Statement sqlStmt = null;
    
    public UDPServer() {
        initDBCon();
        initSocketCon();
    }
    
    private void initDBCon() {
        try
        {
        	
            //Get a connection
        	//conn = DriverManager.getConnection(dbURL);
        	conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
//            ClientDataSource jdbc = new ClientDataSource();
//            jdbc.setServerName("localhost");
//            jdbc.setUser("server");
//            jdbc.setPassword("server");
//            jdbc.setDatabaseName(dbName);
//            jdbc.getCreateDatabase();
//            jdbc.setPortNumber(1527);
//            conn = jdbc.getConnection();
        	
        	// instantiaza carti
            Carte c1 = new Carte("Cota-0005", "Cel mai iubit dintre pamanteni",
            		"Marin Preda", 1978);
            Carte c2 = new Carte("Cota-0008", "ActiveMQ in Action", 
            		"Bruce Snyder", 2016);
            
        	//createTable();
        	//insertCarte(c2);
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    } 
    
    private void createTable() {
    	try {
	    	sqlStmt = conn.createStatement();
	    	sqlStmt.execute("CREATE TABLE CARTE (COTA VARCHAR(16) PRIMARY KEY, TITLU VARCHAR(64), AUTORI VARCHAR(64), AN INT)");
    	} catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
    }
    
    private void insertCarte(Carte carte)
    {
        try
        {
            sqlStmt = conn.createStatement();
            sqlStmt.execute("insert into " + dbUser + "." + tableName + " values ('" +
                    carte.getCota() + "', '" + 
            		carte.getTitlu() + "', '" + 
                    carte.getAutori() + "', " +
            		carte.getAn() + ")");
            sqlStmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }
    
    private void initSocketCon() {                                         
        try{
            s = new DatagramSocket(8888);
            Thread fir = new Thread(new Runnable() {
                @Override
                public void run() {
                    raspuns();
                }
            });
            
            fir.start();
            System.out.println("Conexiune socket UDP pornita");
        } catch(Exception ex) {
        	System.out.println(ex.toString());
        }
    }
        
    private void raspuns() {
    	
        while(true) {
            try{
                byte[] bIn = new byte[1000];
                DatagramPacket pIn = new DatagramPacket(bIn, bIn.length);
                s.receive(pIn);
                
                ByteArrayInputStream in = new ByteArrayInputStream(pIn.getData());
                ObjectInputStream fin = new ObjectInputStream(in);
                int an =  (Integer)fin.readObject();
                System.out.println("A fost receptionat anul: " + an);
                fin.close();
                
                Statement sttm = conn.createStatement
                        (ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = sttm.executeQuery("select * from carte where an >=" + an);
               
                rs.last();
                int nrLinii = rs.getRow();
                System.out.println("Au fost selectate din baza de date " + nrLinii + " inregistrari");
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream fout = new ObjectOutputStream(out);
                //scrie pe sctream numarul de obiecte carte extrase din DB
                fout.writeObject(new Integer(nrLinii));
                
                rs.beforeFirst();
                while (rs.next()) {
                	Carte carte = new Carte();
    			    carte.setCota(rs.getString("cota"));
    			    carte.setTitlu(rs.getString(2));
    			    carte.setAutori(rs.getString(3));
    			    carte.setAn(rs.getInt(4));
    			    System.out.println(carte.toString());
    			    //pune obiect Carte pe stream-ul de iesire
                    fout.writeObject(carte);
                }
                sttm.close();
                
                byte[] bOut = out.toByteArray();
                System.out.println(pIn.getAddress() + " : " + pIn.getPort());
                DatagramPacket pOut = new DatagramPacket(bOut, bOut.length,
                        pIn.getAddress(), pIn.getPort());
                fout.close();
                s.send(pOut);
            }
            catch(Exception ex){
                System.out.println(ex.toString());
            }
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	new UDPServer();
    }
        
}
