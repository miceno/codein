
import java.text.SimpleDateFormat

class Helper{
    
    static final String ATOM_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss"
    
    /**
     * getDate: get a Date object from a String formated as an Atom Date (yyyy-MM-dd'T'hh:mm:ss)
     * @param String    Date in format yyyy-MM-dd'T'hh:mm:ss
     * @return Date     Date object
     */
    static def getDate( String date){
        new SimpleDateFormat( ATOM_DATE_FORMAT).parse( date)
    }
}
