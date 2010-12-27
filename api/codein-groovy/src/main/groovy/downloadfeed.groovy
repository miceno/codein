/**
* Download a feed in raw format and send it to a queue
*/

import groovy.jms.*
import javax.jms.Message



import groovyx.net.http.*
import static groovyx.net.http.ContentType.*



import es.tid.socialcoding.*
import es.tid.socialcoding.producer.*

/**
 * Read a feed and map elements to fields
 */
 
class FeedReader {
    def readFeed( url )
    {
        println "about to read " + url
    
      def xmlFeed = new XmlParser().parse(url);

      return xmlFeed
    }
}

class RemoteReader{
    public RemoteReader(){}
    def readFeed( url )
    {
        println "about to read " + url
    
        def remote = new RESTClient( url )

        // set a default response content-type
        def resp = remote.get( contentType: TEXT )    

        return resp
    }
    
}

def cli = new CliBuilder( usage: 'groovy ' )

cli.h(longOpt: 'help', 'usage information')
cli.u(argName:'url', longOpt:'url', required: true,
      args: 1, 'URL of an RSS/Atom feed')
cli.c(argName:'config', longOpt:'config', required: true,
      args: 1, 'configuration file')

def opt = cli.parse(args)
if (!opt) return
if (opt.h) {
   cli.usage()
   return
}

// FeedReader resultado= new FeedReader()
RemoteReader resultado= new RemoteReader()
resp= resultado.readFeed( opt.u)

strResultado= new StringWriter() 
   strResultado<<  resp.getData()

println "resultado: " + strResultado.toString()

p= new Producer( opt.c)

Message msg= p.createMessage( MessageType.FEED, strResultado)
p.sendMessage( msg )

System.exit(0)
