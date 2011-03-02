
package es.tid.socialcoding.rest

import java.util.UUID

import org.restlet.Context;  
import org.restlet.data.Form;  
import org.restlet.data.MediaType;  
import org.restlet.data.Request;  
import org.restlet.data.Response;
import org.restlet.data.Reference  
import org.restlet.data.Status;  
import org.restlet.resource.Resource
import org.restlet.resource.DomRepresentation;  
import org.restlet.resource.StringRepresentation;  
import org.restlet.resource.Representation;  
import org.restlet.resource.ResourceException;  
import org.restlet.resource.Variant;  

import groovy.util.logging.Log4j

import es.tid.socialcoding.SocialCodingConfig
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.producer.*

/**
 * TokenResource: A Resource that exposes a CRUD interface to Entries
 * 
 * URL: /socialcoding/token/user/{domain}/{uuid}
 * POST: create a new Token
 * PUT: update an existing Token
 * GET: get an token
 * DELETE: delete an token
 * 
 * Allowed attributes are:
 * String token: unique UUID 
 */

@Log4j
class TokenResource extends Resource
{
     private def userModel
     private def uuid
     private def domain
     
     // Generate a DOM document representing the list of  
     // items.  
     def userTable= new UserDAO( db: new DbHelper().db)

     TokenResource(Context context, Request request, Response response) {  
        super(context, request, response);  
  
        // Get the "itemName" attribute value taken from the URI template  
        // /items/{itemName}.  
        // The domain and uuid are optional, but should be Url decoded
        domain= (String) request.getAttributes().get( RestHelper.STR_DOMAIN, null)
        domain= domain ? Reference.decode( domain) : domain
        
        uuid = (String) request.getAttributes().get( RestHelper.STR_UUID, null)
        uuid= uuid ? Reference.decode( uuid) : uuid
        
        this.userModel= null
        def query= [ UUID: uuid, domain: domain ]
        def result= userTable.findBy( query )
        this.userModel = result?.size() > 0 ? result[0] : null
        
        // Define the supported variant.  
        getVariants().add(new Variant(MediaType.TEXT_HTML));  
        getVariants().add(new Variant(MediaType.APPLICATION_JSON));  

        // By default a resource cannot be updated.  
        setReadable(true);  

        // By default a resource cannot be updated.  
        setModifiable(true);  
    }  

    /** 
     * Returns a listing of the element. 
     */  

    Representation represent(Variant variant) throws ResourceException {  
        // Generate the right representation according to its media type.  
        if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {  
            def rep
            try {  

                if( this.userModel){
                    rep= buildHtmlRepresentation( this.userModel) 
                }
                else{
                    throw new ResourceException( Status.CLIENT_ERROR_NOT_FOUND, 
                        "Resource does not exists!!!")
                }

                // Returns the XML representation of this document.  
                return rep;  
            }
            catch( ResourceException e){
                getResponse().setStatus( e.status);
                getResponse().setEntity( new StringRepresentation (e.message))                  
            }  
            catch (Exception e) {
                    getResponse().setStatus( Status.SERVER_ERROR_INTERNAL);
                    getResponse().setEntity( new StringRepresentation (e.toString()))
            }
        }  
  
        return null;  
    }  

    def buildJsonRepresentation( userModel)
    {
        assert userModel != null
    String userString= "${userModel.domain}:${userModel.UUID}"
        log.info( "buildJsonRepresentation: user ${userString}")
    def titulo= "Token ${userString}"
    
        def writer= new StringWriter ()
        def builder= new groovy.xml.MarkupBuilder( writer)
        def html= builder.html{
          head { title "$titulo" }
          body {
              h1 titulo.toString()
              p "This is $titulo"
              ul { 
                   userModel.each { k,v -> 
                        li "${k} = ${v}" }
              }
          }   
        }
        Representation representation = new StringRepresentation( 
                                                writer.toString(), 
                                                MediaType.APPLICATION_JSON);  
        return representation
    }

    def buildHtmlRepresentation( userModel)
    {
        assert userModel != null
    String userString= "${userModel.domain}:${userModel.UUID}"
        log.info( "buildHtmlRepresentation: user ${userString}")
    def titulo= "Token ${userString}"
    
        def writer= new StringWriter ()
        def builder= new groovy.xml.MarkupBuilder( writer)
        def html= builder.html{
          head { title "$titulo" }
          body {
              h1 titulo.toString()
              p "This is $titulo"
              ul { 
                   userModel.each { k,v -> 
                        li "${k} = ${v}" }
              }
          }   
        }
        Representation representation = new StringRepresentation( 
                                                writer.toString(), 
                                                MediaType.TEXT_HTML);  
        return representation
    }
    
    // POST: Create a new token
    public void acceptRepresentation( Representation r)
    {
        // Set initial message
    String userString= "${domain}:${uuid}"
        log.info( "POST user $userString")
        // {save the new user to the database}
        try {
            if( !userModel){
                throw new ResourceException( Status.CLIENT_ERROR_NOT_FOUND, "Resource does not exists!!!")
            }
            // User exists
            def checkUserQuery= [ UUID: uuid, domain: domain ]
            def updateUserStmt= [ token: generateToken() ]
            log.info( "Updating token: $updateUserStmt")
            userTable.update( updateUserStmt, checkUserQuery)

            // userModel of the new representation
            userModel= checkUserQuery + updateUserStmt
            
            // Show representation of new resource
            log.debug( "New userModel: $userModel")
            response.status= Status.SUCCESS_OK
            // You could support multiple representation by using a
            // parameter
            // in the request like "?response_format=xml"
            response.entity = buildHtmlRepresentation(userModel);
        } 
        catch( ResourceException e){
            getResponse().setStatus( e.status);
            getResponse().setEntity( new StringRepresentation (e.message))                  
        } 
        catch (Exception e) {
            getResponse().setStatus( Status.SERVER_ERROR_INTERNAL);
            getResponse().setEntity( new StringRepresentation (e.toString()))
        }
    }


    // PUT is the same as a POST
    public void storeRepresentation( Representation r){
        log.info( "PUT user $domain:$uuid")
        acceptRepresentation( r)
    }
    
    // DELETE
    /**
     * Handle a DELETE Http Request. Delete an existing user
     * 
     * @param entity
     * @throws ResourceException
     */
     public void removeRepresentations() throws ResourceException {
         def userQuery= [ domain: domain, UUID: uuid ]
         
         log.info "DELETE token: ${userQuery}"
         try {
             if (null == this.userModel) {
                 throw new ResourceException( Status.CLIENT_ERROR_NOT_FOUND, 
                     "Resource does not exists!!!")
                 return;
             }

             // Delete the entry
             def updateUserStmt= [ token: "" ]
             log.info( "Deleting token $userQuery")
             userTable.update( updateUserStmt, userQuery)
             getResponse().setStatus(Status.SUCCESS_OK);

         } 
         catch( ResourceException e){
             getResponse().setStatus( e.status);
             getResponse().setEntity( new StringRepresentation (e.message))                  
         } 
         catch (Exception e) {
             getResponse().setStatus( Status.SERVER_ERROR_INTERNAL);
             getResponse().setEntity( new StringRepresentation (e.toString()))
         }
     }
     /**
      * Generate a UUID token
      */
     def generateToken() {

         return UUID.randomUUID() as String
     }
     
}

