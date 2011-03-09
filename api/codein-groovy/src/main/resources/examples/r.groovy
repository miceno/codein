

import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

String logFileName= 'r.log'
System.setProperty("socialcoding.log.filename", logFileName)

import org.restlet.Context;  
import org.restlet.data.Form;  
import org.restlet.data.MediaType;  
import org.restlet.data.Request;  
import org.restlet.data.Response;  
import org.restlet.data.Status;  
import org.restlet.resource.Resource
import org.restlet.resource.DomRepresentation;  
import org.restlet.resource.Representation;  
import org.restlet.resource.ResourceException;  
import org.restlet.resource.Variant;  
import org.apache.log4j.Logger

String script= '''

import org.apache.log4j.PropertyConfigurator
PropertyConfigurator.configure(new File('log4j.properties').toURL())

import org.apache.log4j.Logger
import org.restlet.Router;  
import org.restlet.resource.Resource
import org.restlet.data.MediaType
import org.restlet.data.Status
import org.restlet.resource.StringRepresentation

import es.tid.socialcoding.dao.*
import es.tid.socialcoding.rest.*
import es.tid.socialcoding.SocialCodingConfig

Logger log= Logger.getLogger( getClass().getName())
def config= SocialCodingConfig.newInstance().config

final Integer PORT=9999

class UserResource{
}

Router r
builder.component{
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/socialcoding"){
        r= router{
/*
            listUser = resource( uri:"/user/{domain}/{user}", ofClass:UserResource)
            println listUser.getClass().getName()
*/
        }
        // a list of all users
        r.attach( "/user", UsersResource.class)
        r.attach( "/user/{domain}", UsersResource.class)
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)

