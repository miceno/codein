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

import es.tid.socialcoding.*

import org.apache.log4j.*
// import org.apache.log4j.PropertyConfigurator

// Resource root dir
final String RESOURCE_ROOT=".."+ File.separator + "resources"
// Default XPATH expression
final String XPATH_EXPRESSION = "//entry"

//Carga de la configuracion de los logs
String configFile= RESOURCE_ROOT + File.separator + 'log4j.properties'
final String URL_FIELD= 'url'

println "about to read $configFile"
PropertyConfigurator.configure(new File( configFile).toURL())

def cli = new CliBuilder( usage: 'groovy consumerconsole' )

cli.h(longOpt: 'help', 'usage information')
cli.c(argName:'configfile', longOpt:'config', required: true,
      args: 1, 'Configuration filename')
cli.f(argName:'expressionfile', longOpt:'expfile', required: false,
      args: 1, 'expression file')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return null
}

Logger log= Logger.getLogger( getClass().getName())

c= new Consumer( opt.c)
def waitTime= 10* 1000

Message msg
String messageType
def messagePayload
def feed

// Create UserFeedDAO
def helper= new DbHelper()
String tablename= 'Entry'
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
        log.debug( "$it = ${msg.getObject( it ).toString()}" )
   }

   messageType= msg.getString( CodeinJMS.MSG_TYPE)
   log.debug( "Mensaje de tipo: $messageType")
   if( messageType != MessageType.FEED_TASK)
       continue

   // TODO: check how to get all the names of the keys of the MessageMap
   // TODO: get the URL string
   def url= msg.getString( URL_FIELD)

   // Get XML feed
   feed = DOMBuilder.parse(
              new InputStreamReader( url.toURL().openStream()),
              false,
              false)
def doc = feed.documentElement

   // Get Parser
def parser= new ExpressionContainer( opt.f)
   
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
    lista= [ 'published', 'updated' ]

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
         log.debug "adding $it"
         table.add( it) 
    }

}// while

System.exit(0)
