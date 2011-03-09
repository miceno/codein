
package es.tid.socialcoding.rest

import java.text.*
import groovy.xml.*
import org.apache.commons.lang.StringEscapeUtils


// this is the atom generator thats going to do the hard
// work, of converting entries into atom.
class SocialCodingAtomGenerator
{
    static final String SOCIALCODING_CREATOR = 'socialcoding'
    static final Integer SUMMARY_LENGTH= 50
    static final String SOCIALCODING_URL= 'http://socialcoding.tid.es'


    static String generateEntries(def entries, def theTitle, def theId)
    {
        // TODO: Load SOCIALCODING_URL from configuration. It is the root of
        // socialcoding site

        // we need a simple date formatter that formats as XML.
        SimpleDateFormat sdf =
              new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        // create the builder as before.
        StringWriter writer = new StringWriter();
        def builder = new StreamingMarkupBuilder();

        builder.encoding= "UTF-8"
        // feed is the root level. In a namespace
        def generatedFeed = {
            def escape= { text->  mkp.yieldUnescaped( StringEscapeUtils.escapeXml(text)) }
            
            mkp.xmlDeclaration( version: '1.0') 
        feed(xmlns:'http://www.w3.org/2005/Atom') {
            // Feed metadata
            // add the top level information about this feed.
            title { 
                escape( theTitle)
            }
            // Id of the feed
            id { escape( theId)  }
            // Link of the feed
            link( rel: "alternate", href: SOCIALCODING_URL)
            // Author Person
            author {
               name( SOCIALCODING_CREATOR)
            }
            // Updated date, but not published date
            updated sdf.format(new Date());

            // for each entry we need to create an entry element
            entries.each { item ->
                 entry {
                     // Title
                     title { 
                        escape( item.title)
                     }
                     // Entry Id
                     id item.id
                     
                     // Author Person
                     if ( item?.authorId )
                        author{ 
                           name { 
                            escape( item.authorId)
                            }
                           uri item.authorLink; 
                        }
                     // Owner/contributor Person 
                     // it is the User that contributed the entry to SocialCoding
                     if( item?.ownerId)
                         contributor { 
                            name { escape( item.ownerId) }
                         }
                         
                     // Published date in case it exists
                     if ( item?.published )
                            published sdf.format(new Date( item.published)) 
                     // Updated date in case it exists
                     if ( item?.updated )
                        updated sdf.format(new Date( item.updated ))
                     // Summary of the entry
                     summary { 
                         escape( SocialCodingAtomGenerator.summarize( item.content))
                     }
                     
                     // Content as html
                     content ( type: "html"){ escape( item.content) }
                     // Link 
                     link(rel: "alternate", href:item.link)
        }}  } }

        // lastly give back a string representation of the xml.
        writer << builder.bind( generatedFeed)
        return writer.toString()
    }

    static String summarize(String input){
        if(input==null)
            return "";

        if(input.length()>SUMMARY_LENGTH){
            return input.substring(0, SUMMARY_LENGTH) + "...";
        }
        else
            return input;
    }
}



/*
mientries= [
    [ title: "entri1",
      link:"http://www.tid.es",
      entryId : "socialcoding,2007,http://www.tid.es",
      updated : 1234537727,
      content : "lorem ipsum"
    ],
    [ title: "entri2",
      link:"http://www.tid.es",
      entryId : "socialcoding,2007,http://www.tid.es",
      updated : 123537727,
      userId  : "miceno",
      userLink: "http://www.tid.es/miceno",
      content : "2 lorem ipsum"
    ],
    [ title: "entri3",
      link:"http://www.tid.es",
      entryId : "socialcoding,2007,http://www.tid.es",
      updated : 12337727000,
      published: 12337727,
      content : "el mi lorem ipsum"
    ]
]

mientries.each{ println it.link }

println SocialCodingAtomGenerator.generateEntries( mientries, 
       "titulo feed", 
       "feedid,2007,http://correo.tid.es")

*/
