/**
* A consumer of tasks that consumes all the message in a queue
*/

package es.tid.socialcoding.consumer

import groovy.jms.*
import groovy.xml.*
import groovy.util.logging.Log4j
import groovy.xml.dom.DOMCategory


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

    private final enum DbStatus {
        DB_UPDATE,
        DB_INSERT,
        NO_VALUE
    }
    
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

        def nodes
        use( DOMCategory){
            // First, obtain the nodes relevant 
            log.debug( "Getting all nodes that matches ${parser.filterExpression}")
            nodes= doc.xpath( parser.filterExpression, NODESET)
            log.debug( "# nodes that matches '${parser.filterExpression}'= ${nodes.size()}")
        }
        
        // Metadata is the owner and the source
        def metadataMap= [ ownerId:     task.UUID,
                           ownerDomain: task.domain ]

        // TODO: get metadata
        
        
        def listaCamposFecha= config?.consumer?.transform_date_fields
        def updatedRecords = 0
        def entry
        // For each node, 
        nodes.each{ node ->
            
            // extract all the expressions
            entry= parser.apply( node)
                                  
            // add the owner and the metadata
            entry.putAll( metadataMap)
        
            // Show trace
            log.debug "entry with metadata: " + entryToString( entry)

            // Format Dates
            log.debug( "Transform dates of fields: $listaCamposFecha") 
            transformDate( listaCamposFecha, entry)
            log.debug "dump entry: ${entry.dump()}"

            // Insert results in database
            log.info( "Inserting entry: ${entry.get( 'id')}") 
            def result= addOrInsert( entry)
            updatedRecords += ( result == DbStatus.DB_UPDATE ? 1 : 0)

        }
        
        use( DOMCategory){
            log.info "Total matching nodes: ${nodes.size()}"
            log.info "Total new entries: ${nodes.size() - updatedRecords}"
        }
        log.info "finished parsing $url"


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
                    Helper.getDate( column.value).getTime() )
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
        def result= entryTable.findBy( [(key): record.get( key, "")])
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
    def addOrInsertAll( entries){

        def updatedRecords= 0
        def result= 0
        
        entries.each{ entry ->
            result= addOrInsert( entry)
            updatedRecords += ( result == DbStatus.DB_UPDATE ? 1 : 0)
        }

        log.info "Total matching nodes: ${entries.size()}"
        log.info "Total new entries: ${entries.size() - updatedRecords}"
    
    }

    /**
     * Add or insert a record in the Entry table
     * @param Entry         An entry as a map of field and values
     * @return operation    Returns whether it was an update or an insert operation
     */
    def addOrInsert( entry){
        def STR_KEY_FIELD = 'id'
        def result= DbStatus.NO_VALUE
        if( exists( entry, STR_KEY_FIELD))
        {
            log.debug "updating: $entry"
            entryTable.update( entry, [ id: entry.id])
            result= DbStatus.DB_UPDATE
        }
        else
        {
            log.debug "new record: $entry"
            entryTable.create( entry) 
            result= DbStatus.DB_INSERT
        }
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
    
    /**
     * Get all the metadata of a feed
     * @param doc       DocumentElement
     * @param parser    Parser used to filter expressions
     * @return List     A list of entries, where is entry is a map like:
     *                  [ name: "name", attributes: [att1: "value1"], text: "text"]
     */
    def getMetadata( def doc, def parser){

        use( DOMCategory){
            doc.'*'.findAll{ ! ( it.nodeName == parser.filterExpression) }.collect{ node->
                log.debug "linea: " + node
                def a= node.attributes
                log.debug "number of attributes: " + a.size()
                def map= [:]
                map= 
                            (0..<a.size()).inject( map){ attributeMap, index -> 
                                def n= a.item( index)
                                log.debug "attribute $index: ${n}" 
                                attributeMap += [ (n.nodeName): n.nodeValue ]
                            }
                log.debug "attributeMap= ${map}"
                log.debug "fin".center( 20, '*')
                [ name: node.nodeName, attributes: map, text: node.text()]
            }
        }
    }
    
    /**
     * showEntries: show a log debug of a set of entries
     */
    private def showEntries( def entries){
        def dbgStr= entries.collect{
            entryToString( it)
        }.join( "\n" + "Entry".center( 20, '-') + "\n")
        log.trace( "Matching nodes\n" + dbgStr)
        log.trace( "END".center( 20, '*') )
    }

    /**
     * showEntry: show a log debug of an entry
     */
    private def entryToString( def entry){
        entry.collect{ """${it.key} =${it.value.substring( 0,  
                                  it.value.length()<100? it.value.length():100)}""" 
                      }.join( ",\n\t")
    }
    
}// FeedProcessor

