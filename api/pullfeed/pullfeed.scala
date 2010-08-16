/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import scala.xml._
import scala.io.Source

object PullFeed {

  /**
   * @param args the command line arguments
   *    String: source to read
   *    String: mapping expression: how to map input xpath fields to Codein API fields. The mapping is like:
   *        xpath-fields-string => codein-fields-strings
   *    String: API key
   */
  def main(args: Array[String]): Unit = {
    println("Hello, world!")
    
    // TODO Algorithm:
    // 1. read parameters: url, mapping expression, secret
    // 2. read the file from the url
    // 3. map xpath fields to codein fields
    // 4. call the push method of codein with the secret
    // 4.1 the secret is used to get the username from Codein API directory

    // val url = "file://localhost/Users/orestes/devel/git/examples/scala/github.xml"
    // val url = "http://techcamp.hi.inet//statusnet/index.php/rss"

val url = if ( args.length < 1 ) "file://localhost/readme" else args( 0 );
val expression= if ( args.length < 2 ) "//author/name" else args( 1 );
var APIkey= if ( args.length < 3 ) "secret" else args( 2 );

    println ( "Reading " + ( url, expression, APIkey))
    
    // println ( url )
    val data = XML.loadString( Source.fromURL(url).mkString )

    data \\ "author" \\ "name" foreach( a=> println( a text))
    // data \ "channel" \ "items" foreach ( a => println( a \ "rdf:Seq" \ "rdf:li"  \ "@rdf:resource" text ))
    // data \ "channel" \ "items" \ "rdf:Seq" \ "rdf:li" foreach ( a => println( a text))

  }

}

