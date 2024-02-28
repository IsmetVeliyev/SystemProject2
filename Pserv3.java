import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.time.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.io.Serializable;
import javax.sound.midi.Synthesizer;
import javax.xml.crypto.Data;

public class Pserv3{
    volatile static ServerSocket clientsSocket;
    volatile static Socket client;
    volatile static Socket responseSocket;
    public static DataBase dataBase;
    public static Boolean a;
    public static Boolean b;
    public static Hashmp Base1;
    public static Hashmp Base2;
    volatile static Queue<Socket> requests;

    public static void main(String args[]){
        try{
            clientsSocket = new ServerSocket(5678);
            System.out.println("Merhaba");
            requests = new LinkedList<>();
            Thread Clistener = new Thread(new ClientListener());
            Clistener.start();
           // dataBase =  new DataBase();
            while(true){
                if(dataBase==null){
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

                    try(Socket soc1 = new Socket("localhost",5152)){
                        ObjectOutputStream ob1 = new ObjectOutputStream(soc1.getOutputStream());
                        ob1.writeUTF("G");
                        ob1.writeObject("I need DataBase");
                        ob1.flush();
                        ObjectInputStream in1 = new ObjectInputStream(soc1.getInputStream());
                        Base2 = (Hashmp)in1.readObject();
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
                        if(Base1.getTime()>Base2.getTime()){
                            dataBase.Table = Base1.getH();
                        }else{
                            dataBase.Table = Base2.getH();
                        }
                        Base1 = new Hashmp(null);
                        Base2 = new Hashmp(null);
                    }else if(a){
                        dataBase = new DataBase();
                        dataBase.Table = Base1.getH();
                        Base1 = new Hashmp(null);
                    }else if(b){
                        dataBase = new DataBase();
                        dataBase.Table = Base2.getH();
                        Base2 = new Hashmp(null);
                    }
                    }else{
                        dataBase = new DataBase();
                    }
                
                if(requests.peek()!=null){
                    Thread cThread = new Thread(new ClientHandler(requests.remove()));
                    cThread.start();
                    client=null;
                }
            }
            }
        catch(Exception e){
            e.printStackTrace();
        }

    }



    static class ClientListener implements Runnable{
        public void run(){
            try{
                while(true){
                 Socket socket = clientsSocket.accept();
                 requests.add(socket);
                 if(socket!=null)
                    System.out.println("Connection established as client to :"+socket.getLocalPort()+" " + socket.getLocalAddress() + " " + socket.getPort()+ " " + socket.getInetAddress());
                }
            }catch(Exception e){
                e.printStackTrace();
            }
    }
 }


 static class ClientHandler implements Runnable{
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public void run(){
        try{
            //dataBase =  new DataBase();
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
            String dataType = objectInputStream.readUTF();
            if(dataType.equals("OBJECT")){
                Musteri incoming  = (Musteri) objectInputStream.readObject();
               dataBase.INSERT(incoming.getgmail(), incoming);
                dataBase.PUTS();      
                out.println(dataBase.Table.size());

             } else if(dataType.equals("STRING")){
                    String gmail_psw  = (String) objectInputStream.readObject();
                    System.out.println(dataBase.DELETE(gmail_psw));
                    dataBase.PUTS();
                    GetData.ObjectSend("localhost", "STRING","IYILESTIRME",5050,(Object)gmail_psw);
                    GetData.ObjectSend("localhost", "STRING","IYILESTIRME",5152,(Object)gmail_psw);
                }else if(dataType.equals("IYILESTIRME")){
                Hashmp map = (Hashmp)objectInputStream.readObject();
                dataBase.Table=map.getH();
                dataBase.PUTS();
                }else if(dataType.equals("G")){
                    String request = (String)objectInputStream.readObject();
                    System.out.println(request);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    Hashmp mp3 = new Hashmp(dataBase.Table);
                    mp3.setTime(dataBase.Timestamp());
                    objectOutputStream.writeObject(mp3);
                }
                
                
        }catch(Exception e){
            e.printStackTrace();
        }
    }


 }


}