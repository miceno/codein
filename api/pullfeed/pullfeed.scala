/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import scala.xml._
import scala.io.Source
import codein._
import scala.collection.mutable._

object PullFeed {

  /*
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
    // 1. read parameters: url, mapping expression, user, template
    // 2. read the file from the url
    // 3. map xpath fields to codein fields
    // 4. call the push method of codein with the secret
    // 4.1 the secret is used to get the username from Codein API directory

    // val url = "file://localhost/Users/orestes/devel/git/examples/scala/github.xml"
    // val url = "http://techcamp.hi.inet//statusnet/index.php/rss"

    // 1. read parameters: url, mapping expression, secret
    val arguments = getArgs(args)

    // 2.1. Read the XML file
    println ( "arguments= " + arguments( "url"))
    var data = XML.loadString(Source.fromURL(arguments("url")) mkString)

/*
    // Elements    
    var e= arguments( "expression") split "/" filter (x=> x.length > 0)
    println( "elementos")
    e foreach ( x=> { print( "\""); print(x); print( "\""); } )
    println( "")
    
    val r= extractElements( data, e)
    println( "elements\n+++++++")
    r foreach println
    println( "")
*/

    val expressions= tokenize( arguments( "expression"))
    var mapa = new HashMap[ String, Array[String]]
    
    expressions foreach { v => 
    // Subelements
        println( "variable= " + v(0))
        val s= v(1) split "//" filter (x=> x.length > 0)
        println( "subelementos")
        s foreach ( x=> { print( "\""); print(x); print( "\" "); } )
        println( "")    
        
        val z= extractSubElements( data, s)
        println( "sub\n+++++++")
        mapa += ( v(0)-> (z  map ( x => x.text) ).toArray)
    }
    
    mapa foreach { v=> println( v._1 + " -> "); v._2 foreach println; println("----") }
    
    var maximo: Int= 0
    mapa foreach {  v=> maximo= maximo max v._2.length; println( "max=" + maximo + " ? " + v._2.length) }
    println ( "maximo=" + maximo)
    
    println( "mi mapa")
    mapa foreach { v=> 
        val s = getValue( mapa, v._1, v._2.length - 1)
        println( v._1 + "-> " + s)
    }
    
    val template= arguments( "template")
    for ( i <- 0 to maximo - 1){
        var mensaje: String= template
        
        for( m <- mapa){
            val indice= if( m._2.length == 1) 0 else i 
            val valor= m._2( indice)
            mensaje= substitute( mensaje, m._1, valor)
        }
        println( "mensaje " + i + ": '"+ mensaje + "'")
        StatusnetAPI.update( arguments( "user"), mensaje, scala.collection.mutable.Map( "source" -> "pullfeed"), "xml");
    }
    
  }
  
  def substitute( template: String, token: String, valor: String) : String =
  {
      // println( "template=" + template)
      // println( "token=" + token)
      // println( "valor=" + valor)
      
      val start= template.indexOf( "{"+token+"}")
      if( start< 0 ) return template
      val resultado = template.patch( start, valor, token.length+2 )
      
      // println( "sustitucion=" + resultado)
      return resultado
  }

  def getValue( mapa: Map[String, Array[String]], token: String, order: Int): String = {
      val row = mapa( token)
      var indice= order
      
      if( row.length == 1)
        indice= 0
    
      return row( indice)
  }
  /* 
  * Get data elements based on expression. Elements are only first level elements
  * @data XML data
  * @e expression xpath
  * @return XML data that matches the expression
  * */

  def extractElements(data: NodeSeq, e: Array[String]): NodeSeq = {
    // Parse expression
    /*
    println("data=" + data)
    print("e=")
    e foreach println
    println("head=" + e.head)
    println("tail=" + e.tail)
    */
    var resultado= data

    if ( e.length == 1 ) {
        resultado= data \ e.head
      }
      else
      resultado= extractElements( data \ e.head, e.tail)

    return resultado

  }


  /* 
  * Get data elements based on expression. Elements are only first level elements
  * @data XML data
  * @e expression xpath
  * @return XML data that matches the expression
  * */

  def extractSubElements(data: NodeSeq, e: Array[String]): NodeSeq = {
    // Parse expression
    /*
    println("data=" + data)
    print("e=")
    e foreach println
    println("head=" + e.head)
    println("tail=" + e.tail)
    */
    var resultado= data

    if( e.head.contains( "/"))
    {
        val elements= e.head.split( "/") filter (x=> x.length > 0)
        if( elements.length == 1)
            resultado= resultado \\ elements.head
        else
            resultado= extractElements( resultado \\ elements.head, elements.tail)
    }
    else if ( e.length == 1 ) {
        resultado= resultado \\ e.head
      }
    else
      resultado= extractSubElements( resultado \\ e.head, e.tail)

    return resultado

  }

  def getArgs(args: Array[String]): Map[String, String] = {
    val resultado: Map[String, String] = Map(
      "url" -> "http://example.com/rss",
      "expression" -> "//entry->content",
      "user" -> "codein",
      "APIkey" -> "a1b2c3d4")

    if (args.length > 0) resultado("url") = args(0)
    if (args.length > 1) resultado("expression") = args(1)
    if (args.length > 2) resultado("user") = args(2)
    if (args.length > 3) resultado("template") = args(3)
    
    println(args mkString)
    println(resultado)
    return resultado
  }
  
  def tokenize(e: String) = e.split(",").map(z => z.split("->"))
  

}

case class Analizador ( url: String, expression: String, last_update : Date, max_elements : Integer, sort_order: Boolean ){

    
    val expressions= tokenize( arguments( "expression"))
    var mapa = new HashMap[ String, Array[String]]

    expressions foreach { v =>
    // Subelements
        println( "variable= " + v(0))
        val s= v(1) split "//" filter (x=> x.length > 0)
        println( "subelementos")
        s foreach ( x=> { print( "\""); print(x); print( "\" "); } )
        println( "")

        val z= extractSubElements( data, s)
        println( "sub\n+++++++")
        mapa += ( v(0)-> (z  map ( x => x.text) ).toArray)
    }

    mapa foreach { v=> println( v._1 + " -> "); v._2 foreach println; println("----") }

    
}


