// package martino.atomdemo;


import org.apache.log4j.Logger;
import org.restlet.Restlet;
import org.restlet.data.Request;
import org.restlet.data.Response;

public class AtomRestlet extends Restlet{
	public AtomRestlet(final AtomFeedGenerator generator){
		this.generator = generator;
	}
	
	@Override  
    public void handle(Request request, Response response) {  
        response.setEntity(generator.getFeed());
        logger.info("[return feed] ");
    }
	
	protected AtomFeedGenerator generator;
	protected static Logger logger = Logger.getLogger(AtomRestlet.class);
}
