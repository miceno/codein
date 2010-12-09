//
//  jms-producer
//
//  Created by Orestes Sanchez on 2010-12-06.
//  Copyright (c) 2010 Telefónica I+D. All rights reserved.
//

package es.tid.socialcoding

import es.tid.socialcoding.producer.Producer

import groovy.jms.JMS
import javax.jms.*
import javax.jms.Message


/**
 * Class
 *
 */
public class ProducerConsole extends Producer
{
         
    public ProducerConsole( String configFile) {
        super( configFile)
    } 
         
    /**
     * Run the application to collect messages and send them to the queue
     */
     
     void run( )
     {
         // Read command line and send to queue
         System.in.withReader{
             while (true){
                 println 'commands: message_type payload' 
                 // TODO: Read message to be sent
             def input =  it.readLine().tokenize()
                 println "read input: $input"
                 // TODO: Sent message to queue
             String messageType = input.remove(0)
                 if( messageType == "exit")
                     System.exit(0);
                 // Sleep to next event
                 else {
                     print "Message: " + messageType
                     print ", cuerpo: " 
                     input.each{ print it}
                     println ""
                     
                     Message msg= createMessage( messageType, input)
                     sendMessage( msg)

                 }// else
             }// while
         }// withReader
     }// void run
    
}// class

def cli = new CliBuilder( usage: 'groovy producerconsole' )

cli.h(longOpt: 'help', 'usage information')
cli.c(argName:'configfile', longOpt:'config', required: true,
      args: 1, 'Configuration filename')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return null
}

def p = new ProducerConsole( opt.c)
      
    p.run() 
