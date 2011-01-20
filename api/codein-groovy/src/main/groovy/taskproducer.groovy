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
String tablename= 'User'
def table= helper.db.dataSet( tablename)

   // Find all the URL feeds to process
   table.each{  row ->
       println "show: " + row

       // Transform the row into a map
   Map mapa = [:]
   def metaData= row.getMetaData()
       if( tablename != 'User' )
       {
          // Send all the rows as messages
          metaData.each{ 
             println "${it.columnName}: " + row."${it.columnName}" 
             mapa[ it.columnName]= row."${it.columnName}"
          }
          Message msg= p.createMessage( MessageType.FEED_TASK, mapa)
          p.sendMessage( msg )
       }
       else
       {
       Map mapaModelo= [:]
          // Construye un mapa modelo
          metaData.each{ 
             println "${it.columnName}: " + row."${it.columnName}" 
             if( it.columnName != 'urls')
               mapaModelo[ it.columnName]= row."${it.columnName}"
          }
          // Extrae cada url
          println "urls= ${row.urls}"
          def listaUrls= row.urls.split(/\|/).collect{ it.trim()}
          listaUrls.removeAll( [''] )
          listaUrls.each{
            mapa.putAll( mapaModelo)
            println "url= ${it}"
            mapa['url'] = it
            Message msg= p.createMessage( MessageType.FEED_TASK, mapa)
            p.sendMessage( msg )
          }
       }
       
       println ""
   }

 
System.exit(0)
