package eu.croussel.sportyfield;

/**
 * Created by afonso on 03-12-2017.
 */

public class user {
    String _userName;
    int _age;
    String _email;
    int _phone;
    int _reputation;
    String _favSport;

    public user()
    {

    }

    public user(String u, int a, String e,int p, int r, String f)
    {
        this._userName=u;
        this._age=a;
        this._email=e;
        this._phone=p;
        this._reputation=r;
        this._favSport=f;
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

    public int getReputatio()
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
}
