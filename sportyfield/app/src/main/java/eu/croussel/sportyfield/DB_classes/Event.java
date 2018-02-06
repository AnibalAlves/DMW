package eu.croussel.sportyfield.DB_classes;

import java.util.Date;
import java.util.List;

/**
 * Created by root on 04/02/18.
 */

public class Event implements Comparable<Event> {
    private int _eventId;
    private String _organizerUID;
    private int _fieldId;
    private String _eventName;
    private Date _eventDate;
    private List<String> _eventPlayers;
    private String _eventDescription;


    public Event(){}
    public Event(int id, String org, int fid, String name, Date d, List<String> players, String descri)
    {
        this._eventId=id;
        this._organizerUID=org;
        this._fieldId=fid;
        this._eventName=name;
        this._eventDate=d;
        this._eventPlayers=players;
        this._eventDescription=descri;
    }
    @Override
    public int compareTo(Event o) {
        return this.getEventDate().compareTo(o.getEventDate());
    }
    public void set_organizerUID(String organizerUID){_organizerUID=organizerUID;}
    public String get_organizerUID(){return this._organizerUID;}
    public int getEventId(){return this._eventId;}
    public void setEventId(int eventId){this._eventId = eventId;}
    public int getFieldId(){return this._fieldId;}
    public void setFieldId(int i){this._fieldId=i;}
    public String getEventName() { return this._eventName;}
    public void setEventName(String n){this._eventName=n;}
    public Date getEventDate(){return this._eventDate;}
    public void setEventDate(Date d){ this._eventDate=d;}
    public List<String> getEventPlayers(){return this._eventPlayers;}
    public void setEventPlayers(List<String> p){this._eventPlayers=p;}
    public String getEventDescription(){return this._eventDescription;}
    public void setEventDescription(String d){this._eventDescription=d;}
}
