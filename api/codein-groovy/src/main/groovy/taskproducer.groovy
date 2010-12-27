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
 
def cli = new CliBuilder( usage: 'groovy ' )

cli.h(longOpt: 'help', 'usage information')
cli.c(argName:'config', longOpt:'config', required: true,
      args: 1, 'configuration file')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return
}

p= new Producer( opt.c)


// Create UserFeedDAO
def helper= new DbHelper()
def table= helper.db.dataSet( 'userfeedview')

   // Find all the URL feeds to process
   table.each{  row ->
       println row.url.padRight( 70, ' ') + row.UUID.padRight( 20, ' ') 

       println "show: " + row

       // Transform the row into a map
   Map mapa = [:]
       row.getMetaData().each{ 
          println "${it.columnName}: " + row."${it.columnName}" 
          mapa[ it.columnName]= row."${it.columnName}"
       }
       
       Message msg= p.createMessage( MessageType.FEED_TASK, mapa)
       p.sendMessage( msg )
       println ""
   }

 
System.exit(0)
