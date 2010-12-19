/**
* A consumer console that consumes all the message in a queue
*/

import groovy.jms.*
import javax.jms.Message

import es.tid.socialcoding.consumer.*
import org.apache.xpath.XPathAPI

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

while (true){
   log.debug( "esperando recibir mensaje... $waitTime")

Message msg = c.getNextMessage( waitTime)

   if( msg != null) {
           log.debug( "mensaje recibido : $msg" )
    def messageType= msg.getString( CodeinJMS.MSG_TYPE)
    def messagePayload= msg.getString( CodeinJMS.MSG_PAYLOAD)
           log.debug( "message type: $messageType")
           log.debug( "message payload: $messagePayload")

           def feed= new XmlSlurper().parseText( messagePayload)

            log.debug "Feed begin".center( 40, "-")
            log.debug feed
            log.debug "Feed end".center( 40, "-")

            log.debug "begin parsing feed"


XPathAPI.selectNodeList(feed, xpath).each{ println it }

            log.debug "end parsing feed"
   }

}
System.exit(0)
