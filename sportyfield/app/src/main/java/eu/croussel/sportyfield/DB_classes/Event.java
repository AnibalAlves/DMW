package eu.croussel.sportyfield.DB_classes;

import java.util.Date;
import java.util.List;

/**
 * Created by root on 04/02/18.
 */

public class Event {
    private int _eventId;
    private String _organizerUID;
    private int _fieldId;
    private String _eventName;
    private Date _eventDate;
    private List<User> _eventPlayers;
    private String _eventDescription;

    public Event(int id, String org, int fid, String name, Date d, List<User> players, String descri)
    {
        this._eventId=id;
        this._organizerUID=org;
        this._fieldId=fid;
        this._eventName=name;
        this._eventDate=d;
        this._eventPlayers=players;
        this._eventDescription=descri;
    }

    public void set_organizerUID(String organizerUID){_organizerUID=organizerUID;}
    public String get_organizerUID(){return this._organizerUID;}
    public int getEventId(){return this._eventId;}
    public void setEventId(int eventId){this._eventId = eventId;}
    public int getFielId(){return this._fieldId;}
    public void setFieldId(int i){this._fieldId=i;}
    public String getEventName() { return this._eventName;}
    public void setEventName(String n){this._eventName=n;}
    public Date getEventDate(){return this._eventDate;}
    public void setEventDate(Date d){ this._eventDate=d;}
    public List<User> getEventPlayers(){return this._eventPlayers;}
    public void setEventPlayers(List<User> p){this._eventPlayers=p;}
    public String getEventDescription(){return this._eventDescription;}
    public void setEventDescription(String d){this._eventDescription=d;}
}
