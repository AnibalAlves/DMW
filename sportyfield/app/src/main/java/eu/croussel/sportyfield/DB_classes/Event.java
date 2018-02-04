package eu.croussel.sportyfield.DB_classes;

/**
 * Created by root on 04/02/18.
 */

public class Event {
    private int _eventId;
    private String _organizerUID;

    public void set_organizerUID(String organizerUID){_organizerUID=organizerUID;}
    public String get_organizerUID(){return this._organizerUID;}
    public int getEventId(){return this._eventId;}
    public void setEventId(int eventId){this._eventId = eventId;}
}
