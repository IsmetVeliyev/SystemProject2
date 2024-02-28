import java.lang.reflect.Executable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.SecureRandom;
import javax.naming.ldap.SortKey;
import javax.print.DocFlavor.STRING;
import javax.xml.crypto.Data;
import java.time.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;

public class Pserv1 {

    volatile static ServerSocket clientsSocket;
    public static DataBase dataBase;
    public static Hashmp Base2;
    public static Hashmp Base3;
    public static Boolean a=false;
    public static Boolean b=false;
    volatile static Queue<Socket> requests;

    
    public static void main(String args[]) {
        try {
             
            clientsSocket = new ServerSocket(5050);
            System.out.println("Merhaba");
            requests = new LinkedList<>();
            Thread Clisten = new Thread(new ClientListener());
            Clisten.start();
            while (true) {
                if(dataBase == null)
                 {
                    try(Socket soc = new Socket("localhost",5152)){
                        ObjectOutputStream ob = new ObjectOutputStream(soc.getOutputStream());
                        ob.writeUTF("G");
                        ob.writeObject((Object)"I need DataBase");
                        ob.flush();
                        ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
                        Base2 = (Hashmp)in.readObject();
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
                        if(Base2.getTime()>Base3.getTime()){
                            dataBase.Table = Base2.getH();
                        }else{
                            dataBase.Table = Base3.getH();
                        }
                        Base2 = new Hashmp(null);
                        Base3 = new Hashmp(null);
                    }else if(a){
                        dataBase = new DataBase();
                        dataBase.Table = Base2.getH();
                        Base2 = new Hashmp(null);
                    }else if(b){
                        dataBase = new DataBase();
                        dataBase.Table = Base3.getH();
                        Base3 = new Hashmp(null);
                    }
                    }else{
                        dataBase = new DataBase();
                    }
                if (requests.peek() != null) {
                    Thread cThread = new Thread(new ClientHandler(requests.remove()));
                    cThread.start();
                }
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("server connection problem");
            dataBase = new DataBase();

        }

    }

    static class ClientListener implements Runnable {

        public void run() {
            try {
                while (true) {
                     Socket socket= clientsSocket.accept();
                    requests.add(socket);
                    if (socket != null) {
                        System.out.println("Connection established as client to :" + socket.getLocalPort() + " "
                                + socket.getLocalAddress() + " " + socket.getPort() + " " + socket.getInetAddress());
                    }
                   
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                ObjectInputStream inputObject = new ObjectInputStream(clientSocket.getInputStream());
                String dataType = inputObject.readUTF();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
              if(dataType.equals("OBJECT")){
                Musteri incoming = (Musteri) inputObject.readObject();
                System.out.println(incoming.getFullname() + " " + incoming.getgmail());
                String psw = pwdCreator();
                incoming.setPassword(psw);
                String response = dataBase.INSERT(incoming.getgmail(), incoming);
                System.out.println(response);
                dataBase.PUTS();
                System.out.println(dataBase.Timestamp());
                GetData.ObjectSend("localhost", "OBJECT","IYILESTIRME", 5152, (Object)incoming);
                GetData.ObjectSend("localhost", "OBJECT","IYILESTIRME",5678, (Object)incoming);
                out.println(response + " Temporary password is " + psw);
              }else if(dataType.equals("STRING")){
                String gmail_psw = (String) inputObject.readObject();
                System.out.println(dataBase.DELETE(gmail_psw));
                out.println(DataBase.Table.size());
                dataBase.PUTS();
              }else if(dataType.equals("IYILESTIRME")){
                Hashmp map = (Hashmp)inputObject.readObject();
                dataBase.Table=map.getH();
                dataBase.PUTS();
              }else if(dataType.equals("G")){
                String request = (String)inputObject.readObject();
                System.out.println(request);
                ObjectOutputStream outputObject = new ObjectOutputStream(clientSocket.getOutputStream());
                outputObject.writeObject(dataBase);
              }

            } catch (IOException  | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        public static String pwdCreator() {

            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
            SecureRandom random = new SecureRandom();
            StringBuilder password = new StringBuilder();

            for (int i = 0; i < 10; i++) {
                int randomIndex = random.nextInt(characters.length());
                password.append(characters.charAt(randomIndex));
            }

            return password.toString();

        }

    }
}