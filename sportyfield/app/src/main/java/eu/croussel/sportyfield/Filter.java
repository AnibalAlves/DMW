package eu.croussel.sportyfield;

import java.io.Serializable;

/**
 * Created by root on 09/12/17.
 */

public class Filter implements Serializable {
    private boolean _outdoor = false ;
    private boolean _indoor = false ;
    private boolean _private = false ;
    private boolean _public = false ;

    private String _fieldType ;

    public Filter(boolean outdoor, boolean indoor, boolean priv, boolean pub, String fieldType){
        this._fieldType = fieldType;
        this._public = pub;
        this._indoor = indoor;
        this._outdoor = outdoor;
        this._private = priv;
    }

    public boolean getPrivate(){
        return _private;
    }
    public boolean getPublic(){
        return _public;
    }
    public boolean getOutdoor(){
        return _outdoor;
    }
    public boolean getIndoor(){
        return _indoor;
    }
    public String getFieldType(){
        return _fieldType;
    }
}
