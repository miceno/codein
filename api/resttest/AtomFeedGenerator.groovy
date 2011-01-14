// package martino.atomdemo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.atom.Category;
import org.restlet.ext.atom.Content;
import org.restlet.ext.atom.Entry;
import org.restlet.ext.atom.Feed;
import org.restlet.ext.atom.Generator;
import org.restlet.ext.atom.Person;
import org.restlet.ext.atom.Text;
import org.restlet.ext.atom.Source;
import org.restlet.resource.StringRepresentation;

import com.noelios.restlet.Engine;

/**
 * Store a list of entries which may be retrieved as an Atom Feed representation
 * for being served by a restlet server.
 */
public class AtomFeedGenerator {
	
	protected Map<String,Category> categories;
	protected List<Entry> entries;
	
	protected String feedId;
	protected String feedTitle;
	protected String authorName;
	protected String authorMail;

	protected int SUMMARY_LENGTH = 100;
	
    final String LINK_PREFIX='http://correo.tid.es'

	public AtomFeedGenerator(String feedId, String feedTitle, String authorName, String authorMail){
		this.feedId = feedId;
		this.feedTitle = feedTitle;
		this.authorName = authorName;
		this.authorMail = authorMail;
		
		clear();
	}
	
	public void clear(){
		categories = new HashMap<String,Category>();
		entries = new Vector<Entry>();
	}
	
	public void addEntry(String id, String title, String text, String category, Date date){
		// Retrieve existing category or add it to the feed
		Category c = null;

		if(category!=null && !"".equals(category)){
			if(categories.containsKey(category)){
				c = categories.get(category);
			}
			else{
				c = new Category();
				c.setLabel(category);
				categories.put(category, c);
			}
		}

		// Handle entry
		Entry entry = new Entry();
		entry.getAuthors().add( getAuthor() );
		
		if(c!=null)
			entry.getCategories().add( c );

		entry.setContent( getContent(text) );
		entry.setId( "entry#" + id );
		entry.setPublished( date);
		entry.setTitle( new Text(MediaType.TEXT_PLAIN, title) );
		entry.setSummary( summarize(text) );
        // entry.setLink( LINK_PREFIX + "/$id");
        entry.setSource( new Source( ).setGenerator( getGenerator()));
		
		entries.add(entry);
	}
	
	protected String summarize(String input){
		if(input==null)
			return "";
		
		if(input.length()>SUMMARY_LENGTH){
			return input.substring(0, SUMMARY_LENGTH) + "...";
		}
		else
			return input;
	}
	
	/** Return a feed representation to be served by a restlet-enabled server. */
	public Feed getFeed(){
		Feed feed = getConfiguredFeed();
		
		for(Map.Entry<String,Category> e: categories.entrySet()) {
	        feed.getCategories().add( e.getValue() );
	    }

		feed.getEntries().addAll(entries);
		feed.setUpdated( new Date() );
		
		return feed;
	}
	
	/******************************** UTILITIES FOR DEFINING THE FEED ***********************************/
	
	private Feed getConfiguredFeed(){
		Feed feed = new Feed();
		feed.getAuthors().add( getAuthor() );
		feed.setGenerator( getGenerator() );
		feed.setId(feedId);
		feed.setTitle(new Text(MediaType.TEXT_PLAIN, feedTitle));
		return feed;
	}
	
	private Content getContent(String txt){
		Content content = new Content();
        content.setInlineContent(new StringRepresentation(txt, MediaType.TEXT_PLAIN));
        return content;
	}
	
	private Person getAuthor(){
		Person person = new Person();
		person.setName(authorName);
		person.setEmail(authorMail);
		return person;
	}
	
	private Generator getGenerator(){
		Generator generator = new Generator();
        generator.setName("Atom extension for Restlet.");
        generator.setUri(new Reference("http://restlet.org"));
        generator.setVersion(Engine.VERSION);
        return generator;
	}
}
