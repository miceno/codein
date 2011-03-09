
package es.tid.socialcoding.push

import org.restlet.*
import org.restlet.data.*
import org.restlet.resource.*

import org.apache.log4j.Logger

import groovy.util.logging.Log4j

import es.tid.socialcoding.SocialCodingConfig
import es.tid.socialcoding.dao.*

@Log4j
class PushFinder extends Resource{
    
    final String TOKEN_ATTRIBUTE    = "token"
    final String PAYLOAD_ATTRIBUTE  = "payload"
    final String SERVICE_ATTRIBUTE  = "service"

    def userTable
    def config
    
    static def plugins= null
    
    static def reloadPlugins(){
        Logger.getRootLogger().debug( "Reloading plugins")
        PushFinder.plugins= new ConfigSlurper().parse(new File('push-plugins.config').toURL())
    }
    
    PushFinder(Context context, Request request, Response response) {  
       super(context, request, response);  
       
       SocialCodingConfig.newInstance().reload()
       config= SocialCodingConfig.newInstance().config

       def variante= new Variant(MediaType.TEXT_PLAIN)
       variante.setCharacterSet( CharacterSet.UTF_8)
       getVariants().add( variante);  

       // This service is write-only.  
       setReadable(false);  

       // This service is write-only.  
       setModifiable(true);  
       
       userTable= new UserDAO( db: new DbHelper().db)
       
    }
    
    // POST: Create a new user
    public void acceptRepresentation( Representation entity)
    {
        log.debug "Entity=${entity.dump()}"

        // Get all the request data
        def req= getRequest()
        def token= req.getAttributes().get( TOKEN_ATTRIBUTE, "")
        def service= req.getAttributes().get( SERVICE_ATTRIBUTE, "")
        log.debug "service=${service}, token=${token}"

        // Entity is a transient one and once read, it cannot be read again
        def payload= getPayload( entity)
        log.debug "Entity Payload=${payload}"
        
        def resp= getResponse()
        
    try {
    
        if( !isValidService( service))
            throw new Error( "Non-valid service $service")
            
        // Check the remote host 
        if( !isValidRemoteHost( req, service))
            throw new Error( "Bad remote host")
    
        // Authenticate the user
        def userModel= getUser( token)
        log.debug "acceptRepresentation userModel: $userModel"
        if( !(userModel && userModel?.size())){
            resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            resp.setEntity( "Not found");
        }
        else{        
            // Decide which plugin to call               
            def instance= getInstanceOf( service)
            log.debug "instance= ${instance}"
            if( !instance){
                throw new Error( "Unable to get a valid $service")
            }            
            else{
                // Call the plugin with the payload
                def result= instance?.process( userModel, payload)
    
                log.debug "Resultado= $result"
                if( result){
                    resp.setStatus( result?.status)
                    resp.setEntity( new StringRepresentation( result?.message, result?.mediatype) );
                } else {
                    resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                }
            }
        }
    } catch (Throwable e) {
        resp.setStatus( Status.SERVER_ERROR_INTERNAL);
        resp.setEntity( new StringRepresentation ( "${e.toString()}: ${e.getStackTrace()}"))
    }
    }
    
    /**
     * Return the userModel that owns the token
     * @param token Token assigned to a user
     * @return Map  The UserModel [domain: , UUID: ], 
     * @return null No user owns the token
     */
    private def getUser( token)
    {
        log.debug "Retrieving user by token: $token"
        if( !token)
            return null
        def query= [ token: token]
        def result= userTable.findBy( query )
        log.debug "getUser: result= $result"
        return (result?.size() > 0 ? result[0] : null)
    }
    
    /**
     * Obtain the payload of an entity
     */
    def getPayload( entity) {
        def result= ""
        Form form = new Form( entity);
        log.debug( "Start to process form: $form")
            // Get parameter payload
        result= form.getFirstValue( PAYLOAD_ATTRIBUTE, "")
    }
    
    private def isValidRemoteHost( req, service) {
        def requestHostname= req.getRootRef().getHostDomain(true)

        def serviceHostname= PushFinder.plugins?."$service"?.remoteHost
        
        log.debug "Remote host for $service is $serviceHostname"
        log.debug "Remote host request is $requestHostname"
        
        return ( serviceHostname == requestHostname )
    }
    
    private def isValidService( service){
        return ( PushFinder.plugins?."$service"?.size())
    }
    
    /**
     * getInstanceOf: Returns an instance of the class name
     * @param String    Service name
     * @return Object   Object
     *         null     No class found for the name
     */
    
    private def getInstanceOf( service){
        def DOTS= /\./
        String className
        if( isValidService( service))
            className= PushFinder.plugins?."$service"?.className
        else 
            return null
        log.debug "className= ${className}"
        
        // Transform the classname to a file name with directories
        String classFileName= className.replaceAll( DOTS, File.separator) + ".groovy"
        log.debug "classFileName= ${classFileName}"
        
        def f= new File( classFileName)
        if( !f.exists())
            return null
            
        // Load the class file
        def	gcl = new GroovyClassLoader() 
        Class serviceClass = gcl.parseClass( f) 

        // Instantiate
        return serviceClass?.newInstance()
    }
    
}