/**
* Download a feed in raw format and send it to a queue
*/

package es.tid.socialcoding.producer

import groovy.jms.*
import javax.jms.Message

import org.apache.log4j.Logger
import groovy.util.logging.Log4j

import es.tid.socialcoding.CodeinJMS
import es.tid.socialcoding.MessageType
import es.tid.socialcoding.SocialCodingConfig
import es.tid.socialcoding.producer.Producer
import es.tid.socialcoding.dao.UserDAO
import es.tid.socialcoding.dao.DbHelper

/**
 * Read a feed and map elements to fields
 */

@Log4j 
class FeedTaskProducer{

    private Producer p

    private def table

    FeedTaskProducer(){
        Logger.getLogger( getClass().getName())
        p = new Producer( SocialCodingConfig.newInstance().config.producer.destinationQueue)
    }
    
    /*
     * Create the task for a user, insert all the tasks in the queue
     * 
    */
    def produceUserTask( def userModel){
        
        // Extrae cada url
        log.info "User Task for user: $userModel"
        
        // Split all the urls
        def listaUrls= userModel.urls.split(/\|/).collect{ it.trim()}
        
        // Remove empty URL
        listaUrls.removeAll( [''] )
        
        // For each URL insert a task in the queue
        listaUrls.each{
            def mapa= [:]
              mapa.putAll( userModel)
              mapa.remove( 'urls')
              log.debug "creating task for feed: ${it}"
              mapa['url'] = it
              try
              { 
                  Message msg= p.createMessage( MessageType.FEED_TASK, mapa)
                  p.sendMessage( msg )
              }catch ( e){
                  log.error "Exception processing User $userModel: ${e.getMessage()}"
              }   
          }
        
    }
    
    /**
     * produceAllTasks: reads all the users and create a task to read each of their feeds
     */
     def produceAllTasks( )
     {
         log.debug( "produceAllTasks")
         if( !table)
            table = new UserDAO(db: new DbHelper().db)
         // Find all the URL feeds to process
         table.findBy().each{ userRow ->
             produceUserTask( userRow)
         }
     }

}
 
