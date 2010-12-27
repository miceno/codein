/**
* A consumer console that consumes all the message in a queue
*/

import org.apache.xpath.XPathAPI
import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory

import groovy.jms.*
import groovy.xml.*
import javax.jms.Message

import es.tid.socialcoding.consumer.*

import es.tid.socialcoding.*

import org.apache.log4j.*
import org.apache.log4j.PropertyConfigurator

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
        messagePayload= new File( opt.f).text
        log.debug messagePayload.getClass().getName()
        feed= DOMBuilder.parse( new FileReader( opt.f))
        log.debug feed.getClass().getName()
   }
   else
   {
        msg = c.getNextMessage( waitTime)

        if( msg != null) {
           log.debug( "mensaje recibido : $msg" )
           messageType= msg.getString( CodeinJMS.MSG_TYPE)
           messagePayload= msg.getString( CodeinJMS.MSG_PAYLOAD)
           feed= new XmlSlurper().parseText( messagePayload)
        }
        else 
           continue
   }

   log.debug( "message type: $messageType")
   log.debug( "message payload: $messagePayload")

   log.debug "Feed begin".center( 40, "-")
   log.debug feed
   log.debug "Feed end".center( 40, "-")

   log.debug "begin parsing feed"

   use( DOMCategory) {
      XPathAPI.selectNodeList(feed.documentElement, xpath).each{ println it }
   }

   log.debug "end parsing feed"
   if( opt.f) break;
}// while

System.exit(0)
