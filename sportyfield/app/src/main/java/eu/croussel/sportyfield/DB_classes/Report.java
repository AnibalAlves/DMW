package eu.croussel.sportyfield.DB_classes;

import java.util.Date;

/**
 * Created by afonso on 29-11-2017.
 */

public class Report {
    private String _descr;
    private int _fieldId; //secondary key that connects to Field
    private int _reportId; //descr number primary key must be incremented automatically
    private Date _date;
    private String _uId;
    private byte[] _reportImage ;

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // String date = sdf.format(new Date());

    //empty
    public Report()
    {

    }
    //contructor
    public Report(String descr, int fieldId, String uId, Date date, byte[] reportImage)
    {
        this._date=date;
        this._descr=descr;
        this._fieldId=fieldId;
        this._uId=uId;
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

    public Date getDate()
    {
        return this._date;
    }

    public void setDate(Date date)
    {
        this._date=date;
    }

    public String getuId()
    {
        return this._uId;
    }
    public void setuId(String uId)
    {
        this._uId=uId;
    }

    public void setRepImage(byte[] reportImage) { this._reportImage = reportImage ;}
    public byte[] getRepImage(){return this._reportImage ;}
}
