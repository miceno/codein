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

// Start logging
String logConfigFile= 'log4j.properties'
String logFilename= getClass().getName() + ".log"

System.setProperty("log.filename", logFilename)
PropertyConfigurator.configure( new File( logConfigFile).toURL())

Logger log= Logger.getLogger( getClass().getName())

// Read configuration
def config= SocialCodingConfig.newInstance().config

String tablename= config.consumer.table_name

// Resource root dir
final String RESOURCE_PATH=config.root.resources_path

// Default XPATH expression
final String XPATH_EXPRESSION = config.consumer.xpath_entry_selector

//Carga de la configuracion de los logs
final String URL_FIELD= config.consumer.url_field_name

final String consumerConfigFile= config.consumer.consumer_config_file
final String producerConfigFile= config.consumer.producer_config_file
final String MESSAGE_TYPE= config.consumer.message_type
final Integer WAIT_TIME= config.consumer.wait_time

log.debug "Reading Consumer configuration from $consumerConfigFile"
c= new Consumer( consumerConfigFile)
log.debug "Reading Producert configuration from $producerConfigFile"
errorQueue= new Producer( producerConfigFile)

Message msg
String messageType
def messagePayload
def feed

// Create UserFeedDAO
def helper= new DbHelper()
def table= helper.db.dataSet( tablename)

while (true){
   log.debug( "Esperando recibir mensaje... $WAIT_TIME")

   // Read a message
   msg = c.getNextMessage( WAIT_TIME)

   if( !msg) {
       log.debug "No message received"
       continue
   }

   log.debug( showMsg( msg))

   messageType= msg.getString( CodeinJMS.MSG_TYPE)
   log.debug( "Mensaje de tipo: $messageType")
   if( messageType != MESSAGE_TYPE)
   {
       log.debug( "Do not process this kind of message: $messageType != ${MESSAGE_TYPE}")
       log.debug( "TODO: Insert the message again")
       continue
   }

   // check how to get all the names of the keys of the MessageMap
   // get the URL string
   def url= msg.getString( URL_FIELD)

   log.info "Start to parse $url"
   // Get XML feed
   try{
      feed = DOMBuilder.parse(
              new InputStreamReader( url.toURL().openStream()),
              false,
              false)
   }
   catch(e){
      String errorText= "Unable to download $url: ${e.getClass().getName()}"
      log.error errorText
      Map map=[:]
      map[ 'errorText']= errorText
      msg.getMapNames().each{ 
        log.debug( "name $it = ${msg.getObject( it ).toString()}" )
        map[ (it)]= msg.getObject( it )
      }
      log.debug "mapa a enviar ${ map.collect{ k,v -> "clave $k: $v" } }"
      Message errorMsg= errorQueue.createMessage( MessageType.ERROR_MESSAGE, map)
      errorQueue.sendMessage( errorMsg)
      continue
   }

def doc = feed.documentElement

   // Get Parser
def parser= new ExpressionContainer( config.consumer.parser_file)
   
   // Parse Text

    // A closure that applies each Expression to an element
    def apply= { listExpressions, element ->
        // TODO: Function to load a default map
        defaultMap=[ownerId:     msg.getString('UUID'),
                    ownerDomain: msg.getString( 'domain') ] 
        listExpressions.inject(defaultMap){ mapa, xexpression ->
            log.debug "Begin parsing feed with ${xexpression.value}"
            def result
            use (DOMCategory) {
                result= element.xpath( xexpression.value.XPATH, NODESET)
                /* Print each element */
                log.debug "${xexpression.value.XPATH}: ${result.size()}= ${result.text()}"
            }
            mapa[ xexpression.key]=result.text()
            mapa
        }// lexpression.collect
    }
   
    // Curry the apply to the Parser
    def applyToElement= apply.curry( parser.expressions)

    def result
    use( DOMCategory){
        // First, obtain the nodes relevant 
        log.debug( "Getting all nodes that matches $XPATH_EXPRESSION")
        nodes= doc.xpath( XPATH_EXPRESSION, NODESET)

        // For each node, extract all the expressions
        result= nodes.collect( applyToElement)
        dbgStr= result.collect{ 
            it.collect{ """${it.key} =${it.value.substring( 0,  
                                  it.value.length()<200? it.value.length():200)}""" 
                      }.join( ",\n\t") 
        }.join( "\n" + "Entry".center( 20, '-') + "\n")
        log.debug( "Matching nodes\n" + dbgStr)
        log.debug( "END".center( 20, '*') )
    }// use

    log.info "Total matching nodes: ${result.size()}"
    // Insert results in database
    log.info( "Inserting results in database") 
    // Preprocess data

    // Format Dates
    listaCamposFecha= config.consumer.transform_date_fields
    log.debug( "Transform dates of fields: $listaCamposFecha") 

def transformDatesList = { list, element ->
      if ( list.isCase( element.key))
      {
           if( element.value)
               element.value= new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                       .parse( element.value)
                       .getTime()
           else
               element.value=0
      }
   }

def transformDates= transformDatesList.curry( listaCamposFecha)
   result.each{ it.each( transformDates) }
   log.debug( "after transform start".center( 40, '*') )
   result.each{ it.each{ log.debug it }}
   log.debug( "end".center( 20, '*') )


def exists= {t, key, record ->
String query= """
 select * from $tablename where $key = '${record.get( key)}'
"""
    log.debug "Query to exists: '$query'"
    result= t.firstRow( query)
    log.debug "Result : '$result'"
    return result?."$key"
}

    updatedRecords= 0
    insertedRecords= 0
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

    // Insert in the Database
    result.each( addOrInsert.curry( table, 'id'))

    log.info "Total new entries: ${insertedRecords - updatedRecords}"
    println "finished parsing $url"

}// while

System.exit(0)

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
