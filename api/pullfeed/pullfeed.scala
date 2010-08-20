/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import scala.xml._
import scala.io.Source
import Codein._

object PullFeed {



  /**
   * @param args the command line arguments
   *    String: source to read
   *    String: mapping expression: how to map input xpath fields to Codein API fields. The mapping is like:
   *        xpath-fields-string => codein-fields-strings
   *    String: API key
   */
  def main(args: Array[String]): Unit = {
      var url = "file://localhost/readme"
      var expression = "//author/name"
      var APIkey = "secret"
   
    // TODO Algorithm:
    // 1. read parameters: url, mapping expression, secret
    // 2. read the file from the url
    // 3. map xpath fields to codein fields
    // 4. call the push method of codein with the secret
    // 4.1 the secret is used to get the username from Codein API directory

    // val url = "file://localhost/Users/orestes/devel/git/examples/scala/github.xml"
    // val url = "http://techcamp.hi.inet//statusnet/index.php/rss"

    // 1. read parameters: url, mapping expression, secret
    if ( args.length > 0 ) url= args( 0 )
    if ( args.length > 1 ) expression = args( 1 )
    if ( args.length > 2 ) APIkey= args( 2 )

    // Starting the Actor
    println( "Starting the actor")
    APIActor.start()

    println ( "Reading " + ( url, expression, APIkey))
    
    // 2. read the file from the url
    var data = XML.loadString( Source.fromURL(url).mkString )

    // 3. map xpath fields to codein fields
    // 3.1 Get the project name
    var project = (data \ "id").text split "/"
    var projectname= project(1) + "/" +project(2)
    println ( "projectname= " + projectname )

    // 3.2 Get a sequence of XML elements
    var n= filter(data, expression)
    
    // 3.3 Process each element
    // TODO: Use Scala Extractors and Classes to get the data
    n foreach { 
        a => {
            // List: commitID, title, author
            val entry= List( (a \ "id").text split "/" last, (a \ "title").text, (a \ "author" \ "name").text )
            // Message body= #projectname~commitID url=commitURL -- description
            val message= "#" + projectname + "~" + entry(0) + "url=http://github.com/" + projectname + "/commit/" + entry(0) + "--" + entry(1);
            println( "-----begin")
            println( message);
            println( "author=" + entry(2) )
            println( "-----end")
            println( "")
            
            // Build a PostEntry object
            val post= new PostEntry( entry(2), message)
            
            APIActor ! post
          }
    }
    // println( "salida")
    // n map ( _ text) foreach println

  }
  
  var counter=1;
  def filter( data: NodeSeq, e: String): NodeSeq ={
      // Parse expression
      val tokens= e split "//"

      // counter= counter + 1
      // println( counter + " tokens=" + (tokens mkString( "", "-", "")) + " data=" + (data toString))
      if( tokens.length == 1) return data \\ tokens.head
      filter( data \\ tokens.head, tokens.tail mkString ("", "//", ""))
  }

}

