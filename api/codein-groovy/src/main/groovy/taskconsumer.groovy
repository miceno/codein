/**
* A consumer of tasks that consumes all the message in a queue
*/

import groovy.util.logging.Log4j


import es.tid.socialcoding.*
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.consumer.*

@Log4j
class taskconsumertest{
    
    def run(){

        def c = new TaskConsumer( )
        def f = new FeedProcessor()
        
        while (true){
            def task= c.readTask()
            log.info "Task: $task"
            
            if( task)
                f.processTask( task)
        }
        
        System.exit(0)
        
    }
}

String logFilename= getClass().getName() + ".log"
System.setProperty("socialcoding.log.filename", logFilename)

new taskconsumertest().run()

