import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.atom.Category;
import org.restlet.ext.atom.Content;
import org.restlet.ext.atom.Entry;
import org.restlet.ext.atom.Feed;
import org.restlet.ext.atom.Generator;
import org.restlet.ext.atom.Person;
import org.restlet.ext.atom.Text;
import org.restlet.resource.StringRepresentation;

import com.noelios.restlet.Engine;


Generator getGenerator(){
	Generator generator = new Generator();
    generator.setName("Atom extension for Restlet.");
    generator.setUri(new Reference("http://restlet.org"));
    generator.setVersion(Engine.VERSION);
    return generator;
}


		Entry entry = new Entry();
		Person person = new Person();
	
String usuario= "áéíóú"
String email= "authorMail@mail.com"
String text= "probando un montón de texto de España?¿¡¡"
String title= "título"
List entries= []

		person.setName( usuario);
		person.setEmail( email);
		
		person.properties.each{ it.toString()}
		entry.getAuthors().add( person );
		
		entry.setContent( new Content().setInlineContent( new StringRepresentation( text)) );
		entry.setId( "entry#");
		entry.setPublished( new Date() );
		entry.setTitle( new Text(MediaType.TEXT_PLAIN, title) );
		entry.setSummary( text );
		
		entries.add(entry);

		Feed feed = new Feed();
		feed.getAuthors().add( person );
		feed.setGenerator( getGenerator() );
		feed.setId( "1");
		feed.setTitle(new Text(MediaType.TEXT_PLAIN, "feedTitle"));

		feed.getEntries().addAll(entries);
		feed.setUpdated( new Date() );

println feed.getText()