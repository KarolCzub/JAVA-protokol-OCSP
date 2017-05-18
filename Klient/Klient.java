package OCSP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
 
 
public class Klient
{
    private static final int PORT =4444;
    private static final int PORT1 =4445;
    private static final int PORT2 =4446;
    private static final String HOST = "127.0.0.1";
    private static final String HOST1 = "127.0.0.1";
    private static boolean odbierajacy=true;
    static boolean flaga = true;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static ServerSocket serverSocket2;
    private static Socket clientSocket2;
    private static Socket socket;
    private static Socket socket1;
    private static Socket socket2;
    static int a1 = 1;
    static int a2=-1;
    static String a3 ="sha1";
    static int a4 = 1;
    static int a5 = 2;
    static int a6= 345;
    static int a7 = 1;
    static String a8= " ";
    static String a9= " ";
    static int a10=-1;
    static String a11= " ";
    static boolean a12 = false;
    static String a13 = " ";
    static String a14 = " ";
 
    public static void main(String[] args) throws IOException, InterruptedException
    {
        socket = null;
        socket1 = null;
        socket2 = null;
        System.out.println("klient wysylajacy czy odbierajacy?");
        String cos;
        Scanner cin=new Scanner(System.in);
        cos = cin.nextLine();
        if (cos.equals("odbierajacy"))
        {
            try
            {
                socket = new Socket(HOST, PORT);
                socket1 = new Socket(HOST1, PORT1);
               
            }
            catch(Exception e)
            {
                System.err.println("Could not connect to "+HOST+":"+PORT);
                System.err.println("Could not connect to "+HOST1+":"+PORT1);
                System.exit(1);
            }
           
        }
        else if (cos.equals("wysylajacy"))
        {
            odbierajacy=false;
            serverSocket = null;
            try
            {
                serverSocket = new ServerSocket(PORT1);
            }
            catch(IOException e)
            {
                System.err.println("Could not listen on port: "+PORT1);
                System.exit(1);
            }
           
            System.out.print("Wating for connection...");
           
            Thread t = new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        while(flaga)
                        {
                            System.out.print(".");
                            Thread.sleep(1000);
                        }
                    }
                    catch(InterruptedException ie)
                    {
                        System.out.println("error");
                    }
                   
                    System.out.println("\nClient connected on port "+PORT1);
                }
            });
            t.start();
            clientSocket = null;
            try
            {
                clientSocket = serverSocket.accept();
                flaga = false;
            }
            catch(IOException e)
            {
                System.err.println("Accept failed.");
                t.interrupt();
                System.exit(1);
            }      
        }
        else {
            System.out.println("zly wybor. Uruchom jeszcze raz.");
             System.exit(1);
        }
     
        Thread t = new Thread(new Runnable()
        {
           
            public void run()
            {
               
                try {
                    if(odbierajacy)
                    {  
                        OCSPklient oko=null;
                        ObjectInputStream clientInputStream = new ObjectInputStream(socket1.getInputStream());
                        try {
                            oko = (OCSPklient)clientInputStream.readObject();
                        } catch (ClassNotFoundException e1) {e1.printStackTrace();}
                        System.out.println("odebrano od klienta wysylajacego");
                        ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        clientOutputStream.writeObject(oko);
                        System.out.println("wyslano do serwera uzupelniony obiekt. np. HashAlgorihtm = " +oko.getHashAlgorithm());          
                        ObjectInputStream clientInputStream2 = new ObjectInputStream(socket.getInputStream());
                        try {
                            oko = (OCSPklient)clientInputStream2.readObject();
                        } catch (ClassNotFoundException e) {e.printStackTrace();}
                        System.out.println("odebrano od serwera certyfikat " + oko.getResponseStatus());
                        clientOutputStream.close();
                        socket.close();
                        socket1.close();
                        try
                        {
                            socket2 = new Socket(HOST1, PORT2);
                        }
                        catch(Exception e)
                        {
                            System.err.println("Could not connect to "+HOST1+":"+PORT2);
                            System.exit(1);
                        }
                        final PrintWriter outk = new PrintWriter(socket2.getOutputStream(),true);
                        final BufferedReader ink = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
                        outk.println("certyfikat: " + oko.getResponseStatus());
                        if (oko.getResponseStatus().equals("wazny"))
                        {
                            System.out.println("Dostep przyznany prawidlowo");
                            System.out.println("komunikacja z drugim uzytkownikiem wlaczona. By zakonczyc wpisz: koniec");
                            String cos2="przyklad";
                            while (!cos2.equals("koniec"))
                            {
                                if (ink.ready())
                                {
                                    String cos3;
                                    cos3=ink.readLine();
                                    System.out.println(cos3);
                                }
                                cos2=cin.nextLine();
                                outk.println(cos2);
                            }
                            socket2.close();
                        }
                        else if (oko.getResponseStatus().equals("uniewazniony"))
                        {
                            System.out.println("certyfikat niewazny, odmowa dostepu");
                        }
                        else if (oko.getResponseStatus().equals("nieznany"))
                        {
                            System.out.println("certyfikat nieznany, odmowa dostepu");
                        }
                    }
                    else
                    {
                        OCSPklient okw=new OCSPklient(a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12,a13,a14);
                        System.out.println("Podaj numer certyfikatu");
                        int x= cin.nextInt();
                        okw.setCertificateID(x);;
                        ObjectOutputStream clientOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        clientOutputStream.writeObject(okw);
                        System.out.println("Wyslano klientowi odbierajacemu");
                        clientOutputStream.close();
                        clientSocket.close();
                        serverSocket.close();
                        odbierajacy=false;
                        serverSocket2 = null;
                        try
                        {
                            serverSocket2 = new ServerSocket(PORT2);
                        }
                        catch(IOException e)
                        {
                            System.err.println("Could not listen on port :"+PORT2+". Certyfikat niewazny lub nieznany");
                           // System.err.println("Could not listen on port: "+PORT2);
                            System.exit(1);
                        }
                       
                        System.out.print("Wating for connection...");
                       
                                try
                                {
                                    while(flaga)
                                    {
                                        System.out.print(".");
                                        Thread.sleep(1000);
                                    }
                                }
                                catch(InterruptedException ie)
                                {
                                    System.out.println("error");
                                }
                               
                                System.out.println("\nClient connected on port "+PORT2);
                               
                        clientSocket2 = null;
                        try
                        {
                            clientSocket2 = serverSocket2.accept();
                            flaga = false;
                        }
                        catch(IOException e)
                        {
                            System.err.println("Accept failed.");
                            System.exit(1);
                        }
                        	final PrintWriter outj = new PrintWriter(clientSocket2.getOutputStream(),true);
                        	final BufferedReader inj = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
                        	String inp = inj.readLine();
                        	System.out.println(inp);
                        	if (inp.equals("certyfikat: wazny")) System.out.println("Dostep przyznany. Mozesz teraz komunikowac sie z drugim uzytkownikiem.");
                        	//u³omny komunikator, który jest tylko dodatkiem sprawdzaj¹cym dzia³anie. Wymaga klikniêcia enter (czasem kilkukrotnego, by otrzymaæ wiadomoœæ.)
                        	String cos2="przyklad";
                            while (!cos2.equals("koniec"))
                            {
                                if (inj.ready())
                                {
                                    String cos3;
                                    cos3=inj.readLine();
                                    System.out.println(cos3);
                                }
                                cos2=cin.nextLine();
                                outj.println(cos2);
                            }
                            clientSocket2.close();
                        } 
                }
                     catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
            }     
        });
        t.start();
        t.join();
        //socket.close();
        //socket1.close();
        
    }
 
    public static String getHost1() {
        return HOST1;
    }
 
    public static int getPort1() {
        return PORT1;
    }
}
