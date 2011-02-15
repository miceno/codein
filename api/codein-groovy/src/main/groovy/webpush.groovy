
import org.restlet.Router 
import org.restlet.data.Protocol 
import org.restlet.Application
import org.restlet.Component
import org.restlet.Restlet

import groovy.util.logging.Log4j

import org.apache.log4j.PropertyConfigurator

import es.tid.socialcoding.rest.*
import es.tid.socialcoding.push.*
import es.tid.socialcoding.SocialCodingConfig

/**
 * WebPushApplication: Application object
 */

@Log4j
class WebPushApplication extends Application {  

    Restlet createRoot() {  
        // Create a router Restlet that defines routes.  
        Router router = new Router(getContext())  
  
        // Defines a route for the resource "list of items"  
        router.attach("/push/{service}/{token}", PushFinder.class)
          
        return router;  
    }
}

/**
 * WebPush:     Launcher of the REST push service
 */

public class WebPush {
    final String     APPLICATION_URL     = "/socialcoding"
    final Integer    DEFAULT_PORT        = 8010
    
    def config

    def init(){
        String logFileName= this.class.name.toLowerCase() + '.log'
        System.setProperty("socialcoding.log.filename", logFileName)

        config= SocialCodingConfig.newInstance().config

        PushFinder.reloadPlugins()
    }
    
    def main(args) throws Exception {
        
        init( )

        final Integer PORT = ( config?.rest?."${this.class.name.toLowerCase()}"?.port ?: 
                               ( config?.rest?.port ?: DEFAULT_PORT) )
                        
        println "Listening to port $PORT"
        // Create a new Component.
        Component component = new Component();

        // Add a new HTTP server listening on port 8182.
        component.getServers().add( Protocol.HTTP, PORT);

        // Attach the sample application.
        component.getDefaultHost().attach( APPLICATION_URL,
            new WebPushApplication());

            // Start the component.
            component.start();
    }

}

new WebPush().main()
