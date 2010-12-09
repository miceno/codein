
import groovy.jms.*
import javax.jms.Message

import es.tid.socialcoding.consumer.*

def cli = new CliBuilder( usage: 'groovy ' )

c= new Consumer( 'consumer.config')

while (true){
Message msg = c.getNextMessage()

   println "mensaje recibido : " + msg

   sleep( 1*1000)
}
System.exit(0)
