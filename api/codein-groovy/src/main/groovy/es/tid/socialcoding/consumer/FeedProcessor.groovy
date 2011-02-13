/**
* A consumer of tasks that consumes all the message in a queue
*/

package es.tid.socialcoding.consumer

import groovy.jms.*
import groovy.xml.*
import groovy.util.logging.Log4j
import groovy.xml.dom.DOMCategory

import java.text.SimpleDateFormat

import static javax.xml.xpath.XPathConstants.*
import javax.xml.xpath.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.DocumentBuilder
import javax.jms.Message

import org.apache.log4j.Level

import es.tid.socialcoding.*
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.consumer.*
import es.tid.socialcoding.producer.*

class FeedProcessorException extends Exception{
    /*
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
    */
}

@Log4j
class FeedProcessor{

    private def config

    private def entryTable

    FeedProcessor( ){
        // Read configuration
        config= SocialCodingConfig.newInstance().config
        Level log_level= Level.toLevel( config.root.log_level.toString())
            log.setLevel( log_level)
            log.info "Log level set to ${log_level}"
        
        // Create UserFeedDAO
        entryTable= new EntryDAO( db: new DbHelper().db)
    }



    /**
     * Process a task
     * @param task Task to process, Map of values, includes the owner user and the url to parse
     * @return void
     */
    void processTask( def task){
        if( !task)
            return 
        def url= task?.url
        log.info "Start to parse $url"
    
        /**
         * Read the feed
         */
        def doc= readFeed( url)

        if( !doc){
            log.error "Unable to build a DOM representation from $url"
            return
        }
        /**
         * Get a Parser
         */
        def parser= getParser( doc, task )

        /**
         * Parse the feed
         */

        def result= [:]
        use( DOMCategory){
            // First, obtain the nodes relevant 
            log.debug( "Getting all nodes that matches ${parser.filterExpression}")
            def nodes= doc.xpath( parser.filterExpression, NODESET)
            log.debug( "# nodes that matches '${parser.filterExpression}'= ${nodes.size()}")

            // For each node, extract all the expressions
            result= parser.apply( nodes)
            log.debug( "# nodes that matches the parser=${result.size()}")
            
            def defaultMap= [ ownerId:     task.UUID,
                              ownerDomain: task.domain ]
                          
            // For each entry add the owner
            result.each{ it.putAll( defaultMap) }
            
            // Show trace
            def dbgStr= result.collect{ 
                it.collect{ """${it.key} =${it.value.substring( 0,  
                                      it.value.length()<100? it.value.length():100)}""" 
                          }.join( ",\n\t") 
            }.join( "\n" + "Entry".center( 20, '-') + "\n")
            log.trace( "Matching nodes\n" + dbgStr)
            log.trace( "END".center( 20, '*') )
        }// use



        /**
         * manipulate the entries to make some filtering and adaptation
         * @param entries input entries
         * @return entries Output entries
         */

        // Format Dates
        def listaCamposFecha= config?.consumer?.transform_date_fields
        log.debug( "Transform dates of fields: $listaCamposFecha") 
        result.each{ transformDate( listaCamposFecha, it) }

        log.debug( "after transform".center( 40, '*') )
        result.each{ it.each{ log.debug it }}
        log.debug( "end".center( 20, '*') )

        /**
         * Insert entries in database
         */

        // Insert results in database
        log.info( "Inserting results in database") 

        // Insert in the Database
        addOrInsert( result)

        println "finished parsing $url"


    }// processTask

    /**
     * Read a feed from a URL
     * @param url URL of the feed
     * @return Node DOM document
     */
    def readFeed( url){
        def result= null
        def feed= null
        // Get XML feed
        try{
            feed = DOMBuilder.parse(
                new InputStreamReader( url.toURL().openStream()),
                false,
                false)
            result= feed?.documentElement    
        }
        catch(e){
            new FeedProcessorException()
        }
        use( DOMCategory){
            log.debug "Feed: $feed"
            log.debug "Read feed content: ${result?.text()}"
        }
        return result
    }


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

    /**
     * Normalize dates to Unix time (in miliseconds)
     * @param list Name of keys to normalize
     * @param elements Map of elements to normalize
     * @return void
     */
    def transformDate( list, entry){      
        entry.each{ column ->
            log.trace "transformDate before $column"
            if ( list.isCase( column.key))
            {
                column.value= ( !column.value ? 0 : 
                    new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                    .parse( column.value)
                    .getTime() )
            }
            log.trace "transformDate after $column"
        }
    }

    /**
     * Check if a registry exists in a table
     * @param t         DAO of the table
     * @param key       Name of the key field
     * @param record    Values of the record we would like to check
     */
    def exists( record, key = 'id'){
        def result= entryTable.findBy( [(key): record."$key"])
        log.debug "Result : '$result'"
        (result.size()> 0)
    }



    /**
     * Extract all the properties of a Message into a Map
     * @param msg       JMS MapMessage
     * @return Map      Map of all the attributes of the Message
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
    def addOrInsert( entries){

        def updatedRecords= 0
        def insertedRecords= 0
    
        entries.each{ record ->
            if( exists( record, 'link'))
            {
                log.debug "register already exists: $record"
                entryTable.update( record, [ id: record.id])
                updatedRecords++
            }
            else
            {
                log.debug "adding row: $record"
                entryTable.create( record) 
                insertedRecords++
            }
        }

        log.info "Total matching nodes: ${entries.size()}"
        log.info "Total new entries: ${insertedRecords - updatedRecords}"
    
    }

    /**
     * Get a parser to process the feed
     * @param feed Feed 
     * @param task Task
     * @return Parser   A parser suitable to process the feed
     */
    def getParser( feed, task){
        def result= null
        
        use( DOMCategory){
        Boolean isRss= (feed?.rss)
        Boolean isAtom= (feed?.feed)
            
            switch(feed.nodeName) {
                case 'rss':
                    result= retrieveRssParser( task)
                break
                case 'feed':                
                    result= retrieveAtomParser( task)
                break
                default :
                    log.error "Unable to retrieve a parser for ${feed.nodeName}, $task"
            }
        }
        return result
    }

    def retrieveAtomParser( task)
    {
        log.debug "RetrieveAtomParser for: $task"
        def parser= new ExpressionContainer( 'atom.parser') 
        parser.filterExpression= '//entry'
        return parser
    }
    
    def retrieveRssParser( task)
    {
        log.debug "RetrieveAtomParser for: $task"
        def parser= new ExpressionContainer( 'rss.parser') 
        parser.filterExpression= '//item'
        return parser
    }
}// FeedProcessor

