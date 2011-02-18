
package es.tid.socialcoding.rest

import org.restlet.Context;  
import org.restlet.data.Form;  
import org.restlet.data.MediaType;  
import org.restlet.data.Method;  
import org.restlet.data.Request;  
import org.restlet.data.Response;  
import org.restlet.resource.Resource
import org.restlet.resource.ResourceException;  
import org.restlet.resource.Variant;  

import org.apache.log4j.Logger

import groovy.time.*
import groovy.util.logging.Log4j
import es.tid.socialcoding.dao.*
import es.tid.socialcoding.SocialCodingConfig

@Log4j
class PaginateResource extends Resource
{
    private final String DEFAULT_SIZE=  '100'
    private final String DEFAULT_START= '0'

    private def config
    
    // Start of listing
    def start
    
    // Size of listing
    def size
    
    PaginateResource(Context context, Request request, Response response) {  
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
        this.start = form.getFirstValue( RestHelper.STR_START, DEFAULT_START) as Integer
        this.size = form.getFirstValue( RestHelper.STR_SIZE, DEFAULT_SIZE) as Integer
        
        log.info( "Paginate Resource [start=$start, size=$size]")
    }  

    def setPagination( def table){
        table.setLimit( this.size)
        table.setOffset( this.start)
    }
}

