
package es.tid.socialcoding.push

import groovy.util.logging.Log4j

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.restlet.data.*
import org.restlet.resource.*

import es.tid.socialcoding.*

@Log4j
class GithubPush{

     final String DEFAULT_LABEL = "#codein"
     def config
     
    /*
     * @params payload   It is the payload received from the remote service.
     *                   It is a JSON payload
     * @params userModel It is the userModel [ domain: "userdomain", UUID: "userUUID"]
     * @return Map      It is a Map of elements: [  message: String, status: Status , mediatype: MediaType]
    */
    
    GithubPush(){
        SocialCodingConfig.newInstance().reload()
        config= SocialCodingConfig.newInstance().config
    }

    Map process( def userModel, String payload){
        def result= []
        log.debug "processing ${this.class.name}"
        log.trace "userModel= $userModel, payload='$payload'"
        
        def root = new JsonSlurper().parseText( payload)
        log.debug "root= $root"
        // Get all overall data from JSON
    def metadata= [
                    ownerId     : userModel.UUID,
                    ownerDomain : userModel.domain,
                    source: "${getSourceId()}:${root.repository.name}"
    ]
        log.debug "metadata=$metadata"

    def ETIQUETA = config."${this.class.name.toLowerCase()}.label" ?: DEFAULT_LABEL
    def TAG = /.*${ETIQUETA}?/
        log.debug "TAG=$TAG"
            
        // Get all the matching commits
        def messages= root.commits.findAll{ 
            it.message =~ TAG
            }.each{ // each commit that matches the TAG
                // Remap properties to create an entry
                log.debug "commit= $it"
                def entry= createEntry( it)
                
                log.debug "Entry with no metadata=$entry"
                // Add all the metadata
                entry.putAll( metadata)
                log.debug "Entry with metadata=$entry"
                
                // Format a text based on the commit
                def message= createText( it, root)
                entry.put( "content", message)
                log.debug "Entry with message=$entry"
                
                // TODO: Create store method to allow an update in case 
                // the record exists or an insert if it doesn't
                log.debug "TODO: storeEntry( metadata)"
            }
            
        // Process each matching commit and produce an Entry
        
        [ message: "Success", status: Status.SUCCESS_OK , mediatype: MediaType.TEXT_PLAIN ]
        
    }
    
    def getSourceId(){
        return "${this.class.name.toLowerCase()}"
    }
    /**
     * createText: get a string from a commit
     */
    def createText( commit, root) {
        def template= """New commit from <a href="mailto:${commit.author.email}">${commit.author.name}</a> 
               @<a href="${root.repository.url}">${root.repository.name}</a>: ${commit.message}
        """
        return template.toString()
    }
    
    
    /**
     * createEntry: take a JSON object representing a commit and map their properties to Entry fields
     */
     def createEntry( commit) {
        
        def entry=[:]
        entry.with{
            id= commit.url
            authorId= commit.author.name 
            authorLink= "mailto:${commit.author.email}"
            link= commit.url
            updated= commit.timestamp
            published= commit.timestamp
        }
        return entry
     }
     
     /**
      * storeEntry: store an Entry in the DB.
      * allow an update in case 
      * the record exists or an insert if it doesn't
      */
     def method() {
        
     }
     
     
}