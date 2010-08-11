import java.util._
import org.apache.http.client._,methods._,params._,entity._
import org.apache.http.impl._,client._
import org.apache.http.auth._
import org.apache.http.protocol._
import org.apache.http.message._

object httpclient
{

	val SUDO = "sudo"
	val PASSWD = "secret"
	val URL = "http://techcamp.hi.inet/statusnet/api/statuses/update.xml"
	val SOURCE_PARAM = "source"
	val STATUS_PARAM = "status"

	def main(args: Array[String]) {

		//set up post params
                val params = new ArrayList[BasicNameValuePair]
                params.add(new BasicNameValuePair(SOURCE_PARAM,"scala"))
                params.add(new BasicNameValuePair(STATUS_PARAM,args(1)))
		
		val client = new DefaultHttpClient()
		val context = new BasicHttpContext()
		val post = new HttpPost(URL)
		post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));


		//set up http basic authentication
		val provider = new BasicCredentialsProvider()
		val creds = new UsernamePasswordCredentials(SUDO + "#" + args(0), PASSWD)
		val scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM)
		provider.setCredentials(scope,creds)
		client.setCredentialsProvider(provider)

		try {
			val responseBody = client.execute(post,context)
			println(responseBody)
		} catch {
			case e =>  {
				println("exception:" + e.getMessage())
				e.printStackTrace()
			}
		}

		//free resources
		client.getConnectionManager().shutdown()

	}
}
