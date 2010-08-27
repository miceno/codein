package codein
import codein.logger.Logs
import codein.config.Properties
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
object StatusnetAPI extends Logs {

  val STATUS = "status"
  val SOURCE = "source"
  val IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id"
  val LAT = "lat"
  val LONG = "long"
  val MEDIA = "media"
  val USER_ID = "user_id"

  val XML = "xml"
  val JSON = "json"
  val RSS = "rss"

  val UPDATE_SUFFIX = "udpate"
  val FRIENDS_TIMELINE_SUFFIX = "friends_timeline"
  val PUBLIC_TIMELINE_SUFFIX = "public_timeline"

  val API_ROOT = Properties.get("API_ROOT")
  val SUDOER = Properties.get("SUDOER")
  val PASSWD = Properties.get("PASSWD")

  private def doGET(url: String, params: scala.collection.mutable.Map[String, String]) {}

  private def doPOST(url: String, auth: String, pass: String, map: scala.collection.mutable.Map[String, String]) {

    //set up http basic authentication
    val provider = new BasicCredentialsProvider
    provider.setCredentials(
      new AuthScope(ANY_HOST, ANY_PORT, ANY_REALM),
      new UsernamePasswordCredentials(auth, pass)
      )

    //set up http client
    val client = new DefaultHttpClient
    val post = new HttpPost(url)
    post.setEntity(new UrlEncodedFormEntity(
      new ArrayList[BasicNameValuePair]() {
        map foreach { case (key, value) => add(new BasicNameValuePair(key, value)) }
      },
      UTF_8));
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
   *  @param status The URL-encoded text of the status update.
   *  @param optional Parameter map. Supported params:
   *	source 	(Optional) The source of the status.
   *	in_reply_to_status_id 	(Optional) The ID of an existing status that the update is in reply to.
   *	lat 	(Optional) The latitude the status refers to.
   *	long 	(Optional) The longitude the status refers to.
   *	media 	(Optional) a media upload, such as an image or movie file.
   */
  def update(auth: String, status: String, optional: scala.collection.mutable.Map[String, String], format: String) {
    doPOST(
      API_ROOT + UPDATE_SUFFIX + "." + format,
      SUDOER + "#" + auth,
      PASSWD,
      optional += (STATUS -> status))
  }

  /**
   * Returns the 20 most recent notices from users throughout the system who have uploaded their own avatars. 
   * Depending on configuration, it may or may not not include notices from automatic posting services.
   *  
   *  @param optional Parameter map. Supported params:
   *	since_id 	(Optional) Returns only statuses with an ID greater than (that is, more recent than) the specified ID.
   *	max_id 	(Optional) Returns only statuses with an ID less than (that is, older than) or equal to the specified ID.
   *	count 	(Optional) Specifies the number of statuses to retrieve.
   *	page 	(Optional) Specifies the page of results to retrieve.
   */
  def publicTimeline(optional: scala.collection.mutable.Map[String, String], format: String) {
    doGET(
      API_ROOT + PUBLIC_TIMELINE_SUFFIX + "." + format,
      optional)
  }

  /**
   * Returns the 20 most recent notices from users throughout the system who have uploaded their own avatars. 
   * Depending on configuration, it may or may not not include notices from automatic posting services.
   *  
   *  @param optional Parameter map. Supported params:
   *  	user_id 	(Optional) Specifies a user by ID
   *  	screen_name 	(Optional) Specifies a user by screename (nickname) 
   *	since_id 	(Optional) Returns only statuses with an ID greater than (that is, more recent than) the specified ID.
   *	max_id 	(Optional) Returns only statuses with an ID less than (that is, older than) or equal to the specified ID.
   *	count 	(Optional) Specifies the number of statuses to retrieve.
   *	page 	(Optional) Specifies the page of results to retrieve.
   */
  def friendsTimeline(optional: scala.collection.mutable.Map[String, String], format: String) {
    doGET(
      API_ROOT + FRIENDS_TIMELINE_SUFFIX + "." + format,
      optional)
  }

  def friendsTimeline(user: String, optional: scala.collection.mutable.Map[String, String], format: String) {
    doGET(
      API_ROOT + FRIENDS_TIMELINE_SUFFIX + "/" + user + "." + format,
      optional += (USER_ID -> user))
  }

  def main(args: Array[String]) {
    update(args(0), args(1), scala.collection.mutable.Map.empty, XML)
  }
}
