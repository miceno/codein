
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

import org.restlet.ext.atom.Feed

import org.apache.log4j.PropertyConfigurator


PropertyConfigurator.configure(new File('log4j.properties').toURL())

class UserFeed {
    def username
    private AtomFeedGenerator generator_

    UserFeed(){
    generator_ = new AtomFeedGenerator("FieldID", "FeedTitle", "AuthorName", "author@mail");
    }

    Feed getFeed(){
        return generator_.getFeed()
    }

    public void test() throws IOException{
        for (int c = 0; c < 3; c++) {
            for (int i = 0; i < 5; i++) {
                generator_.addEntry("Title #" +i, getText(i), "Category #"+i, new Date());
            }
        }
    }

}






String script= '''

def userHandle = { req,resp->
    resp.setEntity( 
         new UserFeed( username: req.attributes.get('user')).getFeed() 
                     )
    // resp.setEntity( new UserFeed( username: req.attributes.get('user')).getFeed() ,mediaType.TEXT_PLAIN )
}

builder.component{
final String USER= "scott"
final String PASSWORD= "tiger"
    current.servers.add(protocol.HTTP, 8182)
    // The REST Application with an initial URI
    application(uri:"/activity"){
        router{
            // The main activity stream
            def activityRestlet= restlet(uri:"/", handle:{req, resp->
                resp.setEntity("Global activity of the site",
                        mediaType.TEXT_PLAIN)
            })
            // The User activity stream
            restlet(uri:"/{user}", handle: userHandle)

            // TODO: guard access to the activity. By now, use HTTP_Basic
            def guard = guard(uri:"/", scheme:challengeScheme.HTTP_BASIC,
                    realm:"SocialCoding")
            // TODO: get the API credentials from the database
            guard.secrets.put( USER, PASSWORD.toCharArray())
            guard.next= activityRestlet
        }
    }
}.start()

'''

InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"));

gr= new GroovyRestlet()
gr.build( is)
