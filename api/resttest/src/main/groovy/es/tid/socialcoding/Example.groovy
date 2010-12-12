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
 
import org.lpny.groovyrestlet.*
import org.lpny.groovyrestlet.builder.*
import org.apache.log4j.*
// import org.apache.log4j.PropertyConfigurator

 
PropertyConfigurator.configure(new File('log4j.properties').toURL())


builder.component{
        current.servers.add(protocol.HTTP, 8182)
        application(uri:""){
            router{
                def guard = guard(uri:"/docs", scheme:challengeScheme.HTTP_BASIC,
                        realm:"Restlet Tutorials")
                guard.secrets.put("scott", "tiger".toCharArray())
                guard.next = restlet(uri: "/docs")
                // guard.next = directory(autoAttach:false, root: "file://docs")
                restlet(uri:"/users/{user}", handle:{req,resp->
                    resp.setEntity("Account of user \"${req.attributes.get('user')}\"",
                            mediaType.TEXT_PLAIN)
                })
                restlet(uri:"/users/{user}/orders", handle:{req, resp->
                    resp.setEntity("Orders or user \"${req.attributes.get('user')}\"",
                            mediaType.TEXT_PLAIN)
                })
                restlet(uri:"/users/{user}/orders/{order}", handle:{req, resp->
                    def attrs = req.attributes
                    def message = "Order \"${attrs.get('order')}\" for User \"${attrs.get('user')}\""
                    resp.setEntity(message, mediaType.TEXT_PLAIN)
                })
            }
        }
    }.start()

