/**
* A consumer console that consumes all the message in a queue
*/

import groovy.xml.*
import javax.xml.xpath.*

import groovy.xml.dom.DOMCategory
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.DocumentBuilder

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
println "about to read $configFile"
PropertyConfigurator.configure(new File( configFile).toURL())

def cli = new CliBuilder( usage: 'groovy consumerconsole' )

cli.h(longOpt: 'help', 'usage information')
cli.c(argName:'configfile', longOpt:'config', required: true,
      args: 1, 'Configuration filename')
cli.x(argName:'xpath', longOpt:'xpath', required: false,
      args: 1, 'xpath expression')
cli.f(argName:'file', longOpt:'file', required: false,
      args: 1, 'xml file')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return null
}

xpath=  (opt.x ?: XPATH_EXPRESSION )

Logger log= Logger.getLogger( getClass().getName())

c= new Consumer( opt.c)
def waitTime= 10* 1000

Message msg
String messageType
def messagePayload
def feed

while (true){
   log.debug( "esperando recibir mensaje... $waitTime")

   if( opt.f)
   {
        log.debug( "reading file: ${opt.f}" )
        messageType= "file"

        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance(); 
        DocumentBuilder builder =
            factory.newDocumentBuilder(); 
        feed = builder.parse( new File( opt.f) ); 

        // feed= DOMBuilder.parse( new FileReader( opt.f))
        // feed= DOMBuilder.newInstance().parseText( messagePayload)
        log.debug feed.getClass().getName()
   }
   else
   {
        msg = c.getNextMessage( waitTime)

        if( msg != null) {
           log.debug( "mensaje recibido : $msg" )
           messageType= msg.getString( CodeinJMS.MSG_TYPE)
           if( messageType != MessageType.FEED )
               continue
           messagePayload= msg.getString( CodeinJMS.MSG_PAYLOAD)
           feed= new XmlSlurper().parseText( messagePayload)
        }
        else 
           continue
   }

   log.debug( "message type: $messageType")
   log.debug( "message payload: $messagePayload")



def expr     = XPathFactory.newInstance().newXPath().compile(xpath)
def nodes    = expr.evaluate(feed, XPathConstants.NODESET)

   log.debug "begin parsing feed"
   nodes.each{ log.debug "${xpath}: ${it.textContent}" }
   log.debug "end parsing feed"

   log.debug "otro begin parsing feed"
   nodes.each{ log.debug "${xpath}: $it" }
   log.debug "otro end parsing feed"

   if( opt.f) break;
}// while

System.exit(0)
