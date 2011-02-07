
// import org.restlet.ext.atom.*

import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

String logFileName= 'r.log'
System.setProperty("socialcoding.log.filename", logFileName)

import org.restlet.Context;  
import org.restlet.data.Form;  
import org.restlet.data.MediaType;  
import org.restlet.data.Request;  
import org.restlet.data.Response;  
import org.restlet.data.Status;  
import org.restlet.resource.Resource
import org.restlet.resource.DomRepresentation;  
import org.restlet.resource.Representation;  
import org.restlet.resource.ResourceException;  
import org.restlet.resource.Variant;  

class UsersResource extends Resource
{
     UsersResource(Context context, Request request, Response response) {  
        super(context, request, response);  
  
        // Get the "itemName" attribute value taken from the URI template  
        // /items/{itemName}.  
        this.domain = (String) getRequest().getAttributes().get("domain");  
  
        // Define the supported variant.  
        getVariants().add(new Variant(MediaType.TEXT_XML));  

        // By default a resource cannot be updated.  
        setReadable(true);  

        // By default a resource cannot be updated.  
        setModifiable(false);  
    }  

    /** 
     * Returns a listing of all registered items. 
     */  
    Representation represent(Variant variant) throws ResourceException {  
        // Generate the right representation according to its media type.  
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {  
            try {  
                DomRepresentation representation = new DomRepresentation(  
                        MediaType.TEXT_XML);  
                // Generate a DOM document representing the list of  
                // items.  
                
                representation.setDocument( d);  

                // Returns the XML representation of this document.  
                return representation;  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        return null;  
    }  


}

String script= '''

import org.apache.log4j.PropertyConfigurator
PropertyConfigurator.configure(new File('log4j.properties').toURL())

import org.apache.log4j.Logger
import org.restlet.resource.StringRepresentation
import org.restlet.data.MediaType
import org.restlet.data.Status

import es.tid.socialcoding.dao.*
import es.tid.socialcoding.SocialCodingConfig

Logger log= Logger.getLogger( getClass().getName())
def config= SocialCodingConfig.newInstance().config

final Integer PORT=9999

// Create UserFeedDAO
def database= new DbHelper().db

builder.component{
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/socialcoding"){
        router{
            // a list of all users
            resource("/user", ofClass:UsersResource)
            // a list of domain users
            resource( uri:"/user/{domain}",        ofClass:UsersResource)
            // The add a user
            resource( uri:"/user/{domain}/{user}", ofClass:UserResource)
        }
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
