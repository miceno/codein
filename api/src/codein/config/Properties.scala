package codein.config

object Properties {

  val PATH = "/Users/sergioa/Desktop/devel/workspace/codein/api/bin/codein/config/"
  val FILENAME = "props.ini.sample"

  private[this] val properties: Option[Map[String, String]] = {
    try {
      val file = new java.io.FileInputStream(PATH + FILENAME)
      val props = new java.util.Properties
      props.load(file)

      file.close
      val iter = props.entrySet.iterator
      val vals = scala.collection.mutable.Map[String, String]()
      while (iter.hasNext) {
        val item = iter.next
        vals += (item.getKey.toString -> item.getValue.toString)
      }
      Some(vals.toMap.withDefaultValue(""))
    } catch {
      case e: Exception => println("Properties.loadFile: " + e)
      None
    }

  }

  def get(key: String) = properties.get(key)

  def main(argv: Array[String]) =
    {

      println(get("PROP2"))
    }

}