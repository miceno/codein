
package es.tid.socialcoding.rest

import org.restlet.Context;  
import org.restlet.data.Form;  
import org.restlet.data.MediaType;  
import org.restlet.data.Method;  
import org.restlet.data.Request;  
import org.restlet.data.Response;  
import org.restlet.data.Status;  
import org.restlet.data.CharacterSet;  
import org.restlet.resource.Resource
import org.restlet.resource.DomRepresentation;  
import org.restlet.resource.StringRepresentation;  
import org.restlet.resource.Representation;  
import org.restlet.resource.ResourceException;  
import org.restlet.resource.Variant;  

import org.apache.log4j.Logger

import groovy.time.*
import groovy.util.logging.Log4j
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.SocialCodingConfig

@Log4j
class ActivityStreamResource extends Resource
{
    private final String STR_UUID=      'uuid'
    private final String STR_DOMAIN=    'domain'
    private final String STR_SIZE=      'size'
    private final String STR_START=     'start'
    
    private final String DEFAULT_SIZE=  '100'
    private final String DEFAULT_START= '0'

    // How long to cache the All activity Stream
    private final Integer DEFAULT_ALL_DURATION= 15

    // How long to cache the User activity Stream
    private final Integer DEFAULT_USER_DURATION= 5
    
    private def uuid
    private def domain
    
    private def config
    
    // Start of listing
    def start
    
    // Size of listing
    def size
    
     ActivityStreamResource(Context context, Request request, Response response) {  
        super(context, request, response);  
        config= SocialCodingConfig.newInstance().config
        // Get the "itemName" attribute value taken from the URI template  
        // /items/{itemName}.  
        
        // We only accept GET method so parameters to this resource are always encoded in the URI
        Form form
        
        if( request.getMethod() == Method.GET)
            form= request.getResourceRef().getQueryAsForm();
        else
            form= new Form( request.getEntity())
        this.start = form.getFirstValue( STR_START, DEFAULT_START) as Integer
        this.size = form.getFirstValue( STR_SIZE, DEFAULT_SIZE) as Integer
        
        // The domain and uuid are optional
        this.domain= (String) request.getAttributes().get( STR_DOMAIN, null)
        this.uuid = (String) request.getAttributes().get( STR_UUID, null)
                
        String userMessage
        
        // In case there is a UUID, there will be also a domain, due to the routing rules
        if( uuid) userMessage= ": user $domain:$uuid}"
        log.info( "Activity stream [start=$start, size=$size]$userMessage")

        // Define the supported variant.  
        def variante= new Variant(MediaType.APPLICATION_ATOM_XML)
        variante.setCharacterSet( CharacterSet.UTF_8)
        getVariants().add( variante);  

        // By default a resource cannot be updated.  
        setReadable(true);  

        // By default a resource cannot be updated.  
        setModifiable(false);  
    }  

    private def getUser( domain, uuid)
    {
        log.debug "Retrieving user from database: $domain:$uuid"
        if( !domain || !uuid)
            return null
        def query= [ domain: domain, UUID: uuid ]
        def userTable= new UserDAO( db: new DbHelper().db)
        def result= userTable.findBy( query )
        log.debug "getUser: result= $result"
        return (result?.size() > 0 ? result[0] : null)
    }
    /** 
     * Returns a listing of all registered items. 
     */  
    Representation represent(Variant variant) throws ResourceException {  
        // Generate the right representation according to its media type.  
        def result= null
        log.info "Represent: ${variant.dump()}"
        if (MediaType.APPLICATION_ATOM_XML.equals(variant.getMediaType())) {  
            try {  
                log.debug "Requesting mediatype=${variant.getMediaType()}"
                def userModel= [:]
                
                // If there is a user attribute, then get the model. If the model is null then error
                if( uuid){
                    userModel= getUser( domain, uuid)
                    if ( !userModel){
                        def mensaje= "Representation not found: $domain:$uuid"
                        log.debug mensaje
                        result= errorRepresentation( variant, mensaje )
                        getResponse().setStatus( Status.CLIENT_ERROR_NOT_FOUND)
                        return result
                    }
                }
                // if there is not a user attribute, then usermodel=null and call getatomrep
                result= getAtomRepresentation( userModel, start, size)
                
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        else{
            log.debug "Request for non valid representation: ${variant.dump()}"
        }  
  
        return result
    }  

    /**
     * errorRepresentation: Create an error representation
     */
    def errorRepresentation( variant, message)
    {
        return new StringRepresentation( message.toString(), variant.getMediaType())
    }

    /**
     * getAtomRepresentation: get an Atom representation of a user or a list of users
     * 
     * @param userModel UserModel
     * @param offset Offset of the SQL query
     * @param limit Limit of the SQL query
     */
    def getAtomRepresentation( userModel, offset, limit)
    {
        log.info( "getAtomRepresentation: $userModel")
    def titulo= "$userModel"
    
        // Generate a DOM document representing the list of  
        // items.  
    def entryTable= new EntryDAO( db: new DbHelper().db)

    def duration= 0
        use( groovy.time.TimeCategory){
            // Duration of the listing of all entries
            duration= new TimeDuration( 0, 
                                        ( config.activity.all_duration ?: 
                                                DEFAULT_ALL_DURATION),
                                        0, 0)
        }
    
    def filtro = [:]
        if( uuid)
        {
            filtro= [ ownerId: userModel.UUID, ownerDomain: userModel.domain ]
            use( groovy.time.TimeCategory){
                duration= new TimeDuration( 0, 
                                            ( config.activity.user_duration ?: 
                                                DEFAULT_USER_DURATION),
                                            0,0)
            }
        }

        log.debug("Filtro: $filtro")
        entryTable.setLimit( limit)
        entryTable.setOffset( offset)
    def entries= entryTable.findBy( filtro)

    def result= SocialCodingAtomGenerator.generateEntries( entries, titulo, titulo)

    Representation representation = new StringRepresentation( 
                                                result.toString(), 
                                                MediaType.APPLICATION_ATOM_XML)
        def expirationDate
        use( TimeCategory) { expirationDate= new Date() + duration }
        log.debug( "Duration: $duration")
        log.debug( "Expiration date: $expirationDate")
        representation.setExpirationDate( expirationDate)
        return representation
    }
    
}

