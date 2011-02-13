/**
* Download a feed in raw format and send it to a queue
*/

import groovy.jms.*
import javax.jms.Message

//import groovyx.net.http.*
//import static groovyx.net.http.ContentType.*

import es.tid.socialcoding.*
import es.tid.socialcoding.producer.*
import es.tid.socialcoding.dao.*

/**
 * Read a feed and map elements to fields
 */

class taskproducer{
    
    static main(args) {
        String logFilename= "taskproducer"+ ".log"
        System.setProperty("socialcoding.log.filename", logFilename)
        
        println "probando... $logFilename"
    def t = new FeedTaskProducer( )
        t.produceAllTasks()
        
        System.exit(0)
        
    }
}


