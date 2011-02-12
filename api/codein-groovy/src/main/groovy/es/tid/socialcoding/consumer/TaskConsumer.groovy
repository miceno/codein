/**
* A consumer of tasks that consumes all the message in a queue
*/

import org.apache.log4j.*

import groovy.xml.*
import static javax.xml.xpath.XPathConstants.*
import javax.xml.xpath.*
import groovy.xml.dom.DOMCategory
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.DocumentBuilder
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.w3c.dom.Element 
import org.w3c.dom.Node

import java.text.SimpleDateFormat

import groovy.jms.*
import javax.jms.Message

import es.tid.socialcoding.*
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.consumer.*
import es.tid.socialcoding.producer.*

class TaskProcessorException extends Exception{
    final String producerConfigFile= config.consumer.producer_config_file
    log.debug "Reading Producer configuration from $producerConfigFile"
    errorQueue= new Producer( producerConfigFile)
    
    String errorText= "Unable to download $url: ${e.getClass().getName()}"
    log.error errorText
    Map map=[:]
    map[ 'errorText']= errorText
    
    map = msg.toMap()
    
    log.debug "mapa a enviar ${ map.collect{ k,v -> "clave $k: $v" } }"
    Message errorMsg= errorQueue.createMessage( MessageType.ERROR_MESSAGE, map)
    errorQueue.sendMessage( errorMsg)
    
}

@Log4j
class TaskProcessor{

    TaskProcessor( task){
        // Read configuration
        def config= SocialCodingConfig.newInstance().config
        Level log_level= Level.toLevel( config.root.log_level.toString())
            log.setLevel( log_level)
            log.info "Log level set to ${log_level}"

        // Default XPATH expression
        final String XPATH_EXPRESSION = config.consumer.xpath_entry_selector

        final String consumerConfigFile= config.consumer.consumer_config_file
        final String MESSAGE_TYPE= config.consumer.message_type

        log.debug "Reading Consumer configuration from $consumerConfigFile"
        c= new Consumer( consumerConfigFile)

        // Create UserFeedDAO
        entryTable= new EntryDAO( db: new DbHelper().db)
    }


    /**
     * Read a Task
     * @param waitTime Time to wait for a message in idle
     */
    def readTask( waitTime){
        def result= [:]
        log.info( "Esperando recibir mensaje... $waitTime")

        // Read a message
        msg = c.getNextMessage( waitTime)

        if( !msg) {
           log.debug "No message received"
        }
        else{
        
            messageType= msg.getString( CodeinJMS.MSG_TYPE)
            log.debug( "Mensaje de tipo: $messageType")
            if( messageType != MESSAGE_TYPE)
            {
               log.info "Do not process this kind of message: $messageType != ${MESSAGE_TYPE}"
               log.info "TODO: Insert the message again"
            }
            else
            {   
                result= msg.toMap()
            }
        }
        log.debug "Received message $result"

        return result
    }

    /**
     * Read a feed from a URL
     * @param url URL of the feed
     * @return Node DOM document
     */
    def readFeed( url){
        def result= null
    
        // Get XML feed
        try{
            feed = DOMBuilder.parse(
                new InputStreamReader( url.toURL().openStream()),
                false,
                false)
            result= feed.documentElement    
        }
        catch(e){
            new TaskProcessorException
        }

        return result
    }

