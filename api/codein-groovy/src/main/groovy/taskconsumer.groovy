/**
* A consumer of tasks that consumes all the message in a queue
*/

import groovy.xml.*
import static javax.xml.xpath.XPathConstants.*
import javax.xml.xpath.*
import java.text.SimpleDateFormat

import groovy.xml.dom.DOMCategory
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.DocumentBuilder

import es.tid.socialcoding.dao.*


import org.w3c.dom.Document; 
import org.w3c.dom.NodeList; 
import org.w3c.dom.Element; 
import org.w3c.dom.Node; 

import groovy.jms.*
import javax.jms.Message

import es.tid.socialcoding.consumer.*
import es.tid.socialcoding.producer.*

import es.tid.socialcoding.*

import org.apache.log4j.*

// Start logging
PropertyConfigurator.configure(new File( 'log4j.properties').toURL())
Logger log= Logger.getLogger( getClass().getName())

// Read configuration
def config= SocialCodingConfig.newInstance().config

// Resource root dir
final String RESOURCE_PATH=config.root.resources_path

// Default XPATH expression
final String XPATH_EXPRESSION = config.consumer.xpath_entry_selector

//Carga de la configuracion de los logs
final String URL_FIELD= config.consumer.url_field_name

log.debug "Reading configuration from $config.consumer.consumer_config_file"
c= new Consumer( config.consumer.consumer_config_file)
errorQueue= new Producer( config.consumer.producer_config_file)
def waitTime= config.consumer.wait_time

Message msg
String messageType
def messagePayload
def feed

// Create UserFeedDAO
def helper= new DbHelper()
String tablename= config.consumer.table_name
def table= helper.db.dataSet( tablename)

while (true){
   log.debug( "esperando recibir mensaje... $waitTime")

   // Read a message
   msg = c.getNextMessage( waitTime)

   if( !msg) {
       continue
   }
   log.debug( "mensaje recibido : $msg" )
   log.debug( "properties" )
   msg.getPropertyNames().each{ log.debug( "property= $it" ) }
   log.debug( "names" )
   msg.getMapNames().each{ 
        log.debug( "name $it = ${msg.getObject( it ).toString()}" )
   }

   messageType= msg.getString( CodeinJMS.MSG_TYPE)
   log.debug( "Mensaje de tipo: $messageType")
   if( messageType != config.consumer.message_type)
   {
       log.debug( "$messageType != ${config.consumer.message_type}")
       continue
   }

   // TODO: check how to get all the names of the keys of the MessageMap
   // TODO: get the URL string
   def url= msg.getString( URL_FIELD)

   // Get XML feed
   try{
      feed = DOMBuilder.parse(
              new InputStreamReader( url.toURL().openStream()),
              false,
              false)
   }
   catch(e){
      String errorText= "Unable to download $url"
      log.debug errorText
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
        listExpressions.inject([:]){ mapa, xexpression ->
            log.debug "begin parsing feed with ${xexpression.value}"
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
        nodes= doc.xpath( XPATH_EXPRESSION, NODESET)

        // For each node, extract all the expressions
        result= nodes.collect( applyToElement)
        log.debug( "Listing elements")
        result.each{ 
            it.each {log.debug "$it.key =$it.value" } 
        }
        log.debug( "END".center( 20, '*') )
    }// use

    // TODO: Insert results in database
    log.debug( "TODO: Insert results in database") 
    // Preprocess data

    // Format Dates
    lista= config.consumer.transform_date_fields

def transformDatesList = { list, element ->
      if ( list.isCase( element.key))
      {
           element.value= new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                       .parse( element.value)
                       .getTime()
      }
   }

def transformDates= transformDatesList.curry( lista)
   result.each{ it.each( transformDates) }
   log.debug( "after transform start".center( 40, '*') )
   result.each{ it.each{ log.debug it }}
   log.debug( "end".center( 20, '*') )

    // Insert in the Database
    result.each{ 
         log.debug "adding row to $tablename: $it"
         table.add( it) 
    }

}// while

System.exit(0)
