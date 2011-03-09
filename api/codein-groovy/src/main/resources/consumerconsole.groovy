/**
* A consumer console that consumes all the message in a queue
*/

import groovy.jms.*
import javax.jms.Message

import es.tid.socialcoding.consumer.*
import es.tid.socialcoding.*

def cli = new CliBuilder( usage: 'groovy consumerconsole' )

cli.h(longOpt: 'help', 'usage information')
cli.c(argName:'configfile', longOpt:'config', required: true,
      args: 1, 'Configuration filename')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return null
}


c= new Consumer( opt.c)
def waitTime= 10* 1000

while (true){
   println "esperando recibir mensaje... $waitTime"
Message msg = c.getNextMessage( waitTime)

   if( msg != null) {
           println "mensaje recibido : " + msg
           println "message type: " + msg.getString( CodeinJMS.MSG_TYPE)
           println "message payload: " + msg.getString( CodeinJMS.MSG_PAYLOAD)
   }

}
System.exit(0)
