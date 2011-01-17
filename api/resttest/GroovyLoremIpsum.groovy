
/**
* Use the lipsum generator to generate Lorem Ipsum dummy paragraphs / words / bytes.
*
* Lorem Ipsum courtesy of www.lipsum.com by James Wilson
*
* @param what in ['paras','words','bytes'], default: 'paras'
* @param amount of paras/words/bytes, default: 2 (for words minimum is 5, for bytes it is 27)
* @param start always start with 'Lorem Ipsum', default = true
**/

class GroovyLoremIpsum
{
    def lipsum = { 
           what = "paras", amount = 2, start = true ->
           def text = new URL("http://www.lipsum.com/feed/xml?what=$what&amount=$amount&start=${start?'yes':'no'}").text
     
           def feed = new XmlSlurper().parseText(text)
           feed.lipsum.text()
    }
}

