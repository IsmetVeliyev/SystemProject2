import java.io.*;
import java.util.*;
import java.net.Socket;
public class Pclnt{
    public static Socket responseSocket;
    public static void main(String args[]){
        try{

        
        Scanner scanner = new Scanner(System.in);
        while(true){
            if(responseSocket == null || responseSocket.isClosed() || !responseSocket.isConnected()){
                String opr = scanner.nextLine();
                if(opr.equals("ABONOL")){
                    responseSocket = new Socket("localhost",5050);
                    Thread sender = new Thread(new SenderFS(responseSocket,opr));
                    sender.start();
                    sender.join();
                }else if(opr.equals("GIRIS")||opr.equals("CIKIS")){
                    responseSocket = new Socket("localhost",5152);
                    Thread sender  = new Thread(new SenderFS(responseSocket,opr));
                    sender.start();
                    sender.join();
                }else if(opr.equals("ABONSIL")){
                    responseSocket = new Socket("localhost",5678);
                    Thread sender = new Thread(new SenderFS(responseSocket,opr));
                    sender.start();
                    sender.join();
                }
            }
        }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}


 class SenderFS implements Runnable{
    private Socket socket;
    private String opr;

    public SenderFS(Socket socket,String opr){
        this.socket=socket;
        this.opr=opr;
    }

    public void run(){
        try{
            Scanner scanner = new Scanner(System.in);
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out  = new PrintWriter(socket.getOutputStream(),true);
            ObjectOutputStream object = new ObjectOutputStream(socket.getOutputStream());
          //  ObjectInputStream objectin = new ObjectInputStream(socket.getInputStream());
            if(opr.equals("ABONOL")){
                Musteri musteri = new Musteri(scanner.nextLine(),scanner.nextLine(),scanner.nextLine());
                object.writeUTF("OBJECT");
                object.writeObject(musteri);
                System.out.println(in.readLine());
                socket.close();
                
            }else if(opr.equals("GIRIS") || opr.equals("CIKIS")){
               switch (opr) {
                case "GIRIS":
                   // ObjectInputStream objectin = new ObjectInputStream(socket.getInputStream());
                    object.writeUTF("Cl_STRING");
                    object.writeObject(scanner.nextLine()+" "+scanner.nextLine());
                    System.out.println(in.readLine());
                    out.println(scanner.nextLine());
                     String response=in.readLine();
                     System.out.println(response);
                    // while (!response.equals("GIRIS")) {
                    //     out.println(scanner.nextLine());
                    //     response = in.readLine();
                    //     System.out.println(response);
                    // }
                    

                    object.flush();
                    socket.close();
               
                default:
                    break;
               }

            }else if(opr.equals("ABONSIL")){
                //out.println(scanner.nextLine()+" " + scanner.nextLine());
               // System.out.println(in.readLine());
                object.writeUTF("STRING");
                object.writeObject(scanner.nextLine()+" "+scanner.nextLine());
                socket.close();
            }else{
                System.out.println("No operation like that");
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}