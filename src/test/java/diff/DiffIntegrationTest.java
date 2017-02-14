package diff;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiffIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void emptyBinaryOnLeft() {
        ResponseEntity<Binary> responseEntity =
                restTemplate.postForEntity("/v1/diff/0/left/", "", Binary.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void emptyBinaryOnRight() {
        ResponseEntity<Binary> responseEntity =
                restTemplate.postForEntity("/v1/diff/0/right/", "", Binary.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void nonBase64StringOnLeft() {
        ResponseEntity<Binary> responseEntity =
                restTemplate.postForEntity("/v1/diff/0/left/", "some string", Binary.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void nonBase64StringOnRight() {
        ResponseEntity<Binary> responseEntity =
                restTemplate.postForEntity("/v1/diff/0/right/", "some string", Binary.class);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void base64StringOnLeft() {
        ResponseEntity<Binary> responseEntity =
                // 'some string' = (base64) c29tZSBzdHJpbmc=
                restTemplate.postForEntity("/v1/diff/0/left/", "data:text/plain;base64,c29tZSBzdHJpbmc=", Binary.class);
        Binary binary = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("text", binary.getContentType());
        assertEquals("plain", binary.getSuffix());
        assertEquals("c29tZSBzdHJpbmc=", binary.getBase64());
    }

    @Test
    public void base64StringOnRight() {
        ResponseEntity<Binary> responseEntity =
                restTemplate.postForEntity("/v1/diff/0/right/", "data:text/plain;base64,YW5vdGhlciBzdHJpbmc=", Binary.class);
        Binary binary = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("text", binary.getContentType());
        assertEquals("plain", binary.getSuffix());
        assertEquals("YW5vdGhlciBzdHJpbmc=", binary.getBase64());
    }

    @Test
    public void diffDifferentPlainText() {
        ResponseEntity<Binary> responseEntityLeft =
                restTemplate.postForEntity("/v1/diff/0/left/", "data:text/plain;base64,c29tZSBzdHJpbmc=", Binary.class);
        Binary binaryLeft = responseEntityLeft.getBody();
        assertEquals(HttpStatus.OK, responseEntityLeft.getStatusCode());
        assertEquals("text", binaryLeft.getContentType());
        assertEquals("plain", binaryLeft.getSuffix());
        assertEquals("c29tZSBzdHJpbmc=", binaryLeft.getBase64());

        ResponseEntity<Binary> responseEntityRight =
                // 'another string' = (base64) YW5vdGhlciBzdHJpbmc=
                restTemplate.postForEntity("/v1/diff/0/right/", "data:text/plain;base64,YW5vdGhlciBzdHJpbmc=", Binary.class);
        Binary binaryRight = responseEntityRight.getBody();
        assertEquals(HttpStatus.OK, responseEntityRight.getStatusCode());
        assertEquals("text", binaryRight.getContentType());
        assertEquals("plain", binaryRight.getSuffix());
        assertEquals("YW5vdGhlciBzdHJpbmc=", binaryRight.getBase64());

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("/v1/diff/0/", String.class);
        String  diffResult = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Not equal. Left file: 11 bytes.Right file: 14 bytes.", diffResult);
    }

    @Test
    public void diffSamePlainText() {
        ResponseEntity<Binary> responseEntityLeft =
                restTemplate.postForEntity("/v1/diff/0/left/", "data:text/plain;base64,c29tZSBzdHJpbmc=", Binary.class);
        Binary binaryLeft = responseEntityLeft.getBody();
        assertEquals(HttpStatus.OK, responseEntityLeft.getStatusCode());
        assertEquals("text", binaryLeft.getContentType());
        assertEquals("plain", binaryLeft.getSuffix());
        assertEquals("c29tZSBzdHJpbmc=", binaryLeft.getBase64());

        ResponseEntity<Binary> responseEntityRight =
                restTemplate.postForEntity("/v1/diff/0/right/", "data:text/plain;base64,c29tZSBzdHJpbmc=", Binary.class);
        Binary binaryRight = responseEntityRight.getBody();
        assertEquals(HttpStatus.OK, responseEntityRight.getStatusCode());
        assertEquals("text", binaryRight.getContentType());
        assertEquals("plain", binaryRight.getSuffix());
        assertEquals("c29tZSBzdHJpbmc=", binaryRight.getBase64());

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("/v1/diff/0/", String.class);
        String  diffResult = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Equal", diffResult);
    }

    @Test
    public void diffDifferentImageBinary() throws IOException {
        String leftImageBase64 = BinaryService.fileToBase64("data:image/png", Paths.get("src/test/resources/greengroup.png"));

        ResponseEntity<Binary> responseEntityLeft =
                restTemplate.postForEntity("/v1/diff/0/left/", leftImageBase64, Binary.class);
        Binary binaryLeft = responseEntityLeft.getBody();
        assertEquals(HttpStatus.OK, responseEntityLeft.getStatusCode());
        assertEquals("image", binaryLeft.getContentType());
        assertEquals("png", binaryLeft.getSuffix());

        // TODO (vasi): base64 from image is too long, paste it into a file
        // assertEquals("", binaryLeft.getBase64());

        String rightImageBase64 = BinaryService.fileToBase64("data:image/png", Paths.get("src/test/resources/github.png"));
        ResponseEntity<Binary> responseEntityRight =
                restTemplate.postForEntity("/v1/diff/0/right/", rightImageBase64, Binary.class);
        Binary binaryRight = responseEntityRight.getBody();
        assertEquals(HttpStatus.OK, responseEntityRight.getStatusCode());
        assertEquals("image", binaryRight.getContentType());
        assertEquals("png", binaryRight.getSuffix());
        // TODO : same as TODO above
        // assertEquals("", binaryRight.getBase64());

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("/v1/diff/0/", String.class);
        String  diffResult = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Not equal. Left file: 25627 bytes.Right file: 7157 bytes.", diffResult);
    }

    // TODO (vasi) (improvement) : increase tests coverage : implement tests sameBinaryImage, sameImageText ...
    // TODO (vasi) (improvement) : increase tests cases : test different binary file types : pdf, jpeg, video, etc ...

    @Test
    public void diffDifferentImageText() throws IOException {
        String leftImageBase64 = new String(Files.readAllBytes(Paths.get("src/test/resources/github.txt")));
        ResponseEntity<Binary> responseEntityLeft =
                restTemplate.postForEntity("/v1/diff/0/left/", leftImageBase64, Binary.class);
        Binary binaryLeft = responseEntityLeft.getBody();
        assertEquals(HttpStatus.OK, responseEntityLeft.getStatusCode());
        assertEquals("image", binaryLeft.getContentType());
        assertEquals("png", binaryLeft.getSuffix());

        // TODO (vasi): base64 from image is too long, paste it into a file
        // assertEquals("", binaryLeft.getBase64());

        String rightImageBase64 = new String(Files.readAllBytes(Paths.get("src/test/resources/github2.txt")));
        ResponseEntity<Binary> responseEntityRight =
                restTemplate.postForEntity("/v1/diff/0/right/", rightImageBase64, Binary.class);
        Binary binaryRight = responseEntityRight.getBody();
        assertEquals(HttpStatus.OK, responseEntityRight.getStatusCode());
        assertEquals("image", binaryRight.getContentType());
        assertEquals("png", binaryRight.getSuffix());
        // TODO : same as TODO above
        // assertEquals("", binaryRight.getBase64());

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("/v1/diff/0/", String.class);
        String  diffResult = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Same size (Left file:7157 bytes / Right file: 7157 bytes), but different content. Offsets/Length: 6697/1, 7155/1.", diffResult);
    }
}
