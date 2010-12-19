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
    
    /**
     * Name of the property file
     */
    String configFile;
    
    // Variables for JMS connection
    ConnectionFactory jms
    Session session
    /**
     * JMS Broker URL
     */
    String brokerUrl;
    
    public CodeinJMS( String configFile){
        this.configFile= configFile
        loadConfig( this.configFile)
        init()
    }
    
    /**
     * Load configuration for Logging and for the application
     */
    private void loadConfig( String configFile){
        // Carga propertyFile
        config = new ConfigSlurper().parse(new File( configFile).toURL())
        // Get brokerUrl from config
        brokerUrl = config.activemq.brokerUrl 
        
        // Get the logger
        logger = Logger.getLogger(this.class)
    }
    
    /**
     * Initiatize JMS session
     */
    private void  init(){
                                    
        //ActiveMQ Configuration
        use( JMS){
            jms = new ActiveMQConnectionFactory( brokerUrl);
            assert jms != null, 'jms parameter must not be null'
            Connection connection= jms.connect()
            session = connection.session()
        }
    }
     
    /**
     * @param type Type of message
     * @param payload Message payload
     */
    Message createMessage( String type, def input){            
        //Mandamos en funcion de la cola necesaria.
        Message msg = session.createMapMessage()

        // Set msg type
        msg.setString( MSG_TYPE, type)
    
        // Set message body
        msg.setString( MSG_PAYLOAD, input.toString())
        return msg
    }

    /**
     * @param queue Destination queue to send the message to
     * @param msg Message to send
     */
    void sendMessage( Message msg, String queue= null){
        if( queue == null)
            queue = config.activemq.destinationQueue
        // Send message
        logger.debug( "about to send message: queue -> " + queue + ", message -> "+ msg)
        use( JMS){
            session.queue( queue ).send( msg)
        }
    }
        
}

