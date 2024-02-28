import java.io.Serializable;

public class Musteri implements Serializable {

    private String Isim;
    private String Soyisim;
    private String gmail;
    private String password;


    Musteri(String Isim,String Soyisim,String gmail){
        this.Isim=Isim;
        this.Soyisim=Soyisim;
        this.gmail = gmail;
    }
    
    public String getFullname(){
        return this.Isim + " " + this.Soyisim;
    }

    public void setFullname(String Isim,String Soyisim){
        this.Isim=Isim;
        this.Soyisim=Soyisim;
    }
    public String getgmail(){
        return this.gmail;
    }

    public String getPassword(){
        return this.password;
    }
    public void setPassword(String pwd){
        this.password=pwd;
        
    }

}
