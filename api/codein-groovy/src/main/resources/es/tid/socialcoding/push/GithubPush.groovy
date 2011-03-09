
package es.tid.socialcoding.push

import groovy.util.logging.Log4j

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.restlet.data.*
import org.restlet.resource.*

import es.tid.socialcoding.*
import es.tid.socialcoding.dao.*

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
    
    def entryTable
    
    GithubPush(){
        SocialCodingConfig.newInstance().reload()
        config= SocialCodingConfig.newInstance().config
        entryTable= new EntryDAO( db: new DbHelper().db)
    }

    Map process( def userModel, String payload){
        def result= [ message: "Success", status: Status.SUCCESS_OK , mediatype: MediaType.TEXT_PLAIN ]
        log.debug "Web Push process ${this.class.name}"
        log.trace "userModel= $userModel, payload='$payload'"
        
        def root = new JsonSlurper().parseText( payload)
        
        log.debug "JSON parsed object= $root"
        // Get all overall data from JSON
    def metadata= [
                    ownerId     : userModel.UUID,
                    ownerDomain : userModel.domain,
                    source      : "${getSourceId()}:${root.repository.name}".toString()
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
                log.debug "Entry with message=${entry}"
                                
                // TODO: Create store method to allow an update in case 
                // the record exists or an insert if it doesn't
                storeEntry( entry)
            }
            
        // Process each matching commit and produce an Entry
        
        [ message: "Success", status: Status.SUCCESS_OK , mediatype: MediaType.TEXT_PLAIN ]
        
    }
    
    def getSourceId(){
        return this.class.simpleName.toLowerCase()
    }
    /**
     * createText: get a string from a commit
     */
    def createText( commit, root) {
        def template= """New commit from
        <a href="mailto:${commit.author.email.toString()}">${commit.author.name.toString()}</a> 
               @<a href="${root.repository.url.toString()}">${root.repository.name.toString()}</a>: 
               ${commit.message.toString()}
        """
        return template.toString()
    }
    
    
    /**
     * createEntry: take a JSON object representing a commit and map their properties to Entry fields
     */
     def createEntry( commit) {
        
        def entry=[:]
        entry.with{
            id= commit.url.toString()
            authorId= commit.author.name.toString()
            title= "New commit from GitHub"
            authorLink= commit.author.email.toString()
            link= commit.url.toString()
            published = es.tid.socialcoding.Helper.getDate( commit.timestamp).getTime()
            updated= published
        }
        return entry
     }
     
     /**
      * storeEntry: store an Entry in the DB.
      *             allow an update in case the
      *             record exists or an insert if it doesn't
      */
     def storeEntry( entry) {
        if( entryTable.findBy( [ id: entry.id]).size()){
            log.debug "UPDATING entry with id=${entry.id}"
            entryTable.update( entry, [ id: entry.id])
        }
        else{
            log.debug "INSERTING entry with id=${entry.id}"
            entryTable.create( entry)
        }
     }
     
}