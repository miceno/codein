// package martino.atomdemo;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.Server;
import org.restlet.data.Protocol;

import org.apache.log4j.PropertyConfigurator

public class AtomLauncher {
	protected Server server_;
	protected AtomFeedGenerator generator_;
	protected static final int PORT = 8666;
	protected static final String HOST = "http://localhost:" + PORT;
	protected static final String URI = "/atom";
	
	public static void main(String[] args) {
        PropertyConfigurator.configure(new File('log4j.properties').toURL())

		AtomLauncher test = new AtomLauncher();
		try {
			test.init();
			test.test();
			//test.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void init() throws Exception {
		generator_ = new AtomFeedGenerator("FieldID", "FeedTitle", "AuthorName", "author@mail");

		
		Application app = getApp();
		List<Protocol> protocols = new Vector<Protocol>();
		protocols.add(Protocol.HTTP);
		
		server_ = new Server(protocols, PORT, app);		
		server_.start();
	}
	
    protected void stop() throws Exception {
		server_.stop();	
	}
    
    protected Application getApp(){
    	return new Application(){
    		public Restlet createRoot() {
    			Router r = new Router();
    			r.attach(URI, new AtomRestlet(generator_));
    	        return r;
    	    }
    	};
    }
	
	public void test() throws IOException{
		for (int c = 0; c < 3; c++) {
			for (int i = 0; i < 5; i++) {
				generator_.addEntry("Title #" +i, getText(i), "Category #"+i, new Date());
			}
		}
	}
	
	public String getText(int i){
		return "This is the text to read for entry number " + i;
	}
}
