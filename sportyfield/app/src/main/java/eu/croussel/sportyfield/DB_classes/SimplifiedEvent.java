package eu.croussel.sportyfield.DB_classes;

import java.util.Date;
import java.util.List;

/**
 * Created by root on 04/02/18.
 */

public class SimplifiedEvent implements Comparable<SimplifiedEvent>  {
        private int _eventId;
        private int _fieldId;
        private String _eventName;
        private Date _eventDate;
        private String _eventDescription;
        public SimplifiedEvent(){};
        public SimplifiedEvent(Event event){
            _eventId = event.getEventId();
            _fieldId = event.getFieldId();
            _eventName = event.getEventName();
            _eventDate = event.getEventDate();
            _eventDescription = event.getEventDescription();
        }

        public SimplifiedEvent(int id, int fid, String name, Date d, String descri)
        {
            this._eventId=id;
            this._fieldId=fid;
            this._eventName=name;
            this._eventDate=d;
            this._eventDescription=descri;
        }
    @Override
    public int compareTo(SimplifiedEvent o) {
        return this.getEventDate().compareTo(o.getEventDate());
    }
        public int getEventId(){return this._eventId;}
        public void setEventId(int eventId){this._eventId = eventId;}
        public int getFieldId(){return this._fieldId;}
        public void setFieldId(int i){this._fieldId=i;}
        public String getEventName() { return this._eventName;}
        public void setEventName(String n){this._eventName=n;}
        public Date getEventDate(){return this._eventDate;}
        public void setEventDate(Date d){ this._eventDate=d;}
        public String getEventDescription(){return this._eventDescription;}
        public void setEventDescription(String d){this._eventDescription=d;}
    }

