/**
 * 
 * REST interface for the activity stream
 * 
 * /activitystream              Site Activity Stream with all the activity of the site
 * /activitystream/{user}       User Activity Stream
 * TODO: add pagination or limit the results
 * 
 */
 
builder.component{
        current.servers.add(protocol.HTTP, 8182)
        application(uri:""){
            router{
                def guard = guard(uri:"/docs", scheme:challengeScheme.HTTP_BASIC,
                        realm:"Restlet Tutorials")
                guard.secrets.put("scott", "tiger".toCharArray())
                guard.next = directory(root:"", autoAttach:false)
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

