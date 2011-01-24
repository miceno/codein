
import java.text.*
import groovy.xml.MarkupBuilder

// this is the atom generator thats going to do the hard
// work, of converting entries into atom.
class SocialCodingAtomGenerator
{
    static final String SOCIALCODING_CREATOR = 'socialcoding'
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
        MarkupBuilder xml = new MarkupBuilder(writer);

        // feed is the root level. In a namespace
        xml.feed(xmlns:'http://www.w3.org/2005/Atom') {

            // add the top level information about this feed.
            title "$theTitle"
            id "$theId"
            link(href: SOCIALCODING_URL)
            author {
               name( SOCIALCODING_CREATOR)
            }
            updated sdf.format(new Date());

            // for each entry we need to create an entry element
            entries.each { item ->
                 entry {
                     title item.title
                     id item.entryId
                     if ( item?.authorId )
                        author{ name item.authorId; uri item.authorLink; }
                     if ( item?.updated )
                        updated sdf.format(new Date( item.updated ))
                     if ( item?.published )
                        published sdf.format(new Date( item.published))
                     summary item.content
                     content item.content
                     link(href:item.link)
                 }
            }
        }

        // lastly give back a string representation of the xml.
        return writer.toString();
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
