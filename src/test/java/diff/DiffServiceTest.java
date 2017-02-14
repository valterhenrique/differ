package diff;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DiffServiceTest {

    @Test
    public void diffSameFile() throws Exception {
        String imageBase64 = new String(Files.readAllBytes(Paths.get("src/test/resources/greengroup.txt")));
        assertEquals("Equal", DiffService.diff(imageBase64,imageBase64));
    }

    // TODO (vasi) (improvement) : increase range of tests with different file types such as pdf, jpeg, .avi, .mp3, etc..

    @Test
    public void diffDifferentFiles() throws Exception {
        String leftImageBase64 = BinaryService.fileToBase64("data:image/png", Paths.get("src/test/resources/greengroup.png"));
        String rightImageBase64 = new String(Files.readAllBytes(Paths.get("src/test/resources/github.txt")));

        String fileSize = DiffService.diff(leftImageBase64,rightImageBase64);
        assertEquals("Not equal. Left file: 25627 bytes.Right file: 7157 bytes.", fileSize);
    }

    @Test
    public void diffDifferentFilesSameSize() throws Exception {
        String leftImageBase64 = new String(Files.readAllBytes(Paths.get("src/test/resources/github.txt")));
        String rightImageBase64 = new String(Files.readAllBytes(Paths.get("src/test/resources/github2.txt")));

        String fileSize = DiffService.diff(leftImageBase64,rightImageBase64);
        assertEquals("Same size (Left file:7157 bytes / Right file: 7157 bytes), but different content. Offsets/Length: 6697/1, 7155/1.", fileSize);
    }

    @Test
    public void sameContentOneFileTest() throws Exception {
        Path path = Files.createTempFile("left-sample", ".txt");
        File file = path.toFile();
        Files.write(path,"some string".getBytes(StandardCharsets.UTF_8));
        assertEquals("Equal", DiffService.diff(path, path));
        file.deleteOnExit();
    }

    @Test
    public void sameContentTwoFilesTest() throws IOException {
        Path pathLeft = Files.createTempFile("left-sample", ".txt");
        File fileLeft = pathLeft.toFile();
        Files.write(pathLeft,"some string".getBytes(StandardCharsets.UTF_8));

        Path pathRight = Files.createTempFile("left-sample", ".txt");
        File fileRight = pathRight.toFile();
        Files.write(pathRight,"some string".getBytes(StandardCharsets.UTF_8));

        assertEquals("Equal", DiffService.diff(pathLeft, pathRight));

        fileLeft.deleteOnExit();
        fileRight.deleteOnExit();
    }

    @Test
    public void differentContentTwoFilesTest() throws IOException {
        Path pathLeft = Files.createTempFile("left-sample", ".txt");
        File fileLeft = pathLeft.toFile();
        Files.write(pathLeft,"some string".getBytes(StandardCharsets.UTF_8));

        Path pathRight = Files.createTempFile("left-sample", ".txt");
        File fileRight = pathRight.toFile();
        Files.write(pathRight,"another string".getBytes(StandardCharsets.UTF_8));

        assertNotEquals("equal", DiffService.diff(pathLeft, pathRight));

        fileLeft.deleteOnExit();
        fileRight.deleteOnExit();
    }

}