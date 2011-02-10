//
//  Codein JMS base class
//
//  Created by Orestes Sanchez on 2010-12-06.
//  Copyright (c) 2010 Telefónica I+D. All rights reserved.
//

package es.tid.socialcoding

import org.apache.activemq.ActiveMQConnectionFactory
import groovy.jms.JMS
import javax.jms.*
import javax.jms.Message
import javax.jms.Session


import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator


/**
 * Class
 *
 */
public class CodeinJMS
{
    public static final String MSG_TYPE       = "type"
    public static final String MSG_PAYLOAD    = "payload"

    // Logging infrastructure
    Logger  logger
    
    // Configuration
    def  config
        
    // Variables for JMS connection
    ConnectionFactory jms
    Session session
    Queue queue
    
    /**
     * JMS Broker URL
     */
    String brokerUrl;
    
    public CodeinJMS( String queueUrl){
        loadConfig( )
        init( queueUrl)
    }
    
    /**
     * Load configuration for Logging and for the application
     */
    private void loadConfig( ){
        // Carga propertyFile
        config = SocialCodingConfig.newInstance().config
        // Get brokerUrl from config
        brokerUrl = config.activemq.brokerUrl 
        
        // Get the logger
        logger = Logger.getLogger(this.class)
    }
    
    /**
     * Initiatize JMS session
     */
    private void  init( String queueUrl){
                                    
        //ActiveMQ Configuration
        use( JMS){
            jms = new ActiveMQConnectionFactory( brokerUrl);
            assert jms != null, 'jms parameter must not be null'            
            Connection connection= jms.connect()
            session = connection.session()
            
            queue = jms.queue( queueUrl)
        }
    }
     
    /**
     * @param type Type of message
     * @param payload Message payload
     */
    Message createMessage( String type, def input){            
        //Mandamos en funcion de la cola necesaria.
        Message msg = session.createMapMessage()

        // Set message body
        input.each{ key,value -> msg.setString( key, value.toString()) }

        // Set msg type
        msg.setString( MSG_TYPE, type)

        return msg
    }

    /**
     * @param queue Destination queue to send the message to
     * @param msg Message to send
     */
    void sendMessage( Message msg){
        // Send message
        logger.debug( "about to send message: queue -> " + queue + ", message -> "+ msg)
        use( JMS){
            queue.send( msg)
        }
    }

    /**
     * Run the application to collect messages and send them to the queue
     */
     
     Message getNextMessage( Integer waitTime = null)
     {
     Message msg

        logger.debug( "waiting for a message in queue $SocialCodingConfig.activemq.destinationQueue" )
     
        use( JMS){ msg = queue.receive( waitTime) }
        
        logger.debug( "received message: " + msg )
        return msg
     }
       
        
}

