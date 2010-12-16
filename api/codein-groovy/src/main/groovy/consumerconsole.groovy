/**
* A consumer console that consumes all the message in a queue
*/

import groovy.jms.*
import javax.jms.Message

import es.tid.socialcoding.consumer.*

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

while (true){
Message msg = c.getNextMessage()

   println "mensaje recibido : " + msg

   sleep( 1*1000)
}
System.exit(0)
