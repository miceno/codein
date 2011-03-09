

import java.nio.charset.*
import java.nio.*

/*
Charset.availableCharsets().each{
println it
}
*/

// Create the encoder and decoder for ISO-8859-1
String encoding= "UTF-8"
Charset charset = Charset.forName( encoding);
CharsetDecoder decoder = charset.newDecoder();
CharsetEncoder encoder = charset.newEncoder();

try {
    // Convert a string to ISO-LATIN-1 bytes in a ByteBuffer
    // The new ByteBuffer is ready to be read.
    String str= "España, áéíóú ñÑ"
    def bytes= str.getBytes()
    
    bytes.each{
        println "byte: $it"
    }
    
    ByteBuffer byteString= ByteBuffer.wrap( str.getBytes())
    ByteBuffer strEncode = encoder.encode( byteString.asCharBuffer());

    println "encode to ${strEncode.toString()}"
    // Convert $encoding bytes in a ByteBuffer to a character ByteBuffer and then to a string.
    // The new ByteBuffer is ready to be read.
    CharBuffer cbuf = decoder.decode(strEncode);
    String sz = cbuf.toString();
} catch (CharacterCodingException e) {
    println "exception $e"
}

