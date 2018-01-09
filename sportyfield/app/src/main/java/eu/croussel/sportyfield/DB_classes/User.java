package eu.croussel.sportyfield.DB_classes;

/**
 * Created by afonso on 03-12-2017.
 */

public class User {
    String _userName;
    String _pass;
    int _age;
    String _email;
    int _phone;
    int _reputation;
    String _favSport;
    String _type; //PRO USER OR AMATEUR USER
    String _photo;

    public User()
    {

    }

    public User(String u, int a, String e, int p, int r, String f, String t, String pw, String ph)
    {
        this._userName=u;
        this._age=a;
        this._email=e;
        this._phone=p;
        this._reputation=r;
        this._favSport=f;
        this._type=t;
        this._pass=pw;
        this._photo=ph;
    }

    public String getUserName()
    {
        return this._userName;
    }

    public void setUserName(String us)
    {
        this._userName=us;
    }

    public int getAge()
    {
        return this._age;
    }

    public void setAge(int a)
    {
        this._age=a;
    }

    public String getEmail()
    {
        return this._email;
    }

    public void setEmail(String e)
    {
        this._email=e;
    }

    public int getPhone()
    {
        return this._phone;
    }

    public void setPhone(int p)
    {
        this._phone=p;
    }

    public int getReputation()
    {
        return this._reputation;
    }

    public void setReputation(int r)
    {
        this._reputation=r;
    }

    public String getFavSport()
    {
        return this._favSport;
    }

    public void setFavSport(String f)
    {
        this._favSport=f;
    }

    public String getType()
    {
        return this._type;
    }

    public void setType(String t)
    {
        this._type=t;
    }

    public String getPw()
    {
        return this._pass;
    }

    public void setPw(String f)
    {
        this._pass=f;
    }

    public String getPhoto(){ return this._photo; }

    public void setPhoto(String p){ this._photo=p;}
}
