//
//  jms-producer
//
//  Created by Orestes Sanchez on 2010-12-06.
//  Copyright (c) 2010 Telefónica I+D. All rights reserved.
//

package es.tid.socialcoding.consumer

import es.tid.socialcoding.CodeinJMS
import groovy.jms.*
import javax.jms.*
import javax.jms.Message


/**
 * Class
 *
 */
public class Consumer extends CodeinJMS
{
          
    //Mysql Configuration
    def  db
         
    public Consumer( String configFile) {
        super( configFile)
        db = [  url         : config.bd.url,
                user        : config.bd.user,
                password    : config.bd.password,
                driver      : config.bd.driver]
        
    } 
         
    
     
    /**
     * Run the application to collect messages and send them to the queue
     */
     
     Message getNextMessage( Integer waitTime = null)
     {
     Message msg

        logger.debug( "waiting for a message in queue $config.activemq.destinationQueue" )
     
        use( JMS){ msg = session.queue( config.activemq.destinationQueue).receive( waitTime) }
        
        logger.debug( "received message: " + msg )
        return msg
     }
    
}

/*
def c = new ConsumerConsole( 'socialcoding.properties')
      
    c.run()      
*/