/**
* A consumer of tasks that consumes all the message in a queue
*/

import org.apache.log4j.*

import groovy.xml.*
import static javax.xml.xpath.XPathConstants.*
import javax.xml.xpath.*
import groovy.xml.dom.DOMCategory
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.DocumentBuilder
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.w3c.dom.Element 
import org.w3c.dom.Node

import java.text.SimpleDateFormat

import groovy.jms.*
import javax.jms.Message

import es.tid.socialcoding.*
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.consumer.*
import es.tid.socialcoding.producer.*

class taskconsumer{
    
    static main(args) {
        String logFilename= getClass().getName() + ".log"
        System.setProperty("socialcoding.log.filename", logFilename)

        def c = new TaskConsumer( QUEUENAME)
        FeedProcessor f
        
        while (true){
            task= queue.readTask()
            log.info "task from $queue: $task"
            
            f.processTask( task)
        }
        
        System.exit(0)
        
    }
}