    /**
     * Process a task
     * @param task Task to process, Map of values, includes the owner user and the url to parse
     */
    def processTask( def task){
        def url= task.url
        log.info "Start to parse $url"
    
        /**
         * Read the feed
         */
        def doc= readFeed( url)

        /**
         * Get a Parser
         */
        def parser= getParser( feed, task )

        /**
         * Parse the feed
         * @param feed DOM representation of the feed
         * @param parser Parser to process the feed
         * @return entries  Set of row for the Entry table
         */

        def result= [:]
        use( DOMCategory){
            // First, obtain the nodes relevant 
            log.debug( "Getting all nodes that matches ${parser.filterExpression}")
            nodes= doc.xpath( parser.filterExpression, NODESET)

            // For each node, extract all the expressions
            result= parser.apply( nodes)
            
            dbgStr= result.collect{ 
                it.collect{ """${it.key} =${it.value.substring( 0,  
                                      it.value.length()<100? it.value.length():100)}""" 
                          }.join( ",\n\t") 
            }.join( "\n" + "Entry".center( 20, '-') + "\n")
            log.debug( "Matching nodes\n" + dbgStr)
            log.debug( "END".center( 20, '*') )
        }// use



        /**
         * manipulate the entries to make some filtering and adaptation
         * @param entries input entries
         * @return entries Output entries
         */

        // Format Dates
        listaCamposFecha= config.consumer.transform_date_fields
        log.debug( "Transform dates of fields: $listaCamposFecha") 
        transformDate( listaCamposFecha, result)

        log.debug( "after transform start".center( 40, '*') )
        result.each{ it.each{ log.debug it }}
        log.debug( "end".center( 20, '*') )

        /**
         * Insert entries in database
         */

        updatedRecords= 0
        insertedRecords= 0

        // Insert results in database
        log.info( "Inserting results in database") 

        // Insert in the Database
        result.each( addOrInsert.curry( entryTable, 'id'))

        log.info "Total matching nodes: ${result.size()}"
        log.info "Total new entries: ${insertedRecords - updatedRecords}"
        println "finished parsing $url"


    }// processTask


String showMsg( def msg){
   result= "mensaje recibido : $msg\n"
   dbgStr= msg.getPropertyNames().collect{ it }.join(",\n\t")
   result+= "Properties: $dbgStr\n" 
   dbgStr= msg.getMapNames().collect{ 
        "name $it = ${msg.getObject( it ).toString()}" 
   }.join(",\n\t")
   result+= "Names: $dbgStr\n"
   return result
}

def reloadConfig( config, log)
{
    config= new SocialCodingConfig().config
    config.each{ log.debug( "Configuration: $it") }
    log_level= Level.toLevel( config.root.log_level.toString())
    log.info "Log level set to ${log_level}"
    log.setLevel( log_level)
}

def transformDate( list, elements){      
    elements.each{ k, v ->
        if ( list.isCase( k))
        {
            v= ( !v ? 0 : 
                v= new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                .parse( element.value)
                .getTime() )
        }
    }
}

def transformDates= transformDatesList.curry( listaCamposFecha)


   def exists= {t, key, record ->
   String query= """
    select * from $tablename where $key = '${record.get( key)}'
   """
       log.debug "Query to exists: '$query'"
       result= t.firstRow( query)
       log.debug "Result : '$result'"
       return result?."$key"
   }



/**
 * Extract all the properties of a Message into a Map
 */
def toMap( msg){
    def map= [:]
    msg.getMapNames().each{ 
        log.debug( "name $it = ${msg.getObject( it ).toString()}" )
        map[ (it)]= msg.getObject( it )
    }
    return map
}

/**
 * Add or insert records in the Entry table
 */
def addOrInsert = { t, key, record ->
        if( exists( t, key, record))
        {
            log.debug "register already exists in $tablename: $record"
        String deleteStm="""
delete from $tablename where $key = '${record.get( key)}'
"""
            log.debug "querying: $deleteStm"
             t.execute( deleteStm)
            updatedRecords++
        }
        log.debug "adding row to $tablename: $record"
        t.add( record) 
        insertedRecords++
}


// A closure that parses each element with a parser
def apply= { parser, element ->
    // TODO: Function to load a default map
    defaultMap=[ownerId:     msg.getString('UUID'),
                ownerDomain: msg.getString( 'domain') ] 
    parser.inject(defaultMap){ mapa, expression ->
        log.debug "Begin parsing feed with ${expression.value}"
        def result
        use (DOMCategory) {
            result= element.xpath( expression.value.XPATH, NODESET)
            /* Print each element */
            log.debug "${expression.value.XPATH}: ${result.size()}= ${result.text()}"
        }
        mapa[ expression.key]=result.text()
        mapa
    }// lexpression.collect
}

    /**
     * Get a parser to process the feed
     * @param feed Feed 
     * @param task Task
     * @return Parser   A parser suitable to process the feed
     */
    def getParser( feed, task){
        return new ExpressionContainer( 'default.exp')
    }

}// TaskProcessor