
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

String script= '''

import org.apache.log4j.PropertyConfigurator
PropertyConfigurator.configure(new File('log4j.properties').toURL())

import org.apache.log4j.Logger
import org.restlet.resource.StringRepresentation
import org.restlet.data.MediaType
import org.restlet.data.Status

import es.tid.socialcoding.dao.*
import es.tid.socialcoding.SocialCodingConfig
Logger log= Logger.getLogger( getClass().getName())

def config= SocialCodingConfig.newInstance().config 

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

builder.component{
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/socialcoding"){
        router{
            // The add a user
            restlet(uri:"/activity", handle: listAllActivity)
            restlet(uri:"/activity/{domain}/{user}", handle: listUserActivity)
        }
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
