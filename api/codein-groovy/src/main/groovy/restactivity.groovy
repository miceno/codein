
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

String logFileName= 'activitystream.log'
System.setProperty("socialcoding.log.filename", logFileName)

String script= '''

import es.tid.socialcoding.SocialCodingConfig

def config= SocialCodingConfig.newInstance().config

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.restlet.resource.StringRepresentation
import org.restlet.data.MediaType
import org.restlet.data.Status
import org.restlet.Router;  

import es.tid.socialcoding.dao.*
import es.tid.socialcoding.rest.*

Logger log= Logger.getLogger( getClass().getName())
Level log_level= Level.toLevel( config.root.log_level.toString())

    log.setLevel( log_level)                                  
    log.info "Log level set to ${log_level}"                  

final Integer PORT=config.rest.activity.port

// Create Database Connection
def db= new DbHelper().db

// List of all entries
def listHandle = { title,entries,req,resp->
    log.debug 'Listing -> ' + title
    
String result= SocialCodingAtomGenerator.generateEntries( entries, title, title)

    log.debug( "listado= ${result}")
    resp.setEntity( result,
                    MediaType.APPLICATION_ATOM_XML
                  )
    resp.setStatus( Status.SUCCESS_OK)
}

// Closure for the list of all entries
def listAllActivity= listHandle.curry( "All", db.dataSet('Entry').findAll())

// Closure for the list of users
def listUserActivity= { req,resp->
    
    user= req.attributes.get( 'user')
    domain= req.attributes.get( 'domain')
    log.debug "user $user , domain $domain"

    // findAll has a bug that prevents it using a variable in the expression
    // findAll{ it.ownerId == user } will not work
    // findAll{ it.ownerId == 'orestes'} will work
    // So I decided to change to directly querying the database
String filterTmpl= """
  select * from Entry 
  where 
    ownerId = '$user' 
    && ownerDomain= '$domain'
"""

List userEntries= db.rows( filterTmpl)

    if ( userEntries.size())
    {
        listUser= listHandle.curry( "User $user of domain $domain",  
                                    userEntries)
        listUser.call( req, resp)
    }
    else
        resp.setEntity( "No hay datos", MediaType.TEXT_HTML)
}

Router r
builder.component{
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/socialcoding"){
        r= router{ }
        r.attach( "/activity", ActivityStreamResource.class)
        r.attach( "/activity/{domain}/{user}", ActivityStreamResource.class)
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
