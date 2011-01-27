
import org.restlet.ext.atom.*
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

import org.apache.log4j.PropertyConfigurator


PropertyConfigurator.configure(new File('log4j.properties').toURL())

String script= '''

import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.Logger
import org.restlet.resource.StringRepresentation
import org.restlet.data.MediaType
import org.restlet.data.Status

import es.tid.socialcoding.dao.*

final Integer PORT=8182

Logger log= Logger.getLogger( getClass().getName())

// Create EntryFeedDAO
def helper= new DbHelper()
def entryTable= helper.db.dataSet( 'Entry')

// List of all entries
def listHandle = { title,scope,req,resp->
    log.debug 'Listing -> ' + title
    
String result= SocialCodingAtomGenerator.generateEntries( scope, title, title)

    log.debug( "listado= ${result}")
    resp.setEntity( new StringRepresentation( result,
                            MediaType.TEXT_HTML) 
                  )
    resp.setStatus( Status.SUCCESS_OK)
}

// Closure for the list of all entries
def listAllActivity= listHandle.curry( "All", entryTable.findAll())

// Closure for the list of users
def listUserActivity= { req,resp->
    
def userEntries= entryTable.findAll( )
    listUser= listHandle.curry( "User "+ req.attributes.get( 'user'), 
                                userEntries)
    listUser.call( req, resp)
}

builder.component{
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/socialcoding"){
        router{
            // The add a user
            restlet(uri:"/activity", handle: listAllActivity)
            restlet(uri:"/activity/{user}", handle: listUserActivity)
        }
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
