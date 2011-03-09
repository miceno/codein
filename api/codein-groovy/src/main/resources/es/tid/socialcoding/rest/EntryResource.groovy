
package es.tid.socialcoding.rest

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

import org.apache.log4j.Logger

import groovy.util.logging.Log4j

import es.tid.socialcoding.SocialCodingConfig
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.producer.*


/**
 * EntryResource: A Resource that exposes a CRUD interface to Entries
 * 
 * URL: /socialcoding/entry/{id}
 * POST: create a new Entry
 * PUT: update an existing Entry
 * GET: get an entry
 * DELETE: delete an entry
 * 
 * Allowed attributes are:
 * String id: unique id of the entry
 * String authorId: name of the user that appears as author of the entry
 * String authorLink: link to the page of the author
 * String title: title of the entry
 * String link: link to the entry
 * int published: Date in miliseconds since 1970
 * int updated: Date in miliseconds since 1970
 * String content: text content with HTML entities encoded
 * String source: 
 * String ownerId: UUID of the owner of the entry, that is, the user of 
 *                      SocialCoding that requested the feed
 * String ownerDomain: domain of the owner of the entry, that is, the user 
 *                      of SocialCoding that requested the feed
 
 */

@Log4j
class EntryResource extends Resource
{
      private def entryModel
      private def id

      // Generate a DOM document representing the list of  
      // items.  
      def entryTable= new EntryDAO( db: new DbHelper().db)

      EntryResource(Context context, Request request, Response response) {  
         super(context, request, response);  

         // Get the "id" attribute value taken from the URI template  
         // /entry/{id}.  
         id= getAttribute( request, RestHelper.STR_ID)

         this.entryModel= null
         def query= [ id: id ]
         def result= entryTable.findBy( query )
         this.entryModel = result?.size() > 0 ? result[0] : null

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

     Representation represent(Variant variant) throws ResourceException {  
         // Generate the right representation according to its media type.  
         if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {  
             def rep
             try {  

                 if( this.entryModel){
                     rep= buildHtmlRepresentation( this.entryModel) 
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

    /**
     * getAttribute: get an attribute from the url 
    */
    def getAttribute( Request request, String name) {
        def value = ""
        value= request.getAttributes().get( name, null)
        (value ? Reference.decode( value) : value )
    }
 

     def buildHtmlRepresentation( entryModel)
     {
         assert entryModel != null
         log.info( "buildHtmlRepresentation: entry ${entryModel.id}")
     def titulo= "Entry ${entryModel.title}"

         def writer= new StringWriter ()
         def builder= new groovy.xml.MarkupBuilder( writer)
         def html= builder.html{
           head { title titulo.toString() }
           body {
               h1 titulo.toString()
               p "This is entry: " + titulo.toString()
               ul { 
                    entryModel.each { k,v -> 
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
         log.info( "POST entry ${id}")
         log.debug( "Representation ${r.dump()}")
         def req= getRequest()
         def resp= getResponse()

         // {save the new entry to the database}
         try {
             if( entryModel){
                 // Entry exists 
                 def strError= "Resource already exists: ${id}"
                 log.error( strError)
                 throw new ResourceException( Status.CLIENT_ERROR_BAD_REQUEST, strError)
             }

             Form form = new Form(r);
             log.debug( "Start to process form: $form")
             
             // Entry does not exists
         def checkEntryQuery= [ id: id ]
         def updateEntryStmt= form.getValuesMap()
             entryModel = checkEntryQuery + updateEntryStmt
             log.info( "Adding to table Entry: ${id}")
             log.debug( "New entryModel: $entryModel")

             entryTable.create( entryModel)

             resp.setStatus(Status.SUCCESS_OK);
             // Show representation of new resource
             // You could support multiple representation by using a
             // parameter
             // in the request like "?response_format=xml"
             Representation rep = buildHtmlRepresentation(entryModel);
             resp.setEntity(rep);

             // Enqueue a task for all the feeds of the user
             new FeedTaskProducer().produceEntryTask( entryModel)
         } 
         catch( ResourceException e){
             getResponse().setStatus( e.status);
             getResponse().setEntity( new StringRepresentation (e.message))                  
         } 
         catch (Exception e) {
             resp.setStatus(Status.SERVER_ERROR_INTERNAL);
             resp.setEntity( new StringRepresentation (e.toString()))
         }
     }


     // PUT is an update
     public void storeRepresentation( Representation r){
         // Set initial message
         log.info( "PUT entry ${id}")
         log.debug( "Representation ${r.dump()}")
         def req= getRequest()
         def resp= getResponse()
         try {
             // Entry exists 
             if( !entryModel){
                 // Entry does not exists 
                 def strError= "Entry does not exists: ${id}"
                 log.error( strError)
                 throw new ResourceException( Status.CLIENT_ERROR_NOT_FOUND, strError)
             }

             Form form = new Form(r);
             log.debug( "Start to process form: $form")
             // {save the entry to the database}
             def checkEntryQuery= [ id: id ]
             def updateEntryStmt= form.getValuesMap()
             
             log.info( "Updating table Entry: ${id}")
             log.debug( "Old entryModel: $entryModel")
             entryTable.update( updateEntryStmt, checkEntryQuery)

             // entryModel of the new representation
             entryModel= checkEntryQuery + updateEntryStmt
             
             // Show representation of new resource
             log.debug( "Updated entryModel: $entryModel")
             resp.setStatus(Status.SUCCESS_OK);
             // You could support multiple representation by using a
             // parameter
             // in the request like "?response_format=xml"
             Representation rep = buildHtmlRepresentation(entryModel);
             resp.setEntity(rep);
             
             // Enqueue a task for all the feeds of the user
             new FeedTaskProducer().produceEntryTask( entryModel)
                 
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

     // DELETE
     /**
      * Handle a DELETE Http Request. Delete an existing user
      * 
      * @param entity
      * @throws ResourceException
      */
      public void removeRepresentations() throws ResourceException {
          log.info "DELETE entry: ${id}"
          try {
              if (null == this.entryModel) {
                  def strError= "Resource does not exists: ${id}"
                  log.error( strError)
                  throw new ResourceException( Status.CLIENT_ERROR_NOT_FOUND, strError)
              }

              // Delete the entry
              entryTable.delete( [ id: id])
              getResponse().setStatus( Status.SUCCESS_OK)
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

}


