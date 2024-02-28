import java.io.Serializable;
import java.util.HashMap;
 
public  class Hashmp implements Serializable{
    public HashMap<String,Object> map;
    public  long Timestamp;

    public Hashmp(HashMap<String,Object> map){
        this.map=map;
    }

    public HashMap<String,Object> getH(){
       return map;
    }

    public void setTime(long Timestamp){
        this.Timestamp = Timestamp;

    }

    public long getTime(){
        return this.Timestamp;
    }
}