
import org.lpny.groovyrestlet.GroovyRestlet
import java.io.File

import org.restlet.ext.atom.Feed

import org.apache.log4j.PropertyConfigurator


PropertyConfigurator.configure(new File('log4j.properties').toURL())


class GlobalFeed {
    def username
    private AtomFeedGenerator generator_

    GlobalFeed( ){
        generator_ = new AtomFeedGenerator( 
                          "GlobalFeed", 
                          "Global Feed of SocialCoding", 
                          "socialcoding", 
                          "socialcoding@example.com");
    }

    Feed getFeed(){
        return generator_.getFeed()
    }

    public void test() throws IOException{
        def seed = System.currentTimeMillis() + Runtime.runtime.freeMemory()
        Random r= new Random( seed)
        for (int c = 0; c < 3; c++) {
            for (int i = 0; i < 15; i++) {
                generator_.authorName= "autor$c-$i"
                generator_.authorMail= "autor$c-$i@example.com"
                generator_.addEntry("$c,$i", "Global Title #$c,$i", getText("$c-$i"), "Category #"+i, new Date() - r.nextInt( 300) );
            }
        }
    }
    String getText(String i){
            return "Global This is the text to read for entry number " + i;
    }
}

class UserFeed {
    def username
    private AtomFeedGenerator generator_

    UserFeed( username){
    this.username = username
    generator_ = new AtomFeedGenerator("FieldID", "FeedTitle", this.username, this.username + "@mail.com");
    }

    Feed getFeed(){
        return generator_.getFeed()
    }

    public void test() throws IOException{
        for (int c = 0; c < 3; c++) {
            for (int i = 0; i < 5; i++) {
                generator_.authorName= username
                generator_.authorMail= "$username@example.com"
                generator_.addEntry("$c,$i", "Title #$c,$i", getText("$c-$i"), "Category #"+i, new Date());
            }
        }
    }

    String getText(String i){
            return "This is the User text to read for entry number " + i;
    }

}






String script= '''

final Integer PORT=8182

def activityHandle = { req,resp->
    def g= new GlobalFeed()
    g.test()
    resp.setEntity( g.getFeed() )
}

def userHandle = { req,resp->
    def u= new UserFeed( req.attributes.get('user'))
    u.test()
    resp.setEntity( u.getFeed())
}

builder.component{
final String USER= "scott"
final String PASSWORD= "tiger"
    current.servers.add(protocol.HTTP, PORT)
    // The REST Application with an initial URI
    application(uri:"/activity"){
        router{
            // The main activity stream
            def activityRestlet= restlet(uri:"/", handle: activityHandle )

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
