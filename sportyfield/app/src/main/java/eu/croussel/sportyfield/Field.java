package eu.croussel.sportyfield;

/**
 * Created by afonso on 29-11-2017.
 */

public class Field {
    int _id; //primary_key
    String _location; //street name or something
    double _lat;
    double _long;
    boolean _private;
    boolean _outdor;
    String _description;
    byte[] _image ;
    //something about the picture, maybe 2 pics into the res folder just to show to prof


    //Empty constructor
    public Field()
    {

    }
    //COnstructor
    public Field(String location, double lat, double longi, boolean priv, boolean outd, int id, String d, byte[] im)
    {
        this._location=location;
        this._lat=lat;
        this._long=longi;
        this._private=priv;
        this._outdor=outd;
        this._id = id;
        this._description = d;
        this._image = im ;
    }

    public int getId()
    {
        return this._id;
    }

    public void setId(int id)
    {
        this._id=id;
    }

    public String getLocation()
    {
        return this._location;
    }

    public void setLocation(String l)
    {
        this._location=l;
    }

    public double getLat()
    {
        return this._lat;
    }

    public void setLat(double lat)
    {
        this._lat=lat;
    }

    public double getLong()
    {
        return this._long;
    }

    public void setLong(double lo)
    {
        this._long=lo;
    }

    public boolean getPriv()
    {
        return this._private;
    }

    public void setPriv(boolean p)
    {
        this._private=p;
    }

    public boolean getOut()
    {
        return this._outdor;
    }

    public void setOut(boolean o)
    {
        this._outdor=o;
    }

    public String getDescription()
    {
        return this._description;
    }
    public String getComment() {
        String res = new String() ;

        if(this._outdor) res.concat("Outdour ");
        else res.concat("Indour :");
        res.concat(this._description);
        res.concat(" field.");
        return res;
    }


    public void setDescription(String d)
    {
        this._description=d;
    }

    public void setImage(byte[] image) { this._image = image ;}
    public byte[] getImage(){return this._image ;}
}

