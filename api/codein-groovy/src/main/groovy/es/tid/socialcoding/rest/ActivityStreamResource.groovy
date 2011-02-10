
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

import groovy.util.logging.Log4j
import es.tid.socialcoding.dao.*

@Log4j
class ActivityStreamResource extends Resource
{
    private final String STR_USER=  'user'
    private final String STR_DOMAIN=  'domain'
    private final String STR_SIZE=  'size'
    private final String STR_START= 'start'
    
    private final String DEFAULT_SIZE=  '100'
    private final String DEFAULT_START= '1'
    
    private def userModel
    
    // Start of listing
    def start
    
    // Size of listing
    def size
    
     ActivityStreamResource(Context context, Request request, Response response) {  
        super(context, request, response);  
  
        // Get the "itemName" attribute value taken from the URI template  
        // /items/{itemName}.  
        Form form = request.getEntityAsForm();
        this.start = (Integer) form.getFirstValue( STR_START, DEFAULT_START) as Integer
        this.size = form.getFirstValue( STR_SIZE, DEFAULT_SIZE) as Integer
        
        this.userModel = getUser( (String) request.getAttributes().get( STR_USER, ''), 
                                    (String) request.getAttributes().get( STR_DOMAIN, ''))
        
        
        String userMessage= ''
        if( userModel) userMessage= ": user ${userModel?.domain}:${userModel?.user}"
        log.info( "Activity stream [start=$start, size=$size]$userMessage")

        // Define the supported variant.  
        getVariants().add(new Variant(MediaType.TEXT_XML));  

        // By default a resource cannot be updated.  
        setReadable(true);  

        // By default a resource cannot be updated.  
        setModifiable(true);  
    }  

    private def getUser( domain, user)
    {
        log.debug "Retrieving user from database: $domain:$user"
        def query= [ domain: domain, UUID: user ]
        def userTable= new UserDAO( db: new DbHelper().db)
        def result= userTable.findBy( query )
        return result?.size() > 0 ? result[0] : null
    }
    /** 
     * Returns a listing of all registered items. 
     */  
    Representation represent(Variant variant) throws ResourceException {  
        // Generate the right representation according to its media type.  
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {  
            try {  

                def rep= buildXmlRepresentation( this.userModel)
                
                // Returns the XML representation of this document.  
                return rep;  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        return null;  
    }  

    def buildXmlRepresentation( user)
    {
        log.info( "buildXmlRepresentation: $user")
    def titulo=""
    
        log.debug ( "Listing -> $titulo" )

        // Generate a DOM document representing the list of  
        // items.  
        def entryTable= new EntryDAO( db: new DbHelper().db)
        
        def filtro = [:]
            filtro= ( user ? [ ownerId: user?.ownerId, ownerDomain: user?.ownerDomain]: filtro)
        def entries= entryTable.findBy( filtro)

        def result= SocialCodingAtomGenerator.generateEntries( entries, titulo, titulo)

        Representation representation = new StringRepresentation( 
                                                result.toString(), 
                                                MediaType.TEXT_XML);  
        return representation
    }
    
}

