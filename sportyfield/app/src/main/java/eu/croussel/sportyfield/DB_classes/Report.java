package eu.croussel.sportyfield.DB_classes;

/**
 * Created by afonso on 29-11-2017.
 */

public class Report {
    private String _descr;
    private int _fieldId; //secondary key that connects to Field
    private int _reportId; //descr number primary key must be incremented automatically
    private String _date;
    private String _userName;
    private byte[] _reportImage ;

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // String date = sdf.format(new Date());

    //empty
    public Report()
    {

    }
    //contructor
    public Report(String descr, int fieldId, String userName, byte[] reportImage)
    {
        this._descr=descr;
        this._fieldId=fieldId;
        this._userName=userName;
        this._reportImage=reportImage;
    }

    public String getDescr()
    {
        return this._descr;
    }

    public void setDescr(String descr)
    {
        this._descr=descr;
    }

    public int getId()
    {
        return this._fieldId;
    }
    public void setId(int fieldId)
    {
        this._fieldId=fieldId;
    }

    public int getreportId()
    {
        return this._reportId;
    }

    public void setreportId(int reportId)
    {
        this._reportId=reportId;
    }

    public String getDate()
    {
        return this._date;
    }

    public void setDate(String date)
    {
        this._date=date;
    }

    public String getUserName()
    {
        return this._userName;
    }

    public void setUserName(String userName)
    {
        this._userName=userName;
    }

    public void setRepImage(byte[] reportImage) { this._reportImage = reportImage ;}
    public byte[] getRepImage(){return this._reportImage ;}
}
