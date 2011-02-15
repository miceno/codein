
package es.tid.socialcoding.rest

import org.restlet.Context;  
import org.restlet.data.Form;  
import org.restlet.data.MediaType;  
import org.restlet.data.Request;  
import org.restlet.data.Response;  
import org.restlet.data.Status;  
import org.restlet.resource.Resource
import org.restlet.resource.DomRepresentation;  
import org.restlet.resource.StringRepresentation;  
import org.restlet.resource.Representation;  
import org.restlet.resource.ResourceException;  
import org.restlet.resource.Variant;  

import org.apache.log4j.Logger

import es.tid.socialcoding.dao.*

class UsersResource extends PaginateResource
{
     private Logger log = Logger.getLogger( getClass().getName())
     private String domain
     private final String STR_DOMAIN=    'domain'

     UsersResource(Context context, Request request, Response response) {  
        super(context, request, response);  
  
        // Get the "itemName" attribute value taken from the URI template  
        // /items/{itemName}.  
        this.domain = (String) getRequest().getAttributes().get( STR_DOMAIN);  
        
        log.info( "Listing for domain '$domain'")

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
         def result= null
         log.info "Represent: ${variant.dump()}"

        // Generate the right representation according to its media type.  
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {  
            try {  

                result= buildHtmlRepresentation( this.domain)
                
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        else{
            log.debug "Request for non valid representation: ${variant.dump()}"
        } 

        return result;  
    }  

    def buildHtmlRepresentation( dominio)
    {
        Map filtro= [:]
        log.info( "buildHtmlRepresentation: $dominio")
    def titulo=""
        if( dominio)
        {   
           titulo= "Domain $dominio"
           filtro += [ domain: dominio]
        }   
        else
           titulo= "All users"
    
        log.debug ( "Listing -> $titulo" )

        // Generate a DOM document representing the list of  
        // items.  
        def userTable= new UserDAO( db: new DbHelper().db)
        setPagination( userTable)

        def writer= new StringWriter ()
        def builder= new groovy.xml.MarkupBuilder( writer)
        def html= builder.html{
          head { title "$titulo" }
          body {
              h1 "$dominio Users"
              p "This is the list of $titulo users available"
              ul { 
                    userTable.findBy(filtro).each{
                      li it.collect{ k, v -> "$k: $v"}.join( ', ')
                    }
              }
          }
        }   
        Representation representation = new StringRepresentation( 
                                                writer.toString(), 
                                                MediaType.TEXT_HTML);  
        return representation
    }
    
}

