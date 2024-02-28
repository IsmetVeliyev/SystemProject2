import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.io.Serializable;

import javax.naming.ldap.HasControls;
import javax.print.DocFlavor.STRING;
import javax.swing.plaf.synth.SynthScrollBarUI;
import javax.xml.crypto.Data;

public class Pserv2{

    public volatile static ServerSocket clientsSocket;
    public static DataBase dataBase;
    static HashMap<String,Boolean> arrayListOnline = new HashMap<>();
    public static Boolean a=false;
    public static Boolean b=false;
    public static Hashmp Base1;
    public static Hashmp Base3;
    volatile static Queue<Socket> requests;


    public static void main(String args[]){

        try{
            clientsSocket = new ServerSocket(5152);
            System.out.println("Merhaba");
            requests = new LinkedList<>();
            Thread Clisten = new Thread(new ClientListener());
            Clisten.start();
            //dataBase =  new DataBase();
            while (true) {
                try{
                if(dataBase==null)
                {
                    try(Socket soc = new Socket("localhost",5050)){
                        ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                        ob.writeUTF("G");
                        ob.writeObject((Object)"I need DataBase");
                        ob.flush();
                        ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
                        Base1 = (Hashmp)in.readObject();
                        soc.close();                       
                        a=true;
                    }catch(Exception e){
                        a=false;
                        System.out.println("Server1 is not listening");
                      
                    }

                    try(Socket soc1 = new Socket("localhost",5678)){
                        ObjectOutputStream ob1 = new ObjectOutputStream(soc1.getOutputStream());
                        ob1.writeUTF("G");
                        ob1.writeObject("I need DataBase");
                        ob1.flush();
                        ObjectInputStream in1 = new ObjectInputStream(soc1.getInputStream());
                        Base3 = (Hashmp)in1.readObject();
                        soc1.close();
                        b=true;
                    }catch(Exception e){
                        b=false;
                        System.out.println("Server3 not listening");
                        if(!a)
                        dataBase = new DataBase();
                    }
                    if(a&&b){
                        dataBase = new DataBase();
                        if(Base1.getTime()>Base3.getTime()){
                            dataBase.Table = Base1.getH();
                        }else{
                            dataBase.Table = Base3.getH();
                        }
                        Base1 = new Hashmp(null);
                        Base3 = new Hashmp(null);
                    }else if(a){
                        dataBase = new DataBase();
                        dataBase.Table = Base1.getH();
                        Base1 = new Hashmp(null);
                    }else if(b){
                        dataBase = new DataBase();
                        dataBase.Table = Base3.getH();
                        Base3 = new Hashmp(null);
                    }
                    }else{
                        dataBase = new DataBase();
                    }

                }
                catch(Exception e){
                    System.out.println("server connection problem");
                    dataBase = new DataBase();

                }
                if(requests.peek()!=null){
                    Thread cThread = new Thread(new ClientHandler(requests.remove()));
                cThread.run();
            }
        }}catch(Exception e){
            e.printStackTrace();
        }
    }



    

    static class ClientListener implements Runnable{

        public void run(){
            try{
                while (true) {
                    Socket socket = clientsSocket.accept();
                    requests.add(socket);
                    if(socket!=null){
                        System.out.println("Connection established as client to :"+socket.getLocalPort()+" " + socket.getLocalAddress() + " " + socket.getPort()+ " " + socket.getInetAddress());
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    static class ClientHandler implements Runnable{
        private Socket clientSocket;
        public  static String gmail_pwd;
        static String response;
        
    
       public  ClientHandler(Socket clienSocket){
        this.clientSocket=clienSocket;
       }
       
       public void run(){
           try{
               PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
               ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
               BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
               String dataType = objectInputStream.readUTF();
               System.out.println(dataType);
               
               if(dataType.equals("OBJECT")){
                   Musteri incoming  = (Musteri) objectInputStream.readObject();
                   dataBase.INSERT(incoming.getgmail(), incoming);
                   dataBase.PUTS();
                   System.out.println(dataBase.Timestamp());
                   out.println(DataBase.Table.size());
                }else if(dataType.equals("STRING")){
                    String gmail_psw = (String)objectInputStream.readObject();
                    System.out.println(dataBase.DELETE(gmail_psw));
                    dataBase.PUTS();
                    String gp[] = gmail_psw.split(" ");
                    if(arrayListOnline.get(gp[0])!=null)
                        arrayListOnline.remove(gp[0]);
                    out.println(DataBase.Table.size());
                }else if(dataType.equals("G")){
                    String request = (String)objectInputStream.readObject();
                    System.out.println(request);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                   Hashmp mp2 = new Hashmp(dataBase.Table);
                   mp2.setTime(dataBase.Timestamp());
                    objectOutputStream.writeObject(mp2);
                }
                else if(dataType.equals("Cl_STRING")){
                    String gmail_psw = (String)objectInputStream.readObject();
                    String gp[] = gmail_psw.split(" ");
                    String response =dataBase.MATCH(gmail_psw);
                    if(response.contains("Successfully")){
                        
                        if(arrayListOnline.get(gp[0])==null){
                           arrayListOnline.put(gp[0],true);
                           out.println("Online users "+arrayListOnline.size() +" "+response);
                        }else{
                            out.println(response);
                        }
                    }else{
                        out.println(response);

                    }
                     String logout;
                    
                        logout = in.readLine();
                        System.out.println(logout);
                       if(logout.equals("CIKIS")){
                            arrayListOnline.put(gp[0],false);
                            out.println("CIKIS yapildi");
                       }
                }else if(dataType.equals("IYILESTIRME")){
                    Hashmp map = (Hashmp)objectInputStream.readObject();
                    dataBase.Table=map.getH();
                    dataBase.PUTS();
                }
        }catch(Exception e){
            e.printStackTrace();
        }
       }

    }
}