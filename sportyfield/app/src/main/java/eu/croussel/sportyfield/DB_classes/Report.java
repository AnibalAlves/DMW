package eu.croussel.sportyfield.DB_classes;

/**
 * Created by afonso on 29-11-2017.
 */

public class Report {
    String _descr;
    int _fieldId; //secondary key that connects to Field
    int _number; //descr number primary key must be incremented automatically
    String _date;
    String _userName;
    byte[] _reportImage ;

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // String date = sdf.format(new Date());

    //empty
    public Report()
    {

    }
    //contructor
    public Report(String descr, int id, String u, byte[] im)
    {
        this._descr=descr;
        this._fieldId=id;
        this._userName=u;
        this._reportImage=im;
    }

    public String getDescr()
    {
        return this._descr;
    }

    public void setDescr(String d)
    {
        this._descr=d;
    }

    public int getId()
    {
        return this._fieldId;
    }
    public void setId(int i)
    {
        this._fieldId=i;
    }

    public int getNumber()
    {
        return this._number;
    }

    public void setNumber(int n)
    {
        this._number=n;
    }

    public String getDate()
    {
        return this._date;
    }

    public void setDate(String d)
    {
        this._date=d;
    }

    public String getUserName()
    {
        return this._userName;
    }

    public void setUserName(String u)
    {
        this._userName=u;
    }

    public void setRepImage(byte[] image) { this._reportImage = image ;}
    public byte[] getRepImage(){return this._reportImage ;}
}
