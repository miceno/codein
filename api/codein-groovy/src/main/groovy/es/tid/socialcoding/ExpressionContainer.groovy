
package es.tid.socialcoding

import groovy.util.logging.Log4j
import groovy.xml.dom.DOMCategory
import static javax.xml.xpath.XPathConstants.*

/*
 * This class is an expression container that reads all the expressions
 * available in Codein
 * Expressions are a variable name, an xpath expression and a regexp
 *   variable -> xpath -> regexp
 * 
 */
 
@Log4j
class ExpressionContainer{
    public static final String XPATH   = ''
    public static final String REGEXP  = ''

    private static final String XPATH_DEFAULT   = '/'
    private static final String REGULAR_DEFAULT = '*'

    Map expressions
    def filterExpression

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
    
    /**
     * A closure that parses each element with a parser
     * @param entries       List of SyndEntry to parse (SyndEntry means an RSS Item or an Atom Entry)
     * @return list         List of entryModels
     */
    def applyAll( syndEntries){
        
        // For each entry, apply all the expressions
        syndEntries.collect{ syndEntry ->
            log.debug "About to apply parser to entry: $syndEntry"
            // Apply each expression to the entries
            apply( syndEntry)
        }
    }
    
    /**
     * A closure that parses each element with a parser
     * @param syndEntry     Entry to parse (SyndEntry means an RSS Item or an Atom Entry)
     * @return map          Map of an entry
     */
    def apply( syndEntry){
        
        log.debug "About to apply parser to entry: $syndEntry"
        // Apply each expression to the entries
        expressions.inject([:]){ mapa, expression ->
            log.debug "Begin parsing feed with ${expression.value}"
            def result
            use (DOMCategory) {
                result= syndEntry.xpath( expression.value.XPATH, NODESET)
                /* Print each element */
                log.debug "Parsing ${expression.value.XPATH}: ${result.size()}= ${result.text()}"
                // Results are acumulated in this variable
                mapa+= [ (expression.key): result.text() ]
            }     
        }// inject
    }
    
}

