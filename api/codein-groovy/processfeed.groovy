
import groovy.jms.*
import javax.jms.Message

import es.tid.socialcoding.consumer.*

def cli = new CliBuilder( usage: 'groovy ' )

cli.h(longOpt: 'help', 'usage information') 
cli.e(argName:'expression', longOpt:'expression', args: 1, 'codein expression') 
cli.u(argName:'user', longOpt:'user',	
      args: 1, 'user:domain') 
cli.t(argName:'template', longOpt:'template', args: 1,
      'template message') 

def opt = cli.parse(args) 
if (!opt) return 
if (opt.h) {
   cli.usage()
   return
}

   c= new Consumer( 'consumer.config')
   Message msg = c.getNextMessage()

   println "mensaje recibido : " + msg

System.exit(0)
