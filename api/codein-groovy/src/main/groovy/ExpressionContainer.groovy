
import org.apache.log4j.Logger

/*
 * This class is an expression container that reads all the expressions
 * available in Codein
 * Expressions are a variable name, an xpath expression and a regexp
 *   variable -> xpath -> regexp
 * 
 */
class ExpressionContainer{
    public static final String XPATH   = ''
    public static final String REGEXP  = ''

    private static final String XPATH_DEFAULT   = '/'
    private static final String REGULAR_DEFAULT = '*'

    private Logger log= Logger.getLogger( getClass().getName())

    Map expressions

    ExpressionContainer( String filename){
         read( filename)
    }

    /* Loads the expressions from a file */
    public read( String filename){
        log.debug "about to read file '$filename'"
        expressions= [:]
        new File( filename).withReader{ r->
             r.splitEachLine( '->'){ 
                log.debug "vector=$it"
                expressions[ it[0]?.trim()]= 
                    [ XPATH   : it[ 1]?.trim() ?: XPATH_DEFAULT,
                      REGEXP  : it[ 2]?.trim() ?: REGULAR_DEFAULT]
             }// splitEachLine
        }
        log.debug "expressions=$expressions"
    }
}

