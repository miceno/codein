
package es.tid.socialcoding.push

import groovy.util.logging.Log4j

import org.restlet.data.*
import org.restlet.resource.*


@Log4j
class GithubPush{

    /*
     * @params payload   It is the payload received from the remote service
     * @params userModel It is the userModel [ domain: "userdomain", UUID: "userUUID"]
     * @return Map      It is a Map of elements: [  message: String, status: Status , mediatype: MediaType]
    */
    Map process( def userModel, String payload){
        def result= []
        log.debug "processing ${this.class.name}"
        log.trace "userModel= $userModel, payload='$payload'"
        result= [ message: payload, status: Status.SUCCESS_OK , mediatype: MediaType.TEXT_PLAIN ]
        return result
    }
    
}