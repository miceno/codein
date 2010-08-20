


package Codein
{

    import scala.actors._
    import scala.actors.Actor._

    object httpclient {
        def send( p: PostEntry) : Unit = { 
            Console println "probando..."
            println( p)
        }
    }

    object APIActor extends Actor {
      def act() {
        loop {
          receive {
            case p: PostEntry => httpclient.send( p);
            case "exit"   => println("exiting..."); exit
            case x: Any   => println("Error: Unknown message! " + x)
          }
        }
      }
    }

}