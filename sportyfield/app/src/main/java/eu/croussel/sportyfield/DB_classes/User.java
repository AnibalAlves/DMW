package eu.croussel.sportyfield.DB_classes;

/**
 * Created by afonso on 03-12-2017.
 */

public class User {
    private String _userName;
    private int _age;
    private String _email;
    private String _phone;
    private int _reputation;
    private String _favSport;
    private String _type; //PRO USER OR AMATEUR USER
    private String _uId ;
    public User()
    {

    }

    public User(String uId, String userName, int age, String email, String phone, int reputation, String favSport, String type)
    {
        this._uId=uId;
        this._userName=userName;
        this._age=age;
        this._email=email;
        this._phone=phone;
        this._reputation=reputation;
        this._favSport=favSport;
        this._type=type;
    }

    public void setUid(String uId){this._uId=uId;}
    public String getUid(){return this._uId;}

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

    public String getPhone()
    {
        return this._phone;
    }

    public void setPhone(String p)
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

}
