package diff;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class BinaryServiceTest {

    @Test
    public void encodeStringToBase64() throws Exception {
        assertEquals(BinaryService.stringToBase64("some string"), "c29tZSBzdHJpbmc=");
    }

    /**
     * Crates a temporary file, write a sample string in it, and asserts its encoded output.
     * @throws Exception
     */
    @Test
    public void encodeBytesArrayToBase64() throws Exception {
        Path path = Files.createTempFile("sample", ".txt");
        File file = path.toFile();
        Files.write(path,"some string".getBytes(StandardCharsets.UTF_8));
        assertEquals("c29tZSBzdHJpbmc=", BinaryService.byteArrayToBase64(Files.readAllBytes(path)));
        file.deleteOnExit();
    }

    @Test
    public void decodeToBase64() throws Exception {
        assertEquals(BinaryService.base64ToString("c29tZSBzdHJpbmc=".getBytes()), "some string");
    }

    /**
     * Generates local file based on a base64 as input, asserts that file was generated and md5sum
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void decodeFromBase64ToFile() throws IOException, NoSuchAlgorithmException {
        String imageInBase64 = new String(Files.readAllBytes(Paths.get("src/test/resources/greengroup.txt")));
        String randomFileName = BinaryService.base64ToFile(imageInBase64);
        assertNotNull(randomFileName);
        assertEquals("619644AC481D42D67E29F42C080EEA5E", BinaryService.md5Sum(Paths.get(randomFileName)));
    }

}