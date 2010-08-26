package codein
import codein.logger.Logs
import io.Source
import java.util._
import java.io._
import org.apache.http.{ HttpResponse, HttpEntity }
import org.apache.http.HttpStatus._
import org.apache.http.client._, methods._, params._, entity._
import org.apache.http.impl._, client._
import org.apache.http.auth._
import org.apache.http.auth.AuthScope._
import org.apache.http.protocol._
import org.apache.http.protocol.HTTP._
import org.apache.http.message._

// httpclient singleton
object httpclient extends Logs {

  val SUDO = "sudo"
  val PASSWD = "secret"
  val SOURCE_PARAM = "source"
  val SOURCE_VALUE = "codein"
  val STATUS_PARAM = "status"

  val API_ROOT = "http://techcamp.hi.inet/statusnet/api/statuses/"
  val UPDATE_SUFFIX = "update.xml"
  val PUBLIC_TIMELINE_SUFFIX = "public_timeline.xml"
  val FRIENDS_TIMELINE_SUFFIX = "friends_timeline.xml"

  private def doPOST(url: String, auth: String, params: ArrayList[BasicNameValuePair]) {

    //set up http basic authentication
    val provider = new BasicCredentialsProvider
    provider.setCredentials(
      new AuthScope(ANY_HOST, ANY_PORT, ANY_REALM),
      new UsernamePasswordCredentials(SUDO + "#" + auth, PASSWD)
      )

    //set up http client
    val client = new DefaultHttpClient
    val post = new HttpPost(url)
    post.setEntity(new UrlEncodedFormEntity(params, UTF_8));
    client.setCredentialsProvider(provider)

    //do request
    try {
      val res: HttpResponse = client.execute(post, new BasicHttpContext)
      val status = res getStatusLine

      //check return code and log result
      status getStatusCode match {
        case SC_OK => {
          debug("OK")
          val istream: InputStream = res.getEntity.getContent
          val content: String = Source.fromInputStream(istream).getLines.mkString
        }
        case _ => debug("NOK")
      }
    } catch {
      case e => {
        error(e)
      }
    }

    //free resources
    client.getConnectionManager.shutdown
  }

  /**
   * Updates the authenticating user's status.
   */
  def update(auth: String, msg: String) {
    doPOST(API_ROOT + UPDATE_SUFFIX,
      auth,
      params = new ArrayList[BasicNameValuePair]() {
        add(new BasicNameValuePair(SOURCE_PARAM, SOURCE_VALUE))
        add(new BasicNameValuePair(STATUS_PARAM, msg))
      }
      )
  }

  def main(args: Array[String]) {
    update(args(0), args(1))
  }
}
