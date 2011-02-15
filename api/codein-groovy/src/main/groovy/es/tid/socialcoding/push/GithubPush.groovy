
package es.tid.socialcoding.push

import groovy.util.logging.Log4j

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.restlet.data.*
import org.restlet.resource.*


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
        
        def ETIQUETA = config."${this.class.name.toLowerCase()}.label" ?: DEFAULT_LABEL
        def TAG = /.*${ETIQUETA}?/
        
        // Get all overall data from JSON
        
        // Get all the matching commits
        def messages= root.commit.findAll{ 
            it.message =~ TAG
            }.each{
                message= transformMessage( it)
                entryTable.store( it)                
            }
            
        // Process each matching commit and produce an Entry
        
        [ message: "Success", status: Status.SUCCESS_OK , mediatype: MediaType.TEXT_PLAIN ]
        
    }
    
    /**
     * transformMessage: transforms a message into an Entry
     */
    
}