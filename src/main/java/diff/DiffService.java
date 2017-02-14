package diff;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

class DiffService {

    /**
     * Diff between two base64 Strings
     * @param left String encoded on base64
     * @param right String encoded on base64
     * @return The difference between Strings.
     * @throws IOException
     */
    static String diff(String left, String right) throws IOException {
        Path pathLeft = Paths.get(BinaryService.base64ToFile(left));
        Path pathRight = Paths.get(BinaryService.base64ToFile(right));
        return diff(pathLeft, pathRight);
    }

    /**
     * Receives to local paths and calculates the difference between them, byte-to-byte.
     * @param pathLeft Local file path
     * @param pathRight Local file path.
     * @return 'Equal' if both files are equal. 'Not equal', if both files are not equal. 'Same size', if both files
     * contains the same size but are not equal.
     * @throws IOException
     */
    // TODO (vasi) (improvement): improve class DiffResult add a result code = 0: equal, -1: different, 1: same size, but different
    static String diff(Path pathLeft, Path pathRight) throws IOException {
        final long fileSizeLeft = Files.size(pathLeft);
        final long fileSizeRight = Files.size(pathRight);

        if ( fileSizeLeft != fileSizeRight) {
            return "Not equal. "
                    + "Left file: " + fileSizeLeft + " bytes."
                    + "Right file: " + fileSizeRight + " bytes.";
        }

        List<DiffResult> diffs = new ArrayList<>();

        Long offset = 0L;
        try (InputStream isLeft = Files.newInputStream(pathLeft);
             InputStream isRight = Files.newInputStream(pathRight)) {
            // comparing byte-by-byte
            // could be faster by having a bigger length while reading the bytes array

            int leftByte = 0;
            while (leftByte != -1) {
                leftByte = isLeft.read();
                int rightByte = isRight.read();

                if (leftByte != rightByte){
                    // create a diff with current diff position and the length of 1 byte
                    diffs.add(new DiffResult(offset, 1L));
                }else {
                    offset++;
                }
            }
        }

        if (!diffs.isEmpty()){
            String offsetAndLength = diffs.stream().map( diff -> diff.getOffset() + "/" + diff.getLength()).collect(Collectors.joining(", "));
            return "Same size (Left file:" + fileSizeLeft + " bytes / Right file: " + fileSizeRight + " bytes), but different content. Offsets/Length: " + offsetAndLength + ".";
        }

        return "Equal";
    }
}
