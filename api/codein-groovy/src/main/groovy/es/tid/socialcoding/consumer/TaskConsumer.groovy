
package es.tid.socialcoding.consumer

import es.tid.socialcoding.*
import es.tid.socialcoding.jms.*

import groovy.jms.*
import javax.jms.Message

import groovy.util.logging.Log4j

@Log4j
class TaskConsumer{

    private def consumerQueue
    private def config
    def waitTime
    
    private String MESSAGE_TYPE
    
    TaskConsumer( def queueName){
        config= SocialCodingConfig.newInstance().config
        consumerQueue= new Consumer( config.consumer.originQueue)
        
        waitTime= config.consumer.wait_time
        MESSAGE_TYPE= config.consumer.message_type   
    }
    
    /**
     * Read a Task
     * @return Map      Returns a Map with the task attributes
     */
    def readTask( ){
        def result= null
        log.info( "Esperando recibir mensaje... $waitTime")

        // Read a message
        def msg = consumerQueue.getNextMessage( waitTime)

        if( !msg) {
           log.debug "No message received"
        }
        else{
            def messageType= msg.getString( CodeinJMS.MSG_TYPE)
            log.debug( "Mensaje de tipo: $messageType")
            if( messageType != MESSAGE_TYPE)
            {
               log.info "Do not process this kind of message: $messageType != ${MESSAGE_TYPE}"
               log.info "TODO: Insert the message again"
            }
            else
            {   
                result= msg.getContentMap()
            }
        }
        log.debug "Received message $result"

        return result
    }


    /**
     * Read a feed from a URL
     * @param url URL of the feed
     * @return Node DOM document
     */
    def readFeed( url){
        def result= null
    
        // Get XML feed
        try{
            feed = DOMBuilder.parse(
                new InputStreamReader( url.toURL().openStream()),
                false,
                false)
            result= feed.documentElement    
        }
        catch(e){
            new FeedProcessorException()
        }

        return result
    }
}