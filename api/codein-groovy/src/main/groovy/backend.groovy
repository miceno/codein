
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
 * BackendApplication: Application object
 */

@Log4j
class BackendApplication extends Application {  

    Restlet createRoot() {  
        // Create a router Restlet that defines routes.  
        Router router = new Router(getContext())  
  
        // User BE: listing
        router.attach( "/user/list",                  UsersResource.class)
        router.attach( "/user/list/{domain}",         UsersResource.class)

        // User BE: CRUD
        router.attach( "/user/user/{domain}/{uuid}",  UserResource.class)
        
        // Entry BE: listing
        router.attach( "/activity/list",                    ActivityStreamResource.class)
        router.attach( "/activity/list/{domain}/{uuid}",    ActivityStreamResource.class)

        // Entry BE: CRUD
        router.attach( "/activity/entry/{id}",              EntryResource.class)

        // TODO: implement a search capability
        // router.attach( "/activity/search/{query}",          SearchResource.class)
          
        return router;  
    }
}


/**
 * Backend:     Launcher of the REST push service
 */

public class Backend {
    final String     APPLICATION_URL     = "/socialcoding"
    final Integer    DEFAULT_PORT        = 8010
    
    def config

    def init(){
        String logFileName= this.class.name.toLowerCase() + '.log'
        System.setProperty("socialcoding.log.filename", logFileName)

        config= SocialCodingConfig.newInstance().config
        
    }
    
    def main(args) throws Exception {
        
        init( )

        final Integer PORT = ( config?.rest?."${this.class.name.toLowerCase()}"?.port ?: 
                               ( config?.rest?.port ?: DEFAULT_PORT) )
                               
        println "escuchando puerto $PORT"

        // Create a new Component.
        Component component = new Component();

        // Add a new HTTP server listening on port 8182.
        component.getServers().add( Protocol.HTTP, PORT);

        // Attach the sample application.
        component.getDefaultHost().attach( APPLICATION_URL,
                                            new BackendApplication());

        // Start the component.
        component.start();
    }

}

new Backend().main()
