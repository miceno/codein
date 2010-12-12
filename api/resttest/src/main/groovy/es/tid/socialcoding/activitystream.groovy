package es.tid.socialcoding

/**
 * 
 * REST interface for the activity stream
 * 
 * /activitystream              Site Activity Stream with all the activity of the site
 * /activitystream/{user}       User Activity Stream
 * TODO: add pagination or limit the results
 * 
 */
 
/*
import org.lpny.groovyrestlet.*
import org.lpny.groovyrestlet.builder.*
import org.apache.log4j.*
*/
 
builder.component{
        current.servers.add(protocol.HTTP, 8182)
        application(uri:"/activity"){
            router{
                // TODO: guard access to the activity. By now, use HTTP_Basic
                final String USER= "scott"
                final String PASSWORD= "tiger"
                def guard = guard(uri:"/", scheme:challengeScheme.HTTP_BASIC,
                        realm:"SocialCoding")
                // TODO: get the API credentials from the database
                guard.secrets.put( USER, PASSWORD.toCharArray())
                // guard.next = directory(autoAttach:false, root: "file://docs")
                restlet(uri:"/{user}", handle:{req,resp->
                    resp.setEntity("Account of user \"${req.attributes.get('user')}\"", mediaType.TEXT_PLAIN)
                })
                restlet(uri:"/", handle:{req, resp->
                    resp.setEntity("Global activity of the site",
                            mediaType.TEXT_PLAIN)
                })
            }
        }
    }.start()

