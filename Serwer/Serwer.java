package OCSP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
 
public class Serwer
{
    public long[] wazne;
    public long[] uniewaznione;
    public int liczbaw = 0;
    public int liczbau = 0;
    public long certyfikat;
   
    private static final int PORT =4444;
    static boolean flaga = true;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
   
    public void UtworzTablice() throws FileNotFoundException
    {
        Scanner plik = new Scanner(new File("D:/dane.txt"));
        String dane;
        while (plik.hasNextLine())
        {
            dane = plik.next();
            if (dane.equals("U")) liczbau++;
            else if (dane.equals("W")) liczbaw++;
        }
        wazne=new long[liczbaw];
        uniewaznione=new long[liczbau];
        plik.close();
    }
   
    public void Wczytaj() throws FileNotFoundException
    {
        UtworzTablice();
        Scanner plik = new Scanner(new File("D:/dane.txt"));
        String dane;
        int indeksw=0;
        int indeksu=0;
        while (plik.hasNextLine())
        {
            dane = plik.next();
            if (dane.equals("U"))
            {
                uniewaznione[indeksu]=plik.nextLong();
                indeksu++;
            }
            else if (dane.equals("W"))
            {
                wazne[indeksw]=plik.nextLong();
                indeksw++;
            }
        }
        plik.close();
    }
   
    public static void Wypelnij(OCSPklient a)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String data=dateFormat.format(calendar.getTime());
       
        a.setResponseType("Basic OCSP Response");
        a.setResponderId(1234);
        String Time1 = data; // tu czas obecny
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        String Time2 = data; // tu czas za tydzien
        a.setProducedAt(Time1);
        a.setThisUpdate(Time1);
        a.setNextUpdate(Time2);
    }
   
    public static void main(String[] args) throws IOException
    {
    	Serwer n1 = new Serwer();
        n1.Wczytaj();
        serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch(IOException e)
        {
            System.err.println("Could not listen on port: "+PORT);
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
               
                System.out.println("\nClient connected on port "+PORT);
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
        System.out.println("Przed inicjalizacja bufferow");
       
        t = new Thread(new Runnable()
                {
            public void run()
            {
                try
                {
                    OCSPklient osw=null;
                    ObjectInputStream serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
                    System.out.println("Po inicjalizacji bufferow");
                    osw = (OCSPklient) serverInputStream.readObject();
                    System.out.println("Otrzymano obiekt od odbierajacego. Dowod(ceryfikat): " + osw.getCertificateID());
                    Wypelnij(osw);
                    System.out.println("Uzupelniono dane serwera. Dowod(nr maszyny): " + osw.getResponderId());
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String data=dateFormat.format(calendar.getTime());
                    String Time1 = data;
                    calendar.add(Calendar.DAY_OF_MONTH, 7);
                    String Time2 = data;
                    osw.setProducedAt(Time1);
                    osw.setThisUpdate(Time1);
                    osw.setNextUpdate(Time2);
                    ObjectOutputStream serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    if (osw.getCertificateID()>0)
                        {
                            boolean pom=false;
                            for (int i=0; i<n1.liczbaw; i++)
                            {
                                if (n1.wazne[i]==osw.getCertificateID())
                                {
                                    pom=true;
                                    osw.setResponseStatus("wazny");
                                    osw.CertStatus=true;
                                }
                            }
                            if (pom == false)
                            {
                                for (int i=0; i<n1.liczbau; i++)
                                {
                                    if (n1.uniewaznione[i]==osw.getCertificateID())
                                    {
                                        pom=true;
                                        osw.setResponseStatus("uniewazniony");
                                        osw.CertStatus=false;
                                    }
                                }
                            }
                            if (pom == false)
                            {
                                osw.setResponseStatus("nieznany");
                                osw.CertStatus=false;
                            }
                            serverOutputStream.writeObject(osw);
                            //serverOutputStream.close();
                            System.out.println("wyslano do klienta pelny komunikat");
                            clientSocket.close();
                            serverSocket.close();
                           
                        }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}