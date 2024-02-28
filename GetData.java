import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

class GetData{
    public static void ObjectSend(String HOST,String UTF1,String UTF2,int PORT,Object object){
        try(Socket socket = new Socket(HOST,PORT);
            ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            )
        {
            objectOutputStream1.writeUTF(UTF1);
            objectOutputStream1.writeObject(object);
            objectOutputStream1.flush();
            int a=Integer.parseInt(in.readLine());
            socket.close();
            if(a<DataBase.Table.size()){
                Socket socket1 = new Socket(HOST,PORT);
                ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(socket1.getOutputStream());
                objectOutputStream2.writeUTF(UTF2);
                // HashMap<String,Object> map = dataBase.Table;
                Hashmp mp = new Hashmp(DataBase.Table);
                objectOutputStream2.writeObject(mp);
                objectOutputStream2.flush();
                socket1.close();
            }
        }catch(Exception e){
            System.out.println("Not Ready " +PORT);
        }
    }
}