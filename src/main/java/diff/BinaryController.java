package diff;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
public class BinaryController {

    /**
     * Encodes a string into base64
     * @param aString String that will the encoded
     * @return aString encoded
     * @throws UnsupportedEncodingException If received String encoding is not supported
     */
    @RequestMapping("/v1/binary/toBase64")
    public String convertStringToBase64(@RequestParam(value = "string", defaultValue = "") String aString) throws UnsupportedEncodingException {
        return BinaryService.stringToBase64(aString);
    }
}
