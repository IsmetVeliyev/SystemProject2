import java.util.HashMap;
import java.io.Serializable;
import java.time.Instant;
import java.time.*;

public class DataBase implements Serializable{
    public static HashMap<String,Object> Table = new HashMap<>();
    public static long Timestamp;
    public String INSERT(String gmail,Object object){
        Instant.now().toEpochMilli();
        Table.put(gmail,object);
        Timestamp=Instant.now().toEpochMilli();
        return gmail + " Successfulyy Registered";

    }

    public Musteri SELECT(String gmail){
        if(Table.get(gmail)!=null){
            return (Musteri)Table.get(gmail);
        }
        return new Musteri(null,null,null);
    }

    public String DELETE(String gmail_psw){
        String gp[] = gmail_psw.split(" ");
       if(Table.get(gp[0])!=null){
        Musteri musteri =(Musteri) Table.get(gp[0]);
        if(musteri.getPassword().equals(gp[1])){
            Table.remove(gp[0]);
            Timestamp=Instant.now().toEpochMilli();
            return gp[0] + "Successfully Removed";
        }
       }
       return "Threre is no Register related " + gp[0];

    }

    public String MATCH(String gmail_psw){
        String gp[] = gmail_psw.split(" ");
        if(Table.get(gp[0])!=null){
        Musteri musteri =(Musteri) Table.get(gp[0]);
            if(musteri.getPassword().equals(gp[1]))
            return "Successfully Logged in";
        }
        return "Not logged";
    }
    public  void PUTS(){
        System.out.println(Table);

    }

    public  long Timestamp(){
        return Timestamp;
    }

}

