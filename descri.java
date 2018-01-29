package eu.croussel.sportyfield;

/**
 * Created by afonso on 29-11-2017.
 */

public class descri {
    String _descr;
    int _fieldId; //secondary key that connects to field
    int _number; //descr number primary key must be incremented automatically
    String _date;

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // String date = sdf.format(new Date());

    //empty
    public descri()
    {

    }
    //contructor
    public descri(String descr,int id, int number, String date)
    {
        this._descr=descr;
        this._fieldId=id;
        this._number=number;
        this._date=date;
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
}
