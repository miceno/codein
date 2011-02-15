
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

import es.tid.socialcoding.SocialCodingConfig
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.producer.*

class UserResource extends Resource
{
     private Logger log = Logger.getLogger( getClass().getName())
     private def userModel
     private def uuid
     private def domain
     
     // Generate a DOM document representing the list of  
     // items.  
     def userTable= new UserDAO( db: new DbHelper().db)

     UserResource(Context context, Request request, Response response) {  
        super(context, request, response);  
  
        // Get the "itemName" attribute value taken from the URI template  
        // /items/{itemName}.  
        domain = (String) getRequest().getAttributes().get("domain");  
        uuid   = (String) getRequest().getAttributes().get("uuid");  
        
        this.userModel= null
        def query= [ UUID: uuid, domain: domain ]
        def result= userTable.findBy( query )
        this.userModel = result?.size() > 0 ? result[0] : null
        
        // Define the supported variant.  
        getVariants().add(new Variant(MediaType.TEXT_HTML));  

        // By default a resource cannot be updated.  
        setReadable(true);  

        // By default a resource cannot be updated.  
        setModifiable(true);  
    }  

    /** 
     * Returns a listing of the element. 
     */  
/*
    Representation represent(Variant variant) throws ResourceException {  
        // Generate the right representation according to its media type.  
        if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {  
            try {  

                def rep= buildHtmlRepresentation( this.userModel) 

                // Returns the XML representation of this document.  
                return rep;  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        return null;  
    }  
*/

    def buildHtmlRepresentation( userModel)
    {
        assert userModel != null
    String userString= "${userModel.domain}:${userModel.UUID}"
        log.info( "buildHtmlRepresentation: user ${userString}")
    def titulo= "User ${userString}"
    
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
    
    // POST: Create a new user
    public void acceptRepresentation( Representation r)
    {
        // Set initial message
    String userString= "${domain}:${uuid}"
        log.info( "POST user $userString")
        def req= getRequest()
        def resp= getResponse()
        try {
            log.debug( "Representation ${r.dump()}")
            Form form = new Form(r);
            log.debug( "Start to process form: $form")
            if( form.size()){
                // {save the new user to the database}
                def checkUserQuery= [ UUID: uuid, domain: domain ]
                def updateUserStmt= form.getValuesMap()
                if( !userModel){
                    // User does not exist
                    log.info( "Adding User $userString")
                def createUserQuery = checkUserQuery + updateUserStmt
                    userTable.create( createUserQuery)
                }
                else{
                    // User exists 
                    log.info( "Updating User $userString")
                    userTable.update( updateUserStmt, checkUserQuery)
                }

                // userModel of the new representation
                userModel= checkUserQuery + updateUserStmt
                
                // Enqueue a task for all the feeds of the user
                new FeedTaskProducer().produceUserTask( userModel)
                // Show representation of new resource
                log.debug( "New userModel: $userModel")
                resp.setStatus(Status.SUCCESS_OK);
                // You could support multiple representation by using a
                // parameter
                // in the request like "?response_format=xml"
                Representation rep = buildHtmlRepresentation(userModel);
                resp.setEntity(rep);
            } else {
                    resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        } catch (Exception e) {
                    resp.setStatus(Status.SERVER_ERROR_INTERNAL);
                    resp.setEntity( new StringRepresentation (e.toString()))
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
         try {
             getResponse().setEntity( new StringRepresentation( "Pending TODO Delete operation"));
             if (null == this.userModel) {
                 getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                 return;
             }
             // :TODO {delete the user from the database}
             getResponse().setStatus(Status.SUCCESS_OK);
             } catch (Exception e) {
                 getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
         }
     }
    
}

